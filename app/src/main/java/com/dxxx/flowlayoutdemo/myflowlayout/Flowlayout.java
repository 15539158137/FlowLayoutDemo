package com.dxxx.flowlayoutdemo.myflowlayout;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.dxxx.flowlayoutdemo.R;
import com.dxxx.flowlayoutdemo.SizeUtil;

import java.util.ArrayList;
import java.util.List;

public class Flowlayout extends ViewGroup {
    OnFlowlayoutItemClickListener onFlowlayoutItemClickListener;

    public void setOnFlowlayoutItemClickListener(OnFlowlayoutItemClickListener onFlowlayoutItemClickListener) {
        this.onFlowlayoutItemClickListener = onFlowlayoutItemClickListener;
    }

    public Flowlayout(Context context) {
        super(context);
    }

    public Flowlayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Flowlayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Flowlayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    //外部传入数据
    public void setDatas(List<DataBean> datas) {
        this.dataBeanList = datas;
        //invalidate();这个方法只会触发onDraw
        requestLayout();//这个只会触发onDraw和OnMeasure
    }

    //设置是否可以多选
    private boolean canChooseMulite;

    public void setChooseMulite(boolean canChooseMulite) {
        this.canChooseMulite = canChooseMulite;
    }


    //通过数据源来控制子view的点击事件的方法
    public void notifyDataChanged() {
        //这个会触发onMeasure方法，但是必须保证dataBeanList还是同一个对象才行
        requestLayout();
    }

    private List<DataBean> dataBeanList;

    private void initView() {
        //先清空所有
        this.removeAllViews();
        for (int i = 0; i < dataBeanList.size(); i++) {
            final DataBean dataBean = dataBeanList.get(i);
            final TextView textView = new TextView(getContext());
            textView.setText(dataBean.getFlowItemName());
            //设置开始是否被选中
            if (dataBean.isFlowItemIsChoosed()) {
                textView.setBackgroundResource(R.drawable.shape_tv_red);
            } else {
                textView.setBackgroundResource(R.drawable.shape_tv_blue);
            }
            textView.setBackgroundResource(R.drawable.shape_item_bg);
            textView.setPadding(SizeUtil.dip2px(getContext(), 5), SizeUtil.dip2px(getContext(), 3), SizeUtil.dip2px(getContext(), 5), SizeUtil.dip2px(getContext(), 3));
            MarginLayoutParams marginLayoutParams = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            marginLayoutParams.rightMargin = SizeUtil.dip2px(getContext(), 10);
            marginLayoutParams.leftMargin = SizeUtil.dip2px(getContext(), 10);
            marginLayoutParams.topMargin = SizeUtil.dip2px(getContext(), 5);
            marginLayoutParams.bottomMargin = SizeUtil.dip2px(getContext(), 5);
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //可以多选
                    dataBean.setFlowItemIsChoosed(!dataBean.isFlowItemIsChoosed());
                    if (dataBean.isFlowItemIsChoosed()) {
                        textView.setBackgroundResource(R.drawable.shape_tv_red);
                    } else {
                        textView.setBackgroundResource(R.drawable.shape_tv_blue);
                    }
                    if (onFlowlayoutItemClickListener != null) {
                        onFlowlayoutItemClickListener.onItemClick(textView, dataBean);
                    }
                    if (!canChooseMulite) {
                        boolean nowState = !dataBean.isFlowItemIsChoosed();
                        //不管当前这个点击之后的状态是不是选中，其他的都必须是未选中。
                        for (int j = 0; j < dataBeanList.size(); j++) {
                            if (v == getChildAt(j)) {
                                //这个就是当前点击的这个子view
                            } else {
                                dataBeanList.get(j).setFlowItemIsChoosed(false);
                                getChildAt(j).setBackgroundResource(R.drawable.shape_tv_blue);
                            }
                        }
                    }
                }
            });
            textView.setLayoutParams(marginLayoutParams);
            this.addView(textView, i);//后面的i表示index就是getChildAt index
        }
    }

    //所有的view
    List<List<View>> allViews;
    //每一行上的view
    List<View> oneLineViews;
    //每一行的高度
    List<Integer> allHeights;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.e("onMeasure", "===");
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initView();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthMeasure = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightMeasure = MeasureSpec.getSize(heightMeasureSpec);


        //实际操作中height一定是wrap，如果height写成match或者具体尺寸，也不判断是否能容纳下
        //width不管写成warp还是match还是具体尺寸，都不影响，因为wrap默认就是match，所以实际宽度就是这个计算出来的数值
        int width = widthMeasure;
        int height = heightMeasure;
        //先计算viewgroup横向上实际能够使用的宽度：width-padding
        //可以使用的宽度是
        int canUseWidth = width - getPaddingLeft() - getPaddingRight();
        //for循环view，计算每个view的宽高，当宽度超出就换行
        //list<list<view>>  内层是这行的view，外层是行数
        int childCount = getChildCount();
        Log.e("当前页面有多少个child", childCount + "");
        //当前一行上累计的宽度
        int nowWidth = 0;
        //这一行的高度
        int nowHeight = 0;
        //
        int totalHeight = 0;
        //所有的view
        allViews = new ArrayList<>();
        //每一行上的view
        oneLineViews = new ArrayList<>();
        //每一行的高度
        allHeights = new ArrayList<>();
        Log.e("横向可容纳的宽度是", canUseWidth + "");
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            // measureChildWithMargins();把viewgroup的margin都加进去计算进去
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            MarginLayoutParams marginLayoutParams = (MarginLayoutParams) child.getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            nowWidth = nowWidth + childWidth + marginLayoutParams.rightMargin + marginLayoutParams.leftMargin;
            Log.e("当前nowWidth是", nowWidth + "");
            //viewgroup的高度也开始计算了，得到这一行最高的高度然后相加
            int childTotalHeight = childHeight + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;
            Log.e("当前的下标", i + "");
            if (nowWidth > canUseWidth) {
//这一行放不下了，该换行了
                totalHeight = totalHeight + nowHeight;//把上一行的最大值加上
                allHeights.add(nowHeight);//设置上一行的行高
                Log.e("大于", totalHeight + "");
                //把上一行加进去，新开一行并到当前view加到新的一行去
                allViews.add(oneLineViews);
                oneLineViews = new ArrayList<>();
                oneLineViews.add(child);
                //换行了，下一行的nowwidth和nowheight是这个view的宽高
                nowWidth = childWidth + marginLayoutParams.rightMargin + marginLayoutParams.leftMargin;
                nowHeight = childTotalHeight;
            } else if (nowWidth == canUseWidth) {
                //正好放满
                if (childTotalHeight > nowHeight) {
                    nowHeight = childTotalHeight;
                }
                totalHeight = totalHeight + nowHeight;
                Log.e("等于", totalHeight + "");
                allHeights.add(nowHeight);//设置这一行的行高
                nowHeight = 0;
                //刚好能放下--加到当前行并开新行
                oneLineViews.add(child);
                allViews.add(oneLineViews);
                oneLineViews = new ArrayList<>();
                nowWidth = 0;

            } else {
                //小于，加到当前行里面
                oneLineViews.add(child);
                if (childTotalHeight > nowHeight) {
                    nowHeight = childTotalHeight;
                }
                if (i == childCount - 1) {
                    //如果他单独一行，上面已经new oneline了 而且已经add过了，所以不管是这行之前还有其他view还是他单独一行，只用add一下就可以，
                    allViews.add(oneLineViews);
                    allHeights.add(nowHeight);
                    totalHeight = totalHeight + nowHeight;
                    nowHeight = 0;
                    nowWidth = 0;

                }
                Log.e("小于", totalHeight + "");
            }

        }
        if (widthMode == MeasureSpec.EXACTLY) {
            //返回计算出来的
        } else {
//还是计算出来的
        }
        if (heightMode == MeasureSpec.EXACTLY) {
            //返回计算出来的
        } else {
            height = totalHeight + getPaddingTop() + getPaddingBottom();
        }
        Log.e("计算出来的宽高是", width + "==" + height);
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int startY = 0;
        Log.e("一共有多少行", allViews.size() + "");
        for (int i = 0; i < allViews.size(); i++) {
            Log.e("每一行的行高是", allHeights.get(i) + "");
            List<View> line = allViews.get(i);
            int startX = 0;
            for (int j = 0; j < line.size(); j++) {
                View child = line.get(j);
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) child.getLayoutParams();
                int childWidth = child.getMeasuredWidth();
                int childHeight = child.getMeasuredHeight();
                child.layout(startX + marginLayoutParams.leftMargin, startY + marginLayoutParams.topMargin, startX + marginLayoutParams.leftMargin + childWidth, startY + marginLayoutParams.topMargin + childHeight);
                startX = startX + childWidth + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            }
            //当前累计的行高加当前行的
            startY = startY + allHeights.get(i);
        }
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        //return super.generateLayoutParams(attrs);
        //子控件带margin,使用margin
        return new MarginLayoutParams(getContext(), attrs);
    }
}


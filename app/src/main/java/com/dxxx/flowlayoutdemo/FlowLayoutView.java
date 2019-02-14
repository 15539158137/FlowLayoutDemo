package com.dxxx.flowlayoutdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
//这个是参考的demo
public class FlowLayoutView extends ViewGroup {
    public FlowLayoutView(Context context) {
        super(context);
        testAddView();
    }

    public FlowLayoutView(Context context, AttributeSet attrs) {
        super(context, attrs);
        testAddView();
    }

    public FlowLayoutView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        testAddView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FlowLayoutView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        testAddView();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.e("触发一次onDraw","===");
        super.onDraw(canvas);
//viewgroup相当于一个容器，真正绘制的是view的onDraw方法。想让Viewgroup走onDraw可以设置个背景
//        Paint paint;
//        paint = new Paint();
//        paint.setTextSize(SizeUtil.sp2px(getContext(), 20.0f));
//        paint.setColor(Color.parseColor("#ff0000"));
//        paint.setAntiAlias(true);
//        paint.setFilterBitmap(true);//位图过滤
//        canvas.drawText("HHHHHH",0,70,paint);
    }

    final List<Boolean> list = new ArrayList<>();

    private void testAddView() {

        for (int i = 0; i < 5; i++) {
            list.add(false);
        }
        for (int i = 0; i < 5; i++) {
            final TextView textView = new TextView(getContext());
            textView.setPadding(SizeUtil.dip2px(getContext(), 5), SizeUtil.dip2px(getContext(), 5), SizeUtil.dip2px(getContext(), 5), SizeUtil.dip2px(getContext(), 5));
            textView.setText("新的数据");
            MarginLayoutParams marginLayoutParams = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            textView.setBackgroundResource(R.drawable.shape_tv_blue);
            marginLayoutParams.setMargins(SizeUtil.dip2px(getContext(),8),SizeUtil.dip2px(getContext(),8),SizeUtil.dip2px(getContext(),8),SizeUtil.dip2px(getContext(),8));
            textView.setLayoutParams(marginLayoutParams);
            boolean nowIsChecked = list.get(i);
            int finalI = i;
            boolean b = list.get(finalI);

            final int finalI1 = i;
            final int finalI2 = i;
            final int finalI3 = i;
            textView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.set(finalI1, !list.get(finalI2));
                    if (list.get(finalI3)) {
                        textView.setBackgroundResource(R.drawable.shape_tv_blue);
                    } else {
                        textView.setBackgroundResource(R.drawable.shape_tv_red);
                    }


                }
            });
            this.addView(textView, i);
        }


    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        Log.e("触发一次onLayout", "===");
        int left = getPaddingLeft();
        int top = getPaddingTop();
        //一共有几行
        int lines = mLineViews.size();
        for (int i = 0; i < lines; i++) {
            //每行行高
            int lineHeight = mLineHeight.get(i);
            //行内有几个子View
            List<View> viewList = mLineViews.get(i);
            int views = viewList.size();

            for (int j = 0; j < views; j++) {
                View view = viewList.get(j);
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) view.getLayoutParams();
                int vl = left + marginLayoutParams.leftMargin;
                int vt = top + marginLayoutParams.topMargin;
                int vr = vl + view.getMeasuredWidth();
                int vb = vt + view.getMeasuredHeight();
                view.layout(vl, vt, vr, vb);
                left += view.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
            }
            left = getPaddingLeft();
            top += lineHeight;

        }

    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    private List<List<View>> mLineViews = new ArrayList<List<View>>();
    private List<Integer> mLineHeight = new ArrayList<Integer>();

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.e("触发一次onMeasure", "===");
        //由于onMeasure会执行多次,避免重复的计算控件个数和高度,这里需要进行清空操作
        mLineViews.clear();
        mLineHeight.clear();

        //获取测量的模式和尺寸大小
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        //减去左右padding得到测量大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec) + getPaddingTop() + getPaddingBottom();


        //记录ViewGroup真实的测量宽高
        int viewGroupWidth = 0 - getPaddingLeft() - getPaddingRight();
        int viewGroupHeight = getPaddingTop() + getPaddingBottom();

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            //表示给定宽度
            viewGroupWidth = widthSize;
            viewGroupHeight = heightSize;
        } else {
            //当前所占的宽高
            int currentLineWidth = 0;
            int currentLineHeight = 0;

            //用来存储每一行上的子View
            List<View> lineView = new ArrayList<View>();
            int childViewsCount = getChildCount();
            for (int i = 0; i < childViewsCount; i++) {
                View childView = getChildAt(i);
                //对子View进行测量
                measureChild(childView, widthMeasureSpec, heightMeasureSpec);
                MarginLayoutParams marginLayoutParams = (MarginLayoutParams) childView.getLayoutParams();
                int childViewWidth = childView.getMeasuredWidth() + marginLayoutParams.leftMargin + marginLayoutParams.rightMargin;
                int childViewHeight = childView.getMeasuredHeight() + marginLayoutParams.topMargin + marginLayoutParams.bottomMargin;

                if (currentLineWidth + childViewWidth > widthSize) {
                    //当前行宽+子View+左右外边距>ViewGroup的宽度,换行
                    viewGroupWidth = Math.max(currentLineWidth, widthSize);
                    viewGroupHeight += currentLineHeight;
                    //添加行高
                    mLineHeight.add(currentLineHeight);
                    //添加行对象
                    mLineViews.add(lineView);

                    //new新的一行
                    lineView = new ArrayList<View>();
                    //添加行对象里的子View
                    lineView.add(childView);
                    currentLineWidth = childViewWidth;

                } else {
                    //当前行宽+子View+左右外边距<=ViewGroup的宽度,不换行
                    currentLineWidth += childViewWidth;
                    currentLineHeight = Math.max(currentLineHeight, childViewHeight);
                    //添加行对象里的子View
                    lineView.add(childView);
                }


                if (i == childViewsCount - 1) {
                    //最后一个子View的时候
                    //添加行对象
                    mLineViews.add(lineView);
                    viewGroupWidth = Math.max(childViewWidth, viewGroupWidth);
                    viewGroupHeight += childViewHeight;
                    //添加行高
                    mLineHeight.add(currentLineHeight);

                }


            }

        }


        setMeasuredDimension(viewGroupWidth, viewGroupHeight);
    }


}

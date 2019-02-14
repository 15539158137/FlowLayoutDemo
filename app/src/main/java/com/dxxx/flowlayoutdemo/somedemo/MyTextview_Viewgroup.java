package com.dxxx.flowlayoutdemo.somedemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
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

public class MyTextview_Viewgroup extends ViewGroup {
    public MyTextview_Viewgroup(Context context) {
        super(context);
    }

    public MyTextview_Viewgroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAtters(context, attrs);
    }

    public MyTextview_Viewgroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAtters(context, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyTextview_Viewgroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAtters(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int startLeft = getPaddingLeft();
        int startTop = getPaddingTop();
        int startRight = getPaddingLeft();
        int startBottom = getPaddingTop();
        for (int i = 0; i < childs.size(); i++) {
            View child = childs.get(i);
            startRight = startRight + child.getMeasuredWidth();
            startBottom = startBottom + child.getMeasuredHeight();
            //left top right bottom
            child.layout(startLeft, startTop, startRight, startBottom);
            startTop = startTop + child.getMeasuredHeight();

        }
    }

    String text = "需要显示的文字";

    private void initAtters(Context context, @Nullable AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyTextview);
        text = array.getString(R.styleable.MyTextview_texcontent);

    }

    private void initView(int widthSize) {
        //因为onmeasure会执行多次
        this.removeAllViews();
        //首先还是判断能不能一行放下
        Paint paint = null;
        Rect rect = null;
        if (paint == null) {
            paint = new Paint();
            paint.setTextSize(SizeUtil.sp2px(getContext(), 20.0f));
            paint.setColor(Color.parseColor("#000000"));
            paint.setAntiAlias(true);
            paint.setFilterBitmap(true);//位图过滤
            ;
        }
        if (rect == null) {
            rect = new Rect();
        }
        paint.getTextBounds(text, 0, text.length(), rect);
        //
        if (widthSize - getPaddingLeft() - getPaddingRight() >= rect.width()) {
            //一行容纳
            MyTextview textView = new MyTextview(getContext());
            textView.setPadding(0, 0, 0, 0);
            ViewGroup.LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(layoutParams);
            textView.setText(text);
            this.addView(textView);
            return;
        }

        List<String> rowStrings = new ArrayList<>();
        double rows = text.length() / 7.0;
        int lineNum = (int) Math.ceil(rows);
        //一行文字所包含的文字数
        //0开始，依次加然后判断
        for (int i = 0; i < lineNum; i++) {
            String rowText = "";
            if (i == lineNum - 1) {
                rowStrings.add(text.substring(i * 7, text.length()));
                rowText = text.substring(i * 7, text.length());
            } else {
                rowStrings.add(text.substring(i * 7, (i + 1) * 7));
                rowText = text.substring(i * 7, (i + 1) * 7);
            }
            MyTextview textView = new MyTextview(getContext());
            textView.setPadding(0, 0, 0, 0);
            ViewGroup.LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            textView.setLayoutParams(layoutParams);
            textView.setText(rowText);
            this.addView(textView);
        }

    }

    List<View> childs;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        childs = new ArrayList<>();
        //       Log.e("MyViewgroup+onMeasure", "现在有多少个子view" + getChildCount());
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);   //获取宽的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec); //获取高的模式
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);   //获取宽的尺寸
        int heightSize = MeasureSpec.getSize(heightMeasureSpec); //获取高的尺寸
        initView(widthSize);
        //测量子view的宽高，然后得到整个的宽高
        int width = 0;
        int height = 0;

        int hadChildCount = getChildCount();
        for (int i = 0; i < hadChildCount; i++) {
            View child = getChildAt(i);
            childs.add(child);
            //测量下子view的大小

            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            int childMeasuredHeight = child.getMeasuredHeight();
            int childMeasuredWidth = child.getMeasuredWidth();
            // Log.e("当前child的宽和高是", childMeasuredWidth + "-" + childMeasuredHeight);
            if (childMeasuredWidth > width) {
                width = childMeasuredWidth + getPaddingLeft() + getPaddingRight();
            }
            height = height + childMeasuredHeight;
        }
        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {

        }
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        }
        //保存测量宽度和测量高度
        setMeasuredDimension(width, height + getPaddingBottom() + getPaddingTop());
        Log.e("计算出来viewgroup的高度是", height + getPaddingTop() + getPaddingBottom() + "");
    }


}

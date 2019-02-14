package com.dxxx.flowlayoutdemo.somedemo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.text.StaticLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.dxxx.flowlayoutdemo.R;
import com.dxxx.flowlayoutdemo.SizeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

//伪换行的textview
public class MyTextview extends View {

    public void setText(String text) {
        this.text = text;
        initView();
        invalidate();
    }

    public MyTextview(Context context) {
        super(context);
        initView();
    }

    public MyTextview(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAtters(context, attrs);
        initView();

    }

    public MyTextview(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAtters(context, attrs);
        initView();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MyTextview(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAtters(context, attrs);
        initView();

    }

    private void initAtters(Context context, @Nullable AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.MyTextview);
        text = array.getString(R.styleable.MyTextview_texcontent);

    }

    //画笔
    Paint paint;
    //文字所占的区域，用来确定文字的起始坐标
    Rect rect;
    String text = "需要显示的文字";

    private void initView() {
        Log.e("init里面放的数字是", text);
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
    }

    String TAG = "MyTextview";

    @Override
    protected void onDraw(Canvas canvas) {
        //super.onDraw(canvas);
        Log.e("view触发onDraw","====");
        //为了让文字纵向居中，计算正确的Y值
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        //ascent的绝对值=descent+2*(文字纵向中线与基准线的高度差)
        //float baseline = 0;
        //Math.abs(fontMetrics.ascent) =fontMetrics.descent+2*(baseline-height/2);

        if (lineNum > 1) {
            //绘制文字
            int startY = getPaddingTop();
            for (int i = 0; i < rowStrings.size(); i++) {
                Rect rect_T = new Rect();
                //得到写这些文字需要占据的方形区域
                paint.getTextBounds(rowStrings.get(i), 0, rowStrings.get(i).length(), rect_T);
                int height = rect_T.height();
                float y = height / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;
                canvas.drawText(rowStrings.get(i), 0 + getPaddingLeft(), startY + y, paint);
                startY = startY + height;
            }

        } else {
            float y = rect.height() / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;
            canvas.drawText(text, 0 + getPaddingLeft(), y + getPaddingTop(), paint);
        }


    }

    List<String> rowStrings;
    int lineNum;

    //模拟一行可以放下就不换行，如果大于1行每行只容纳五个字符(只是模拟，实际操作还需要判断当前位置一行还能不能放下5个字符)
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //得到写这些文字需要占据的方形区域
        Log.e("view触发onMeasure","====");
        rowStrings = new ArrayList<>();
        lineNum = 0;
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);   //获取宽的模式
        int heightMode = MeasureSpec.getMode(heightMeasureSpec); //获取高的模式
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);   //获取宽的尺寸
        int heightSize = MeasureSpec.getSize(heightMeasureSpec); //获取高的尺寸
        //判断，如果文字的长度大于宽度就换行
        //因为默认写wrap时候相当于match，所以上面测量的width其实就是match也就是屏幕的宽度
        int canUseWidth = widthSize - getPaddingLeft() - getPaddingRight();
        if (rect.width() > canUseWidth) {
//表示一行放不下
            //一行只容纳7个字符
            double rows = text.length() / 7.0;
            lineNum = (int) Math.ceil(rows);
            //一行文字所包含的文字数
            for (int i = 0; i < lineNum; i++) {
                if (i == lineNum - 1) {
                    rowStrings.add(text.substring(i * 7, text.length()));
                } else {
                    rowStrings.add(text.substring(i * 7, (i + 1) * 7));
                }

            }
        } else {
            lineNum = 1;
        }


        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            //如果match_parent或者具体的值，直接赋值
            width = widthSize;
        } else {
            //如果是wrap_content，我们要得到控件需要多大的尺寸
            float textWidth = rect.width();   //文本的宽度
            //控件的宽度就是文本的宽度加上两边的内边距。内边距就是padding值，在构造方法执行完就被赋值
            //根据行数来计算宽高
            if (lineNum > 1) {
                //计算分割的文字里面的最大宽度
                width = 0;
                for (String s : rowStrings) {
                    Rect rect_T = new Rect();
                    //得到写这些文字需要占据的方形区域
                    paint.getTextBounds(s, 0, s.length(), rect_T);
                    int width_T = rect_T.width();
                    if (width_T > width) {
                        width = width_T;
                    }
                }
                width = (int) (getPaddingLeft() + width + getPaddingRight());

            } else {
                width = (int) (getPaddingLeft() + textWidth + getPaddingRight());
            }


        }

        //高度跟宽度处理方式一样
        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            float textHeight = rect.height();
            if (lineNum > 1) {
                //计算分割的文字里面的最大宽度
                height = 0;
                for (String s : rowStrings) {
                    Rect rect_T = new Rect();
                    //得到写这些文字需要占据的方形区域
                    paint.getTextBounds(s, 0, s.length(), rect_T);
                    int height_T = rect_T.height();
                    height = height + height_T;
                }
                height = (int) (getPaddingTop() + height + getPaddingBottom());
            } else {
                height = (int) (getPaddingTop() + textHeight + getPaddingBottom());
            }
            Log.e("计算出来textview高度", height + "");
        }
        //保存测量宽度和测量高度
        setMeasuredDimension(width, height);

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Log.e("view触发onLayout","====");
        super.onLayout(changed, left, top, right, bottom);
    }
}

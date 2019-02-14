package com.dxxx.flowlayoutdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

public class Text2BitmapUtil {
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Bitmap drawRectf(Context context, String text) {
        String inputTxt = "输入的文字";
        inputTxt = text;
        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#000000"));
        paint.setAntiAlias(true);
        ;
        paint.setTextSize(SizeUtil.sp2px(context, 17));
        Rect rect = new Rect();
        paint.getTextBounds(inputTxt, 0, inputTxt.length(), rect);
        int width = SizeUtil.dip2px(context, 150);
        int height = SizeUtil.dip2px(context, 100);
        width = rect.width();
        height = rect.height();
        //因为有边框的5个px，宽高需要加上
        width = width + 10;
        height = height + 10;
        Log.e("计算出来的宽度是", width + "");
        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint1 = new Paint();
        paint1.setColor(Color.parseColor("#ff0000"));
        paint1.setStrokeWidth(5);
        paint1.setAntiAlias(true);//抗锯齿
        paint1.setFilterBitmap(true);//位图过滤
        paint1.setStyle(Paint.Style.STROKE);
        //这里是起点和宽高，减去2.5是因为设置了边框宽5，如果从0，0开始则会出现圆角地方比四周边框粗，因为他实际是从-2.5开始画的，就导致看不到呢一部分
        canvas.drawRoundRect(2.5f, 2.5f, width - 2.5f, height - 2.5f, 10,10,paint1);
        //绘制非圆角的
       //canvas.drawRect(2.5f, 2.5f, width - 2.5f, height - 2.5f, paint1);
        //文字左下角的坐标，这里会发现某些文字会出现紧贴这底部边框，出现的原因是下面的参数xy的y表示的是横向的基准线，他只是基准线并不是底线。类似于英语书写的四线格
        //canvas.drawText(inputTxt, 5f, height - 5f, paint);

        //为了让文字纵向居中，计算正确的Y值
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        //ascent的绝对值=descent+2*(文字纵向中线与基准线的高度差)
        //float baseline = 0;
       //Math.abs(fontMetrics.ascent) =fontMetrics.descent+2*(baseline-height/2);
        float y = height / 2 + (Math.abs(fontMetrics.ascent) - fontMetrics.descent) / 2;
        canvas.drawText(text, 5f, y, paint);
        return bitmap;
    }
}

package com.way2.countdown_widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class DrawBitmapUtil {

    private static DecimalFormat myDecimalFormat = new DecimalFormat("0", DecimalFormatSymbols.getInstance());

    public static Bitmap getWidgetBitmap(Context context, float percentage, float daysLeft, String countdownEventString, int textColor, int progressColor, int backColor) {
        int width = 400;
        int height = 400;
        int stroke = 50;
        int backStroke = 20;
        int padding = 0;
        float density = context.getResources().getDisplayMetrics().density;

        //Paint for arc stroke.
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(stroke);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        Paint backPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        backPaint.setStrokeWidth(backStroke);
        backPaint.setStyle(Paint.Style.STROKE);
        backPaint.setStrokeCap(Paint.Cap.ROUND);

        //Paint for text values.
        Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize((int) (context.getResources().getDimension(R.dimen.widget_text_large_value) / density));
        mTextPaint.setColor(textColor);
        mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(context.getResources().getDimension(R.dimen.widget_text_large_title) / density);
        textPaint.setColor(textColor);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        textPaint.setTextAlign(Paint.Align.CENTER);

        final RectF arc = new RectF();
        arc.set((stroke/2) + padding, (stroke/2) + padding, width-padding-(stroke/2), height-padding-(stroke/2));

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //First draw full arc as background.
        backPaint.setColor(backColor);
        canvas.drawArc(arc, 160, 220, false, backPaint);
        //Then draw arc progress with actual value.
        paint.setColor(progressColor);
        float progressSweep = percentage * 220;
        canvas.drawArc(arc, 160, progressSweep, false, paint);
        //Draw text value.
        canvas.drawText(myDecimalFormat.format(daysLeft), bitmap.getWidth() / 2, (bitmap.getHeight() - mTextPaint.ascent() - stroke) / 2, mTextPaint);
        //Draw widget title.
        canvas.drawText(TextUtils.ellipsize((CharSequence) countdownEventString,textPaint, (float) width, TextUtils.TruncateAt.END).toString(), bitmap.getWidth() / 2, bitmap.getHeight() - backStroke, textPaint);
        return  bitmap;
    }

    public static Bitmap getWidgetPreviewBitmap(String countdownEventString, int textColor, int progressColor, int backColor) {
        Log.i("TEST_LOG", "getWidgetPreviewBitmap: [" + countdownEventString + "] - [" + textColor + "] - [" + progressColor + "] - [" + backColor + "]");
        int width = 400;
        int height = 400;
        int stroke = 50;
        int backStroke = 20;
        int padding = 0;

        //Paint for arc stroke.
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(stroke);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);

        Paint backPaint = new Paint(Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG | Paint.ANTI_ALIAS_FLAG);
        backPaint.setStrokeWidth(backStroke);
        backPaint.setStyle(Paint.Style.STROKE);
        backPaint.setStrokeCap(Paint.Cap.ROUND);

        //Paint for text values.
        Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(120);
        mTextPaint.setColor(textColor);
        mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        mTextPaint.setTextAlign(Paint.Align.CENTER);

        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(80);
        textPaint.setColor(textColor);
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        textPaint.setTextAlign(Paint.Align.CENTER);

        final RectF arc = new RectF();
        arc.set((stroke/2) + padding, (stroke/2) + padding, width-padding-(stroke/2), height-padding-(stroke/2));

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        //First draw full arc as background.
        backPaint.setColor(backColor);
        canvas.drawArc(arc, 160, 220, false, backPaint);
        //Then draw arc progress with actual value.
        paint.setColor(progressColor);
        float progressSweep = .3f * 220;
        canvas.drawArc(arc, 160, progressSweep, false, paint);
        //Draw text value.
        canvas.drawText(myDecimalFormat.format(10), bitmap.getWidth() / 2, (bitmap.getHeight() - mTextPaint.ascent() - stroke) / 2, mTextPaint);
        //Draw widget title.
        canvas.drawText(TextUtils.ellipsize((CharSequence) countdownEventString,textPaint, (float) width, TextUtils.TruncateAt.END).toString(), bitmap.getWidth() / 2, bitmap.getHeight() - backStroke, textPaint);
        return  bitmap;
    }

}

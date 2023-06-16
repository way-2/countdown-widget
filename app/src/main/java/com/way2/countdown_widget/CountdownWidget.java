package com.way2.countdown_widget;

import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREFS_NAME;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_BACK_COLOR_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_DATE_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_PROGRESS_COLOR_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_START_DATE_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_TEXT_COLOR_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_TEXT_KEY;
import static java.time.temporal.ChronoUnit.DAYS;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link CountdownWidgetConfigureActivity CountdownWidgetConfigureActivity}
 */
public class CountdownWidget extends AppWidgetProvider {

    public static DateTimeFormatter myDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static DecimalFormat myDecimalFormat = new DecimalFormat("0", DecimalFormatSymbols.getInstance());

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String countdownDateString = prefs.getString(PREF_PREFIX_DATE_KEY + appWidgetId, LocalDate.now().format(myDateFormatter));
        String startedDateString = prefs.getString(PREF_PREFIX_START_DATE_KEY + appWidgetId, LocalDate.now().format(myDateFormatter));
        String countdownEventString = prefs.getString(PREF_PREFIX_TEXT_KEY + appWidgetId, "Example");
        int textColor = prefs.getInt(PREF_PREFIX_TEXT_COLOR_KEY + appWidgetId, Color.BLACK);
        int progressColor = prefs.getInt(PREF_PREFIX_PROGRESS_COLOR_KEY + appWidgetId, Color.BLACK);
        int backgroundColor = prefs.getInt(PREF_PREFIX_BACK_COLOR_KEY + appWidgetId, Color.BLACK);
        if (countdownEventString.length() > 12) {
            countdownEventString = countdownEventString.substring(0,9) + "...";
        }
        LocalDate countdownDate = null;
        LocalDate startedDate = null;
        try {
            countdownDate = LocalDate.parse(countdownDateString, myDateFormatter);
            startedDate = LocalDate.parse(startedDateString, myDateFormatter);
        } catch (DateTimeParseException dateTimeParseException) {
            Log.w("ERROR", "updateAppWidget: Cannot parse dates using current date");
            countdownDate = LocalDate.now();
            startedDate = LocalDate.now();
        }
        float totalDays = DAYS.between(startedDate, countdownDate);
        float daysLeft = DAYS.between(LocalDate.now(), countdownDate);
        float percent = 0;
        if (daysLeft > 0) {
            percent = (totalDays - daysLeft) / totalDays;
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.countdown_widget);
        views.setImageViewBitmap(R.id.progress_bar_image_view, getWidgetBitmap(context, percent, daysLeft, countdownEventString, textColor, progressColor, backgroundColor));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    static Bitmap getWidgetBitmap(Context context, float percentage, float daysLeft, String countdownEventString, int textColor, int progressColor, int backColor) {

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
        mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        mTextPaint.setTextSize((int) (context.getResources().getDimension(R.dimen.widget_text_large_title) / density));
        canvas.drawText(countdownEventString, bitmap.getWidth() / 2, bitmap.getHeight() - backStroke, mTextPaint);
        return  bitmap;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        for (int appWidgetId : appWidgetIds) {
            CountdownWidgetConfigureActivity.deletePrefs(context, appWidgetId);
            WorkManagerService.getInstance().deleteWork(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

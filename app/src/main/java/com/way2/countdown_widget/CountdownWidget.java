package com.way2.countdown_widget;

import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREFS_NAME;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_BACK_COLOR_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_DATE_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_PROGRESS_COLOR_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_START_DATE_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_TEXT_COLOR_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_TEXT_KEY;
import static com.way2.countdown_widget.DrawBitmapUtil.getWidgetBitmap;
import static java.time.temporal.ChronoUnit.DAYS;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.work.Data;

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

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String countdownDateString = prefs.getString(PREF_PREFIX_DATE_KEY + appWidgetId, LocalDate.now().format(myDateFormatter));
        String startedDateString = prefs.getString(PREF_PREFIX_START_DATE_KEY + appWidgetId, LocalDate.now().format(myDateFormatter));
        String countdownEventString = prefs.getString(PREF_PREFIX_TEXT_KEY + appWidgetId, "Example");
        int textColor = prefs.getInt(PREF_PREFIX_TEXT_COLOR_KEY + appWidgetId, Color.BLACK);
        int progressColor = prefs.getInt(PREF_PREFIX_PROGRESS_COLOR_KEY + appWidgetId, Color.BLACK);
        int backgroundColor = prefs.getInt(PREF_PREFIX_BACK_COLOR_KEY + appWidgetId, Color.BLACK);
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
        float percent = 1;
        if (daysLeft > 0) {
            percent = (totalDays - daysLeft) / totalDays;
        }

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.countdown_widget);
        views.setViewPadding(R.id.parent, 0,0,0,0);
        views.setViewPadding(R.id.progress_bar_image_view, 0,0,0,0);
        views.setImageViewBitmap(R.id.progress_bar_image_view, getWidgetBitmap(context, percent, daysLeft, countdownEventString, textColor, progressColor, backgroundColor));
        views.setOnClickPendingIntent(R.id.progress_bar_image_view, getPendingSelfIntent(context, appWidgetId));

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    protected static PendingIntent getPendingSelfIntent(Context context, int id) {
        ComponentName componentName = new ComponentName("com.way2.countdown_widget", "com.way2.countdown_widget.WidgetExpandedView");
        Intent intent = new Intent(context, CountdownWidget.class);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(componentName);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
        return PendingIntent.getActivity(context, id, intent, PendingIntent.FLAG_IMMUTABLE);
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

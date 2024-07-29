package com.way2.countdown_widget;

import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREFS_NAME;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_BACK_COLOR_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_DATE_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_PROGRESS_COLOR_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_START_DATE_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_TEXT_COLOR_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_TEXT_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_WEEKEND_TOGGLE_KEY;
import static com.way2.countdown_widget.DrawBitmapUtil.getWidgetBitmap;
import static java.time.temporal.ChronoUnit.DAYS;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.widget.RemoteViews;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

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
        int textColor = prefs.getInt(PREF_PREFIX_TEXT_COLOR_KEY + appWidgetId, Color.rgb(255,255,255));
        int progressColor = prefs.getInt(PREF_PREFIX_PROGRESS_COLOR_KEY + appWidgetId, Color.rgb(66, 135, 245));
        int backgroundColor = prefs.getInt(PREF_PREFIX_BACK_COLOR_KEY + appWidgetId, Color.rgb(150,150,150));
        boolean includeWeekends = prefs.getBoolean(PREF_PREFIX_WEEKEND_TOGGLE_KEY + appWidgetId, true);

        Utils.DaysLeftCalculations daysLeftCalculations = Utils.calculatePercentLeft(countdownDateString, startedDateString, includeWeekends);

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.countdown_widget);
        views.setViewPadding(R.id.parent, 0,0,0,0);
        views.setViewPadding(R.id.progress_bar_image_view, 0,0,0,0);
        views.setImageViewBitmap(R.id.progress_bar_image_view, getWidgetBitmap(context, daysLeftCalculations.getPercent(), daysLeftCalculations.getDaysLeft(), countdownEventString, textColor, progressColor, backgroundColor));
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

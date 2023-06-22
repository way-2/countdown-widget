package com.way2.countdown_widget;

import static com.way2.countdown_widget.CountdownWidget.myDateFormatter;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREFS_NAME;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_BACK_COLOR_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_DATE_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_PROGRESS_COLOR_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_START_DATE_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_TEXT_COLOR_KEY;
import static com.way2.countdown_widget.CountdownWidgetConfigureActivity.PREF_PREFIX_TEXT_KEY;
import static java.time.temporal.ChronoUnit.DAYS;

import androidx.appcompat.app.AppCompatActivity;

import android.appwidget.AppWidgetManager;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class WidgetExpandedView extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_expanded_view);
        int id = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFS_NAME, 0);
        String countdownDateString = prefs.getString(PREF_PREFIX_DATE_KEY + id, LocalDate.now().format(myDateFormatter));
        String startedDateString = prefs.getString(PREF_PREFIX_START_DATE_KEY + id, LocalDate.now().format(myDateFormatter));
        String countdownEventString = prefs.getString(PREF_PREFIX_TEXT_KEY + id, "Example");
        int textColor = prefs.getInt(PREF_PREFIX_TEXT_COLOR_KEY + id, Color.BLACK);
        int progressColor = prefs.getInt(PREF_PREFIX_PROGRESS_COLOR_KEY + id, Color.BLACK);
        int backgroundColor = prefs.getInt(PREF_PREFIX_BACK_COLOR_KEY + id, Color.BLACK);
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
        imageView = findViewById(R.id.large_image_view);
        imageView.setImageBitmap(DrawBitmapUtil.getExpandedWidgetBitmap(this, percent, daysLeft, countdownEventString, textColor, progressColor, backgroundColor));
    }

}
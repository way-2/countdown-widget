package com.way2.countdown_widget;

import static android.app.PendingIntent.getActivity;
import static com.way2.countdown_widget.CountdownWidget.myDateFormatter;
import static com.way2.countdown_widget.DrawBitmapUtil.getWidgetBitmap;

import android.app.DatePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.SwitchCompat;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.way2.countdown_widget.databinding.ColorPickerButtonBinding;
import com.way2.countdown_widget.databinding.CountdownWidgetConfigureBinding;
import com.way2.countdown_widget.databinding.DatePickerButtonBinding;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The configuration screen for the {@link CountdownWidget CountdownWidget} AppWidget.
 */
public class CountdownWidgetConfigureActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "com.way2.countdown_widget.CountdownWidget";
    public static final String PREF_PREFIX_TEXT_KEY = "appwidget_text_";
    public static final String PREF_PREFIX_DATE_KEY = "appwidget_date_";
    public static final String PREF_PREFIX_START_DATE_KEY = "appwidget_start_date_";
    public static final String PREF_PREFIX_TEXT_COLOR_KEY = "appwidget_text_color_";
    public static final String PREF_PREFIX_PROGRESS_COLOR_KEY = "appwidget_progress_color_";
    public static final String PREF_PREFIX_BACK_COLOR_KEY = "appwidget_back_color_";
    public static final String PREF_PREFIX_WEEKEND_TOGGLE_KEY = "appwidget_weekend_toggle_";
    public static final String BACKGROUND_COLOR = "BACKGROUND_COLOR";
    public static final String PROGRESS_COLOR = "PROGRESS_COLOR";
    public static final String TEXT_COLOR = "TEXT_COLOR";
    private static Map<String, Integer> colorMap = new HashMap<>();

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetText;
    TextView mAppWidgetDate;
    TextView textColorTextView;
    ImageView textColorColorView;
    TextView progressColorTextView;
    ImageView progressColorColorView;
    TextView backgroundColorTextView;
    ImageView backgroundColorColorView;
    ColorPickerButtonBinding textColorPickerButtonBinding;
    ColorPickerButtonBinding backgroundColorPickerButtonBinding;
    ColorPickerButtonBinding progressColorPickerButtonBinding;
    DatePickerButtonBinding datePickerButtonBinding;
    AppCompatImageView previewImageView;
    Button licenseButton;
    SwitchCompat toggleWeekendSwitch;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = CountdownWidgetConfigureActivity.this;

            // When the button is clicked, store the string locally
            String widgetText = mAppWidgetText.getText().toString();
            String widgetDate = mAppWidgetDate.getText().toString().replace(String.format("%s: ",getString(R.string.event_date)),"");
            saveTitlePref(context, mAppWidgetId, widgetText);
            saveDatePerf(context, mAppWidgetId, widgetDate);
            saveTextColorPerf(context, mAppWidgetId, colorMap.get(TEXT_COLOR));
            saveBackColorPerf(context, mAppWidgetId, colorMap.get(BACKGROUND_COLOR));
            saveProgressColorPerf(context, mAppWidgetId, colorMap.get(PROGRESS_COLOR));
            saveStartDatePerf(context, mAppWidgetId, LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            saveIncludeWeekendPerf(context, mAppWidgetId, toggleWeekendSwitch.isChecked());

            // It is the responsibility of the configuration activity to update the app widget
            WorkManagerService.getInstance().startWork(context, mAppWidgetId);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            CountdownWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    private void saveIncludeWeekendPerf(Context context, int appWidgetId, boolean includeWeekends) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putBoolean(PREF_PREFIX_WEEKEND_TOGGLE_KEY + appWidgetId, includeWeekends);
        prefs.apply();
    }

    private void saveProgressColorPerf(Context context, int appWidgetId, int widgetProgressColor) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_PROGRESS_COLOR_KEY + appWidgetId, widgetProgressColor);
        prefs.apply();
    }

    private void saveBackColorPerf(Context context, int appWidgetId, int widgetBackColor) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_BACK_COLOR_KEY + appWidgetId, widgetBackColor);
        prefs.apply();
    }

    private void saveTextColorPerf(Context context, int appWidgetId, int widgetTextColor) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(PREF_PREFIX_TEXT_COLOR_KEY + appWidgetId, widgetTextColor);
        prefs.apply();
    }

    private void saveStartDatePerf(Context context, int appWidgetId, String widgetDate) {
        SharedPreferences prefReader = context.getSharedPreferences(PREFS_NAME, 0);
        if (prefReader.getString(PREF_PREFIX_START_DATE_KEY + appWidgetId, "none").equals("none")) {
            SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
            prefs.putString(PREF_PREFIX_START_DATE_KEY + appWidgetId, widgetDate);
            prefs.apply();
        }
    }

    View.OnClickListener mDateOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = CountdownWidgetConfigureActivity.this;
            final Calendar c = Calendar.getInstance();
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
            String existingDateString = prefs.getString(PREF_PREFIX_DATE_KEY + mAppWidgetId, myDateFormatter.format(LocalDate.now()));
            LocalDate existingLocalDate = LocalDate.parse(existingDateString, myDateFormatter);
            c.set(existingLocalDate.getYear(), existingLocalDate.getMonthValue(), existingLocalDate.getDayOfMonth());
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) - 1;
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    context,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            String month = String.format(Locale.getDefault(),"%02d",monthOfYear +1);
                            String day = String.format(Locale.getDefault(),"%02d",dayOfMonth);
                            mAppWidgetDate.setText(String.format("%s: %s-%s-%s", getString(R.string.event_date), year, month, day));
                        }
                    }, year, month, day
            );
            datePickerDialog.show();
        }
    };

    View.OnClickListener textColorClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            new ColorPickerDialog.Builder(CountdownWidgetConfigureActivity.this)
                    .setPositiveButton("Select",
                            new ColorEnvelopeListener() {
                                @Override
                                public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                    colorMap.put(TEXT_COLOR,envelope.getColor());
                                    textColorColorView.setColorFilter(envelope.getColor());
                                    reloadPreview();
                                }
                            })
                    .show();
        }
    };

    View.OnClickListener arcColorClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            new ColorPickerDialog.Builder(CountdownWidgetConfigureActivity.this)
                    .setPositiveButton("Select",
                            new ColorEnvelopeListener() {
                                @Override
                                public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                    colorMap.put(BACKGROUND_COLOR,envelope.getColor());
                                    backgroundColorColorView.setColorFilter(envelope.getColor());
                                    reloadPreview();
                                }
                            })
                    .show();
        }
    };

    View.OnClickListener progressColorClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            new ColorPickerDialog.Builder(CountdownWidgetConfigureActivity.this)
                    .setPositiveButton("Select",
                            new ColorEnvelopeListener() {
                                @Override
                                public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                    colorMap.put(PROGRESS_COLOR,envelope.getColor());
                                    progressColorColorView.setColorFilter(envelope.getColor());
                                    reloadPreview();
                                }
                            })
                    .show();
        }
    };

    View.OnFocusChangeListener textOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (!hasFocus) {
                reloadPreview();
            }
        }
    };

    private void saveDatePerf(Context context, int appWidgetId, String widgetDate) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_DATE_KEY + appWidgetId, widgetDate);
        prefs.apply();
    }

    private CountdownWidgetConfigureBinding binding;

    public CountdownWidgetConfigureActivity() {
        super();
    }

    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_PREFIX_TEXT_KEY + appWidgetId, text);
        prefs.apply();
    }

    static void deletePrefs(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_PREFIX_TEXT_KEY + appWidgetId);
        prefs.remove(PREF_PREFIX_DATE_KEY + appWidgetId);
        prefs.remove(PREF_PREFIX_BACK_COLOR_KEY + appWidgetId);
        prefs.remove(PREF_PREFIX_PROGRESS_COLOR_KEY + appWidgetId);
        prefs.remove(PREF_PREFIX_TEXT_COLOR_KEY + appWidgetId);
        prefs.remove(PREF_PREFIX_START_DATE_KEY + appWidgetId);
        prefs.apply();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        binding = CountdownWidgetConfigureBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupColorMap();
        setupUiBindings();

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            reloadIfNeeded(mAppWidgetId);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    private void setupUiBindings() {
        mAppWidgetText = binding.appwidgetText;
        mAppWidgetText.setOnFocusChangeListener(textOnFocusChangeListener);
        datePickerButtonBinding = binding.datePickerButton;
        mAppWidgetDate = datePickerButtonBinding.datePickerText;
        datePickerButtonBinding.getRoot().setOnClickListener(mDateOnClickListener);
        textColorPickerButtonBinding = binding.textColorPicker;
        textColorTextView = textColorPickerButtonBinding.colorTextView;
        textColorTextView.setText(R.string.text_color);
        textColorColorView = textColorPickerButtonBinding.textColorDisplay;
        textColorPickerButtonBinding.getRoot().setOnClickListener(textColorClickListener);
        backgroundColorPickerButtonBinding = binding.backgroundColorPicker;
        backgroundColorTextView = backgroundColorPickerButtonBinding.colorTextView;
        backgroundColorTextView.setText(R.string.background_bar_color);
        backgroundColorColorView = backgroundColorPickerButtonBinding.textColorDisplay;
        backgroundColorPickerButtonBinding.getRoot().setOnClickListener(arcColorClickListener);
        progressColorPickerButtonBinding = binding.progressColorPicker;
        progressColorTextView = progressColorPickerButtonBinding.colorTextView;
        progressColorTextView.setText(R.string.progress_bar_color);
        progressColorColorView = progressColorPickerButtonBinding.textColorDisplay;
        progressColorPickerButtonBinding.getRoot().setOnClickListener(progressColorClickListener);
        mAppWidgetDate.setOnClickListener(mDateOnClickListener);
        previewImageView = binding.previewImageView;
        binding.addButton.setOnClickListener(mOnClickListener);
        licenseButton = binding.licenses;
        licenseButton.setOnClickListener(licenseOnClickListener);
        toggleWeekendSwitch = binding.weekendToggle;
        toggleWeekendSwitch.setOnCheckedChangeListener(mToggleOnCheckChangedListener);
        toggleWeekendSwitch.setText(toggleWeekendSwitch.isChecked()?R.string.include_weekends:R.string.exclude_weekends);
        reloadPreview();
    }

    CompoundButton.OnCheckedChangeListener mToggleOnCheckChangedListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
            if (b) {
                toggleWeekendSwitch.setText(R.string.include_weekends);
            } else {
                toggleWeekendSwitch.setText(R.string.exclude_weekends);
            }
        }
    };

    View.OnClickListener licenseOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(CountdownWidgetConfigureActivity.this, OssLicensesMenuActivity.class));
        }
    };


    private static void setupColorMap() {
        if (!colorMap.containsKey(TEXT_COLOR)) {
            colorMap.put(TEXT_COLOR, Color.rgb(255,255,255));
        }
        if (!colorMap.containsKey(BACKGROUND_COLOR)) {
            colorMap.put(BACKGROUND_COLOR, Color.rgb(150,150,150));
        }
        if (!colorMap.containsKey(PROGRESS_COLOR)) {
            colorMap.put(PROGRESS_COLOR, Color.rgb(66, 135, 245));
        }
    }

    private void reloadIfNeeded(int mAppWidgetId) {
        final Context context = CountdownWidgetConfigureActivity.this;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        if (!prefs.getAll().isEmpty()) {
            colorMap.put(TEXT_COLOR, prefs.getInt(PREF_PREFIX_TEXT_COLOR_KEY + mAppWidgetId, Color.rgb(255,255,255)));
            colorMap.put(BACKGROUND_COLOR, prefs.getInt(PREF_PREFIX_BACK_COLOR_KEY + mAppWidgetId, Color.rgb(150,150,150)));
            colorMap.put(PROGRESS_COLOR, prefs.getInt(PREF_PREFIX_PROGRESS_COLOR_KEY + mAppWidgetId, Color.rgb(66, 135, 245)));
            textColorColorView.setColorFilter(colorMap.get(TEXT_COLOR));
            backgroundColorColorView.setColorFilter(colorMap.get(BACKGROUND_COLOR));
            progressColorColorView.setColorFilter(colorMap.get(PROGRESS_COLOR));
            mAppWidgetText.setText(prefs.getString(PREF_PREFIX_TEXT_KEY + mAppWidgetId, "Event Title"));
            mAppWidgetDate.setText(String.format("%s: %s",getString(R.string.event_date),prefs.getString(PREF_PREFIX_DATE_KEY + mAppWidgetId, myDateFormatter.format(LocalDate.now()))));
            toggleWeekendSwitch.setChecked(prefs.getBoolean(PREF_PREFIX_WEEKEND_TOGGLE_KEY + mAppWidgetId, true));
        }
        reloadPreview();
    }

    private void reloadPreview() {
        String countdownEventString = mAppWidgetText.getText().toString();
        int textColor = colorMap.get(TEXT_COLOR);
        int progressColor = colorMap.get(PROGRESS_COLOR);
        int backgroundColor = colorMap.get(BACKGROUND_COLOR);
        Bitmap bitmap = getWidgetBitmap(getApplicationContext(), .4f, 4, countdownEventString, textColor, progressColor, backgroundColor);
        previewImageView.setImageBitmap(bitmap);
    }

}
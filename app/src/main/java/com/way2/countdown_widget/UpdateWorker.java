package com.way2.countdown_widget;

import static com.way2.countdown_widget.WorkManagerService.WIDGET_ID;

import android.appwidget.AppWidgetManager;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class UpdateWorker extends Worker {

    private Context context;
    private WorkerParameters workerParameters;
    public static final String COUNTDOWN_WIDGET_UPDATE_WORKER = "COUNTDOWN_WIDGET_UPDATE_WORKER_";

    public UpdateWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.workerParameters = workerParams;
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int mAppWidgetId = workerParameters.getInputData().getInt(WIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        CountdownWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);
        return Result.success();
    }

}

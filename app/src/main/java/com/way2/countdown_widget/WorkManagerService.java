package com.way2.countdown_widget;

import static com.way2.countdown_widget.UpdateWorker.COUNTDOWN_WIDGET_UPDATE_WORKER;

import android.content.Context;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class WorkManagerService {

    public static final String WIDGET_ID = "WIDGET_ID";
    private static WorkManagerService workManagerServiceInstance;

    public static synchronized WorkManagerService getInstance() {
        if (workManagerServiceInstance == null) {
            workManagerServiceInstance = new WorkManagerService();
        }
        return workManagerServiceInstance;
    }

    public void startWork(Context context, int id) {
        Calendar calendar = Calendar.getInstance();
        long nowMillis = calendar.getTimeInMillis();
        calendar.set(Calendar.HOUR_OF_DAY, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(Calendar.getInstance())) {
            calendar.add(Calendar.DATE, 1);
        }
        long diff = calendar.getTimeInMillis() - nowMillis;
        Data inputData = new Data.Builder().putInt(WIDGET_ID, id).build();
        PeriodicWorkRequest.Builder workBuilder = new PeriodicWorkRequest.Builder(UpdateWorker.class, 24, TimeUnit.HOURS).setInitialDelay(diff, TimeUnit.MILLISECONDS).addTag(COUNTDOWN_WIDGET_UPDATE_WORKER + id).setInputData(inputData);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build();
        PeriodicWorkRequest runWork = workBuilder.setConstraints(constraints).build();
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.enqueueUniquePeriodicWork(COUNTDOWN_WIDGET_UPDATE_WORKER + id, ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, runWork);
    }

    public void deleteWork(Context context, int appWidgetId) {
        WorkManager workManager = WorkManager.getInstance(context);
        workManager.cancelAllWorkByTag(COUNTDOWN_WIDGET_UPDATE_WORKER + appWidgetId);
    }
}

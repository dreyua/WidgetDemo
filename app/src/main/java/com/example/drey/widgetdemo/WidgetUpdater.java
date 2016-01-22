package com.example.drey.widgetdemo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.widget.RemoteViews;

/**
 * Created by drey on 20.01.2016.
 */
public class WidgetUpdater extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        new Thread() {
            @Override
            public void run() {
                AppWidgetManager mngr = AppWidgetManager.getInstance(getApplicationContext());
                int[] appWidgetIds = mngr.getAppWidgetIds(new ComponentName(getApplicationContext(), MyWidgetProvider.class));
                if (appWidgetIds != null && appWidgetIds.length > 0) {
                    int imgRes = getConnStateRes(WidgetUpdater.this);
                    for (int i = 0; i < appWidgetIds.length; i++) {
                        int appWidgetId = appWidgetIds[i];
                        RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.widget);
                        views.setInt(R.id.image, "setImageResource", imgRes);
                        mngr.updateAppWidget(appWidgetId, views);
                    }
                }
            }
        }.start();

        return START_REDELIVER_INTENT;
    }

    int getConnStateRes(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        int res = R.drawable.gray_circle;
        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
                if (Utils.pingGoogle()) {
                    switch (activeNetwork.getType()) {
                        case ConnectivityManager.TYPE_WIFI:
                            res = Utils.isOpenOrPSK(context) ? R.drawable.red_circle : R.drawable.green_circle;
                            break;
                        case ConnectivityManager.TYPE_MOBILE:
                            res = R.drawable.yellow_circle;
                            break;
                        default:
                        case ConnectivityManager.TYPE_ETHERNET:
                            res = R.drawable.white_circle;
                    }
                } else {
                    AlarmManager am = (AlarmManager) context.getApplicationContext().getSystemService(ALARM_SERVICE);
                    Intent i = new Intent(context, WidgetUpdater.class);
                    PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
                    am.set(AlarmManager.RTC, 60*1000, pi);
                    res = R.drawable.empty_circle;
                }
            } else {
                res = R.drawable.empty_circle;
            }
        }
        return res;
    }
}

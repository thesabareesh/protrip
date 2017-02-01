package me.sabareesh.trippie.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import me.sabareesh.trippie.util.Log;
import android.widget.RemoteViews;

import me.sabareesh.trippie.R;


/**
 * Created by Sabareesh on 12-Jan-17.
 */

public class PlacesWidgetProvider extends AppWidgetProvider {
    public static final String TOAST_ACTION = "me.sabareesh.trippie.placeswidget.TOAST_ACTION";
    private static final String TAG = PlacesWidgetProvider.class.getSimpleName();


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int i = 0; i < appWidgetIds.length; i++) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_collection);

            Intent intent = new Intent(context, PlacesWidgetRemoteViewsService.class);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            views.setRemoteAdapter(R.id.widget_list, intent);

            Intent toastIntent = new Intent(context, PlacesWidgetRemoteViewsService.class);
            toastIntent.setAction(PlacesWidgetProvider.TOAST_ACTION);
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
            PendingIntent toastPendingIntent = PendingIntent.getBroadcast(context, 0, toastIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_list, toastPendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], views);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        Log.d(TAG, "onEnabled");
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        Log.d(TAG, "onDisabled");
        super.onDisabled(context);
    }

}

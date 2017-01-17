package me.sabareesh.trippie.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import me.sabareesh.trippie.R;
import me.sabareesh.trippie.provider.PlacesProvider;
import me.sabareesh.trippie.provider.PlacesSQLiteHelper;
import me.sabareesh.trippie.ui.PlaceDetailActivity;

/**
 * Created by VE288800 on 12-Jan-17.
 */

public class PlacesWidgetRemoteViewsService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new PlacesWidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class PlacesWidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private static final String TAG = PlacesWidgetRemoteViewsFactory.class.getSimpleName();
    private static Context mContext;
    final Handler mMainHandler = new Handler(Looper.getMainLooper());
    private Cursor mPlaceCursor;
    private int mQuoteWidgetId;

    public PlacesWidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mQuoteWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    public void onCreate() {
        //Log.d(TAG, "onCreate");
    }

    public void onDestroy() {
        //Log.d(TAG, "onDestroy");

        if (mPlaceCursor != null) {
            mPlaceCursor.close();
        }
    }

    public int getCount() {
        //Log.d(TAG, "getCount");
        if (mPlaceCursor != null) {
            return mPlaceCursor.getCount();
        } else {
            return 0;
        }
    }

    public RemoteViews getViewAt(int position) {

        String place_id = "";
        String place_name = "";
        String place_poster_url = "";
        String place_rating = "";
        String place_address = "";

        if (mPlaceCursor.moveToPosition(position)) {

            place_id = mPlaceCursor.getString(mPlaceCursor.getColumnIndex(PlacesSQLiteHelper.ID));
            place_name = mPlaceCursor.getString(mPlaceCursor.getColumnIndex(PlacesSQLiteHelper.TITLE));
            place_poster_url = mPlaceCursor.getString(mPlaceCursor.getColumnIndex(PlacesSQLiteHelper.POSTERPATH_WIDE));
            place_rating = mPlaceCursor.getString(mPlaceCursor.getColumnIndex(PlacesSQLiteHelper.RATING_AVG));
            place_address = mPlaceCursor.getString(mPlaceCursor.getColumnIndex(PlacesSQLiteHelper.ADDRESS_FULL));

        }

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.widget_collection_item);
        //RemoteViews rootView = new RemoteViews(mContext.getPackageName(), R.layout.widget_collection);
        //Intent intent = new Intent(mContext, PlaceDetailActivity.class);
        //intent.putExtra("place_id", place_id);
        //PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 0, intent, 0);

        //rv.setViewVisibility(R.id.widget_nofavs_badge, View.INVISIBLE);

        rv.setTextViewText(R.id.place_id, place_id);
        rv.setTextViewText(R.id.place_name, place_name);
        rv.setTextViewText(R.id.place_Address, place_address);
        place_rating = (place_rating == null) ? "0" : place_rating;
        rv.setTextViewText(R.id.rating, place_rating);
        //rv.setOnClickPendingIntent(R.id.root_view, pendingIntent);

        try {
            if (!place_poster_url.isEmpty()) {
                Bitmap b = Picasso.with(mContext).load(place_poster_url).get();
                rv.setImageViewBitmap(R.id.place_pic, b);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        //Log.d(TAG, "getLoadingView");
        return null;
    }


    public int getViewTypeCount() {
        //Log.d(TAG, "getViewTypeCount");
        return 1;
    }

    public long getItemId(int position) {
        //   Log.d(TAG, "getItemId");
        return position;
    }

    public boolean hasStableIds() {
        return true;
    }

    public void onDataSetChanged() {
        //Log.d(TAG, "onDataSetChanged");
        if (mPlaceCursor != null) {
            mPlaceCursor.close();
        }
        Uri uri = PlacesProvider.CONTENT_URI;

        final long token = Binder.clearCallingIdentity();
        try {
            mPlaceCursor = mContext.getContentResolver().query(uri, null, null, null, PlacesSQLiteHelper.ROW_ID);
        } finally {
            Binder.restoreCallingIdentity(token);
        }

    }

}
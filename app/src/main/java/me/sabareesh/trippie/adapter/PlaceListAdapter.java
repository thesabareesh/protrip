package me.sabareesh.trippie.adapter;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.sabareesh.trippie.R;
import me.sabareesh.trippie.model.PlaceList;
import me.sabareesh.trippie.ui.PlaceDetailActivity;
import me.sabareesh.trippie.util.Constants;
import me.sabareesh.trippie.util.Utils;

/**
 * Created by ve288800 on 03-Jan-17.
 */

public class PlaceListAdapter extends RecyclerView.Adapter<PlaceListAdapter.ViewHolder> {

    public static final String TAG = "PlaceListAdapter";
    Context mContext;
    private LayoutInflater mInflater;
    List<PlaceList> list;


    public PlaceListAdapter(Context ctx, List<PlaceList> list) {
        this.mContext = ctx;
        this.list = list;
        mInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public PlaceListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.recycle_view_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String place_id = list.get(holder.getAdapterPosition()).getPlace_id();
                String place_name = list.get(holder.getAdapterPosition()).getPlace_name();

                String place_imageURL = (list.get(holder.getAdapterPosition()).getPhoto_reference() != null)
                        ? Constants.PLACE_PHOTO +
                        list.get(holder.getAdapterPosition()).getPhoto_reference().get(0) +
                        "&key=" + Constants.API_VALUE : "";

                Intent intent = new Intent(mContext, PlaceDetailActivity.class);
                ImageView thumbnail = (ImageView) v.findViewById(R.id.place_pic);
                //ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(CityActivity.this, thumbnail, getString(R.string.transition_image));
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight());
                intent.putExtra("place_id", place_id);
                intent.putExtra("place_name", place_name);
                intent.putExtra("image_URL", place_imageURL);
                mContext.startActivity(intent, options.toBundle());
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        PlaceList placeList = list.get(i);
        String url;

        if (placeList.getPhoto_reference() != null) {
            url = Constants.PLACE_THUMBNAIL +
                    placeList.getPhoto_reference().get(0) +
                    "&" + Constants.API_KEY_PARAM + "=" + Constants.API_VALUE;
            Log.d(TAG, "Image url " + url);
        } else {
            url = placeList.getIcon_url();
        }

        viewHolder.place_address.setText(placeList.getPlace_address());
        viewHolder.place_id.setText(placeList.getPlace_id());
        viewHolder.place_name.setText(placeList.getPlace_name());
        if (placeList.getPlace_rating() != null) {
            viewHolder.rating.setRating(Float.parseFloat(String.valueOf(placeList.getPlace_rating())));
        }

        if (url != null && !url.isEmpty()) {
            Picasso.with(mContext).load(url).fit().into(viewHolder.place_pic);
        }
        new Utils().animateView(viewHolder.itemView);
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView place_address, place_name, place_id;
        ImageView place_pic;
        RatingBar rating;

        public ViewHolder(View itemView) {
            super(itemView);

            place_address = (TextView) itemView.findViewById(R.id.place_Address);
            place_name = (TextView) itemView.findViewById(R.id.place_name);
            place_id = (TextView) itemView.findViewById(R.id.place_id);
            place_pic = (ImageView) itemView.findViewById(R.id.place_pic);
            rating = (RatingBar) itemView.findViewById(R.id.rating);

        }
    }


}





package me.sabareesh.trippie.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sabareesh on 08-Jan-17.
 */

public class Extras implements Parcelable {
    public static final Parcelable.Creator<Extras> CREATOR = new Parcelable.Creator<Extras>() {
        @Override
        public Extras createFromParcel(Parcel parcel) {
            return new Extras(parcel);
        }

        @Override
        public Extras[] newArray(int i) {
            return new Extras[i];
        }

    };
    private Review[] reviews;

    public Extras(Review[] reviews) {
        this.reviews = reviews;
    }

    private Extras(Parcel in) {

        reviews = in.createTypedArray(Review.CREATOR);
    }

    public int getReviewsNum() {
        return this.reviews.length;
    }

    public Review getReviewAtIndex(int i) {
        return reviews[i];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeTypedArray(reviews, 0);

    }
}


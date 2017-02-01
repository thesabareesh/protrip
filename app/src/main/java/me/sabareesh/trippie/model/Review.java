package me.sabareesh.trippie.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Sabareesh on 08-Jan-17.
 */

public class Review implements Parcelable {
    public static final Parcelable.Creator<Review> CREATOR = new Parcelable.Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel parcel) {
            return new Review(parcel);
        }

        @Override
        public Review[] newArray(int i) {
            return new Review[i];
        }

    };
    Double rating;
    private String author;
    private String body;
    private String avatar_url;

    public Review(String author, String body, Double rating) {
        this.author = author;
        this.body = body;
        this.rating = rating;

    }

    public Review(String author, String body, Double rating, String avatar_url) {
        this.author = author;
        this.body = body;
        this.rating = rating;
        this.avatar_url = avatar_url;
    }

    private Review(Parcel in) {
        author = in.readString();
        body = in.readString();
        rating = in.readDouble();
        avatar_url = in.readString();
    }

    public String getAuthor() {
        return author;
    }

    public String getBody() {
        return body;
    }

    public String getAvatar_url() {
        return avatar_url;
    }

    public void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(body);
        dest.writeDouble(rating);
        dest.writeString(avatar_url);

    }
}

package me.sabareesh.trippie.model;


/**
 * Created by Sabareesh on 05-Jan-17.
 */

public class PlaceDetail {

    Double place_detail_rating;
    private String place_detail_name, place_detail_id, place_detail_url, place_detail_phone;
    private String place_detail_address, place_detail_icon_url, place_detail_website;

    public PlaceDetail() {

    }

    public PlaceDetail(String place_detail_name, String place_detail_id,
                       String place_detail_url, String place_detail_phone,
                       Double place_detail_rating, String place_detail_address,
                       String place_detail_icon_url) {
        this.place_detail_name = place_detail_name;
        this.place_detail_id = place_detail_id;

        this.place_detail_url = place_detail_url;
        this.place_detail_phone = place_detail_phone;
        this.place_detail_rating = place_detail_rating;
        this.place_detail_address = place_detail_address;
        this.place_detail_icon_url = place_detail_icon_url;
        this.place_detail_website = place_detail_website;
    }

    public String getPlace_detail_website() {
        return place_detail_website;
    }

    public void setPlace_detail_website(String place_detail_website) {
        this.place_detail_website = place_detail_website;
    }

    public String getPlace_detail_name() {
        return place_detail_name;
    }

    public void setPlace_detail_name(String place_detail_name) {
        this.place_detail_name = place_detail_name;
    }

    public String getPlace_detail_id() {
        return place_detail_id;
    }

    public void setPlace_detail_id(String place_detail_id) {
        this.place_detail_id = place_detail_id;
    }

    public String getPlace_detail_url() {
        return place_detail_url;
    }

    public void setPlace_detail_url(String place_detail_url) {
        this.place_detail_url = place_detail_url;
    }

    public String getPlace_detail_phone() {
        return place_detail_phone;
    }

    public void setPlace_detail_phone(String place_detail_phone) {
        this.place_detail_phone = place_detail_phone;
    }

    public Double getPlace_detail_rating() {
        return place_detail_rating;
    }

    public void setPlace_detail_rating(Double place_detail_rating) {
        this.place_detail_rating = place_detail_rating;
    }

    public String getPlace_detail_icon_url() {
        return place_detail_icon_url;
    }

    public void setPlace_detail_icon_url(String place_detail_icon_url) {
        this.place_detail_icon_url = place_detail_icon_url;
    }

    public String getPlace_detail_address() {
        return place_detail_address;
    }

    public void setPlace_detail_address(String place_detail_address) {
        this.place_detail_address = place_detail_address;
    }


}

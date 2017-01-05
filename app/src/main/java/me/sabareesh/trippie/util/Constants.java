package me.sabareesh.trippie.util;

/**
 * Created by ve288800 on 03-Jan-17.
 */

public class Constants {



    //Parameters
    public static final String BASE_URL_PLACES = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
    public static final String BASE_URL_PLACE_DETAILS = "https://maps.googleapis.com/maps/api/place/details/json?";
    public static final String PLACE_THUMBNAIL = "https://maps.googleapis.com/maps/api/place/photo?maxheight=220&photoreference=";
    public static final String PLACE_PHOTO = "https://maps.googleapis.com/maps/api/place/photo?maxheight=800&photoreference=";
    public static final String API_KEY_PARAM = "key";
    public static final String LOCATION_PARAM = "location";
    public static final String TYPE_PARAM = "type";
    public static final String TYPES_PARAM = "types";
    public static final String RADIUS_PARAM = "radius";
    public static final String RANK_BY_PARAM="rankby";
    public static final String PLACE_ID_PARAM="placeid";

    //Values
    //public static final String API_VALUE = "";
    //public static final String API_VALUE = "";
    public static final String API_VALUE = "";
    //public static final String API_VALUE = "";

    public static final String TYPE_VALUE_RESTAURANT = "restaurant";
    public static final String TYPE_VALUE_HOTEL = "lodging";
    public static final String TYPE_VALUE_TOP_SPOTS = "amusement_park|aquarium|art_gallery|casino|church|museum|night_club|park|place_of_worship|shopping_mall|stadium|zoo";
    public static final String TYPE_VALUE_POI = "point_of_interest";
    public static final String RADIUS_VALUE="5000";
    public static final String RANK_BY_VALUE="prominence";

}

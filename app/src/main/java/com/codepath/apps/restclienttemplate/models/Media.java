package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

@Parcel
public class Media {

    // Attributes
    String imagePath;
    String type;

    /**
     * Empty constructor for Parcel
     */
    public Media() {
    }

    /**
     * Create a new Media object from JSONObject returned by Twitter's API
     * @param jsonObject: The JSONObject to convert into a Media object
     * @return Media
     * @throws JSONException: Key not found in object
     */
    public static Media fromJsonObject(JSONObject jsonObject) throws JSONException {
        Media media = new Media();
        media.type = jsonObject.getString("type");
        media.imagePath = jsonObject.getString("media_url_https");
        return media;
    }

}

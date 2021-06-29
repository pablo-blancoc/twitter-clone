package com.codepath.apps.restclienttemplate.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Media {

    // Attributes
    String imagePath;
    String type;

    public static Media fromJsonObject(JSONObject jsonObject) throws JSONException {
        Media media = new Media();
        media.type = jsonObject.getString("type");
        media.imagePath = jsonObject.getString("media_url_https");
        return media;
    }

}

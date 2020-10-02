package fr.aboucorp.variantchess.app.utils;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonExtractor {

    public static <T> T ectractAttributeByName(String json, String attrName) {
        JSONObject mainObject = null;
        try {
            mainObject = new JSONObject(json);
            return (T) mainObject.get(attrName);
        } catch (JSONException e) {
            Log.e("fr.aboucorp.variantchess", "Cannot get value from json");
            return null;
        }
    }
}

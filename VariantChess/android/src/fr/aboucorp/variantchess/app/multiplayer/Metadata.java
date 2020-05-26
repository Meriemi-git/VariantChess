package fr.aboucorp.variantchess.app.multiplayer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Metadata<T> extends HashMap<String, T> {

    public Metadata() {
    }

    public Metadata(Map<String, T> values) {
        this.entrySet().addAll(values.entrySet());
    }

    public void setMetadataFromString(String json) {
        Gson gson = new Gson();
        Type datasetListType = new TypeToken<HashMap<String, T>>() {
        }.getType();
        HashMap<String, T> values = gson.fromJson(json, datasetListType);
        this.entrySet().addAll(values.entrySet());
    }

    public String getJsonFromMetadata() {
        Gson gson = new Gson();
        Type datasetListType = new TypeToken<HashMap<String, T>>() {
        }.getType();
        String json = gson.toJson(this, datasetListType);
        return json;
    }
}

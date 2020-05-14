package fr.aboucorp.variantchess.app.multiplayer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Metadata {
    public Map<String,String> values = new HashMap();

    public Metadata() {}

    public Metadata( Map<String,String> values) {
        this.values = values;
    }

    public static Metadata getMetadataFromString(String json){
        Gson gson = new Gson();
        Type datasetListType = new TypeToken<Map<String,String>>() {}.getType();
        Map<String,String> values = gson.fromJson(json, datasetListType);
        return new Metadata(values);
    }

    public static String getJsonFromMetadata(Metadata metadata){
        Gson gson = new Gson();
        Type datasetListType = new TypeToken<Map<String,String>>() {}.getType();
        String json = gson.toJson(metadata.values, datasetListType);
        return json;
    }
}

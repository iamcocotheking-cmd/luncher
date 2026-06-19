package net.kdt.pojavlaunch.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public final class GsonJsonUtils {
    private GsonJsonUtils() {}

    public static JsonObject getJsonObjectSafe(JsonElement element) {
        if (element == null || element.isJsonNull() || !element.isJsonObject()) return null;
        return element.getAsJsonObject();
    }

    public static JsonObject getJsonObjectSafe(JsonObject object, String key) {
        if (object == null || !object.has(key)) return null;
        return getJsonObjectSafe(object.get(key));
    }

    public static JsonArray getJsonArraySafe(JsonObject object, String key) {
        if (object == null || !object.has(key)) return null;
        JsonElement element = object.get(key);
        if (element == null || element.isJsonNull() || !element.isJsonArray()) return null;
        return element.getAsJsonArray();
    }

    public static String getStringSafe(JsonObject object, String key) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) return null;
        return object.get(key).getAsString();
    }

    public static int getIntSafe(JsonObject object, String key, int defaultValue) {
        if (object == null || !object.has(key) || object.get(key).isJsonNull()) return defaultValue;
        try { return object.get(key).getAsInt(); } catch (Exception e) { return defaultValue; }
    }
}

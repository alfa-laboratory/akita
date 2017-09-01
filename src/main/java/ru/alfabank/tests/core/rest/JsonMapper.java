package ru.alfabank.tests.core.rest;

import com.google.gson.*;

import java.math.BigDecimal;

/**
 * Created by U_M0UKA on 18.01.2017.
 */
@Deprecated
public class JsonMapper {

    private JsonMapper() {
    }

    @Deprecated
    public static String getString(JsonElement element, String fieldName){
        if (!(element.getAsJsonObject().get(fieldName) instanceof JsonNull)) {
            return element.getAsJsonObject().get(fieldName).getAsString();
        } else {return "";}
    }

    @Deprecated
    public static BigDecimal getBigDecimal(JsonElement element, String fieldName){
        return element.getAsJsonObject().get(fieldName).getAsBigDecimal();
    }

    @Deprecated
    public static Boolean getBoolean(JsonElement element, String fieldName){
        return element.getAsJsonObject().get(fieldName).getAsBoolean();
    }

    @Deprecated
    public static int getInt(JsonElement element, String fieldName){
        return element.getAsJsonObject().get(fieldName).getAsInt();
    }

    @Deprecated
    public static JsonObject getJsonObject(JsonElement element, String fieldName){
        return element.getAsJsonObject().get(fieldName).getAsJsonObject();
    }

    @Deprecated
    public static JsonArray getJsonArrayFromString(String initialString, String nameOfArray) {
        Gson gson = new Gson();
        JsonElement element = gson.fromJson(initialString, JsonElement.class);
        JsonObject jsonObj = element.getAsJsonObject();
        return jsonObj.get(nameOfArray).getAsJsonArray();
    }
}

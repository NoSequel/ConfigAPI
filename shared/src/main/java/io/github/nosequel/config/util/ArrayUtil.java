package io.github.nosequel.config.util;

import com.google.gson.*;
import io.github.nosequel.config.adapter.ConfigTypeAdapter;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;

@UtilityClass
public class ArrayUtil {

    public final static JsonParser PARSER = new JsonParser();
    public final static Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING).create();

    /**
     * Convert an {@link Object[]} to a {@link String}
     *
     * @param array       the array to convert into a string
     * @param typeAdapter the type adapter to use to convert the individual objects into a string
     * @return the converted string
     */
    public String convertArrayToString(Object[] array, ConfigTypeAdapter<?> typeAdapter) {
        final JsonArray jsonArray = new JsonArray();

        for (Object object : array) {
            jsonArray.add(PARSER.parse(GSON.toJson(typeAdapter.convertCasted(object))));
        }

        return GSON.toJson(jsonArray);
    }

    /**
     * Extract an {@link T[]} from a {@link String}
     *
     * @param array       the string to extract the array of the type from
     * @param typeAdapter the type adapter to use to convert the string into the object
     * @param <T>         the type of the returned array
     * @return the returned array
     */
    public <T> T[] extractArrayFromString(String array, Class<?> type, ConfigTypeAdapter<T> typeAdapter) {
        final JsonArray jsonArray = GSON.fromJson(array, JsonArray.class);
        final T[] objects = (T[]) Array.newInstance(type, jsonArray.size());

        for (int i = 0; i < objects.length; i++) {
            objects[i] = typeAdapter.convert(jsonArray.get(i).getAsString());
        }

        return objects;
    }
}

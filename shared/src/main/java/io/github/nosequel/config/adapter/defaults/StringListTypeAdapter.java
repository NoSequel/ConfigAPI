package io.github.nosequel.config.adapter.defaults;

import io.github.nosequel.config.adapter.ConfigTypeAdapter;

import java.util.Arrays;
import java.util.List;

public class StringListTypeAdapter implements ConfigTypeAdapter<List<String>> {

    /**
     * Convert a string back to the object
     *
     * @param string the string to convert back
     * @return the converted object
     */
    @Override
    public List<String> convert(String string) {
        return Arrays.asList(string.replace("[", "").replace("]", "").split(","));
    }

    /**
     * Convert an object to a string
     *
     * @param object the object to convert
     * @return the converted string
     */
    @Override
    public String convert(List<String> object) {
        return object.toString();
    }
}

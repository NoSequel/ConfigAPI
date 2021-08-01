package io.github.nosequel.config.adapter.defaults;

import io.github.nosequel.config.adapter.ConfigTypeAdapter;

public class IntegerTypeAdapter implements ConfigTypeAdapter<Integer> {

    /**
     * Convert a string back to the object
     *
     * @param string the string to convert back
     * @return the converted object
     */
    @Override
    public Integer convert(String string) {
        return Integer.parseInt(string);
    }

    /**
     * Convert an object to a string
     *
     * @param object the object to convert
     * @return the converted string
     */
    @Override
    public String convert(Integer object) {
        return object + "";
    }
}

package io.github.nosequel.config.adapter;

public interface ConfigTypeAdapter<T> {

    /**
     * Convert a string back to the object
     *
     * @param string the string to convert back
     * @return the converted object
     */
    T convert(String string);

    /**
     * Convert an object to a string
     *
     * @param object the object to convert
     * @return the converted string
     */
    String convert(T object);

    /**
     * Convert an object to a string
     *
     * @param object the object to convert
     * @return the converted string
     */
    default String convertCasted(Object object) {
        return this.convert((T) object);
    }

}

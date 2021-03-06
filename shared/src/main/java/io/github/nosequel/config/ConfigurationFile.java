package io.github.nosequel.config;

import com.google.gson.JsonElement;

public interface ConfigurationFile {

    void load();
    void save();

    /**
     * Set an object in the configuration
     *
     * @param path   the path to set the value to
     * @param object the value to set it to
     * @param <T>    the type of the value
     */
    <T> void set(String path, T object);

    /**
     * Set an element in the configuration
     *
     * @param path    the path to set the value to
     * @param element the value to set it to
     */
    void set(String path, JsonElement element);

    /**
     * Get the string from a path
     *
     * @param path the path to get the string from
     * @return the string, or null
     */
    String get(String path);

}

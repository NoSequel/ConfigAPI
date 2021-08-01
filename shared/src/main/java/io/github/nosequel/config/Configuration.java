package io.github.nosequel.config;

import com.google.gson.*;
import com.sun.org.apache.xpath.internal.objects.XObject;
import io.github.nosequel.config.adapter.ConfigTypeAdapter;
import io.github.nosequel.config.adapter.defaults.IntegerTypeAdapter;
import io.github.nosequel.config.adapter.defaults.StringListTypeAdapter;
import io.github.nosequel.config.annotation.Configurable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class Configuration {

    private final ConfigurationFile file;

    private final JsonParser parser = new JsonParser();
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING).create();

    private final Map<Class<?>, ConfigTypeAdapter<?>> adapterMap = new HashMap<>();

    /**
     * Constructor to make a new configuration
     * object with a provided file.
     *
     * @param file the file to read the data from
     */
    public Configuration(ConfigurationFile file) {
        this.file = file;
        this.adapterMap.put(List.class, new StringListTypeAdapter());
        this.adapterMap.put(Integer.class, new IntegerTypeAdapter());
    }

    public void load() throws IllegalAccessException {
        file.load();

        for (Field field : this.getClass().getDeclaredFields()) {
            if (!field.isAnnotationPresent(Configurable.class)) {
                continue;
            }

            final Configurable configurable = field.getAnnotation(Configurable.class);
            final String path = configurable.path();
            final Class<?> fieldType = field.getType().isArray() ?
                    field.getType().getComponentType()
                    : field.getType();

            final ConfigTypeAdapter<?> adapter = this.adapterMap.get(fieldType);

            if (this.file.get(path) != null) {
                if (adapter != null) {
                    if (field.getType().isArray()) {
                        field.set(this, field.getType().cast(this.extractArrayFromString(this.file.get(path), adapter).toArray(new Object[0])));
                    } else {
                        field.set(this, adapter.convert(this.file.get(path)));
                    }
                } else {
                    if (field.getType().equals(String.class)) {
                        field.set(this, this.file.get(path));
                    } else {
                        field.set(this, gson.fromJson(this.parser.parse(this.file.get(path)), field.getType()));
                    }
                }
            }
        }
    }

    public void save() throws IllegalAccessException {
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);

            if (!field.isAnnotationPresent(Configurable.class)) {
                continue;
            }

            final Configurable configurable = field.getAnnotation(Configurable.class);
            final String path = configurable.path();
            final Class<?> fieldType = field.getType().isArray() ?
                    field.getType().getComponentType()
                    : field.getType();

            final ConfigTypeAdapter<?> adapter = this.adapterMap.get(fieldType);
            final Object invokingFrom = Modifier.isStatic(field.getModifiers())
                    ? null
                    : this;

            if (adapter == null) {
                if (field.getType().equals(String.class)) {
                    this.file.set(path, field.get(invokingFrom).toString());
                } else {
                    this.file.set(path, this.gson.toJson(field.get(invokingFrom)));
                }

                continue;
            }

            if (field.getType().isArray()) {
                this.file.set(path, this.convertArrayToString((Object[]) field.get(invokingFrom), adapter));
            } else {
                this.file.set(path, adapter.convertCasted(field.get(invokingFrom)));
            }
        }

        file.save();
    }

    /**
     * Convert an {@link Object[]} to a {@link String}
     *
     * @param array       the array to convert into a string
     * @param typeAdapter the type adapter to use to convert the individual objects into a string
     * @return the converted string
     */
    private String convertArrayToString(Object[] array, ConfigTypeAdapter<?> typeAdapter) {
        final JsonArray jsonArray = new JsonArray();

        for (Object object : array) {
            jsonArray.add(this.parser.parse(gson.toJson(typeAdapter.convertCasted(object))));
        }

        return this.gson.toJson(jsonArray);
    }

    /**
     * Extract an {@link T[]} from a {@link String}
     *
     * @param array       the string to extract the array of the type from
     * @param typeAdapter the type adapter to use to convert the string into the object
     * @param <T>         the type of the returned array
     * @return the returned array
     */
    private <T> List<T> extractArrayFromString(String array, ConfigTypeAdapter<T> typeAdapter) {
        final JsonArray jsonArray = this.gson.fromJson(array, JsonArray.class);
        final List<T> objects = new ArrayList<>();

        for (JsonElement jsonElement : jsonArray) {
            objects.add(typeAdapter.convert(jsonElement.getAsString()));
        }

        return objects;
    }

    /**
     * Register a new {@link ConfigTypeAdapter} to the {@link Configuration#adapterMap} map
     *
     * @param clazz       the type of the adapter
     * @param typeAdapter the adapter to register
     * @param <T>         the type of the adapter
     */
    public <T> void registerAdapter(Class<T> clazz, ConfigTypeAdapter<T> typeAdapter) {
        this.adapterMap.put(clazz, typeAdapter);
    }

}
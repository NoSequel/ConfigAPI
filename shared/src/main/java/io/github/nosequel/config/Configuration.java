package io.github.nosequel.config;

import io.github.nosequel.config.adapter.ConfigTypeAdapter;
import io.github.nosequel.config.adapter.defaults.IntegerTypeAdapter;
import io.github.nosequel.config.adapter.defaults.StringListTypeAdapter;
import io.github.nosequel.config.annotation.Configurable;
import io.github.nosequel.config.util.ArrayUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public abstract class Configuration {

    private final ConfigurationFile file;
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
            final Object object;

            if (this.file.get(path) != null) {
                if (adapter != null) {
                    object = field.getType().isArray() ?
                            ArrayUtil.extractArrayFromString(this.file.get(path), fieldType, adapter)
                            : adapter.convert(this.file.get(path));

                    field.set(this, object);
                } else {
                    object = field.getType().equals(String.class)
                            ? this.file.get(path)
                            : ArrayUtil.GSON.fromJson(ArrayUtil.PARSER.parse(this.file.get(path)), field.getType());
                }

                field.set(this, field.getType().cast(object));
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

            final Object object;

            if (adapter == null) {
                object = field.getType().equals(String.class)
                        ? field.get(invokingFrom).toString()
                        : ArrayUtil.GSON.toJson(field.get(invokingFrom));

            } else {
                object = field.getType().isArray() ?
                        ArrayUtil.convertArrayToString((Object[]) field.get(invokingFrom), adapter)
                        : adapter.convertCasted(field.get(invokingFrom));
            }

            this.file.set(path, object);
        }

        file.save();
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
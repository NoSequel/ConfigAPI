package io.github.nosequel.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.LongSerializationPolicy;
import io.github.nosequel.config.adapter.ConfigTypeAdapter;
import io.github.nosequel.config.adapter.defaults.IntegerTypeAdapter;
import io.github.nosequel.config.adapter.defaults.StringListTypeAdapter;
import io.github.nosequel.config.annotation.Configurable;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            final ConfigTypeAdapter<?> adapter = this.adapterMap.get(field.getType());

            if (this.file.get(path) != null) {
                if (adapter != null) {
                    field.set(this, adapter.convert(this.file.get(path)));
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

            final ConfigTypeAdapter<?> adapter = this.adapterMap.get(field.getType());
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

            this.file.set(path, adapter.convertCasted(field.get(invokingFrom)));
        }

        file.save();
    }
}
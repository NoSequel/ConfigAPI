package io.github.nosequel.config.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.LongSerializationPolicy;
import io.github.nosequel.config.ConfigurationFile;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class JsonConfigurationFile implements ConfigurationFile {

    private Map<Object, Object> map = new HashMap<>();

    private final File file;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .setLongSerializationPolicy(LongSerializationPolicy.STRING)
            .create();

    @Override
    @SneakyThrows
    public void load() {
        if (!this.file.exists()) {
            if (this.file.createNewFile()) {
                System.out.println("Created new file with path \"" + this.file.getPath() + "\"");
            }
        }

        final FileReader reader = new FileReader(this.file);
        final Map<?, ?> map = gson.fromJson(reader, Map.class);

        if (map != null) {
            this.map = (Map<Object, Object>) map;
        }
    }

    @Override
    @SneakyThrows
    public void save() {
        if (!this.file.exists()) {
            if (this.file.createNewFile()) {
                System.out.println("Created new file with path \"" + this.file.getPath() + "\"");
            }
        }

        final FileWriter writer = new FileWriter(this.file);

        gson.toJson(map, writer);

        writer.flush();
        writer.close();
    }

    /**
     * Set an object in the configuration
     *
     * @param path   the path to set the value to
     * @param object the value to set it to
     */
    @Override
    public <T> void set(String path, T object) {
        this.map.put(path, object);
    }

    /**
     * Set an element in the configuration
     *
     * @param path    the path to set the value to
     * @param element the value to set it to
     */
    @Override
    public void set(String path, JsonElement element) {
        this.map.put(path, element);
    }

    /**
     * Get the string from a path
     *
     * @param path the path to get the string from
     * @return the string, or null
     */
    @Override
    public String get(String path) {
        final Object object = this.map.get(path);

        if(object == null) {
            return null;
        }

        if (object instanceof String) {
            return (String) object;
        }

        return object.toString();
    }
}

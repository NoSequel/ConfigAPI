package io.github.nosequel.config.bukkit;


import io.github.nosequel.config.ConfigurationFile;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

@RequiredArgsConstructor
public class BukkitConfigurationFile implements ConfigurationFile {

    private final File file;
    private final YamlConfiguration configuration;

    @SneakyThrows
    @Override
    public void load() {
        this.ensureFileExistence();
        this.configuration.load(this.file);
    }

    @SneakyThrows
    @Override
    public void save() {
        this.ensureFileExistence();
        this.configuration.save(this.file);
    }

    @SneakyThrows
    private void ensureFileExistence() {
        if(!this.file.getParentFile().exists() && file.getParentFile().mkdirs()) {
            System.out.println("Created parent files");
        }

        if(!this.file.exists() && file.createNewFile()) {
            System.out.println("Creating new configuration with name \"" + file.getName() + "\"");
        }
    }

    /**
     * Set an object in the configuration
     *
     * @param path   the path to set the value to
     * @param object the value to set it to
     */
    @Override
    public <T> void set(String path, T object) {
        this.configuration.set(path, object);
    }

    /**
     * Get the string from a path
     *
     * @param path the path to get the string from
     * @return the string, or null
     */
    @Override
    public String get(String path) {
        return this.configuration.getString(path);
    }
}
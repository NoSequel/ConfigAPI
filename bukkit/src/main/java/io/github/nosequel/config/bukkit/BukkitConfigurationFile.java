package io.github.nosequel.config.bukkit;

import com.google.gson.JsonElement;
import io.github.nosequel.config.ConfigurationFile;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class BukkitConfigurationFile implements ConfigurationFile {

    private final File file;
    private final YamlConfiguration configuration;

    @Deprecated
    public BukkitConfigurationFile(File file, YamlConfiguration configuration) {
        this.file = file;
        this.configuration = configuration;
    }

    public BukkitConfigurationFile(File file) {
        this.file = file;
        this.ensureFileExistence();
        this.configuration = YamlConfiguration.loadConfiguration(file);
    }

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
        if(this.file.getParentFile() != null && !this.file.getParentFile().exists() && file.getParentFile().mkdirs()) {
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
     * Set an element in the configuration
     *
     * @param path    the path to set the value to
     * @param element the value to set it to
     */
    @Override
    public void set(String path, JsonElement element) {
        this.set(path, element.toString());
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
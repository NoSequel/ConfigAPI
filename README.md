# Config API
This is just a simple configuration API I made a while back because I needed one, code is pretty outdated.

## Current Supported Data Types
We have a config type adapter system - and there are currently 2 default implementations of it, including ``Integer, List<String> and String``

## Usage
### Bukkit

#### Creating a new instance of the configuration:
```java
public class BukkitPlugin extends JavaPlugin {
    
    @Override
    public void onEnable() {
        final File file = new File(this.getDataFolder(), "config.yml");
        
        new BaseConfiguration(file, YamlConfiguration.loadConfiguration(file));
    }
}
```

### The configuration class itself:
```java
public class BaseConfiguration extends Configuration {

    @Configurable(path = "message")
    public static String MESSAGE = "&eHello";

    @SneakyThrows
    public MessageConfiguration(ConfigurationFile file) {
        super(file);
    }
}
```
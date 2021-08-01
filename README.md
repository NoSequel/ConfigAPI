# Config API
This is just a simple configuration API I made a while back because I needed one, code is pretty outdated.

# Usage
## Bukkit

### Creating a new instance of the configuration:
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
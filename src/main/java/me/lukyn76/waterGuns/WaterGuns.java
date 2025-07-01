package me.lukyn76.waterGuns;


import me.lukyn76.waterGuns.commands.RefillCommand;
import me.lukyn76.waterGuns.commands.TabComplete;
import me.lukyn76.waterGuns.commands.WaterGunCommand;
import me.lukyn76.waterGuns.listeners.PlayerEvents;
import me.lukyn76.waterGuns.listeners.ProjectileEvents;
import me.lukyn76.waterGuns.managers.AmmoManager;
import me.lukyn76.waterGuns.managers.BossBarManager;
import me.lukyn76.waterGuns.managers.SprayManager;
import me.lukyn76.waterGuns.utils.ConfigManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class WaterGuns extends JavaPlugin {

    private static WaterGuns instance;
    private ConfigManager configManager;
    private AmmoManager ammoManager;
    private BossBarManager bossBarManager;
    private SprayManager sprayManager;


    @Override
    public void onEnable() {
        instance = this;

        // Initialize managers
        configManager = new ConfigManager(this);
        configManager.loadConfig();

        ammoManager = new AmmoManager(this);
        bossBarManager = new BossBarManager(this);
        sprayManager = new SprayManager();


        // Register commands
        getCommand("watergun").setExecutor(new WaterGunCommand(this));
        getCommand("refill").setExecutor(new RefillCommand(this));
        getCommand("waterguns").setTabCompleter(new TabComplete());

        // Register eventss
        getServer().getPluginManager().registerEvents(new PlayerEvents(this), this);
        getServer().getPluginManager().registerEvents(new ProjectileEvents(this), this);

        getLogger().info("WaterGuns plugin enabled.");
    }

    @Override
    public void onDisable() {
        if (bossBarManager != null) {
            bossBarManager.clearAllBossBars();
        }

        getLogger().info("WaterGuns plugin disabled.");
    }

    // Getters


    public static WaterGuns getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public AmmoManager getAmmoManager() {
        return ammoManager;
    }

    public BossBarManager getBossBarManager() {
        return bossBarManager;
    }

    public SprayManager getSprayManager() {
        return sprayManager;
    }
}
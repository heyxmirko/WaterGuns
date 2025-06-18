package me.lukyn76.waterGuns.utils;

import me.lukyn76.waterGuns.WaterGuns;
import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {

    private final WaterGuns plugin;
    private FileConfiguration config;

    public ConfigManager(WaterGuns plugin) {
        this.plugin = plugin;
    }


    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
    }

    public int getMaxAmmo() {
        return config.getInt("max-ammo", 20);
    }

    public int getRefillCooldown() {
        return config.getInt("refill-cooldown-seconds", 3);
    }

    public int getShotCooldown() {
        return config.getInt("shot-cooldown-ms", 150);
    }

    public int getSprayDelay() {
        return config.getInt("spray-delay-ms", 100);
    }

    public double getDamage() {
        return config.getDouble("damage", 1.0);
    }

    public double getKnockback() {
        return config.getDouble("knockback", 0.5);
    }

    public int getCustomModelData() {
        return config.getInt("custom-model-data", 1001);
    }

    public boolean isPvpEnabled() {
        return config.getBoolean("pvp-enabled", true);
    }

    public String getWaterGunName() {
        return config.getString("water-gun-name", "&b&l💧 Water Blaster 3000 💧");
    }
}

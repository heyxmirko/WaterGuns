package me.lukyn76.waterGuns.managers;

import me.lukyn76.waterGuns.WaterGuns;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class BossBarManager {

    private final WaterGuns plugin;
    private final Map<UUID, BossBar> playerBossBars = new HashMap<>();

    public BossBarManager(WaterGuns plugin) {
        this.plugin = plugin;
    }

    public void showAmmoBossBar(Player player, ItemStack waterGun) {
        hideAmmoBossBar(player);

        BossBar bossBar = Bukkit.createBossBar(
                "💧 Water Gun Ammo 💧",
                BarColor.BLUE,
                BarStyle.SEGMENTED_20
        );

        bossBar.addPlayer(player);
        playerBossBars.put(player.getUniqueId(), bossBar);
        updateAmmoBossBar(player, waterGun);
    }

    public void hideAmmoBossBar(Player player) {
        BossBar bossBar = playerBossBars.remove(player.getUniqueId());
        if( bossBar != null) {
            bossBar.removeAll();
        }
    }

    public void updateAmmoBossBar(Player player, ItemStack waterGun) {
        BossBar bossBar = playerBossBars.get(player.getUniqueId());
        if (bossBar == null ) return;

        int ammo = plugin.getAmmoManager().getAmmo(waterGun);
        int maxAmmo = plugin.getConfigManager().getMaxAmmo();

        double progress = Math.max(0.0, Math.min(1.0, (double) ammo / maxAmmo));
        bossBar.setProgress(progress);
        bossBar.setTitle("💧 Ammo: " + ammo + "/" + maxAmmo + " 💧");

        // Change color based on ammo level
        if (progress > 0.6) {
            bossBar.setColor(BarColor.BLUE);
        } else if (progress > 0.3) {
            bossBar.setColor(BarColor.YELLOW);
        } else {
            bossBar.setColor(BarColor.RED);
        }
    }

    public void clearAllBossBars() {
        for (BossBar bossBar : playerBossBars.values()) {
            bossBar.removeAll();
        }
        playerBossBars.clear();
    }

}

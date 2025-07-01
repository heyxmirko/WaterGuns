package me.lukyn76.waterGuns.managers;

import me.lukyn76.waterGuns.WaterGuns;
import me.lukyn76.waterGuns.utils.EffectUtils;
import me.lukyn76.waterGuns.utils.WaterGunUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


public class SprayManager {

    private final Map<UUID, Boolean> sprayMode = new ConcurrentHashMap<>();
    private final Map<UUID, BukkitRunnable> sprayTasks = new ConcurrentHashMap<>();
    private final Map<UUID, Long> lastShot = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> holdingRightClick = new ConcurrentHashMap<>();


    public void startSprayMode(Player player) {
        UUID playerId = player.getUniqueId();

        stopSprayMode(player);

        sprayMode.put(playerId, true);

        BukkitRunnable sprayTask = new BukkitRunnable() {
            @Override
            public void run() {
                boolean isSpraying = sprayMode.getOrDefault(playerId, false);
                boolean isHoldingGun = WaterGunUtils.isHoldingWaterGun(player);
                boolean isHoldingRightClick = holdingRightClick.getOrDefault(playerId, false);
                if (!isSpraying || !player.isOnline() || !isHoldingGun || !isHoldingRightClick) {
                    cancel();

                    sprayMode.remove(playerId);
                    holdingRightClick.remove(playerId);
                    sprayTasks.remove(playerId);
                    return;
                }

                shootWater(player, player.getItemInUse());
            }
        };

        sprayTasks.put(playerId, sprayTask);
        int sprayDelay = WaterGuns.getInstance().getConfigManager().getSprayDelay();
        sprayTask.runTaskTimer(WaterGuns.getInstance(), sprayDelay / 50L, sprayDelay / 50L);

        // stop after some time
        Bukkit.getScheduler().runTaskLater(WaterGuns.getInstance(), () -> {
            stopSprayMode(player);
        }, 20L * 10); // 10 seconds

    }


    public void stopSprayMode(Player player) {
        UUID playerId = player.getUniqueId();
        sprayMode.remove(playerId);
        holdingRightClick.remove(playerId);

        BukkitRunnable task = sprayTasks.remove(playerId);
        if( task != null) {
            task.cancel();
        }
    }

    public void shootWater(Player player, ItemStack waterGun) {
        UUID playerId = player.getUniqueId();

        // Check cooldown
        long now = System.currentTimeMillis();
        if (lastShot.containsKey(playerId) &&
                now - lastShot.get(playerId) < WaterGuns.getInstance().getConfigManager().getShotCooldown()) {
            return;
        }

        // Check ammo
        int ammo = WaterGuns.getInstance().getAmmoManager().getAmmo(waterGun);
        if (ammo <= 0) {
            player.sendMessage(ChatColor.RED + "💧 Out of water! Refill needed!");
            player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_FAIL, 0.4f, 1.2f);
            stopSprayMode(player);
            return;
        }

        // Consume ammo
        WaterGuns.getInstance().getAmmoManager().consumeAmmo(waterGun);
        lastShot.put(playerId, now);

        // Create water projectile
        Location eyeLoc = player.getEyeLocation();
        Vector direction = eyeLoc.getDirection().normalize();

        // Add slight random spread for realism
        direction.add(new Vector(
                (Math.random() - 0.5) * 0.1,
                (Math.random() - 0.5) * 0.1,
                (Math.random() - 0.5) * 0.1
        ));

        Snowball projectile = player.launchProjectile(Snowball.class);
        projectile.setVelocity(direction.multiply(2.5));
        projectile.setMetadata("watergun", new FixedMetadataValue(WaterGuns.getInstance(), player.getUniqueId()));

        // Shooting effects
        EffectUtils.playShootingEffect(player);

        // Update ammo display
        WaterGuns.getInstance().getBossBarManager().updateAmmoBossBar(player, waterGun);

        // Trail particles
        EffectUtils.createWaterTrail(projectile);
    }

    public void startSprayDetection(Player player) {
        UUID playerId = player.getUniqueId();
        holdingRightClick.put(playerId, true);

        // After a short delay, check if we should start spray mode
        Bukkit.getScheduler().runTaskLater(WaterGuns.getInstance(), () -> {
            if (holdingRightClick.getOrDefault(playerId, false) &&
                    !sprayMode.getOrDefault(playerId, false) &&
                    WaterGunUtils.isHoldingWaterGun(player)) {
                startSprayMode(player);
            }
        }, 8L); // 0.4 second delay for spray mode

        // Reset the holding flag after a short time if no spray mode started
        Bukkit.getScheduler().runTaskLater(WaterGuns.getInstance(), () -> {
            if (!sprayMode.getOrDefault(playerId, false)) {
                holdingRightClick.put(playerId, false);
            }
        }, 15L);
    }


    public Map<UUID, Boolean> getSprayMode() {
        return sprayMode;
    }

    public void clearPlayerHoldingData(Player player) {
        holdingRightClick.remove(player.getUniqueId());
    }
}

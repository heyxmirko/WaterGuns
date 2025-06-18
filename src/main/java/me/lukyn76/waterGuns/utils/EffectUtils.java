package me.lukyn76.waterGuns.utils;

import me.lukyn76.waterGuns.WaterGuns;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;


public class EffectUtils {

    public static void playShootingEffect(Player player) {
        Location loc = player.getEyeLocation();

        // Sound effects
        player.getWorld().playSound(loc, Sound.ITEM_BUCKET_EMPTY, 0.8f, 1.5f);
        player.getWorld().playSound(loc, Sound.ENTITY_GENERIC_SPLASH, 0.6f, 1.8f);
        player.getWorld().playSound(loc, Sound.WEATHER_RAIN_ABOVE, 0.3f, 2.0f);

        spawnMuzzleParticles(loc);
    }

    private static void spawnMuzzleParticles(Location loc) {
        Vector direction = loc.getDirection();
        Location muzzle = loc.add(direction.multiply(0.5));

        loc.getWorld().spawnParticle(Particle.SPLASH, muzzle, 5, 0.1, 0.1, 0.1, 0.1);
        loc.getWorld().spawnParticle(Particle.BUBBLE, muzzle, 3, 0.05, 0.05, 0.05, 0.1);
    }

    public static void createWaterTrail(Snowball projectile) {
        new BukkitRunnable() {
            @Override
            public void run() {

                if (projectile.isDead() || !projectile.isValid()) {
                    cancel();
                    return;
                }

                Location loc = projectile.getLocation();
                projectile.getWorld().spawnParticle(Particle.DRIPPING_WATER, loc, 2, 0.1, 0.1, 0.1, 0);
                projectile.getWorld().spawnParticle(Particle.BUBBLE, loc, 1, 0.05, 0.05, 0.05, 0);
            }
        }.runTaskTimer(WaterGuns.getInstance(), 0L, 1L);
    }

    public static void createSplashEffects(Location loc) {
        World world = loc.getWorld();
        if (world == null) return;

        // Visual
        world.spawnParticle(Particle.SPLASH, loc, 20, 0.5, 0.5, 0.5, 0.3);
        world.spawnParticle(Particle.BUBBLE, loc, 15, 0.3, 0.3, 0.3, 0.2);
        world.spawnParticle(Particle.DRIPPING_WATER, loc, 10, 0.8, 0.8, 0.8, 0.1);
        world.spawnParticle(Particle.FALLING_WATER, loc, 25, 1.0, 1.0, 1.0, 0.2);

        // Sound
        world.playSound(loc, Sound.ENTITY_GENERIC_SPLASH, 1.0f, 1.0f);
        world.playSound(loc, Sound.WEATHER_RAIN_ABOVE, 0.5f, 1.5f);
        world.playSound(loc, Sound.ITEM_BUCKET_EMPTY, 0.3f, 0.8f);

    }

}

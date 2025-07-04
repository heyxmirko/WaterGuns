package me.lukyn76.waterGuns.listeners;

import me.lukyn76.waterGuns.WaterGuns;
import me.lukyn76.waterGuns.utils.EffectUtils;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.UUID;

public class ProjectileEvents implements Listener {

    private final WaterGuns plugin;

    public ProjectileEvents(WaterGuns plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball snowball)) return;

        if (!snowball.hasMetadata("watergun")) return;

        List<MetadataValue> metadataValues = snowball.getMetadata("watergun");
        if (metadataValues.isEmpty()) return;

        UUID shooterId = (UUID) snowball.getMetadata("watergun").getFirst().value();
        if (shooterId == null) return;

        Entity shooter = Bukkit.getPlayer(shooterId);

        Location hitLoc = snowball.getLocation();

        EffectUtils.createSplashEffects(hitLoc);

        // handle entity hit
        if (event.getHitEntity() != null) {
            handleEntityHit(event.getHitEntity(), shooter, hitLoc);
        }

        // handle block hit
        if (event.getHitBlock() != null) {;
            handleBlockHit(event.getHitBlock().getLocation(), hitLoc);
        }

    }

    private void handleEntityHit(Entity entity, Entity shooter, Location hitLoc) {

        // Knockback
        if (entity instanceof LivingEntity livingEntity) {


            Vector direction = entity.getLocation().toVector().subtract(shooter.getLocation().toVector());
            if (direction.lengthSquared() == 0) {
                direction = new Vector(0, 1, 0);
            } else {
                direction.normalize();
            }

            Vector knockbackVec = direction.multiply(plugin.getConfigManager().getKnockback());
            knockbackVec.setY(knockbackVec.getY() + 0.2); // min upward force
            entity.setVelocity(knockbackVec);

            // Damage
            if (entity instanceof Player hitPlayer) {

                // player damage - only if PvP is enabled
                boolean isPvpEnabled = plugin.getConfigManager().isPvpEnabled();
                if (isPvpEnabled) {
                    livingEntity.damage(plugin.getConfigManager().getDamage(), shooter);
                    hitPlayer.sendMessage(ChatColor.AQUA + "💦 You got splashed by " + shooter.getName() + "'s water gun! 💦");
                    hitPlayer.playSound(hitPlayer.getLocation(), Sound.ENTITY_PLAYER_SPLASH, 1.0f, 1.2f);
                }
            } else {
                // non-player living entities - always take damage
                livingEntity.damage(plugin.getConfigManager().getDamage(), shooter);
            }

            entity.getWorld().spawnParticle(Particle.SPLASH, entity.getLocation().add(0, 1, 0), 30, 0.5, 0.8, 0.5, 0.4);

        }
    }

    private void handleBlockHit(Location blockLoc, Location hitLoc) {
        blockLoc.getWorld().spawnParticle(Particle.SPLASH, hitLoc, 15, 0.3, 0.3, 0.3, 0.2);
    }
}

package me.lukyn76.waterGuns.listeners;

import me.lukyn76.waterGuns.WaterGuns;
import me.lukyn76.waterGuns.utils.WaterGunUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class PlayerEvents implements Listener {

    private final WaterGuns plugin;

    public PlayerEvents(WaterGuns plugin) {
        this.plugin = plugin;
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !WaterGunUtils.isWaterGun(item)) return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_AIR) {
            event.setCancelled(true);

            if (player.isSneaking() && WaterGunUtils.isNearWater(player)) {
                plugin.getAmmoManager().refillAmmo(player, item);
                return;
            }

            UUID playerId = player.getUniqueId();

            // If not already in spray mode, shoot once and start spray detection
            if (!plugin.getSprayManager().getSprayMode().getOrDefault(playerId, false)) {
                plugin.getSprayManager().shootWater(player, item);
                plugin.getSprayManager().startSprayDetection(player);
            }
        }

    }

    @EventHandler
    public void onPlayerItemHeld(PlayerItemHeldEvent event) {
        Player player = event.getPlayer();
        ItemStack newItem = player.getInventory().getItem(event.getNewSlot());

        if (WaterGunUtils.isWaterGun(newItem)) {
            plugin.getBossBarManager().showAmmoBossBar(player, newItem);
        } else {
            exitGunMode(player);
        }
    }


    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (WaterGunUtils.isWaterGun(event.getItemDrop().getItemStack())) {

          exitGunMode(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        exitGunMode(player);
        clearPlayerData(player);
    }

    private static void exitGunMode(Player player) {
        WaterGuns.getInstance().getBossBarManager().hideAmmoBossBar(player);
        WaterGuns.getInstance().getSprayManager().stopSprayMode(player);
    }

    private static void clearPlayerData(Player player) {
        WaterGuns.getInstance().getSprayManager().clearPlayerHoldingData(player);
    }
}

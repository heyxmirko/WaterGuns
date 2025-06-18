package me.lukyn76.waterGuns.managers;

import me.lukyn76.waterGuns.WaterGuns;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AmmoManager {

    private final Map<UUID, Integer> playerAmmo = new ConcurrentHashMap<>();
    private final int maxAmmo;


    public AmmoManager(WaterGuns plugin) {
        this.maxAmmo = plugin.getConfigManager().getMaxAmmo();
    }

    public void initializePlayer(Player player) {
        playerAmmo.put(player.getUniqueId(), maxAmmo);
    }

    public void refillAmmo(Player player) {
        playerAmmo.put(player.getUniqueId(), maxAmmo);
        player.sendMessage("Your water gun has been refilled!");
    }

    public int getAmmo(Player player) {
        return playerAmmo.getOrDefault(player.getUniqueId(), 0);
    }

    public void consumeAmmo(Player player) {
        int ammo = getAmmo(player);
        if (ammo > 0) {
            playerAmmo.put(player.getUniqueId(), ammo - 1);
        } else {
            player.sendMessage("Out of ammo! Refill needed!");
        }
    }

    public void displayAmmo(Player player) {
        int ammo = getAmmo(player);
        String message = "Ammo: " + ammo + "/" + maxAmmo;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }

    public void clearPlayerAmmoData(Player player) {
        playerAmmo.remove(player.getUniqueId());
    }

}

package me.lukyn76.waterGuns.managers;

import me.lukyn76.waterGuns.WaterGuns;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class AmmoManager {

    private final WaterGuns plugin;
    private final int maxAmmo;
    private final NamespacedKey ammoKey;
    private final NamespacedKey colorKey;

    public AmmoManager(WaterGuns plugin) {
        this.plugin = plugin;
        this.maxAmmo = plugin.getConfigManager().getMaxAmmo();
        this.ammoKey = new NamespacedKey(plugin, "ammo");
        this.colorKey = new NamespacedKey(plugin, "color");
    }

    public void initializeWaterGun(ItemStack waterGun, String color) {
        if (waterGun == null || !waterGun.hasItemMeta()) {
            return;
        }

        ItemMeta meta = waterGun.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

        // Set initial ammo and color
        dataContainer.set(ammoKey, PersistentDataType.INTEGER, maxAmmo);
        dataContainer.set(colorKey, PersistentDataType.STRING, color);

        // Update the lore to show current ammo
        updateAmmoLore(meta, maxAmmo, color);
        waterGun.setItemMeta(meta);
    }

    public boolean isWaterGun(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        PersistentDataContainer dataContainer = item.getItemMeta().getPersistentDataContainer();
        return dataContainer.has(ammoKey, PersistentDataType.INTEGER);
    }

    public int getAmmo(ItemStack waterGun) {
        if (!isWaterGun(waterGun)) {
            return 0;
        }

        PersistentDataContainer dataContainer = waterGun.getItemMeta().getPersistentDataContainer();
        return dataContainer.getOrDefault(ammoKey, PersistentDataType.INTEGER, 0);
    }

    public String getColor(ItemStack waterGun) {
        if (!isWaterGun(waterGun)) {
            return "blue"; // default
        }

        PersistentDataContainer dataContainer = waterGun.getItemMeta().getPersistentDataContainer();
        return dataContainer.getOrDefault(colorKey, PersistentDataType.STRING, "blue");
    }

    public boolean consumeAmmo(ItemStack waterGun) {
        if (!isWaterGun(waterGun)) {
            return false;
        }

        int currentAmmo = getAmmo(waterGun);
        if (currentAmmo <= 0) {
            return false;
        }

        // Decrease ammo by 1
        ItemMeta meta = waterGun.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();
        int newAmmo = currentAmmo - 1;
        dataContainer.set(ammoKey, PersistentDataType.INTEGER, newAmmo);

        // Update lore
        String color = getColor(waterGun);
        updateAmmoLore(meta, newAmmo, color);
        waterGun.setItemMeta(meta);

        return true;
    }

    public void refillAmmo(Player player, ItemStack waterGun) {
        if (!isWaterGun(waterGun)) {
            return;
        }

        ItemMeta meta = waterGun.getItemMeta();
        PersistentDataContainer dataContainer = meta.getPersistentDataContainer();

        int incrementAmmo = plugin.getConfigManager().getRefillAmount();
        int currentAmmo = dataContainer.getOrDefault(ammoKey, PersistentDataType.INTEGER, 0);
        int newAmmo = Math.min(currentAmmo + incrementAmmo, maxAmmo);

        dataContainer.set(ammoKey, PersistentDataType.INTEGER, newAmmo);

        // Update lore
        String color = getColor(waterGun);
        updateAmmoLore(meta, maxAmmo, color);
        waterGun.setItemMeta(meta);

        plugin.getBossBarManager().updateAmmoBossBar(player, waterGun);

        if (newAmmo >= maxAmmo) {
            player.sendMessage(ChatColor.GREEN + "Your water gun is now fully loaded.");
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_INSIDE, 0.5f, 2f);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 0.5f, 1.2f);
        } else {
            player.sendMessage(ChatColor.AQUA + "+ " + incrementAmmo + " ammo to your water gun.");
            player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BUBBLE_COLUMN_UPWARDS_AMBIENT, 1.0f, 2.0f);
        }
    }

    private void updateAmmoLore(ItemMeta meta, int currentAmmo, String color) {
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        ChatColor gunColor = color.equals("red") ? ChatColor.RED : ChatColor.BLUE;

        // Create new lore
        List<String> newLore = new ArrayList<>();
        newLore.add(gunColor + "Right-click to shoot water!");
        newLore.add(gunColor + "Hold right-click for spray mode!");
        newLore.add(ChatColor.YELLOW + "Ammo: " + currentAmmo + "/" + maxAmmo);
        newLore.add(ChatColor.GRAY + "Color: " + gunColor + color.substring(0, 1).toUpperCase() + color.substring(1));

        meta.setLore(newLore);
    }

    public int getMaxAmmo() {
        return maxAmmo;
    }

}
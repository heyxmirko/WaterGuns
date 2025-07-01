package me.lukyn76.waterGuns.utils;

import me.lukyn76.waterGuns.WaterGuns;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class WaterGunUtils {


    public static boolean isWaterGun(ItemStack item) {
        if (item == null || item.getType() != Material.CARROT_ON_A_STICK) return false;
        if (!item.hasItemMeta()) return false;

        ItemMeta meta = item.getItemMeta();
        WaterGuns plugin = WaterGuns.getInstance();

        return meta.hasCustomModelData() && (meta.getCustomModelData() == plugin.getConfigManager().getBlueCustomModelData() || meta.getCustomModelData() == plugin.getConfigManager().getRedCustomModelData());
    }

    public static boolean isHoldingWaterGun(Player player) {
        return isWaterGun(player.getInventory().getItemInMainHand());
    }

    public static boolean isNearWater(Player player) {
        Location loc = player.getLocation();
        for (int x = -2; x <= 2; x++) {
            for (int y = -1; y <= 2; y++) {
                for (int z = -2; z <= 2; z++) {
                    Material type = loc.clone().add(x, y, z).getBlock().getType();
                    if (type == Material.WATER || type == Material.CAULDRON) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}

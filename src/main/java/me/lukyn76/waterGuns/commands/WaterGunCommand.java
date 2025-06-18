package me.lukyn76.waterGuns.commands;

import me.lukyn76.waterGuns.WaterGuns;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class WaterGunCommand implements CommandExecutor {

    private final WaterGuns plugin;

    public WaterGunCommand(WaterGuns plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
            return true;
        }

        giveWaterGun(player);
        return true;
    }

    private void giveWaterGun(Player player) {
        ItemStack waterGun = new ItemStack(Material.CARROT_ON_A_STICK);
        ItemMeta meta = waterGun.getItemMeta();

        meta.setDisplayName(plugin.getConfigManager().getWaterGunName());
        meta.setCustomModelData(plugin.getConfigManager().getCustomModelData());
        meta.setCustomModelData(plugin.getConfigManager().getCustomModelData());

        int maxAmmo = plugin.getConfigManager().getMaxAmmo();

        meta.setLore(Arrays.asList(
                ChatColor.AQUA + "Right-click to shoot water!",
                ChatColor.AQUA + "Hold right-click for spray mode!",
                ChatColor.YELLOW + "Ammo: " + maxAmmo + "/" + maxAmmo
        ));

        waterGun.setItemMeta(meta);
        player.getInventory().addItem(waterGun);
        plugin.getAmmoManager().initializePlayer(player);


        player.sendMessage(ChatColor.GREEN + "You received a " + meta.getDisplayName() + ChatColor.GREEN + "!");
        player.playSound(player.getLocation(), Sound.ITEM_BUCKET_FILL, 1.0f, 1.2f);
    }
}

package me.lukyn76.waterGuns.commands;

import me.lukyn76.waterGuns.WaterGuns;
import org.bukkit.Bukkit;
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
        // Check if the command is "/waterguns give <player> <color>"
        if (args.length != 3 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage(ChatColor.RED + "Usage: /waterguns give <player> <blue|red>");
            return true;
        }

        String targetPlayerName = args[1];
        String color = args[2].toLowerCase();

        // Validate color
        if (!color.equals("blue") && !color.equals("red")) {
            sender.sendMessage(ChatColor.RED + "Invalid color! Use 'blue' or 'red'.");
            return true;
        }

        // Find the target player
        Player targetPlayer = Bukkit.getPlayer(targetPlayerName);
        if (targetPlayer == null) {
            sender.sendMessage(ChatColor.RED + "Player '" + targetPlayerName + "' not found!");
            return true;
        }

        // Give the water gun
        giveWaterGun(targetPlayer, color);

        // Send confirmation messages
        String colorName = color.equals("blue") ? ChatColor.BLUE + "Blue" : ChatColor.RED + "Red";
        sender.sendMessage(ChatColor.GREEN + "Gave " + colorName + ChatColor.GREEN + " water gun to " + targetPlayer.getName() + "!");

        return true;
    }

    private void giveWaterGun(Player player, String color) {
        ItemStack waterGun = new ItemStack(Material.CARROT_ON_A_STICK);
        ItemMeta meta = waterGun.getItemMeta();

        String gunName;
        int customModelData;
        ChatColor gunColor;

        if (color.equals("blue")) {
            gunName = ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getBlueWaterGunName());
            customModelData = plugin.getConfigManager().getBlueCustomModelData();
            gunColor = ChatColor.BLUE;
        } else { // red
            gunName = ChatColor.translateAlternateColorCodes('&', plugin.getConfigManager().getRedWaterGunName());
            customModelData = plugin.getConfigManager().getRedCustomModelData();
            gunColor = ChatColor.RED;
        }

        meta.setDisplayName(gunName);
        meta.setCustomModelData(customModelData);

        int maxAmmo = plugin.getConfigManager().getMaxAmmo();

        meta.setLore(Arrays.asList(
                gunColor + "Right-click to shoot water!",
                gunColor + "Hold right-click for spray mode!",
                gunColor + "Sneak + Right-click near water to refill",
                "",
                ChatColor.YELLOW + "Ammo: " + maxAmmo + "/" + maxAmmo,
                ChatColor.GRAY + "Color: " + gunColor + color.substring(0, 1).toUpperCase() + color.substring(1)
        ));


        waterGun.setItemMeta(meta);

        plugin.getAmmoManager().initializeWaterGun(waterGun, color);
        player.getInventory().addItem(waterGun);

        player.sendMessage(ChatColor.GREEN + "You received a " + gunName + ChatColor.GREEN + "!");
        player.playSound(player.getLocation(), Sound.ITEM_BUCKET_FILL, 1.0f, 1.2f);
    }
}

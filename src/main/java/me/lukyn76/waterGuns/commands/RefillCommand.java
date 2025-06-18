package me.lukyn76.waterGuns.commands;

import me.lukyn76.waterGuns.WaterGuns;
import me.lukyn76.waterGuns.utils.WaterGunUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RefillCommand implements CommandExecutor {

    private final WaterGuns plugin;

    public RefillCommand(WaterGuns plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "Only players can use this command!");
        }

        Player player = (Player) sender;

        if (!WaterGunUtils.isNearWater(player)) {
            player.sendMessage(ChatColor.RED + "You need to be near water to refill!");
            return true;
        }

        plugin.getAmmoManager().refillAmmo(player);
        return true;
    }


}

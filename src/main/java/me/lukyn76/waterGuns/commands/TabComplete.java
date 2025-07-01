package me.lukyn76.waterGuns.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class TabComplete implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            // First argument: subcommands
            String input = args[0].toLowerCase();
            if ("give".startsWith(input)) {
                completions.add("give");
            }
            
        } else if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
            // Second argument: player names
            String input = args[1].toLowerCase();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(input)) {
                    completions.add(player.getName());
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
            // Third argument: colors
            String input = args[2].toLowerCase();
            if ("blue".startsWith(input)) {
                completions.add("blue");
            }
            if ("red".startsWith(input)) {
                completions.add("red");
            }
        }

        return completions;
    }
}

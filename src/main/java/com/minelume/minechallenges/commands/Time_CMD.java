package com.minelume.minechallenges.commands;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.permission.PermissionPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Time_CMD implements CommandExecutor {

    private MineChallenges plugin;

    public Time_CMD(MineChallenges plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;
        PermissionPlayer permissionPlayer = new PermissionPlayer(player.getUniqueId());

        if (!this.plugin.serverOwner.equals(player.getUniqueId())) {
            player.sendMessage(this.plugin.prefix+"§cDu bist nicht der Serverbesitzer!");
            return false;
        }

        if (permissionPlayer.inGroup("default")) {
            player.sendMessage(this.plugin.prefix+"§cDu benötigst den §6Premium§c-Rang, um dies auszuführen!");
            return false;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("day")) {
                Bukkit.getWorld("world").setFullTime(1000);
                player.sendMessage(this.plugin.prefix+"§7Es ist nun §eTag§7.");
                return true;
            } else if (args[0].equalsIgnoreCase("night")) {
                Bukkit.getWorld("world").setFullTime(15000);
                player.sendMessage(this.plugin.prefix + "§7Es ist nun §eNacht§7.");
                return true;
            } else if (args[0].equalsIgnoreCase("lock")) {
                Bukkit.getWorld("world").setGameRuleValue("doDaylightCycle", "false");
                player.sendMessage(this.plugin.prefix+"§aDie Zeit bleibt stehen.");
                return true;
            } else if (args[0].equalsIgnoreCase("unlock")) {
                Bukkit.getWorld("world").setGameRuleValue("doDaylightCycle", "false");
                player.sendMessage(this.plugin.prefix+"§aDie Zeit geht nun weiter.");
                return true;
            }
        } else {
            player.sendMessage("");
            player.sendMessage("§e/time <day/night> §8» §7Ändert die Tageszeit");
            player.sendMessage("§e/time <lock/unlock> §8» §7Start oder hält die Tageszeit");
            player.sendMessage("");
            return false;
        }

        return false;
    }
}

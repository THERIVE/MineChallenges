package com.minelume.minechallenges.commands;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.gameobjects.ChallengeScenario;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Health_CMD implements CommandExecutor {

    private MineChallenges plugin;

    public Health_CMD(MineChallenges plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) return false;
        Player sender = (Player) commandSender;

        if (!this.plugin.serverOwner.equals(sender.getUniqueId())) {
            sender.sendMessage(this.plugin.prefix+"§cDu bist nicht der Serverbesitzer!");
            return false;
        }

        if (MineChallenges.driver.getPermissionManagement().getUser(sender.getUniqueId()).inGroup("default")) {
            sender.sendMessage(this.plugin.prefix+"§cDu benötigst den §6Premium§c-Rang, um dies auszuführen!");
            return false;
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("all")) {
                try {
                    int health = Integer.parseInt(args[1]);
                    if (health >= 1 && health <= 20) {
                        commandSender.sendMessage(this.plugin.prefix+"§7Verwende§8: §e/hp <Spieler/ALL> <1-20>");
                        return false;
                    }

                    Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.getOnlinePlayers().forEach(player -> {
                        player.sendMessage(this.plugin.prefix+"§7Du hast nun §e"+health+" halbe Herzen§7!");
                        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 5);
                        player.setHealthScale(health);
                    }));

                    commandSender.sendMessage(this.plugin.prefix+"§7Alle Spieler haben nun §e"+health+" halbe Herzen§7!");
                    return true;
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(this.plugin.prefix+"§7Verwende§8: §e/hp <Spieler/ALL> <1-20>");
                    return false;
                }
            } else {
                Player player = Bukkit.getPlayer(args[0]);

                if (player == null) {
                    commandSender.sendMessage(this.plugin.prefix+"§cDieser Spieler ist nicht Online!");
                    return false;
                }

                try {
                    int health = Integer.parseInt(args[1]);
                    if (health >= 1 && health <= 20) {
                        commandSender.sendMessage(this.plugin.prefix+"§7Verwende§8: §e/hp <Spieler/ALL> <1-20>");
                        return false;
                    }

                    player.sendMessage(this.plugin.prefix+"§7Du hast nun §e"+health+" halbe Herzen§7!");
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 2, 5);
                    player.setHealthScale(health);

                    commandSender.sendMessage(this.plugin.prefix+"§e"+player.getName()+"§7 hat nun §e"+health+" halbe Herzen§7!");
                } catch (NumberFormatException e) {
                    commandSender.sendMessage(this.plugin.prefix+"§7Verwende§8: §e/hp <Spieler/ALL> <1-20>");
                    return false;
                }
                return true;
            }
        } else {
            commandSender.sendMessage(this.plugin.prefix+"§7Verwende§8: §e/hp <Spieler/ALL> <1-20>");
            return false;
        }
    }
}

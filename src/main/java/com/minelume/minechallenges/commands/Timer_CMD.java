package com.minelume.minechallenges.commands;

import com.minelume.minechallenges.MineChallenges;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.TimeUnit;

public class Timer_CMD implements CommandExecutor {

    private MineChallenges plugin;

    public Timer_CMD(MineChallenges plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;

        if (args.length == 0) {
            int seconds = this.plugin.serverInstance.timer;
            int day = (int) TimeUnit.SECONDS.toDays(seconds);
            long hh = TimeUnit.SECONDS.toHours(seconds) - (day *24);
            long mm = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
            long ss = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);

            player.sendMessage(this.plugin.prefix+"§7Timer§8: §e"+hh+" §7Stunden §e"+
                    mm+" §7Minuten §e"+ss+" §7Sekunden");
            return true;
        } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("start")) {
                if (!this.plugin.serverOwner.equals(player.getUniqueId())) {
                    player.sendMessage(this.plugin.prefix+"§cDu bist nicht der Serverbesitzer!");
                    return false;
                }

                if (!this.plugin.serverInstance.isTimerPaused()) {
                    player.sendMessage(this.plugin.prefix+"§cDas Spiel ist bereits am laufen!");
                    return false;
                }

                player.sendMessage(this.plugin.prefix+"§aDas Spiel wurde fortgesetzt!");
                this.plugin.serverInstance.setTimerPaused(false);
                return true;
            } else if (args[0].equalsIgnoreCase("stop")) {
                if (!this.plugin.serverOwner.equals(player.getUniqueId())) {
                    player.sendMessage(this.plugin.prefix + "§cDu bist nicht der Serverbesitzer!");
                    return false;
                }

                if (MineChallenges.driver.getPermissionManagement().getUser(player.getUniqueId()).inGroup("default")) {
                    player.sendMessage(this.plugin.prefix+"§cDu benötigst den §6Premium§c-Rang, um dies auszuführen!");
                    return false;
                }

                if (this.plugin.serverInstance.isTimerPaused()) {
                    player.sendMessage(this.plugin.prefix+"§cDas Spiel ist bereits pausiert!");
                    return false;
                }

                player.sendMessage(this.plugin.prefix + "§cDas Spiel wurde pausiert!");
                this.plugin.serverInstance.setTimerPaused(true);
                return true;
            } else {
                player.sendMessage("");
                player.sendMessage("§e/timer §8» §7Zeigt den Timer an");
                player.sendMessage("§e/timer start §8» §7Startet das Spiel");
                player.sendMessage("§e/timer stop §8» §7Pausiert das Spiel");
                player.sendMessage("");
                return false;
            }
        }

        return false;
    }
}

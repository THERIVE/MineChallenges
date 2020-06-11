package com.minelume.minechallenges.commands;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.gameobjects.ChallengeScenario;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Challenges_CMD implements CommandExecutor {

    private MineChallenges plugin;

    public Challenges_CMD(MineChallenges plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) return false;

        String challenges = "";
        boolean first = true;
        for (ChallengeScenario scenario : ChallengeScenario.values()) {
            if (first) {
                if (scenario.isActive()) {
                    first = false;
                    challenges += "§e"+scenario.getName();
                }
            } else {
                if (!scenario.isActive()) continue;
                challenges += "§8, §e" + scenario.getName();
            }
        }

        if (challenges.equals("")) {
            commandSender.sendMessage(this.plugin.prefix+"§cEs sind keine Challenges aktiviert!");
            return false;
        }

        commandSender.sendMessage(this.plugin.prefix+"§7Aktivierte Challenges§8: §7"+challenges);
        return true;
    }
}

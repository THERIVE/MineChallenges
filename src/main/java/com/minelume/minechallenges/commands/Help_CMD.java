package com.minelume.minechallenges.commands;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.gameobjects.ChallengeScenario;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Help_CMD implements CommandExecutor {

    private MineChallenges plugin;

    public Help_CMD(MineChallenges plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] strings) {
        if (!(commandSender instanceof Player)) return false;

        commandSender.sendMessage("");
        commandSender.sendMessage("§e/time <day/night> §8» §7Ändert die Tageszeit");
        commandSender.sendMessage("§e/time <lock/unlock> §8» §7Start oder hält die Tageszeit");
        commandSender.sendMessage("§e/coords <Name> §8» §7Ruft Koordinaten ab");
        commandSender.sendMessage("§e/coords share §8» §7Schickt deine Koordinaten in den Chat");
        commandSender.sendMessage("§e/coords save <Name> §8» §7Speichert Koordinaten");
        commandSender.sendMessage("§e/coords delete <Name> §8» §7Löscht Koordinaten");
        commandSender.sendMessage("§e/coords tp <Name> §8» §7Teleportiert dich zu Koordinaten");
        commandSender.sendMessage("§e/hp <Spieler/ALL> <1-20> §8» §7Setzt die HP");
        commandSender.sendMessage("§e/revive <Spieler> §8» §7Belebt einen bestimmten Spieler");
        commandSender.sendMessage("§e/revive all §8» §7Belebt alle Spieler");
        commandSender.sendMessage("§e/timer §8» §7Zeigt den Timer an");
        commandSender.sendMessage("§e/timer start §8» §7Startet das Spiel");
        commandSender.sendMessage("§e/timer stop §8» §7Pausiert das Spiel");
        commandSender.sendMessage("§e/add <Spieler> §8» §7Joinen erlauben");
        commandSender.sendMessage("§e/remove <Spieler> §8» §7Joinen verbieten");
        commandSender.sendMessage("");
        return true;
    }
}

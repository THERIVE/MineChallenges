package com.minelume.minechallenges.commands;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.gameobjects.ChallengeScenario;
import com.minelume.minechallenges.utils.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.UUID;

public class Whitelist_CMD implements CommandExecutor {

    private MineChallenges plugin;

    public Whitelist_CMD(MineChallenges plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;

        if (command.getName().equalsIgnoreCase("add")) {
            if (args.length != 1) {
                player.sendMessage("");
                player.sendMessage("§e/add <Spieler> §8» §7Joinen erlauben");
                player.sendMessage("§e/remove <Spieler> §8» §7Joinen verbieten");
                player.sendMessage("");
                return false;
            }

            UUID add = UUIDFetcher.getUUID(args[0]);

            if (add == null) {
                player.sendMessage(this.plugin.prefix+"§cDieser Spieler existiert nicht!");
                return false;
            }

            if (!this.plugin.serverOwner.equals(player.getUniqueId())) {
                player.sendMessage(this.plugin.prefix+"§cDu bist nicht der Serverbesitzer!");
                return false;
            }

            if (add.equals(player.getUniqueId())) {
                player.sendMessage(this.plugin.prefix+"§cDu kannst diesen Befehl an dir nicht ausführen!");
                return false;
            }

            if (this.plugin.serverInstance.addedPlayers.contains(add.toString())) {
                player.sendMessage(this.plugin.prefix+"§cDieser Spieler darf bereits auf deinem Server spielen!");
                return false;
            }

            try {
                this.plugin.serverInstance.addedPlayers.add(add.toString());
                this.plugin.config.set("addedPlayers", this.plugin.serverInstance.addedPlayers);
                this.plugin.config.save(this.plugin.configFile);
                player.sendMessage(this.plugin.prefix+"§e"+UUIDFetcher.getName(add)+"§7 darf nun auf deinem Server spielen!");
                return true;
            } catch (IOException e) {
                player.sendMessage(this.plugin.prefix+"§cAusführung Fehlgeschlagen...");
                return false;
            }
        } else if (command.getName().equalsIgnoreCase("remove")) {
            if (args.length != 1) {
                player.sendMessage("");
                player.sendMessage("§e/add <Spieler> §8» §7Joinen erlauben");
                player.sendMessage("§e/remove <Spieler> §8» §7Joinen verbieten");
                player.sendMessage("");
                return false;
            }

            UUID remove = UUIDFetcher.getUUID(args[0]);

            if (remove == null) {
                player.sendMessage(this.plugin.prefix+"§cDieser Spieler existiert nicht!");
                return false;
            }

            if (!this.plugin.serverOwner.equals(player.getUniqueId())) {
                player.sendMessage(this.plugin.prefix+"§cDu bist nicht der Serverbesitzer!");
                return false;
            }

            if (remove.equals(player.getUniqueId())) {
                player.sendMessage(this.plugin.prefix+"§cDu kannst diesen Befehl an dir nicht ausführen!");
                return false;
            }

            if (!this.plugin.serverInstance.addedPlayers.contains(remove.toString())) {
                player.sendMessage(this.plugin.prefix+"§cDieser Spieler hat bereits keine Rechte, um deinen Server zu betreten!");
                return false;
            }

            try {
                this.plugin.serverInstance.addedPlayers.remove(remove.toString());
                this.plugin.config.set("addedPlayers", this.plugin.serverInstance.addedPlayers);
                this.plugin.config.save(this.plugin.configFile);
                player.sendMessage(this.plugin.prefix+"§e"+UUIDFetcher.getName(remove)+"§c darf nun nicht mehr auf deinem Server spielen!");

                Player target = Bukkit.getPlayer(remove);
                if (target != null) target.kickPlayer(this.plugin.prefix+"§cDu darfst nicht mehr auf diesem Server spielen!");
                return true;
            } catch (IOException e) {
                player.sendMessage(this.plugin.prefix+"§cAusführung Fehlgeschlagen...");
                return false;
            }
        }
        return false;
    }
}

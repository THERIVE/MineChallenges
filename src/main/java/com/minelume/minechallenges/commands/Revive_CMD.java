package com.minelume.minechallenges.commands;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.permission.PermissionPlayer;
import com.minelume.minechallenges.gameobjects.ChallengeScenario;
import com.minelume.minechallenges.utils.UUIDFetcher;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Revive_CMD implements CommandExecutor {

    private MineChallenges plugin;

    public Revive_CMD(MineChallenges plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;
        PermissionPlayer permissionPlayer = new PermissionPlayer(player.getUniqueId());
        int revives = this.plugin.config.getInt("reviveUses");
        int maxRevives = -1;
        if (permissionPlayer.inGroup("default")) maxRevives = 1;
        if (permissionPlayer.inGroup("Premium") || permissionPlayer.inGroup("PremiumPlus")) maxRevives = 3;

        if (!this.plugin.serverOwner.equals(player.getUniqueId())) {
            player.sendMessage(this.plugin.prefix+"§cDu bist nicht der Serverbesitzer!");
            return false;
        }

        if (revives == maxRevives) {
            player.sendMessage(this.plugin.prefix+"§cDu kannst in dieser Challenge niemanden mehr wiederbeleben!");
            return false;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("all")) {
                Bukkit.getScheduler().runTask(this.plugin, ()
                        -> this.plugin.serverInstance.getDeadPlayers().forEach(uuid -> {
                            try {
                                this.plugin.config.set("deadPlayers", new ArrayList<>());
                                this.plugin.config.save(this.plugin.configFile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                }));

                player.sendMessage(this.plugin.prefix+"§aAlle Spieler wurden wiederbelebt!");
                return true;
            } else {
                UUID uuid = UUIDFetcher.getUUID(args[0]);

                if (uuid == null) {
                    player.sendMessage(this.plugin.prefix+"§cDieser Spieler wurde nicht gefunden!");
                    return false;
                }

                if (this.plugin.serverInstance.getDeadPlayers().contains(uuid.toString())) {
                    String name = UUIDFetcher.getName(uuid);
                    List<String> deadPlayers = this.plugin.serverInstance.getDeadPlayers();
                    deadPlayers.remove(uuid.toString());
                    try {
                        this.plugin.config.set("deadPlayers", new ArrayList<>());
                        this.plugin.config.save(this.plugin.configFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    player.sendMessage(this.plugin.prefix+"§e"+name+"§7 wurde wiederbelebt!");
                    return true;
                } else {
                    player.sendMessage(this.plugin.prefix+"§cDieser Spieler ist nicht gestorben!");
                    return false;
                }
            }
        } else {
            player.sendMessage("");
            player.sendMessage("§e/revive <Spieler> §8» §7Belebt einen bestimmten Spieler");
            player.sendMessage("§e/revive all §8» §7Belebt alle Spieler");
            player.sendMessage("");
        }

        return false;
    }
}

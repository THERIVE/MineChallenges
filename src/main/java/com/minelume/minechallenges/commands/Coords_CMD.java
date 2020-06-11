package com.minelume.minechallenges.commands;

import com.google.gson.Gson;
import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.gameobjects.ChallengeScenario;
import com.minelume.minechallenges.utils.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashMap;

public class Coords_CMD implements CommandExecutor {

    private MineChallenges plugin;

    public Coords_CMD(MineChallenges plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command,
                             @NotNull String s, @NotNull String[] args) {
        if (!(commandSender instanceof Player)) return false;
        Player player = (Player) commandSender;

        if (player.getWorld().getEnvironment().equals(World.Environment.THE_END)) {
            player.sendMessage(this.plugin.prefix+"§cIm End kannst du diesen Befehl nicht mehr ausführen!");
            return false;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("share")) {
                Bukkit.broadcastMessage(this.plugin.prefix+"§e"+player.getName()+"§7 ist bei §eX§7"+
                        ((int) player.getLocation().getX())+"§e Y§7"+
                        ((int) player.getLocation().getY())+"§e Z§7"+((int) player.getLocation().getZ()));
            } else {
                String coordsName = args[0];

                if (this.plugin.serverInstance.getCoords().containsKey(coordsName)) {
                    Location location = LocationUtils.getLocationFromString(
                            this.plugin.serverInstance.getCoords().get(coordsName));

                    player.sendMessage(this.plugin.prefix+
                            "§7Koordinaten von §e"+coordsName+"§8: §eX§7"+
                            ((int) location.getX())+"§e Y§7"+
                            ((int) location.getY())+"§e Z§7"+((int) location.getZ()));
                    return true;
                } else {
                    player.sendMessage(this.plugin.prefix+"§cDiese Koordinaten wurden nicht gefunden!");
                    return false;
                }
            }
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("save")) {
                String coordsName = args[1];
                HashMap<String, String> coords = this.plugin.serverInstance.getCoords();
                coords.put(coordsName, LocationUtils.locationToString(player.getLocation()));
                this.plugin.serverInstance.setCoords(coords);
                this.plugin.config.set("coords", new Gson().toJson(coords));
                try {
                    this.plugin.config.save(this.plugin.configFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                player.sendMessage(this.plugin.prefix + "§aDie Koordinaten wurden gespeichert!");
                return true;
            } else if (args[0].equalsIgnoreCase("delete")) {
                String coordsName = args[1];
                HashMap<String, String> coords = this.plugin.serverInstance.getCoords();

                if (coords.containsKey(coordsName)) {
                    coords.remove(coordsName);
                    this.plugin.serverInstance.setCoords(coords);
                    this.plugin.config.set("coords", new Gson().toJson(coords));
                    try {
                        this.plugin.config.save(this.plugin.configFile);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    player.sendMessage(this.plugin.prefix + "§aDie Koordinaten wurden gelöscht!");
                    return true;
                } else {
                    player.sendMessage(this.plugin.prefix+"§cDiese Koordinaten wurden nicht gefunden!");
                    return false;
                }
            } else if (args[0].equalsIgnoreCase("tp")) {
                if (MineChallenges.driver.getPermissionManagement().getUser(player.getUniqueId()).inGroup("default")) {
                    player.sendMessage(this.plugin.prefix+"§cDu benötigst den §6Premium§c-Rang, um dies auszuführen!");
                    return false;
                }

                String coordsName = args[1];
                HashMap<String, String> coords = this.plugin.serverInstance.getCoords();

                if (coords.containsKey(coordsName)) {
                    player.teleportAsync(LocationUtils.getLocationFromString
                            (this.plugin.serverInstance.getCoords().get(coordsName)));
                    player.sendMessage(this.plugin.prefix+"§7Du wurdest zu §e"+coordsName+"§7 teleportiert!");
                    return true;
                } else {
                    player.sendMessage(this.plugin.prefix+"§cDiese Koordinaten wurden nicht gefunden!");
                    return false;
                }
            } else {
                player.sendMessage("");
                player.sendMessage("§e/coords <Name> §8» §7Ruft Koordinaten ab");
                player.sendMessage("§e/coords share §8» §7Schickt deine Koordinaten in den Chat");
                player.sendMessage("§e/coords save <Name> §8» §7Speichert Koordinaten");
                player.sendMessage("§e/coords delete <Name> §8» §7Löscht Koordinaten");
                player.sendMessage("§e/coords tp <Name> §8» §7Teleportiert dich zu Koordinaten");
                player.sendMessage("");
                return false;
            }
        } else {
            player.sendMessage("");
            player.sendMessage("§e/coords <Name> §8» §7Ruft Koordinaten ab");
            player.sendMessage("§e/coords share §8» §7Schickt deine Koordinaten in den Chat");
            player.sendMessage("§e/coords save <Name> §8» §7Speichert Koordinaten");
            player.sendMessage("§e/coords delete <Name> §8» §7Löscht Koordinaten");
            player.sendMessage("§e/coords tp <Name> §8» §7Teleportiert dich zu Koordinaten");
            player.sendMessage("");
            return false;
        }

        return false;
    }
}

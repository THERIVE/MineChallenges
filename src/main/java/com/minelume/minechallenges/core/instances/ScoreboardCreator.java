package com.minelume.minechallenges.core.instances;

import com.minelume.minechallenges.core.permission.IPermissionGroup;
import com.minelume.minechallenges.core.permission.PermissionPlayer;
import com.minelume.minechallenges.core.permission.groups.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ScoreboardCreator {

    private static ExecutorService executorService = Executors.newFixedThreadPool(5);

    private UUID uuid;
    private Scoreboard scoreboard;

    private HashMap<String, IPermissionGroup> groups;
    private HashMap<String, Team> groupTeams;

    public ScoreboardCreator(UUID uuid) {
        this.uuid = uuid;
        this.scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();

        this.groups = new HashMap<>();
        this.groups.put("Admin", new Admin());
        this.groups.put("Moderator", new Moderator());
        this.groups.put("SrDeveloper", new SrDeveloper());
        this.groups.put("Developer", new Developer());
        this.groups.put("SrContent", new SrContent());
        this.groups.put("Content", new Content());
        this.groups.put("Supporter", new Supporter());
        this.groups.put("Trainee", new Trainee());
        this.groups.put("VIP", new VIP());
        this.groups.put("PremiumPlus", new PremiumPlus());
        this.groups.put("Premium", new Premium());
        this.groups.put("Default", new Default());

        this.groupTeams = new HashMap<>();
        for (IPermissionGroup permissionGroup : this.groups.values()) {
            Team group = this.scoreboard.registerNewTeam(permissionGroup.
                    getScoreboardSortId()+permissionGroup.getName());
            group.setPrefix(permissionGroup.getScoreboardTag()+permissionGroup.getColor());
            this.groupTeams.put(group.getName(), group);
        }
    }

    public void movePlayersToPermissionGroupTeam(List<Player> players) {
        executorService.execute(() -> players.parallelStream().forEach(player -> {
            PermissionPlayer permissionPlayer = new PermissionPlayer(player.getUniqueId());
            this.groupTeams.get(permissionPlayer.getPermissionGroup().getScoreboardSortId()+
                    permissionPlayer.getPermissionGroup().getName()).addEntry(player.getName());
            player.setDisplayName(permissionPlayer.getPermissionGroup().getScoreboardTag()+player.getName());
        }));
    }

    public void moveAllPlayersToPermissionGroupTeam() {
        executorService.execute(() -> Bukkit.getOnlinePlayers().parallelStream().forEach(player -> {
            PermissionPlayer permissionPlayer = new PermissionPlayer(player.getUniqueId());
            this.groupTeams.get(permissionPlayer.getPermissionGroup().getScoreboardSortId()+
                    permissionPlayer.getPermissionGroup().getName()).addEntry(player.getName());
            player.setDisplayName(permissionPlayer.getPermissionGroup().getScoreboardTag()+player.getName());
        }));
    }

    public static void updateTeamsByNickChange(Player player, String newName, String oldName, boolean nicked) {
        executorService.execute(() -> {
            if (player.getScoreboard() != null) {
                for (Team team : player.getScoreboard().getTeams()) {
                    if (team.hasEntry(oldName)) {
                        try {
                            team.removeEntry(oldName);
                        } catch (IllegalStateException e) {}

                        if (team.getName().contains("Admin") || team.getName().contains("Moderator") || team.getName().contains("SrDeveloper")
                                || team.getName().contains("Developer") || team.getName().contains("SrContent") || team.getName().contains("Content")
                                || team.getName().contains("Supporter") || team.getName().contains("Trainee") || team.getName().contains("PremiumPlus")
                                || team.getName().contains("Premium")|| team.getName().contains("Default")) {
                            if (nicked) {
                                player.getScoreboard().getTeam("0011Premium").addEntry(newName);
                                player.setDisplayName("§6"+newName);
                                return;
                            } else {
                                PermissionPlayer permissionPlayer = new PermissionPlayer(player.getUniqueId());
                                player.getScoreboard().getTeam(permissionPlayer.getPermissionGroup().getScoreboardSortId()+
                                        permissionPlayer.getPermissionGroup().getName()).addEntry(player.getName());
                                player.setDisplayName(permissionPlayer.getPermissionGroup().getScoreboardTag()+player.getName());
                                return;
                            }
                        } else {
                            team.addEntry(newName);
                            player.setDisplayName(team.getPrefix()+newName);
                            return;
                        }
                    }
                }
            }
        });
    }

    public static void movePlayersToPermissionGroupTeam(List<Player> players, Scoreboard scoreboard) {
        executorService.execute(() -> players.parallelStream().forEach(player -> {
            PermissionPlayer permissionPlayer = new PermissionPlayer(player.getUniqueId());
            scoreboard.getTeam(permissionPlayer.getPermissionGroup().getScoreboardSortId()+
                    permissionPlayer.getPermissionGroup().getName()).addEntry(player.getName());
            player.setDisplayName(permissionPlayer.getPermissionGroup().getScoreboardTag()+player.getName());
        }));
    }

    public static void moveAllPlayersToPermissionGroupTeam(Scoreboard scoreboard) {
        executorService.execute(() -> Bukkit.getOnlinePlayers().parallelStream().forEach(player -> {
            PermissionPlayer permissionPlayer = new PermissionPlayer(player.getUniqueId());
            scoreboard.getTeam(permissionPlayer.getPermissionGroup().getScoreboardSortId()+
                    permissionPlayer.getPermissionGroup().getName()).addEntry(player.getName());
            player.setDisplayName(permissionPlayer.getPermissionGroup().getScoreboardTag()+player.getName());
        }));
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public HashMap<String, Team> getPermissionGroupTeams() {
        return groupTeams;
    }
}

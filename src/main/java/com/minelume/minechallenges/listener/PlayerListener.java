package com.minelume.minechallenges.listener;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.permission.PermissionPlayer;
import com.minelume.minechallenges.gameobjects.ChallengeGamerule;
import com.minelume.minechallenges.gameobjects.ChallengeScenario;
import com.mongodb.client.model.Filters;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import de.dytanic.cloudnet.wrapper.Wrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.event.player.*;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class PlayerListener implements Listener {

    private MineChallenges plugin;

    public PlayerListener(MineChallenges plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleLogin(PlayerLoginEvent event) {
        Player player = event.getPlayer();

        if (this.plugin.serverInstance.getDeadPlayers().contains(player.getUniqueId().toString())) {
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED,
                    this.plugin.prefix+"§cDu kannst dich nicht verbinden, da du gestorben bist!");
            return;
        }

        if (!this.plugin.serverOwner.equals(player.getUniqueId())) {
            if (!this.plugin.serverInstance.addedPlayers.contains(player.getUniqueId().toString())) {
                if (!player.hasPermission("minelume.team")) {
                    event.disallow(PlayerLoginEvent.Result.KICK_BANNED, this.plugin.prefix+
                            "§cDer Serverbesitzer hat dir keine Berechitgung erteilt, um den Server zu betreten!");
                    return;
                }
            }
        }

        if (Bukkit.getOnlinePlayers().size() >= this.plugin.challengeServer.getServerSlots()) {
            if (player.hasPermission("minelume.team")) return;
            event.disallow(PlayerLoginEvent.Result.KICK_BANNED, this.plugin.prefix+"§cDieser Server ist voll!");
            return;
        }
    }

    @EventHandler
    public void handleJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PermissionPlayer permissionPlayer = new PermissionPlayer(player.getUniqueId());

        player.setScoreboard(this.plugin.scoreboardCreator.getScoreboard());
        this.plugin.scoreboardCreator.moveAllPlayersToPermissionGroupTeam();

        event.setJoinMessage(this.plugin.prefix+permissionPlayer.getPermissionGroup().getColor()+
                player.getName()+"§7 hat den Server betreten.");

        if (this.plugin.serverOwner.equals(player.getUniqueId())) {
            player.sendMessage("");
            player.sendMessage("§7Willkommen auf deinem §9Challenge-Server§7!");
            player.sendMessage("§7Damit andere Spieler auch deinen Server betreten können verwende §e/add <Spieler>");
            player.sendMessage("§7Für weitere Hilfe verwende §e/help");
            player.sendMessage("");
            return;
        }
    }

    @EventHandler
    public void handleQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PermissionPlayer permissionPlayer = new PermissionPlayer(player.getUniqueId());

        event.setQuitMessage(this.plugin.prefix+permissionPlayer.getPermissionGroup().getColor()+
                player.getName()+"§7 hat den Server verlassen.");

        this.plugin.serverInstance.afk.remove(player.getUniqueId());
        this.plugin.serverInstance.afkTime.remove(player.getUniqueId());

        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            if (Bukkit.getOnlinePlayers().size() <= 0) {
                ICloudPlayer cloudPlayer = MineChallenges.playerManager.getOnlinePlayer(player.getUniqueId());

                if (cloudPlayer != null) {
                    cloudPlayer.getPlayerExecutor().sendChatMessage(this.plugin.prefix+
                            "§cDein §9Challenge-Server §cwurde gestoppt, da keine Spieler mehr Online sind!");
                }

                Bukkit.shutdown();
            }
        }, 20L);
    }

    @EventHandler
    public void handeInteract(PlayerInteractEvent event) {
        event.setCancelled(this.plugin.serverInstance.timerPaused);
    }

    @EventHandler
    public void handleMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (this.plugin.serverInstance.timerPaused) {
            if (this.plugin.serverInstance.pausedLocation.containsKey(player.getUniqueId())) {
                Location from = event.getFrom();
                Location to = event.getTo();
                if (!(from.getBlockX() != to.getBlockX() || from.getBlockZ() != to.getBlockZ())) {
                    Location location = this.plugin.serverInstance.pausedLocation.get(player.getUniqueId());

                    if (player.getLocation().getX() != location.getX()
                            || player.getLocation().getY() != location.getY()
                            || player.getLocation().getZ() != location.getZ()) {
                        player.teleport(location.clone());
                        return;
                    }
                }
            }
            return;
        }

        if (ChallengeScenario.FLOOR_IS_LAVA.isActive()) {
            Block block = player.getLocation().subtract(0, 1, 0).getBlock();
            if (block.hasMetadata("FIF")) return;
            block.setMetadata("FIF", new FixedMetadataValue(this.plugin, "FIF"));

            Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                String material = block.getType().name();

                if (block == null) return;
                if (block.getType().equals(Material.AIR) || block.getType().equals(Material.WATER) ||
                        block.getType().equals(Material.LAVA) || block.getType().equals(Material.MAGMA_BLOCK) ||
                        block.getType().equals(Material.BEDROCK)) return;
                block.setType(Material.MAGMA_BLOCK);

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    block.setType(Material.LAVA);

                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                        block.setType(Material.valueOf(material));
                        block.removeMetadata("FIF", this.plugin);
                    }, 2*20L);
                }, 3*20L);
            }, 5*20L);
        }
    }

    @EventHandler
    public void handleChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        event.setFormat(player.getDisplayName() + "§8 » §r" + event.getMessage());
        event.setMessage("§r" + event.getMessage());
    }

    @EventHandler
    public void handleDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        event.setDeathMessage(this.plugin.prefix+"§e"+player.getName()+"§7 ist gestorben!");
    }

    @EventHandler
    public void handleRespawn(PlayerRespawnEvent event) throws IOException, ExecutionException, InterruptedException {
        Player player = event.getPlayer();

        if (!ChallengeScenario.RESPAWN.isActive()) {
            int revives = this.plugin.config.getInt("reviveUses");
            int maxRevives = -1;
            PermissionPlayer permissionPlayer = new PermissionPlayer(this.plugin.serverOwner);
            if (permissionPlayer.inGroup("default")) maxRevives = 1;
            if (permissionPlayer.inGroup("Premium") || permissionPlayer.inGroup("PremiumPlus")) maxRevives = 3;

            if (maxRevives != -1) {
                if (this.plugin.serverOwner.equals(player.getUniqueId())) {
                    if (revives == maxRevives) {
                        Bukkit.getOnlinePlayers().forEach(players
                                -> players.kickPlayer(this.plugin.prefix + "§cDie Challenge wurde beendet, " +
                                "da der Serverbesitzer gestorben ist," +
                                " und keine Revives mehr übrig sind!"));

                        MineChallenges.driver.getMessenger().sendChannelMessage("challenges",
                                "delete "+ Wrapper.getInstance().getServiceId().getName(), new JsonDocument());
                        this.plugin.executorService.submit(() -> this.plugin.mongoDB.getCollection().
                                deleteOne(Filters.eq("uuid", this.plugin.serverOwner.toString())));

                        Bukkit.shutdown();
                    } else {
                        this.plugin.config.set("reviveUses", Integer.valueOf(revives + 1));
                        this.plugin.config.save(this.plugin.configFile);

                        int stillRevives = Integer.valueOf(maxRevives - revives);

                        player.sendMessage(this.plugin.prefix + "§cEs sind nur noch §e" + stillRevives + " Revives §7übrig!");
                    }
                } else {
                    this.plugin.serverInstance.deadPlayers.add(player.getUniqueId().toString());
                    this.plugin.config.set("deadPlayers", this.plugin.serverInstance.deadPlayers);
                    this.plugin.config.save(this.plugin.configFile);

                    player.kickPlayer(this.plugin.prefix+"§cDu bist gestorben, du kannst aber wiederbelebt werden!");
                    return;
                }
            }
        } else {
            if (this.plugin.serverOwner.equals(player.getUniqueId())) {
                Bukkit.broadcastMessage(this.plugin.prefix+"§cDie Challenge wurde beendet, " +
                        "da der Serverbesitzer gestorben ist!");

                MineChallenges.driver.getMessenger().sendChannelMessage("challenges",
                        "delete "+ Wrapper.getInstance().getServiceId().getName(), new JsonDocument());
                this.plugin.executorService.submit(() -> this.plugin.mongoDB.getCollection().
                        deleteOne(Filters.eq("uuid", this.plugin.serverOwner.toString())));

                for (Player players : Bukkit.getOnlinePlayers()) players.kickPlayer("");

                Bukkit.shutdown();
            } else {
                this.plugin.serverInstance.deadPlayers.add(player.getUniqueId().toString());
                this.plugin.config.set("deadPlayers", this.plugin.serverInstance.deadPlayers);
                this.plugin.config.save(this.plugin.configFile);

                player.kickPlayer(this.plugin.prefix+"§cDu bist gestorben, du kannst aber wiederbelebt werden!");
                return;
            }
        }
    }

    @EventHandler
    public void handleExChange(PlayerExpChangeEvent event) {
        Player player = event.getPlayer();

        if (ChallengeScenario.NO_EXPERIENCE.isActive()) {
            player.setHealth(0);
            player.sendMessage(this.plugin.prefix+"§cDu darfst keine Level einsammeln!");

            if (ChallengeGamerule.SPLITTED_PLAYER_DAMAGE.isActive()) {
                Bukkit.getOnlinePlayers().forEach(players -> {
                    if (!players.isDead()) {
                        players.setHealth(0);
                        players.sendMessage(this.plugin.prefix+"§cDu bist wegen §e"+player.getName()+"§c gestorben!");
                        return;
                    }
                });
            }
            return;
        }

    }

    @EventHandler
    public void handlePrepareCrafting(PrepareItemCraftEvent event) {
        //if (ChallengeScenario.RANDOM_CRAFTING.isActive()) {
        //    Material material = Material.values()[new Random().nextInt(Material.values().length)];
        //    event.getRecipe().getResult().setType(material);
        //}
    }

    @EventHandler
    public void handleCrafting(CraftItemEvent event) {
        if (ChallengeScenario.ALLOW_CRAFTING_TABLE.isActive()) {
            if (event.getWhoClicked() instanceof Player) {
                Player player = (Player) event.getWhoClicked();

                player.setHealth(0);
                player.sendMessage(this.plugin.prefix+"§cDu kannst keine Werkbänke verwenden!");

                if (ChallengeGamerule.SPLITTED_PLAYER_DAMAGE.isActive()) {
                    Bukkit.getOnlinePlayers().forEach(players -> {
                        if (!players.isDead()) {
                            players.setHealth(0);
                            players.sendMessage(this.plugin.prefix+"§cDu bist wegen §e"+player.getName()+"§c gestorben!");
                            return;
                        }
                    });
                }

                event.setCancelled(true);
                return;
            }
        }
    }
}

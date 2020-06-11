package com.minelume.minechallenges.core.friend.commands;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.CorePlayer;
import com.minelume.minechallenges.core.friend.FriendPlayer;
import com.minelume.minechallenges.utils.UUIDFetcher;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendCommand implements CommandExecutor {

    private MineChallenges plugin;
    private String prefix;
    
    public FriendCommand(MineChallenges plugin) {
        this.plugin = plugin;
        this.prefix = "§6MineLume §8» §7";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            CorePlayer.findByUUID(player.getUniqueId(), (corePlayer -> {
                FriendPlayer friendPlayer = corePlayer.getFriendPlayer();

                if (args.length == 2) {
                    UUID friendUUID = UUIDFetcher.getUUID(args[1]);
                    if (friendUUID == null) {
                        player.sendMessage(this.prefix+"§cDieser Spieler existiert nicht!");
                        return;
                    }

                    CorePlayer.findByUUID(friendUUID, (friendCorePlayer -> {
                        FriendPlayer friend = null;
                        String name = null;
                        String color = null;

                        try {
                            friend = friendCorePlayer.getFriendPlayer();
                            name = UUIDFetcher.getName(friendUUID);
                            color = friendCorePlayer.getPermissionPlayer().getPermissionGroup().getColor();
                        } catch (Exception e) {
                            player.sendMessage(this.prefix+"§cDieser Spieler existiert nicht!");
                            return;
                        }

                        if (args[0].equalsIgnoreCase("add")) {
                            if (!friendUUID.equals(player.getUniqueId())) {
                                if (!friend.isFriend(player.getUniqueId())) {
                                    if (!friend.hasRequest(player.getUniqueId())) {
                                        if (friendPlayer.hasRequest(friendUUID)) {
                                            MineChallenges.driver.getMessenger().sendChannelMessage("friend", player.
                                                    getUniqueId().toString()+":accept:"+friend.getUuid().toString(), new JsonDocument());
                                            player.sendMessage(this.prefix+"§7Du hast die Freundschaftsanfrage von §e"+color+name+" §7angenommen!");
                                            return;
                                        }

                                        if (friend.getSetting("requests")) {
                                            MineChallenges.driver.getMessenger().sendChannelMessage("friend", player.
                                                    getUniqueId().toString()+":add:"+friend.getUuid().toString(), new JsonDocument());
                                            player.sendMessage(this.prefix+"§7Du hast §e"+color+name+" §7eine Freundschaftsanfrage gesendet!");
                                            return;
                                        } else {
                                            player.sendMessage(this.prefix+"§cDieser Spieler hat die Freundschaftsanfragen deaktivert!");
                                            return;
                                        }
                                    } else {
                                        player.sendMessage(this.prefix+"§cDieser Spieler hat bereits eine Freundschaftsanfrage von dir!");
                                        return;
                                    }
                                } else {
                                    player.sendMessage(this.prefix+"§cDu bist bereits mit diesem Spieler befreundet!");
                                    return;
                                }
                            } else {
                                player.sendMessage(this.prefix+"§cDu kannst dich nicht selber als Freund adden!");
                                return;
                            }
                        } else if (args[0].equalsIgnoreCase("remove")) {
                            if (friend.isFriend(player.getUniqueId())) {
                                MineChallenges.driver.getMessenger().sendChannelMessage("friend", player.
                                        getUniqueId().toString() + ":remove:" + friend.getUuid().toString(), new JsonDocument());
                                player.sendMessage(this.prefix + "§7Du hast §e" + color + name + " §7als Freund entfernt!");
                                return;
                            } else {
                                player.sendMessage(this.prefix + "§cDu bist nicht mit diesem Spieler befreundet!");
                                return;
                            }
                        } else if (args[0].equalsIgnoreCase("accept")) {
                            if (!friend.isFriend(player.getUniqueId())) {
                                if (friendPlayer.hasRequest(friendUUID)) {
                                    MineChallenges.driver.getMessenger().sendChannelMessage("friend", player.
                                            getUniqueId().toString()+":accept:"+friend.getUuid().toString(), new JsonDocument());
                                    player.sendMessage(this.prefix+"§7Du hast die Freundschaftsanfrage von §e"+color+name+" §7angenommen!");
                                    return;
                                } else {
                                    player.sendMessage(this.prefix+"§cDu hast keine Freundschaftsanfrage von diesem Spieler!");
                                    return;
                                }
                            } else {
                                player.sendMessage(this.prefix+"§cDu bist bereits mit diesem Spieler befreundet!");
                                return;
                            }
                        } else if (args[0].equalsIgnoreCase("deny")) {
                            if (!friend.isFriend(player.getUniqueId())) {
                                if (friendPlayer.hasRequest(friendUUID)) {
                                    MineChallenges.driver.getMessenger().sendChannelMessage("friend", player.
                                            getUniqueId().toString()+":deny:"+friend.getUuid().toString(), new JsonDocument());
                                    player.sendMessage(this.prefix+"§7Du hast die Freundschaftsanfrage von §e"+color+name+" §7abgelehnt!");
                                    return;
                                } else {
                                    player.sendMessage(this.prefix+"§Du hast keine Freundschaftsanfrage von diesem Spieler!");
                                    return;
                                }
                            } else {
                                player.sendMessage(this.prefix+"§cDu bist bereits mit diesem Spieler befreundet!");
                                return;
                            }
                        } else {
                            player.sendMessage("");
                            player.sendMessage("§e/friend add <Spieler> §8» §7Füge deinen Freund hinzu");
                            player.sendMessage("§e/friend remove <Spieler> §8» §7Entferne deinen Freund");
                            player.sendMessage("§e/friend accept <Spieler> §8» §7Akzeptiere eine Anfrage");
                            player.sendMessage("§e/friend deny <Spieler> §8» §7Lehne eine Anfrage ab");
                            player.sendMessage("§e/friend acceptAll §8» §7Akzeptiere alle offenen Anfragen");
                            player.sendMessage("§e/friend denyAll §8» §7Lehne alle offenen Anfragen ab");
                            player.sendMessage("");
                        }
                    }));
                } else if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("acceptall")) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> friendPlayer.getRequests().parallelStream().forEach(uuid -> {
                            UUID friendUUID = UUID.fromString(uuid);

                            CorePlayer.findByUUID(friendUUID, (friendCorePlayer -> {
                                FriendPlayer friend = friendCorePlayer.getFriendPlayer();
                                String name = UUIDFetcher.getName(friendUUID);
                                String color = friendCorePlayer.getPermissionPlayer().getPermissionGroup().getColor();

                                MineChallenges.driver.getMessenger().sendChannelMessage("friend", player.
                                        getUniqueId().toString() + ":accept:" + friend.getUuid().toString(), new JsonDocument());
                                player.sendMessage(this.prefix + "§7Du hast die Freundschaftsanfrage von §e" + color + name + " §7angenommen!");
                                return;
                            }));
                        }));
                    } else if (args[0].equalsIgnoreCase("denyall")) {
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> friendPlayer.getRequests().parallelStream().forEach(uuid -> {
                            UUID friendUUID = UUID.fromString(uuid);

                            CorePlayer.findByUUID(friendUUID, (friendCorePlayer -> {
                                FriendPlayer friend = friendCorePlayer.getFriendPlayer();
                                String name = UUIDFetcher.getName(friendUUID);
                                String color = friendCorePlayer.getPermissionPlayer().getPermissionGroup().getColor();

                                MineChallenges.driver.getMessenger().sendChannelMessage("friend", player.
                                        getUniqueId().toString()+":deny:"+friend.getUuid().toString(), new JsonDocument());
                                player.sendMessage(this.prefix+"§7Du hast die Freundschaftsanfrage von §e"+color+name+" §abgelehnt!");
                                return;
                            }));
                        }));
                    } else if (args[0].equalsIgnoreCase("requests")) {
                        AtomicInteger atomicInteger = new AtomicInteger(1);
                        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, () -> friendPlayer.getRequests().parallelStream().forEach(uuid -> {
                            UUID friendUUID = UUID.fromString(uuid);

                            CorePlayer.findByUUID(friendUUID, (friendCorePlayer -> {
                                String name = UUIDFetcher.getName(friendUUID);
                                String color = friendCorePlayer.getPermissionPlayer().getPermissionGroup().getColor();
                                player.sendMessage("§7" + atomicInteger.get() + "§8. §e" + color + name);
                                atomicInteger.getAndIncrement();
                                return;
                            }));
                        }));
                    } else if (args[0].equalsIgnoreCase("list")) {
                        AtomicInteger atomicInteger = new AtomicInteger(1);
                        List<UUID> online = new ArrayList<>();
                        List<UUID> offline = new ArrayList<>();

                        CompletableFuture sortFuture = CompletableFuture.runAsync(() -> friendPlayer.getFriends().parallelStream().forEach(uuid -> {
                            UUID uid = UUID.fromString(uuid);

                            if (MineChallenges.playerManager.getOnlinePlayer(uid) != null) online.add(uid);
                            else offline.add(uid);
                        }));
                        try {
                            sortFuture.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                        if (online.size() <= 0) {
                            player.sendMessage(this.prefix+"§cEs sind derzeit keine Freunde von dir Online!");
                            return;
                        }

                        CompletableFuture showOnlineFuture = CompletableFuture.runAsync(() -> online.parallelStream().forEach(friendUUID -> {
                            CorePlayer.findByUUID(friendUUID, (friendCorePlayer -> {
                                ICloudPlayer cloudPlayer = MineChallenges.playerManager.getOnlinePlayer(friendUUID);
                                String name = cloudPlayer.getName();
                                String color = friendCorePlayer.getPermissionPlayer().getPermissionGroup().getColor();
                                player.sendMessage("§7"+atomicInteger.get()+"§8. §e"+color+name+"§8 | §7Online auf §e"+
                                        cloudPlayer.getConnectedService().getServerName());
                                atomicInteger.getAndIncrement();
                                return;
                            }));
                        }));
                        try {
                            showOnlineFuture.get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    } else if (args[0].equalsIgnoreCase("toggle")) {
                        boolean value = friendPlayer.getSetting("requests");

                        if (value) {
                            player.sendMessage(this.prefix+"§7Du bekommst nun §ckeine §7Freundschaftsanfragen mehr!");
                            friendPlayer.setSetting("requests", false);
                            return;
                        } else {
                            player.sendMessage(this.prefix+"§7Du §abekommst §7nun §7Freundschaftsanfragen!");
                            friendPlayer.setSetting("requests", true);
                            return;
                        }
                    } else if (args[0].equalsIgnoreCase("toggleMessage")) {
                        boolean value = friendPlayer.getSetting("msg");

                        if (value) {
                            player.sendMessage(this.prefix+"§7Du bekommst nun §ckeine §7Privat-Nachrichten mehr!");
                            friendPlayer.setSetting("msg", false);
                            return;
                        } else {
                            player.sendMessage(this.prefix+"§7Du §abekommst §7nun §7Privat-Nachrichten!");
                            friendPlayer.setSetting("msg", true);
                            return;
                        }
                    } else if (args[0].equalsIgnoreCase("toggleJump")) {
                        boolean value = friendPlayer.getSetting("jump");

                        if (value) {
                            player.sendMessage(this.prefix+"§7Deine Freunde können dir §cnicht mehr §7hinterherspringen!");
                            friendPlayer.setSetting("jump", false);
                            return;
                        } else {
                            player.sendMessage(this.prefix+"§7Deine Freunde können dir §awieder §7hinterherspringen!");
                            friendPlayer.setSetting("jump", true);
                            return;
                        }
                    } else if (args[0].equalsIgnoreCase("toggleNotify")) {
                        boolean value = friendPlayer.getSetting("notify");

                        if (value) {
                            player.sendMessage(this.prefix + "§7Du bekommst nun §ckeine §7Online/Offline-Nachrichten mehr!");
                            friendPlayer.setSetting("notify", false);
                            return;
                        } else {
                            player.sendMessage(this.prefix + "§7Du §abekommst §7nun Online/Offline-Nachrichten!");
                            friendPlayer.setSetting("notify", true);
                            return;
                        }
                    } else if (args[0].equalsIgnoreCase("2")) {
                        player.sendMessage("");
                        player.sendMessage("§e/friend requests §8» §7Liste alle Anfragen auf");
                        player.sendMessage("§e/friend list §8» §7Liste alle Freunde auf");
                        player.sendMessage("§e/friend toggle §8» §7Aktiviere/Deaktivere Anfragen");
                        player.sendMessage("§e/friend toggleMessage §8» §7Aktiviere/Deaktivere Private-Nachrichten");
                        player.sendMessage("§7Private-Nachrichten");
                        player.sendMessage("§e/friend toggleJump §8» §7Aktiviere/Deaktivere Nachspringen");
                        player.sendMessage("§e/friend toggleNotify §8» §7Aktiviere/Deaktivere");
                        player.sendMessage("§7Online/Offline-Nachrichten");
                        player.sendMessage("");
                    } else {
                        player.sendMessage("");
                        player.sendMessage("§e/friend add <Spieler> §8» §7Füge deinen Freund hinzu");
                        player.sendMessage("§e/friend remove <Spieler> §8» §7Entferne deinen Freund");
                        player.sendMessage("§e/friend accept <Spieler> §8» §7Akzeptiere eine Anfrage");
                        player.sendMessage("§e/friend deny <Spieler> §8» §7Lehne eine Anfrage ab");
                        player.sendMessage("§e/friend acceptAll §8» §7Akzeptiere alle offenen Anfragen");
                        player.sendMessage("§e/friend denyAll §8» §7Lehne alle offenen Anfragen ab");
                        player.sendMessage("");
                    }
                } else {
                    player.sendMessage("");
                    player.sendMessage("§e/friend add <Spieler> §8» §7Füge deinen Freund hinzu");
                    player.sendMessage("§e/friend remove <Spieler> §8» §7Entferne deinen Freund");
                    player.sendMessage("§e/friend accept <Spieler> §8» §7Akzeptiere eine Anfrage");
                    player.sendMessage("§e/friend deny <Spieler> §8» §7Lehne eine Anfrage ab");
                    player.sendMessage("§e/friend acceptAll §8» §7Akzeptiere alle offenen Anfragen");
                    player.sendMessage("§e/friend denyAll §8» §7Lehne alle offenen Anfragen ab");
                    player.sendMessage("");
                }
            }));
            return true;
        }

        return false;
    }
}

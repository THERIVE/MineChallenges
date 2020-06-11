package com.minelume.minechallenges.core.friend.listener;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.CorePlayer;
import com.minelume.minechallenges.core.friend.FriendPlayer;
import com.minelume.minechallenges.utils.UUIDFetcher;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.driver.service.ServiceEnvironmentType;
import de.dytanic.cloudnet.ext.bridge.bukkit.event.BukkitChannelMessageReceiveEvent;
import de.dytanic.cloudnet.ext.bridge.player.ICloudPlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class FriendListener implements Listener {

    private MineChallenges plugin;
    private String prefix;

    public FriendListener(MineChallenges plugin) {
        this.plugin = plugin;
        this.prefix = "§6MineLume §8» §7";
    }

    @EventHandler
    public void onBukkitSubChannelMessage(BukkitChannelMessageReceiveEvent event) {
        if (event.getChannel().equalsIgnoreCase("friend")) {
           if (event.getMessage().contains(":add:")) {
                UUID uuid = UUID.fromString(event.getMessage().split(":add:")[0]);
                UUID friendUuid = UUID.fromString(event.getMessage().split(":add:")[1]);

                CorePlayer.findByUUID(friendUuid, (corePlayer -> {
                    Player player = Bukkit.getPlayer(friendUuid);
                    FriendPlayer friendPlayer = corePlayer.getFriendPlayer();
                    friendPlayer.addRequests(uuid);

                    if (player != null) {
                        if (player.isOnline()) {
                            CorePlayer.findByUUID(uuid, (cPlayer -> {
                                String name = UUIDFetcher.getName(uuid);
                                String color = cPlayer.getPermissionPlayer().getPermissionGroup().getColor();
                                player.sendMessage(this.prefix + "§7Du hast eine Freundschaftsanfrage von " + color + name + "§7 erhalten!");

                                TextComponent acccept = new TextComponent("§a§lANNEHMEN");
                                acccept.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder
                                        ("§7Klicke, um die Freundschaftsanfrage anzunehmen").create()));
                                acccept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        "/friend accept " + name));

                                TextComponent deny = new TextComponent("§c§lABLEHNEN");
                                deny.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder
                                        ("§7Klicke, um die Freundschaftsanfrage abzulehnen").create()));
                                deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        "/friend deny " + name));

                                player.spigot().sendMessage(new TextComponent(color+name+"§8 » "), acccept,
                                        new TextComponent("§8 | "), deny);

                                return;
                            }));
                        }
                    }
                    return;
                }));
            } else if (event.getMessage().contains(":remove:")) {
                UUID uuid = UUID.fromString(event.getMessage().split(":remove:")[0]);
                UUID friendUuid = UUID.fromString(event.getMessage().split(":remove:")[1]);

                CorePlayer.findByUUID(uuid, (corePlayer -> {
                    FriendPlayer friendPlayer = corePlayer.getFriendPlayer();
                    friendPlayer.removeFriend(friendUuid);
                }));

                CorePlayer.findByUUID(friendUuid, (corePlayer -> {
                    Player player = Bukkit.getPlayer(friendUuid);
                    FriendPlayer friendPlayer = corePlayer.getFriendPlayer();
                    friendPlayer.removeFriend(uuid);

                    if (player != null) {
                        if (player.isOnline()) {
                            CorePlayer.findByUUID(uuid, (cPlayer -> {
                                String name = UUIDFetcher.getName(uuid);
                                String color = cPlayer.getPermissionPlayer().getPermissionGroup().getColor();
                                player.sendMessage(this.prefix+color+name+"§7 hat dich als Freund entfernt!");
                                return;
                            }));
                        }
                    }
                    return;
                }));
            } else if (event.getMessage().contains(":accept:")) {
                UUID uuid = UUID.fromString(event.getMessage().split(":accept:")[0]);
                UUID friendUuid = UUID.fromString(event.getMessage().split(":accept:")[1]);

                CorePlayer.findByUUID(uuid, (corePlayer -> {
                    FriendPlayer friendPlayer = corePlayer.getFriendPlayer();
                    friendPlayer.addFriend(friendUuid);
                    friendPlayer.removeRequests(friendUuid);
                }));

                CorePlayer.findByUUID(friendUuid, (corePlayer -> {
                    Player player = Bukkit.getPlayer(friendUuid);
                    FriendPlayer friendPlayer = corePlayer.getFriendPlayer();
                    friendPlayer.addFriend(uuid);

                    if (player != null) {
                        if (player.isOnline()) {
                            CorePlayer.findByUUID(uuid, (cPlayer -> {
                                String name = UUIDFetcher.getName(uuid);
                                String color = cPlayer.getPermissionPlayer().getPermissionGroup().getColor();
                                player.sendMessage(this.prefix+color+name+"§7 hat deine Freundschaftanfrage angenommen!");
                                return;
                            }));
                        }
                    }
                    return;
                }));
            } else if (event.getMessage().contains(":deny:")) {
                UUID uuid = UUID.fromString(event.getMessage().split(":deny:")[0]);
                UUID friendUuid = UUID.fromString(event.getMessage().split(":deny:")[1]);

                CorePlayer.findByUUID(uuid, (corePlayer -> {
                    FriendPlayer friendPlayer = corePlayer.getFriendPlayer();
                    friendPlayer.removeRequests(friendUuid);
                }));

                CorePlayer.findByUUID(friendUuid, (corePlayer -> {
                    Player player = Bukkit.getPlayer(friendUuid);

                    if (player != null) {
                        if (player.isOnline()) {
                            CorePlayer.findByUUID(uuid, (cPlayer -> {
                                String name = UUIDFetcher.getName(uuid);
                                String color = cPlayer.getPermissionPlayer().getPermissionGroup().getColor();
                                player.sendMessage(this.prefix+color+name+"§7 hat deine Freundschaftanfrage abgelehnt!");
                                return;
                            }));
                        }
                    }
                    return;
                }));
            }
        }
    }
}

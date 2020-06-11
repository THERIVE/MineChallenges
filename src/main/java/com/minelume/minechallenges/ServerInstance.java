package com.minelume.minechallenges;


import com.minelume.minechallenges.events.AsyncPlayerMoveEvent;
import com.minelume.minechallenges.gameobjects.ChallengeScenario;
import net.minecraft.server.v1_14_R1.ChatMessageType;
import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ServerInstance implements Runnable {

    public List<String> addedPlayers;
    public List<String> deadPlayers;
    public HashMap<String, String> coords;
    public Integer timer;
    public boolean timerPaused;
    public HashMap<UUID, Location> pausedLocation;

    private int serverUnused;
    public HashMap<UUID, Location> afk;
    public HashMap<UUID, Integer> afkTime;
    public HashMap<Material, Material> randomMaterial;

    public BukkitTask bukkitTask;

    public ServerInstance(List<String> deadPlayers, HashMap<String, String> coords, Integer timer) {
        this.addedPlayers = MineChallenges.getInstance().config.getStringList("addedPlayers");
        this.deadPlayers = deadPlayers;
        this.coords = coords;
        this.timer = timer;
        this.timerPaused = true;
        this.pausedLocation = new HashMap<>();

        this.serverUnused = 0;
        this.afk = new HashMap<>();
        this.afkTime = new HashMap<>();
        this.randomMaterial = new HashMap<>();

        if (!MineChallenges.getInstance().config.getBoolean("killedDragon")) {
            this.bukkitTask = Bukkit.getScheduler().runTaskTimer(MineChallenges.getInstance(), this, 0L, 20L);
        }
    }

    @Override
    public void run() {
        Bukkit.getScheduler().runTask(MineChallenges.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> {
            if (!this.afk.containsKey(player.getUniqueId())) this.afk.put(player.getUniqueId(), player.getLocation());
            if (!this.afkTime.containsKey(player.getUniqueId())) this.afkTime.put(player.getUniqueId(), 0);

            if (this.afk.get(player.getUniqueId()).equals(player.getLocation())) {
                this.afkTime.put(player.getUniqueId(), Integer.valueOf(this.afkTime.get(player.getUniqueId()) + 1));

                if (this.afkTime.get(player.getUniqueId()) == 300) {
                    this.afk.remove(player.getUniqueId());
                    this.afkTime.remove(player.getUniqueId());

                    player.kickPlayer(MineChallenges.getInstance().prefix+"§cDu wurdest wegen Inaktivität gekickt!");
                }
            } else {
                this.afk.put(player.getUniqueId(), player.getLocation());
                this.afkTime.put(player.getUniqueId(), 0);
            }
        }));

        if (Bukkit.getOnlinePlayers().size() <= 0) {
            if (this.serverUnused == 180) Bukkit.shutdown();
            else this.serverUnused++;
        } else this.serverUnused = 0;

        if (this.timerPaused) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                this.pausedLocation.put(player.getUniqueId(), player.getLocation().clone());

                String message = "§7Verwende §e/timer start§7, um weiter zu spielen";
                IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \""+message+"\"}");
                PacketPlayOutChat bar = new PacketPlayOutChat(icbc, ChatMessageType.GAME_INFO);
                ((CraftPlayer)player).getHandle().playerConnection.sendPacket(bar);

                for (Entity entity : player.getNearbyEntities(20, 20, 20)) {
                    if (entity == null) continue;
                    if (entity.isDead()) continue;
                    if (entity instanceof Player) continue;
                    try {
                        LivingEntity livingEntity = (LivingEntity) entity;

                        if (!livingEntity.hasPotionEffect(PotionEffectType.SLOW)) {
                            livingEntity.addPotionEffect(new PotionEffect(PotionEffectType.SLOW,
                                    Integer.MAX_VALUE, 10, false, false));
                        }
                    } catch (ClassCastException e) { }
                }

                for (Player players : Bukkit.getOnlinePlayers())
                    players.playEffect(player.getLocation(), Effect.ENDER_SIGNAL, 3);

            }
            return;
        }

        Bukkit.getScheduler().runTask(MineChallenges.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(player -> {
            if (this.pausedLocation.containsKey(player.getUniqueId())) this.pausedLocation.remove(player.getUniqueId());

            int seconds = this.timer;
            int day = (int) TimeUnit.SECONDS.toDays(seconds);
            long hh = TimeUnit.SECONDS.toHours(seconds) - (day *24);
            long mm = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
            long ss = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);

            String message = "§7Timer§8: §e";
            if (hh == 0 && mm == 0) message += ss+(ss > 1 ? " Sekunden" : " Sekunde");
            else if (hh == 0) message += mm+(mm > 1 ? " Minuten " : " Minute ")+ss+(ss > 1 ? " Sekunden" : " Sekunde");
            else message += hh+(hh > 1 ? " Stunden" : " Stunde");

            IChatBaseComponent icbc = IChatBaseComponent.ChatSerializer.a("{\"text\": \""+message+"\"}");
            PacketPlayOutChat bar = new PacketPlayOutChat(icbc, ChatMessageType.GAME_INFO);
            ((CraftPlayer)player).getHandle().playerConnection.sendPacket(bar);

            Bukkit.getScheduler().runTask(MineChallenges.getInstance(), ()
                    -> player.getNearbyEntities(20, 20, 20).forEach(entity -> {
                if (entity == null) return;
                if (entity.isDead()) return;
                try {
                    LivingEntity livingEntity = (LivingEntity) entity;

                    if (livingEntity.hasPotionEffect(PotionEffectType.SLOW)) {
                        if (livingEntity.getPotionEffect(PotionEffectType.SLOW).getAmplifier() == 10) {
                            livingEntity.removePotionEffect(PotionEffectType.SLOW);
                        }
                    }
                } catch (ClassCastException e) { }
            }));
        }));

        this.timer++;

        MineChallenges.executorService.execute(() -> {
            try {
                MineChallenges.getInstance().config.set("timer", this.timer);
                MineChallenges.getInstance().config.save(MineChallenges.getInstance().configFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public boolean isTimerPaused() {
        return timerPaused;
    }

    public boolean isPlayerDead(String String) {
        return this.deadPlayers.contains(String);
    }

    public void addDeadPlayers(String String) {
        if (!this.deadPlayers.contains(String)) this.deadPlayers.add(String);
    }

    public void removeDeadPlayers(String String) {
        this.deadPlayers.remove(String);
    }

    public void setDeadPlayers(List<String> deadPlayers) {
        this.deadPlayers = deadPlayers;
    }

    public void setCoords(HashMap<String, String> coords) {
        this.coords = coords;
    }

    public void setTimer(Integer timer) {
        this.timer = timer;
    }

    public void setTimerPaused(boolean timerPaused) {
        this.timerPaused = timerPaused;
    }

    public List<String> getDeadPlayers() {
        return deadPlayers;
    }

    public HashMap<String, String> getCoords() {
        return coords;
    }

    public Integer getTimer() {
        return timer;
    }

}

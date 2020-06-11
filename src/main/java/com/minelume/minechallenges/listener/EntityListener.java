package com.minelume.minechallenges.listener;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.CorePlayer;
import com.minelume.minechallenges.gameobjects.ChallengeGamerule;
import com.minelume.minechallenges.gameobjects.ChallengeScenario;
import com.minelume.minechallenges.utils.MessageTranslator;
import com.mongodb.client.model.Filters;
import de.dytanic.cloudnet.common.document.gson.JsonDocument;
import de.dytanic.cloudnet.wrapper.Wrapper;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class EntityListener implements Listener {

    private MineChallenges plugin;
    private ArrayList<UUID> damageDelay;

    public EntityListener(MineChallenges plugin) {
        this.plugin = plugin;
        this.damageDelay = new ArrayList<>();
    }

    @EventHandler
    public void handleEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();

        if (entity instanceof Player) {
            Player player = (Player) entity;

            if (this.plugin.serverInstance.timerPaused) {
                event.setCancelled(true);
                return;
            }

            if (ChallengeScenario.NO_FALLDAMAGE.isActive()) {
                if (event.getCause().equals(EntityDamageEvent.DamageCause.FALL)) {
                    if (!player.isDead()) {
                        player.setHealth(0);
                        player.sendMessage(this.plugin.prefix+"§cDu darfst kein Fallschaden bekommen!");

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
            }

            if (this.damageDelay.contains(player.getUniqueId())) return;
            this.damageDelay.add(player.getUniqueId());
            Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, ()
                    -> this.damageDelay.remove(player.getUniqueId()), 5L);

            if (ChallengeGamerule.SPLITTED_PLAYER_DAMAGE.isActive()) {
                Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.getOnlinePlayers().forEach(players -> {
                    if (!players.getUniqueId().equals(player.getUniqueId())) {
                        players.damage(event.getDamage());
                    }
                }));
            }


            if (ChallengeGamerule.SPLITTED_PLAYER_DAMAGE.isActive()) {
                Bukkit.getScheduler().runTask(this.plugin, () -> Bukkit.getOnlinePlayers().forEach(players -> {
                    if (!players.getUniqueId().equals(player.getUniqueId())) {
                        players.sendMessage(this.plugin.prefix+"§7Du hast §eSchaden §7durch §e"+
                                player.getName()+"§7 erhalten!");
                    }

                    players.sendMessage(this.plugin.prefix+"§e"+player.getName()+"§7 erhielt §e"+event.getDamage()+
                            " Herzen §7schaden durch §e"+ MessageTranslator.translateDamage(event.getCause())+"§7!");
                }));
            } else {
                if (ChallengeGamerule.DAMAGE_SHOWN_IN_CHAT.isActive()) {
                    Bukkit.broadcastMessage(this.plugin.prefix+"§e"+player.getName()+"§7 erhielt §e"+event.getDamage()+
                            " Herzen §7schaden durch §e"+ MessageTranslator.translateDamage(event.getCause())+"§7!");
                }
            }
        }
    }

    @EventHandler
    public void handleEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        Entity damager = event.getDamager();
    }

    @EventHandler
    public void handleDeath(EntityDeathEvent event) throws IOException {
        LivingEntity entity = event.getEntity();

        if (entity.getType().equals(EntityType.ENDER_DRAGON)) {
            int seconds = this.plugin.serverInstance.timer;
            int day = (int) TimeUnit.SECONDS.toDays(seconds);
            long hh = TimeUnit.SECONDS.toHours(seconds) - (day *24);
            long mm = TimeUnit.SECONDS.toMinutes(seconds) - (TimeUnit.SECONDS.toHours(seconds)* 60);
            long ss = TimeUnit.SECONDS.toSeconds(seconds) - (TimeUnit.SECONDS.toMinutes(seconds) *60);

            Bukkit.broadcastMessage(this.plugin.prefix+"§aHerlichen Glückwunsch! §7Du hast Minecraft durchgespielt!");
            Bukkit.broadcastMessage(this.plugin.prefix+"§7Spielzeit§8: §e"+hh+" §7Stunden §e"+
                    mm+" §7Minuten §e"+ss+" §7Sekunden");
            Bukkit.broadcastMessage(this.plugin.prefix+"§7Beende die §9Challenge§7, in dem du ins End-Portal springst!");

            this.plugin.config.set("killedDragon", true);
            this.plugin.config.save(this.plugin.configFile);
            this.plugin.serverInstance.bukkitTask.cancel();
            return;
        }

        if (ChallengeScenario.CUT_CLEAN.isActive()) {
            if (event.getEntityType().equals(EntityType.PIG)) {
                event.getDrops().clear();
                Bukkit.getWorld("world").dropItem(event.getEntity().getLocation()
                        , new ItemStack(Material.COOKED_PORKCHOP, 3));
            } else if (event.getEntityType().equals(EntityType.COW)) {
                event.getDrops().clear();
                Bukkit.getWorld("world").dropItem(event.getEntity().getLocation()
                        , new ItemStack(Material.COOKED_BEEF, 3));
                Bukkit.getWorld("world").dropItem(event.getEntity().getLocation()
                        , new ItemStack(Material.LEATHER, 1));
            } else if (event.getEntityType().equals(EntityType.SHEEP)) {
                event.getDrops().clear();
                Bukkit.getWorld("world").dropItem(event.getEntity().getLocation()
                        , new ItemStack(Material.COOKED_MUTTON, 3));
            } else if (event.getEntityType().equals(EntityType.CHICKEN)) {
                event.getDrops().clear();
                Bukkit.getWorld("world").dropItem(event.getEntity().getLocation()
                        , new ItemStack(Material.COOKED_CHICKEN, 3));
                Bukkit.getWorld("world").dropItem(event.getEntity().getLocation()
                        , new ItemStack(Material.FEATHER, 1));
            } else if (event.getEntityType().equals(EntityType.RABBIT)) {
                event.getDrops().clear();
                Bukkit.getWorld("world").dropItem(event.getEntity().getLocation()
                        , new ItemStack(Material.COOKED_RABBIT, 2));
            }
        }
    }

    private List<UUID> delayHandlePortal = new ArrayList<>();

    @EventHandler
    public void handlePortal(EntityPortalEnterEvent event) throws IOException {
        if (event.getEntity() instanceof Player) {

            Player player = (Player) event.getEntity();

            if (event.getLocation().getWorld().getEnvironment().equals(World.Environment.THE_END)) {
                if (this.delayHandlePortal.contains(player.getUniqueId())) return;
                this.delayHandlePortal.add(player.getUniqueId());

                Bukkit.getScheduler().runTaskLaterAsynchronously(this.plugin, () ->
                        this.delayHandlePortal.remove(player.getUniqueId()), 5*20L);

                if (this.plugin.serverOwner.equals(player.getUniqueId())) {
                    for (Player players : Bukkit.getOnlinePlayers()) {
                        players.sendMessage(this.plugin.prefix+"§aDu hast die Challenge geschafft!");
                        players.kickPlayer(this.plugin.prefix+"§7Du erhälst §e750 Coins§7!");
                        CorePlayer.findByUUID(players.getUniqueId(), corePlayer -> corePlayer.addCoins(750));
                    }

                    MineChallenges.driver.getMessenger().sendChannelMessage("challenges",
                            "delete "+ Wrapper.getInstance().getServiceId().getName(), new JsonDocument());
                    this.plugin.executorService.submit(() -> this.plugin.mongoDB.getCollection().
                            deleteOne(Filters.eq("uuid", this.plugin.serverOwner.toString())));

                    Bukkit.getScheduler().runTaskLater(this.plugin, () -> Bukkit.shutdown(), 20*3);
                } else {
                    player.sendMessage(this.plugin.prefix+"§aDu hast die Challenge geschafft!");
                    player.kickPlayer(this.plugin.prefix+"§7Du erhälst §e750 Coins§7!");
                    CorePlayer.findByUUID(player.getUniqueId(), corePlayer -> corePlayer.addCoins(750));
                }
                return;
            }
        }
    }

    @EventHandler
    public void handleCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getEntity() instanceof Monster) {
            event.setCancelled(!ChallengeGamerule.DO_MOB_SPAWNING.isActive());
        }
    }

    @EventHandler
    public void handleRegainHealth(EntityRegainHealthEvent event) {
        event.setCancelled(ChallengeScenario.NO_REGENERATION.isActive());
    }
}

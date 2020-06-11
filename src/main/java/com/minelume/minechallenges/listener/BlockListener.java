package com.minelume.minechallenges.listener;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.gameobjects.ChallengeGamerule;
import com.minelume.minechallenges.gameobjects.ChallengeScenario;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class BlockListener implements Listener {

    private MineChallenges plugin;
    private ArrayList<Material> materials;

    public BlockListener(MineChallenges plugin) {
        this.plugin = plugin;
        this.materials = new ArrayList<>();
    }

    @EventHandler
    public void handlePlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(this.plugin.serverInstance.isTimerPaused());

        if (ChallengeScenario.NO_BLOCK_PLACE.isActive()) {
            if (event.getBlock().getType().equals(Material.LAVA) ||
                    event.getBlock().getType().equals(Material.FIRE) ||
                    event.getBlock().getType().equals(Material.WATER) ||
                    event.getBlock().getType().name().contains("STRIPPED") ||
                    event.getBlock().getType().equals(Material.FARMLAND) ||
                    event.getBlock().getType().equals(Material.GRASS_PATH) ||
                    event.getBlock().getType().equals(Material.END_PORTAL_FRAME) ||
                    event.getBlock().getType().equals(Material.DRAGON_EGG)) return;

            if (!player.isDead()) {
                player.setHealth(0);
                player.sendMessage(this.plugin.prefix+"§cDu darfst keine Blöcke setzen!");

                if (ChallengeGamerule.SPLITTED_PLAYER_DAMAGE.isActive()) {
                    Bukkit.getOnlinePlayers().forEach(players -> {
                        if (!players.isDead()) {
                            players.setHealth(0);
                            players.sendMessage(this.plugin.prefix+"§cDu bist wegen §e"+player.getName()+"§c gestorben!");
                            return;
                        }
                    });
                }
            }

            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void handleBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        event.setCancelled(this.plugin.serverInstance.isTimerPaused());

        if (!this.plugin.serverInstance.isTimerPaused()) {
            if (ChallengeScenario.RANDOM_ITEM_DROP.isActive()) {
                if (!this.plugin.serverInstance.randomMaterial.containsKey(event.getBlock().getType())) {
                    List<Material> mats = Arrays.asList(Material.values());
                    mats.remove(Material.AIR);

                    this.plugin.serverInstance.randomMaterial.put(event.getBlock().getType(),
                            mats.get(new Random().nextInt(mats.size())));
                }
                Material material = this.plugin.serverInstance.randomMaterial.get(event.getBlock().getType());

                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    World world = event.getPlayer().getWorld();

                    world.dropItem(event.getBlock().getLocation().clone(),
                            new ItemStack(material, 1));
                }, 2L);
            }
        }

        if (ChallengeScenario.NO_BLOCK_BREAK.isActive()) {
            if (!player.isDead()) {
                player.setHealth(0);
                player.sendMessage(this.plugin.prefix+"§cDu darfst keine Blöcke abbauen!");

                if (ChallengeGamerule.SPLITTED_PLAYER_DAMAGE.isActive()) {
                    Bukkit.getOnlinePlayers().forEach(players -> {
                        if (!players.isDead()) {
                            players.setHealth(0);
                            players.sendMessage(this.plugin.prefix+"§cDu bist wegen §e"+player.getName()+"§c gestorben!");
                            return;
                        }
                    });
                }
            }

            event.setCancelled(true);
            return;
        }
        
        if (ChallengeScenario.CUT_CLEAN.isActive()) {
            int amount = 1;
            
            if (event.getBlock().getType().equals(Material.IRON_ORE)) {
                event.getBlock().setType(Material.AIR);
                event.setCancelled(true);

                Bukkit.getWorld("map").spawn(event.getBlock().getLocation().clone().add(0.5,-0.5,0.5), ExperienceOrb.class)
                        .setExperience(5);
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> event.getBlock().getWorld().dropItem
                        (event.getBlock().getLocation().clone().add(0.5,-0.5,0.5), new ItemStack(Material.IRON_INGOT, amount)), 5L);
            } else if (event.getBlock().getType().equals(Material.GOLD_ORE)) {
                event.getBlock().setType(Material.AIR);
                event.setCancelled(true);

                Bukkit.getWorld("map").spawn(event.getBlock().getLocation().clone().add(0.5,-0.5,0.5), ExperienceOrb.class)
                        .setExperience(5);
                Bukkit.getScheduler().runTaskLater(this.plugin, () -> event.getBlock().getWorld().dropItem(event.getBlock().getLocation().clone().add(0.5,-0.5,0.5),
                        new ItemStack(Material.GOLD_INGOT, amount)), 5L);
            } else if (event.getBlock().getType().equals(Material.COAL_ORE)) {
                event.getBlock().setType(Material.AIR);
                event.setCancelled(true);

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    Bukkit.getWorld("map").spawn(event.getBlock().getLocation().clone().add(0.5,-0.5,0.5), ExperienceOrb.class)
                            .setExperience(event.getExpToDrop());
                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation().clone().add(0.5,-0.5,0.5), new ItemStack(Material.TORCH, amount));
                }, 5L);
            } else if (event.getBlock().getType().equals(Material.DIAMOND_ORE)) {
                event.getBlock().setType(Material.AIR);
                event.setCancelled(true);

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    Bukkit.getWorld("map").spawn(event.getBlock().getLocation().clone().add(0.5,-0.5,0.5), ExperienceOrb.class)
                            .setExperience(event.getExpToDrop());
                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation().clone().add(0.5,-0.5,0.5), new ItemStack(Material.DIAMOND, amount));
                }, 5L);
            } else if (event.getBlock().getType().equals(Material.EMERALD_ORE)) {
                event.getBlock().setType(Material.AIR);
                event.setCancelled(true);

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    Bukkit.getWorld("map").spawn(event.getBlock().getLocation().clone().add(0.5,-0.5,0.5), ExperienceOrb.class)
                            .setExperience(event.getExpToDrop());
                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation().clone().add(0.5,-0.5,0.5), new ItemStack(Material.EMERALD, amount));
                }, 5L);
            } else if (event.getBlock().getType().equals(Material.LAPIS_ORE)) {
                event.getBlock().setType(Material.AIR);
                event.setCancelled(true);

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    Bukkit.getWorld("map").spawn(event.getBlock().getLocation().clone().add(0.5,-0.5,0.5), ExperienceOrb.class)
                            .setExperience(event.getExpToDrop());
                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation().clone().add(0.5,-0.5,0.5),
                            new ItemStack(Material.LAPIS_LAZULI, 1));
                }, 5L);
            } else if (event.getBlock().getType().equals(Material.REDSTONE_ORE)) {
                event.getBlock().setType(Material.AIR);
                event.setCancelled(true);

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    Bukkit.getWorld("map").spawn(event.getBlock().getLocation().clone().add(0.5, -0.5, 0.5), ExperienceOrb.class)
                            .setExperience(event.getExpToDrop());
                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation().clone().add(0.5, -0.5, 0.5), new ItemStack(Material.REDSTONE, amount));
                }, 5L);
            } else if (event.getBlock().getType().equals(Material.GRAVEL)) {
                event.setCancelled(true);
                event.getBlock().setType(Material.AIR);

                Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
                    event.getBlock().getWorld().dropItem(event.getBlock().getLocation().clone()
                            .add(0.5, -0.5, 0.5), new ItemStack(Material.FLINT, 1));
                }, 5L);
            }
        }
    }
}

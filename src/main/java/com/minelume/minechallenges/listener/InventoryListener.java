package com.minelume.minechallenges.listener;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.gameobjects.ChallengeGamerule;
import com.minelume.minechallenges.gameobjects.ChallengeScenario;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

public class InventoryListener implements Listener {

    private MineChallenges plugin;

    public InventoryListener(MineChallenges plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void handleClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();

            if (event.getClickedInventory() != null) {
                if (event.getClickedInventory().getType().equals(InventoryType.MERCHANT)) {
                    if (event.getCurrentItem() != null) {
                        if (!event.getCurrentItem().getType().equals(Material.AIR)) {
                            if (event.getSlotType().equals(InventoryType.SlotType.RESULT)) {
                                if (ChallengeScenario.ALLOW_VILLAGER_TRADE.isActive()) {
                                    player.setHealth(0);
                                    player.sendMessage(this.plugin.prefix+"§cDu kannst kannst mit keinem Villager handeln!");

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
                }
            }
        }
    }
}

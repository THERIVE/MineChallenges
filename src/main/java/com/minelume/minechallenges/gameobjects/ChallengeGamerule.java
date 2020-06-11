package com.minelume.minechallenges.gameobjects;

import com.minelume.minechallenges.core.instances.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public enum ChallengeGamerule {

    DAMAGE_SHOWN_IN_CHAT("Schaden im Chat anzeigen", false, 1,
            ItemStackBuilder.create(Material.LEGACY_SIGN, 1, 0, "§bSchaden im Chat anzeigen",
                    Arrays.asList("§7Bekommt ein Spieler Schaden,", "§7wird die Menge und der Grund",
                            "§7im Chat angezeigt."))),
    SPLITTED_PLAYER_DAMAGE("Geteilter Schaden", false, 1,
            ItemStackBuilder.create(Material.SUGAR, 1, 0, "§bGeteilter Schaden",
                    Arrays.asList("§7Bekommt ein Spieler Schaden, bekommt", "§7jeder andere Spieler die selbe Menge",
                            "§7an Schaden."))),
    DO_MOB_LOOT("Monster droppen Items", true, 1,
            ItemStackBuilder.create(Material.INK_SAC, 1, 0, "§bMonster droppen Items",
                    Arrays.asList("§7Monster droppen Items."))),
    DO_MOB_SPAWNING("Monster Spawnen", true, 1,
            ItemStackBuilder.create(Material.ZOMBIE_SPAWN_EGG, 1, 0, "§bMonster spawnen",
                    Arrays.asList("§7Es können Monster spawnen."))),
    DO_MOB_GRIEFING("Monster können die Welt zerstören", true, 1,
            ItemStackBuilder.create(Material.CREEPER_HEAD, 1, 4, "§bMonster können die Welt zerstören",
                    Arrays.asList("§7Monster wie Creeper können", "§7Blöcke kaputt machen.")));

    private String name;
    private boolean active;
    private int page;
    private ItemStack itemStack;

    ChallengeGamerule(String name, boolean active, int page, ItemStack itemStack) {
        this.name = name;
        this.active = active;
        this.page = page;
        this.itemStack = itemStack;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public int getPage() {
        return page;
    }

    public ItemStack getItemStack(ChallengeServer challengeServer) {
        ItemStack item = itemStack.clone();
        ItemMeta itemMeta = item.getItemMeta();
        List<String> lore = itemMeta.getLore();
        lore.add("");
        lore.add("§7Status§8: "+(challengeServer.getChallengeGamerules().get(this.name())
                ? "§aAktiviert" : "§cDeaktiviert"));

        itemMeta.setLore(lore);
        item.setItemMeta(itemMeta);

        return item;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }
}

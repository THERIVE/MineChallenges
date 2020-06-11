package com.minelume.minechallenges.gameobjects;

import com.minelume.minechallenges.core.instances.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public enum ChallengeScenario {

    NO_NATURAL_REGENERATION("Keine natürliche Regeneration", false, 1,
            ItemStackBuilder.create(Material.GOLDEN_APPLE,
                    1, 1, "§bKeine natürliche Regeneration",
                    Arrays.asList("§7Natürliche Regeneration ist deaktiviert."))),

    NO_REGENERATION("Keine Regeneration", false, 1,
            ItemStackBuilder.create(Material.MAGMA_CREAM, 1, 0, "§bKeine Regeneration",
                    Arrays.asList("§7Es gibt keine Möglichkeit zu", "§7regenerieren."))),

    RESPAWN("Respawnen verbieten", false, 1,
            ItemStackBuilder.create(Material.POTION, 1, 8197, "§bRespawnen verbieten",
                    Arrays.asList("§7Wenn ein Spieler stirbt, kann er nicht", "§7mehr wiederbelebt werden."))),

    //RANDOM_CRAFTING("Gecraftete Items sind zufällig", false, 1,
    //        ItemStackBuilder.create(Material.RABBIT_FOOT, 1, 0, "§bGecraftete Items sind zufällig",
    //                Arrays.asList("§7Craftet man etwas, kommt nicht das", "§7geplante Item raus, sondern",
    //                        "§7ein zufälliges Item."))),

    RANDOM_ITEM_DROP("Beim abbauen zufällige Items droppen lassen", false, 1,
            ItemStackBuilder.create(Material.POPPY, 1, 0, "§bBeim abbauen zufällige Items droppen lassen",
                    Arrays.asList("§7Baut man einen Block ab,", "§7dropt ein zufälliges Item."))),

    ALLOW_CRAFTING_TABLE("Craften verbieten", false, 1,
            ItemStackBuilder.create(Material.CRAFTING_TABLE, 1, 0, "§bCraften verbieten",
                    Arrays.asList("§7Wird im Crafting Table oder im", "§7Inventar gecraftet, stirbt man."))),

    //BACKPACK("Backpack einstellen", false, 1,
    //        ItemStackBuilder.create(Material.ENDER_CHEST, 1, 0, "§bBackpack einstellen",
    //                Arrays.asList("§7Mit /backpack öffnet sich ein zusätzliches", "§7Inventar, welches für alle Spieler",
    //                        "§7benutzbar ist."))),

    //MLG("MLG's müssen erledigt werden", false, 1,
    //       ItemStackBuilder.create(Material.WATER_BUCKET, 1, 0, "§bMLG's müssen erledigt werden",
    //                Arrays.asList("§7Alle paar Minuten muss ein MLG erledigt", "§7werden. Schafft man den MLG nicht,",
    //                       "§7stirbt man."))),

    ALLOW_VILLAGER_TRADE("Traden verbieten", false, 1,
            ItemStackBuilder.create(Material.ZOMBIE_SPAWN_EGG, 1, 0, "§bTraden verbieten",
                    Arrays.asList("§7Man stirbt, wenn man mit einem",
                            "§7Händler oder Dorfbewohner handelt."))),

    FLOOR_IS_LAVA("Der Boden wird zu Lava", false, 1,
            ItemStackBuilder.create(Material.LAVA_BUCKET, 1, 0, "§bDer Boden wird zu Lava",
                    Arrays.asList("§7Unter dir wird der Boden zu Lava."))),

    //LIMIT_INVENTORY("Inventar begrenzen", false, 1,
    //        ItemStackBuilder.create(Material.BARRIER,
    //                1, 0, "§bInventar begrenzen",
    //                Arrays.asList("§7Das Inventar wird auf 9 Slots begrenzt."))),

    NO_FALLDAMAGE("Fallschaden verbieten", false, 1,
            ItemStackBuilder.create(Material.GOLDEN_BOOTS,
                    1, 0, "§bFallschaden verbieten",
                    Arrays.asList("§7Bekommt man Fallschaden", "§7ist man sofort tot."))),

    NO_BLOCK_PLACE("Blöcke platzieren verbieten", false, 1,
            ItemStackBuilder.create(Material.GRASS,
                    1, 0, "§bBlöcke platzieren verbieten",
                    Arrays.asList("§7Man stirbt, wenn man einen Block platziert"))),

    NO_BLOCK_BREAK("Blöcke zerstören verbieten", false, 1,
            ItemStackBuilder.create(Material.STONE,
                    1, 0, "§bBlöcke zerstören verbieten",
                    Arrays.asList("§7Man stirbt, wenn man einen Block abbaut"))),

    NO_EXPERIENCE("Erfahrung einsammeln verbieten", false, 1,
            ItemStackBuilder.create(Material.EXPERIENCE_BOTTLE,
                    1, 0, "§bErfahrung einsammeln verbieten",
                    Arrays.asList("§7Sammelt man einen Erfahrungspunkt ein,", "§7stirbt man."))),

    //LIMIT_HP("Leben begrenzen", false, 1,
    //        ItemStackBuilder.create(Material.CHICKEN_SPAWN_EGG, 1, 0, "§bLeben begrenzen",
    //               Arrays.asList("§7Ist diese Challenge aktiviert,", "§7wird das Leben eines Spielers",
    //                        "§7auf ein halbes Herz begrenzt."))),

    CUT_CLEAN("Cutclean aktivieren", false, 1,
            ItemStackBuilder.create(Material.DIAMOND_PICKAXE,
                    1, 0, "§bCutClean aktivieren",
                    Arrays.asList("§7Erze werden direkt geschmolzen")));

    private String name;
    private boolean active;
    private int page;
    private ItemStack itemStack;

    ChallengeScenario(String name, boolean active, int page, ItemStack itemStack) {
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
        lore.add("§7Status§8: "+(challengeServer.getChallengeScenarios().get(this.name())
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

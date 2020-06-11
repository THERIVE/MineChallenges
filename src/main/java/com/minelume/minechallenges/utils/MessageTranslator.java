package com.minelume.minechallenges.utils;

import org.bukkit.event.entity.EntityDamageEvent;

public class MessageTranslator {

    public static String translateDamage(EntityDamageEvent.DamageCause damageCause) {
        if (damageCause.equals(EntityDamageEvent.DamageCause.CONTACT))
            return "Block- oder Mob/Spielerschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK))
            return "Mob/Spielerschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK))
            return "Mob/Spielerschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.PROJECTILE))
            return "Projektilschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.SUFFOCATION))
            return "Erstickungsschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.FALL))
            return "Fallschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.FIRE))
            return "Feuerschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.FIRE_TICK))
            return "Feuerschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.MELTING))
            return "Schmelzen";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.LAVA))
            return "Lavaschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.DROWNING))
            return "Ertrinken";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION))
            return "Explosionsschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION))
            return "Explosionsschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.VOID))
            return "Voidschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.LIGHTNING))
            return "Blitzschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.SUICIDE))
            return "Selbstmord";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.STARVATION))
            return "Hungerschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.POISON))
            return "Giftschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.MAGIC))
            return "Magieschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.WITHER))
            return "Witherschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.FALLING_BLOCK))
            return "Erstickungsschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.THORNS))
            return "Dornenschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.DRAGON_BREATH))
            return "Drachenatemschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.CUSTOM))
            return "Benutzerdefinierter Schaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.FLY_INTO_WALL))
            return "Erstickungsschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.HOT_FLOOR))
            return "Magmablockschaden";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.CRAMMING))
            return "Erdrückung";
        else if (damageCause.equals(EntityDamageEvent.DamageCause.DRYOUT))
            return "Trockenschaden";
        return "NULL";
    }
}

package com.minelume.minechallenges.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class AsyncPlayerMoveEvent extends Event {

    public static HandlerList handlers = new HandlerList();

    private Player player;
    private Location location;

    public AsyncPlayerMoveEvent(Player player, Location location) {
        this.player = player;
        this.location = location;
    }

    public Player getPlayer() {
        return player;
    }

    public Location getLocation() {
        return location.clone();
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
}

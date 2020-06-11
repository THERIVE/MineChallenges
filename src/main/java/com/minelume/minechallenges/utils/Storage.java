package com.minelume.minechallenges.utils;

import com.minelume.minechallenges.core.CorePlayer;

import java.util.HashMap;
import java.util.UUID;

public class Storage {

    private HashMap<UUID, CorePlayer> corePlayers;

    public Storage() {
        this.corePlayers = new HashMap<>();
    }

    public HashMap<UUID, CorePlayer> getCorePlayers() {
        return corePlayers;
    }
}

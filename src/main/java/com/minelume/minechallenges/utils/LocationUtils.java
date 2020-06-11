package com.minelume.minechallenges.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtils {

    public static Location getLocationFromString(String location) {
        return new Location(Bukkit.getWorld(location.split(":")[0]),
                Double.parseDouble(location.split(":")[1]),
                Double.parseDouble(location.split(":")[2]),
                Double.parseDouble(location.split(":")[3]),
                (float) Double.parseDouble(location.split(":")[4]),
                (float) Double.parseDouble(location.split(":")[5]));
    }

    public static String locationToString(Location location) {
        return location.getWorld().getName()+":"+location.getX()+":"+location.getY()+":"+location.getZ()+
                ":"+location.getYaw()+":"+location.getPitch();
    }
}

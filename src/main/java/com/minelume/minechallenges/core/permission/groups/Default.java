package com.minelume.minechallenges.core.permission.groups;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.Permission;

import java.util.Collection;

public class Default implements IPermissionGroup {


    @Override
    public String getName() {
        return "Spieler";
    }

    @Override
    public String getColor() {
        return "§7";
    }

    @Override
    public String getScoreboardTag() {
        return "§7";
    }

    @Override
    public String getScoreboardSortId() {
        return "0012";
    }

    @Override
    public de.dytanic.cloudnet.driver.permission.IPermissionGroup getCloudGroup() {
        return MineChallenges.driver.getPermissionManagement().getGroup("default");
    }

    @Override
    public Collection<Permission> getPermissions() {
        return this.getCloudGroup().getPermissions();
    }
}

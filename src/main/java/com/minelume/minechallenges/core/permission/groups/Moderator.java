package com.minelume.minechallenges.core.permission.groups;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.Permission;

import java.util.Collection;

public class Moderator implements IPermissionGroup {


    @Override
    public String getName() {
        return "Moderator";
    }

    @Override
    public String getColor() {
        return "§c";
    }

    @Override
    public String getScoreboardTag() {
        return "§cMod §8| §c";
    }

    @Override
    public String getScoreboardSortId() {
        return "0003";
    }

    @Override
    public de.dytanic.cloudnet.driver.permission.IPermissionGroup getCloudGroup() {
        return MineChallenges.driver.getPermissionManagement().getGroup("Moderator");
    }

    @Override
    public Collection<Permission> getPermissions() {
        return this.getCloudGroup().getPermissions();
    }
}

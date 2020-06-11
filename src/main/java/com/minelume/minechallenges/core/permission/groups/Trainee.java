package com.minelume.minechallenges.core.permission.groups;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.Permission;

import java.util.Collection;

public class Trainee implements IPermissionGroup {


    @Override
    public String getName() {
        return "Trainee";
    }

    @Override
    public String getColor() {
        return "§3";
    }

    @Override
    public String getScoreboardTag() {
        return "§3T §8| §3";
    }

    @Override
    public String getScoreboardSortId() {
        return "0008";
    }

    @Override
    public de.dytanic.cloudnet.driver.permission.IPermissionGroup getCloudGroup() {
        return MineChallenges.driver.getPermissionManagement().getGroup("Trainee");
    }

    @Override
    public Collection<Permission> getPermissions() {
        return this.getCloudGroup().getPermissions();
    }
}

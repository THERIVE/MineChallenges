package com.minelume.minechallenges.core.permission.groups;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.Permission;

import java.util.Collection;

public class VIP implements IPermissionGroup {


    @Override
    public String getName() {
        return "VIP";
    }

    @Override
    public String getColor() {
        return "§5";
    }

    @Override
    public String getScoreboardTag() {
        return "§5VIP §8| §5";
    }

    @Override
    public String getScoreboardSortId() {
        return "0009";
    }

    @Override
    public de.dytanic.cloudnet.driver.permission.IPermissionGroup getCloudGroup() {
        return MineChallenges.driver.getPermissionManagement().getGroup("VIP");
    }

    @Override
    public Collection<Permission> getPermissions() {
        return this.getCloudGroup().getPermissions();
    }
}

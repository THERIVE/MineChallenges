package com.minelume.minechallenges.core.permission.groups;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.Permission;

import java.util.Collection;

public class Admin implements IPermissionGroup {

    @Override
    public String getName() {
        return "Admin";
    }

    @Override
    public String getColor() {
        return "§4";
    }

    @Override
    public String getScoreboardTag() {
        return "§4Admin §8| §4";
    }

    @Override
    public String getScoreboardSortId() {
        return "0001";
    }

    @Override
    public de.dytanic.cloudnet.driver.permission.IPermissionGroup getCloudGroup() {
        return MineChallenges.driver.getPermissionManagement().getGroup("Admin");
    }

    @Override
    public Collection<Permission> getPermissions() {
        return this.getCloudGroup().getPermissions();
    }
}

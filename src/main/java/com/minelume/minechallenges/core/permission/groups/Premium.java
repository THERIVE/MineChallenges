package com.minelume.minechallenges.core.permission.groups;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.Permission;

import java.util.Collection;

public class Premium implements IPermissionGroup {


    @Override
    public String getName() {
        return "Premium";
    }

    @Override
    public String getColor() {
        return "§6";
    }

    @Override
    public String getScoreboardTag() {
        return "§6";
    }

    @Override
    public String getScoreboardSortId() {
        return "0011";
    }

    @Override
    public de.dytanic.cloudnet.driver.permission.IPermissionGroup getCloudGroup() {
        return MineChallenges.driver.getPermissionManagement().getGroup("Premium");
    }

    @Override
    public Collection<Permission> getPermissions() {
        return this.getCloudGroup().getPermissions();
    }
}

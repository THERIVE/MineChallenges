package com.minelume.minechallenges.core.permission.groups;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.Permission;

import java.util.Collection;

public class Content implements IPermissionGroup {


    @Override
    public String getName() {
        return "Content";
    }

    @Override
    public String getColor() {
        return "§2";
    }

    @Override
    public String getScoreboardTag() {
        return "§2Content §8| §2";
    }

    @Override
    public String getScoreboardSortId() {
        return "0006";
    }

    @Override
    public de.dytanic.cloudnet.driver.permission.IPermissionGroup getCloudGroup() {
        return MineChallenges.driver.getPermissionManagement().getGroup("Content");
    }

    @Override
    public Collection<Permission> getPermissions() {
        return this.getCloudGroup().getPermissions();
    }
}

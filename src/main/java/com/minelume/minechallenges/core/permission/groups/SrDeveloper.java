package com.minelume.minechallenges.core.permission.groups;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.Permission;

import java.util.Collection;

public class SrDeveloper implements IPermissionGroup {


    @Override
    public String getName() {
        return "SrDeveloper";
    }

    @Override
    public String getColor() {
        return "§3";
    }

    @Override
    public String getScoreboardTag() {
        return "§3Sr. Dev §8| §3";
    }

    @Override
    public String getScoreboardSortId() {
        return "0002";
    }

    @Override
    public de.dytanic.cloudnet.driver.permission.IPermissionGroup getCloudGroup() {
        return MineChallenges.driver.getPermissionManagement().getGroup("SrDeveloper");
    }

    @Override
    public Collection<Permission> getPermissions() {
        return this.getCloudGroup().getPermissions();
    }
}

package com.minelume.minechallenges.core.permission.groups;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.Permission;

import java.util.Collection;

public class SrContent implements IPermissionGroup {


    @Override
    public String getName() {
        return "SrContent";
    }

    @Override
    public String getColor() {
        return "§2";
    }

    @Override
    public String getScoreboardTag() {
        return "§2Sr. Con §8| §2";
    }

    @Override
    public String getScoreboardSortId() {
        return "0005";
    }

    @Override
    public de.dytanic.cloudnet.driver.permission.IPermissionGroup getCloudGroup() {
        return MineChallenges.driver.getPermissionManagement().getGroup("SrContent");
    }

    @Override
    public Collection<Permission> getPermissions() {
        return this.getCloudGroup().getPermissions();
    }
}

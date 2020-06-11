package com.minelume.minechallenges.core.permission.groups;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.Permission;

import java.util.Collection;

public class Supporter implements IPermissionGroup {


    @Override
    public String getName() {
        return "Supporter";
    }

    @Override
    public String getColor() {
        return "§a";
    }

    @Override
    public String getScoreboardTag() {
        return "§aSup §8| §a";
    }

    @Override
    public String getScoreboardSortId() {
        return "0007";
    }

    @Override
    public de.dytanic.cloudnet.driver.permission.IPermissionGroup getCloudGroup() {
        return MineChallenges.driver.getPermissionManagement().getGroup("Supporter");
    }

    @Override
    public Collection<Permission> getPermissions() {
        return this.getCloudGroup().getPermissions();
    }
}

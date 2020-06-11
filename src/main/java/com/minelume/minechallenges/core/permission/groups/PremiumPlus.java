package com.minelume.minechallenges.core.permission.groups;

import com.minelume.minechallenges.MineChallenges;
import com.minelume.minechallenges.core.permission.IPermissionGroup;
import de.dytanic.cloudnet.driver.permission.Permission;

import java.util.Collection;

public class PremiumPlus implements IPermissionGroup {


    @Override
    public String getName() {
        return "Premium+";
    }

    @Override
    public String getColor() {
        return "§b";
    }

    @Override
    public String getScoreboardTag() {
        return "§b";
    }

    @Override
    public String getScoreboardSortId() {
        return "0010";
    }

    @Override
    public de.dytanic.cloudnet.driver.permission.IPermissionGroup getCloudGroup() {
        return MineChallenges.driver.getPermissionManagement().getGroup("PremiumPlus");
    }

    @Override
    public Collection<Permission> getPermissions() {
        return this.getCloudGroup().getPermissions();
    }
}

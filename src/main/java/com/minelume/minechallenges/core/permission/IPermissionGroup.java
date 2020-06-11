package com.minelume.minechallenges.core.permission;


import de.dytanic.cloudnet.driver.permission.Permission;

import java.util.Collection;

public interface IPermissionGroup {

    String getName();
    String getColor();

    String getScoreboardTag();
    String getScoreboardSortId();

    de.dytanic.cloudnet.driver.permission.IPermissionGroup getCloudGroup();
    Collection<Permission> getPermissions();
}

package com.minelume.minechallenges.core.permission;


import de.dytanic.cloudnet.driver.CloudNetDriver;
import de.dytanic.cloudnet.driver.permission.IPermissionUser;
import de.dytanic.cloudnet.driver.permission.PermissionUserGroupInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PermissionPlayer {

    private UUID uuid;
    private IPermissionUser permissionEntity;
    private IPermissionGroup permissionGroup;

    public PermissionPlayer(UUID uuid) {
        this.uuid = uuid;
        this.permissionEntity = CloudNetDriver.getInstance().getPermissionManagement().getUser(uuid);

        try {
            List<PermissionUserGroupInfo> groups = new ArrayList<>();
            groups.addAll(this.permissionEntity.getGroups());

            String classPath = null;

            if (groups.size() > 0) {
                classPath = "com.minelume.minechallenges.core.permission.groups."+(groups.get(0).getGroup().contains("default")
                        ? "Default" : groups.get(0).getGroup());
            } else classPath = "com.minelume.minechallenges.core.permission.groups.Default";

            Class<?> c = Class.forName(classPath);
            this.permissionGroup = (IPermissionGroup) c.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public void addPermission(String permission) {
        this.permissionEntity.addPermission(permission);
    }
    public void removePermission(String permission) {
        this.permissionEntity.removePermission(permission);
    }

    public boolean hasPermission(String permission) {
        return this.permissionEntity.hasPermission(permission).asBoolean();
    }

    public void setGroup(String group) {
        this.permissionEntity.getGroups().clear();
        this.permissionEntity.addGroup(group);
    }

    public boolean inGroup(String group) {
        return this.permissionEntity.inGroup(group);
    }

    public void setPermissionGroup(IPermissionGroup permissionGroup) {
        this.permissionGroup = permissionGroup;
        this.setGroup(permissionGroup.getName());
    }

    public IPermissionGroup getPermissionGroup() {
        return this.permissionGroup;
    }
}

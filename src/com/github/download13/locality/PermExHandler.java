package com.github.download13.locality;

import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionManager;
import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;;

public class PermExHandler {
	private PermissionManager manager;
	
	public PermExHandler() {
		manager = PermissionsEx.getPermissionManager();
	}
	
	public String getPrefix(Player player) {
		PermissionUser user = manager.getUser(player);
		if(user == null) return "";
		return user.getPrefix();
	}
	
	public String getSuffix(Player player) {
		PermissionUser user = manager.getUser(player);
		if(user == null) return "";
		return user.getSuffix();
	}
}

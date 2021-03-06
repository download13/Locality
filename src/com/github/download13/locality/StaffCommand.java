package com.github.download13.locality;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffCommand implements CommandExecutor {
	private Locality plugin;
	private ChatColor chatColor;
	
	public StaffCommand(Locality plugin, ChatColor color) {
		this.plugin = plugin;
		this.chatColor = color;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) return true;
		if(!(sender instanceof Player)) return false;
		Player from = (Player) sender;
		
		String msg = Utils.JoinStrings(args, " ");
		
		Set<Player> receivers = new HashSet<Player>();
		for(Player player : plugin.getServer().getOnlinePlayers()) {
			if(player.hasPermission("locality.staff") || player.equals(from)) {
				receivers.add(player);
			}
		}
		
		String format = Utils.FormatString(chatColor, "[STAFF]", Utils.GetPrefixAndSuffix(plugin.getServer(), from));
		Utils.sendChatSkippingListener(plugin, plugin.getServer(), from, receivers, msg, format);
		
		return true;
	}
}

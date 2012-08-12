package com.github.download13.locality;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class hcmd implements CommandExecutor {
	private Locality plugin;
	
	public hcmd(Locality plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { // Whenever a user uses a command
		if(args.length == 0) return true;
		if(!(sender instanceof Player)) return true;
		Player from = (Player)sender; // Turn the CommandSender into a Player
		
		String msg = Utils.JoinStrings(args, " ");
		
		Set<Player> receivers = new HashSet<Player>();
		for(Player player : plugin.getServer().getOnlinePlayers()) {
			if(player.hasPermission("locality.helper") || player.equals(from)) { // TODO: Check this works
				receivers.add(player);
			}
		}
		
		Utils.sendChatSkippingAllListeners(plugin.getServer(), from, receivers, msg, ChatColor.DARK_PURPLE + "[HELP]<%1$s> %2$s");
		
		return true;
	}
}

package com.github.download13.locality;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class gcmd implements CommandExecutor {
	private Locality plugin;
	private Map<String, Integer> timeouts;
	private RateLimiter<Player> rateLimiter;
	
	public gcmd(Locality plugin, ChatColor color, Map<String, Integer> timeouts) {
		this.plugin = plugin;
		this.timeouts = timeouts;
		this.rateLimiter = new RateLimiter<Player>();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { // Whenever a user uses a command
		if(args.length == 0) return true;
		if(!(sender instanceof Player)) return true;
		Player from = (Player)sender; // Turn the CommandSender into a Player
		
		String msg = Utils.JoinStrings(args, " ");
		
		String userType = Utils.GetPlayerType(from);
		System.out.println(userType);
		System.out.println(timeouts.get(userType));
		int timeout = timeouts.get(userType);
		
		if(rateLimiter.checkLimited(from, timeout)) {
			from.sendMessage(ChatColor.RED + "You may only send one global message every " + timeout + " seconds");
			return true;
		}
		
		Utils.broadcastChatSkippingListener(plugin, plugin.getServer(), from, msg, ChatColor.YELLOW + "[G]");
		
		return true;
	}
}

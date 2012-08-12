package com.github.download13.locality;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class gcmd implements CommandExecutor {
	private Locality plugin;
	private int globalTimeout;
	private RateLimiter<Player> rateLimiter;
	
	public gcmd(Locality plugin, int globalTimeout) {
		this.plugin = plugin;
		this.globalTimeout = globalTimeout;
		this.rateLimiter = new RateLimiter<Player>(globalTimeout);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { // Whenever a user uses a command
		if(args.length == 0) return true;
		if(!(sender instanceof Player)) return true;
		Player from = (Player)sender; // Turn the CommandSender into a Player
		
		String msg = Utils.JoinStrings(args, " ");
		
		if(rateLimiter.checkLimited(from)) {
			from.sendMessage(ChatColor.RED + "You may only send one global message every " + globalTimeout + " seconds");
			return true;
		}
		
		Utils.broadcastChatSkippingListener(plugin, plugin.getServer(), from, msg, ChatColor.YELLOW + "[GLOBAL]<%1$s> %2$s");
		
		return true;
	}
}

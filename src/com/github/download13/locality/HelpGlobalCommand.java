package com.github.download13.locality;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpGlobalCommand implements CommandExecutor {
	private Locality plugin;
	private Map<String, Integer> timeouts;
	private RateLimiter<Player> rateLimiter;
	private ChatColor chatColor;
	
	public HelpGlobalCommand(Locality plugin, ChatColor color, Map<String, Integer> timeouts) {
		this.plugin = plugin;
		this.timeouts = timeouts;
		this.chatColor = color;
		this.rateLimiter = new RateLimiter<Player>();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) { // Whenever a user uses a command
		if(args.length == 0) return true;
		if(!(sender instanceof Player)) return false;
		Player from = (Player) sender;
		
		String msg = Utils.JoinStrings(args, " ");
		
		String userType = Utils.GetPlayerType(from);
		int timeout = timeouts.get(userType);
		
		if(rateLimiter.checkLimited(from, timeout)) {
			from.sendMessage(ChatColor.RED + "You may only send one global help message every " + timeout + " seconds");
			return true;
		}
		
		String format = Utils.FormatString(chatColor, "[HELP]", Utils.GetPrefixAndSuffix(plugin.getServer(), from));
		Utils.broadcastChatSkippingListener(plugin, plugin.getServer(), from, msg, format);
		
		return true;
	}
}

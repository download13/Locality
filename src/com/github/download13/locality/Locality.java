package com.github.download13.locality;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class Locality extends JavaPlugin implements Listener, CommandExecutor {
		private int localChatDistance;
		private RateLimiter<Player> localRateLimiter = new RateLimiter<Player>();
		
		private ChatColor localColor = ChatColor.WHITE;
		private ChatColor globalColor = ChatColor.YELLOW;
		private ChatColor helpColor = ChatColor.DARK_PURPLE;
		private ChatColor helpGlobalColor = ChatColor.AQUA;
		private ChatColor staffColor = ChatColor.RED;
		
		private Map<String, Integer> localTimeouts = new HashMap<String, Integer>();
		private Map<String, Integer> globalTimeouts = new HashMap<String, Integer>();
		private Map<String, Integer> helpGlobalTimeouts = new HashMap<String, Integer>();
		
		// Start here
		public void onEnable() {
			saveDefaultConfig();
			loadConfiguration();
			
			getServer().getPluginManager().registerEvents(this, this); // Register ourselves as an event handler
			
			getCommand("g").setExecutor(new GlobalCommand(this, globalColor, globalTimeouts));
			getCommand("h").setExecutor(new HelpCommand(this, helpColor));
			getCommand("gh").setExecutor(new HelpGlobalCommand(this, helpGlobalColor, helpGlobalTimeouts));
			getCommand("s").setExecutor(new StaffCommand(this, staffColor));
			
			getCommand("locality").setExecutor(this);
		}
		
		public void loadConfiguration() {
			reloadConfig();
			
			try {
				localChatDistance = getConfig().getInt("localRadius");
				
				localTimeouts.clear();
				localTimeouts.put("staff", getConfig().getInt("staff.localTimeout"));
				localTimeouts.put("vip", getConfig().getInt("vip.localTimeout"));
				localTimeouts.put("user", getConfig().getInt("user.localTimeout"));
				
				globalTimeouts.clear();
				globalTimeouts.put("staff", getConfig().getInt("staff.globalTimeout"));
				globalTimeouts.put("vip", getConfig().getInt("vip.globalTimeout"));
				globalTimeouts.put("user", getConfig().getInt("user.globalTimeout"));
				
				helpGlobalTimeouts.clear();
				helpGlobalTimeouts.put("staff", getConfig().getInt("staff.helpGlobalTimeout"));
				helpGlobalTimeouts.put("vip", getConfig().getInt("vip.helpGlobalTimeout"));
				helpGlobalTimeouts.put("user", getConfig().getInt("user.helpGlobalTimeout"));
				
				localColor = ChatColor.valueOf(getConfig().getString("color.local").trim().toUpperCase());
				globalColor = ChatColor.valueOf(getConfig().getString("color.global").trim().toUpperCase());
				helpColor = ChatColor.valueOf(getConfig().getString("color.help").trim().toUpperCase());
				helpGlobalColor = ChatColor.valueOf(getConfig().getString("color.helpGlobal").trim().toUpperCase());
				staffColor = ChatColor.valueOf(getConfig().getString("color.staff").trim().toUpperCase());
			} catch(NullPointerException e) {
				getLogger().warning("Config file failed to load all options, plugin may crash if it doesn't have the right options");
				getLogger().warning("Fix the config.yml file or delete it and a fresh valid one will appear");
			} catch(IllegalArgumentException e) {
				getLogger().warning("Config file failed to load all options, plugin may crash if it doesn't have the right options");
				getLogger().warning("Fix the config.yml file or delete it and a fresh valid one will appear");
			}
		}
		
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			if(!(sender instanceof ConsoleCommandSender)) {
				sender.sendMessage(ChatColor.RED + "This command can only be used from the server console");
				return true;
			}
			
			if(args.length == 0) return true;
			
			if(args[0].equals("reload")) {
				loadConfiguration();
				sender.sendMessage(ChatColor.GREEN + "Locality configuration loaded");
			}
			
			return true;
		}
		
		@EventHandler
		public void onChatMessage(AsyncPlayerChatEvent e) {
			if(e.isCancelled()) return;
			
			Player from = e.getPlayer();
			if(from == null) return;
			
			if(!from.hasPermission("locality.local")) {
				from.sendMessage(ChatColor.RED + "You do not have permission to use Local chat");
				e.setCancelled(true);
				return;
			}
			
			String userType = Utils.GetPlayerType(from);
			int timeout = localTimeouts.get(userType);
			
			if(localRateLimiter.checkLimited(from, timeout)) {
				from.sendMessage(ChatColor.RED + "You may only send one local message every " + timeout + " seconds");
				e.setCancelled(true);
				return;
			}
			
			e.setFormat(Utils.FormatString(localColor, "[L]", Utils.GetPrefixAndSuffix(getServer(), from)));
			
			Set<Player> receivers = e.getRecipients();
			synchronized(receivers) { // Make sure we are the only ones modifying this right now
				receivers.clear(); // Clear all receivers
				
				for(Player player : from.getWorld().getPlayers()) {
					if(from.getLocation().distance(player.getLocation()) <= localChatDistance) { // If we are within <config> meters of the other player
						receivers.add(player); // They get to see the message
					}
				}
			}
		}
}

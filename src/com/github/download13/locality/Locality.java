package com.github.download13.locality;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class Locality extends JavaPlugin implements Listener {
		private int localChatDistance;
		private ChatColor localColor;
		
		// Start here
		public void onEnable() {
			saveDefaultConfig();
			localChatDistance = getConfig().getInt("localRadius");
			
			getServer().getPluginManager().registerEvents(this, this); // Register ourselves as an event handler
			
			Map<String, Integer> globalTimeouts = null;
			Map<String, Integer> helpGlobalTimeouts = null;
			ChatColor globalColor = ChatColor.YELLOW;
			ChatColor helpColor = ChatColor.DARK_PURPLE;
			ChatColor helpGlobalColor = ChatColor.AQUA;
			localColor = ChatColor.WHITE;
			
			try {
				globalTimeouts = new HashMap<String, Integer>();
				globalTimeouts.put("staff", getConfig().getInt("staff.globalTimeout"));
				globalTimeouts.put("vip", getConfig().getInt("vip.globalTimeout"));
				globalTimeouts.put("user", getConfig().getInt("user.globalTimeout"));
				
				helpGlobalTimeouts = new HashMap<String, Integer>();
				helpGlobalTimeouts.put("staff", getConfig().getInt("staff.helpGlobalTimeout"));
				helpGlobalTimeouts.put("vip", getConfig().getInt("vip.helpGlobalTimeout"));
				helpGlobalTimeouts.put("user", getConfig().getInt("user.helpGlobalTimeout"));
				
				localColor = ChatColor.valueOf(getConfig().getString("color.local").trim().toUpperCase());
				globalColor = ChatColor.valueOf(getConfig().getString("color.global").trim().toUpperCase());
				helpColor = ChatColor.valueOf(getConfig().getString("color.help").trim().toUpperCase());
				helpGlobalColor = ChatColor.valueOf(getConfig().getString("color.helpGlobal").trim().toUpperCase());
			} catch(NullPointerException e) {
				getLogger().warning("Config file failed to load all options, plugin may crash if it doesn't have the right options");
				getLogger().warning("Fix the config.yml file or delete it and a fresh valid one will appear");
			} catch(IllegalArgumentException e) {
				getLogger().warning("Config file failed to load all options, plugin may crash if it doesn't have the right options");
				getLogger().warning("Fix the config.yml file or delete it and a fresh valid one will appear");
			}
			
			getCommand("g").setExecutor(new gcmd(this, globalColor, globalTimeouts));
			getCommand("h").setExecutor(new hcmd(this, helpColor));
			getCommand("hg").setExecutor(new hgcmd(this, helpGlobalColor, helpGlobalTimeouts));
		}
		// Unload all the events
		public void onDisable() {
			
		}
		
		@EventHandler
		public void onChatMessage(AsyncPlayerChatEvent e) { // Whenever a user sends a chat message
			if(e.isCancelled()) return;
			
			Player from = e.getPlayer();
			if(from == null) return;
			
			e.setFormat(localColor + "[L]" + e.getFormat());
			
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

package com.github.download13.locality;

import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class Locality extends JavaPlugin implements Listener {
		private int localChatDistance;
		
		// Start here
		public void onEnable() {
			saveDefaultConfig();
			localChatDistance = getConfig().getInt("localRadius");
			
			getServer().getPluginManager().registerEvents(this, this); // Register ourselves as an event handler
			getCommand("g").setExecutor(new gcmd(this, getConfig().getInt("globalTimeout"))); // Global channel
			getCommand("h").setExecutor(new hcmd(this));
			getCommand("hg").setExecutor(new hgcmd(this, getConfig().getInt("globalTimeout")));
		}
		// Unload all the events
		public void onDisable() {
			// TODO: Maybe clear something
		}
		
		@EventHandler
		public void onChatMessage(AsyncPlayerChatEvent e) { // Whenever a user sends a chat message
			if(e.isCancelled()) return;
			
			Player from = e.getPlayer();
			if(from == null) return;
			
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

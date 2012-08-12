package com.github.download13.locality;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class Utils {
	public static void sendChatSkippingListener(Listener listener, Server server, Player from, Set<Player> receivers, String msg) {
		sendChatSkippingListener(listener, server, from, receivers, msg, null);
	}
	public static void sendChatSkippingListener(Listener listener, Server server, Player from, Set<Player> receivers, String msg, String format) {
		AsyncPlayerChatEvent event = new AsyncPlayerChatEvent(false, from, msg, receivers);
		if(format != null) {
			event.setFormat(format);
		}
		System.out.println(event.getHandlers().getRegisteredListeners().length);
		if(listener != null) {
			event.getHandlers().unregister(listener);
			System.out.println("skipped");
		}
		System.out.println(event.getHandlers().getRegisteredListeners().length);
		server.getPluginManager().callEvent(event);
		
		if(event.isCancelled()) return;
		
		msg = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
		for(Player receiver : event.getRecipients()) {
			receiver.sendMessage(msg);
		}
	}
	
	public static void broadcastChatSkippingListener(Listener listener, Server server, Player from, String msg) {
		broadcastChatSkippingListener(listener, server, from, msg, null);
	}
	public static void broadcastChatSkippingListener(Listener listener, Server server, Player from, String msg, String format) {
		Set<Player> receivers = PlayerArrayToSet(server.getOnlinePlayers());
		sendChatSkippingListener(listener, server, from, receivers, msg, format);
	}
	
	public static void sendChatSkippingAllListeners(Server server, Player from, Set<Player> receivers, String msg) {
		sendChatSkippingAllListeners(server, from, receivers, msg, null);
	}
	public static void sendChatSkippingAllListeners(Server server, Player from, Set<Player> receivers, String msg, String format) {
		if(format == null) {
			format = "<%1$s> %2$s";
		}
		
		msg = String.format(format, from.getDisplayName(), msg);
		for(Player receiver : receivers) {
			receiver.sendMessage(msg);
		}
	}
	
	public static void broadcastChatSkippingAllListeners(Server server, Player from, String msg) {
		broadcastChatSkippingAllListeners(server, from, msg, null);
	}
	public static void broadcastChatSkippingAllListeners(Server server, Player from, String msg, String format) {
		Set<Player> receivers = PlayerArrayToSet(server.getOnlinePlayers());
		sendChatSkippingAllListeners(server, from, receivers, msg, format);
	}
	
	public static Set<Player> PlayerArrayToSet(Player[] players) {
		Set<Player> playerSet = new HashSet<Player>(players.length);
		
		for(Player player : players) {
			playerSet.add(player);
		}
		
		return playerSet;
	}
	
	public static String JoinStrings(String[] strings) {
		return JoinStrings(strings, "");
	}
	public static String JoinStrings(String[] strings, String sep) {
		StringBuilder builder = new StringBuilder(strings.length * 20); // Why does Java not have an String[].join function?!
		
		builder.append(strings[0]);
		for(int i = 1; i < strings.length; i++) {
			builder.append(sep);
			builder.append(strings[i]);
		}
		
		return builder.toString();
	}
}

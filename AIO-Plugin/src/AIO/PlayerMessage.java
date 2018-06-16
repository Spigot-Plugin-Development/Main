package AIO;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import net.md_5.bungee.api.ChatColor;

import org.bukkit.plugin.Plugin;

public class PlayerMessage implements Listener {
	
	private Plugin plugin;
	
	PlayerMessage(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onMessage(AsyncPlayerChatEvent event) {
		event.setFormat(event.getPlayer().getDisplayName() + ChatColor.GOLD + " >> " + aio.colorize(event.getMessage()));
	}
}
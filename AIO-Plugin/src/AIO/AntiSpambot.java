package AIO;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class AntiSpambot implements Listener {
	
	private List<Player> potentialSpambots = new ArrayList<Player>();
	private Plugin plugin;
	
	AntiSpambot(Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	private void playerJoinSpambot(PlayerJoinEvent event) {
		potentialSpambots.add(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	private void playerMoveSpambot(PlayerMoveEvent event) {
		potentialSpambots.remove(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	private void playerChatSpambot(AsyncPlayerChatEvent event) {
		if (potentialSpambots.contains(event.getPlayer())) {
			event.getPlayer().sendMessage(aio.colorize("&cError: &5Please move before typing a message"));
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	private void playerQuitSpambot(PlayerQuitEvent event) {
		if (potentialSpambots.contains(event.getPlayer())) {
			potentialSpambots.remove(event.getPlayer());
		}
	}
}

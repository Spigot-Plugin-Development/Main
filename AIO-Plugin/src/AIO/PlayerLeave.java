package AIO;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerLeave implements Listener {
	
	private aio plugin;
	
	PlayerLeave(aio plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		event.setQuitMessage(event.getPlayer().getDisplayName().toString() + " has left the game.");
	}
}

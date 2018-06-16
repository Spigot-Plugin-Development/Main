package AIO;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PlayerLeave implements Listener {
	
	private Plugin plugin;
	
	PlayerLeave(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerQuitEvent event) {
		event.setQuitMessage(event.getPlayer().getDisplayName().toString() + " has left the game.");
	}
}

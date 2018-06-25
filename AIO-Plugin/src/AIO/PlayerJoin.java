package AIO;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class PlayerJoin implements Listener {
	
	private aio plugin;
	
	PlayerJoin(aio plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage(event.getPlayer().getDisplayName().toString() + " has joined the game. Welcome!");
		event.getPlayer().sendMessage("Welcome " + event.getPlayer().getDisplayName().toString() + " to the game");
	}
}

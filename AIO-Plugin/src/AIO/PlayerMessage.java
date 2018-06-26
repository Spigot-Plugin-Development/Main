package AIO;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerMessage implements Listener {
	
	private aio plugin;

	private List<Player> mutedPlayers = new ArrayList<>();

	public void mutePlayer(Player player) {
		mutedPlayers.add(player);
	}

	public void unmutePlayer(Player player) {
		mutedPlayers.remove(player);
	}
	
	PlayerMessage(aio plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onMessage(AsyncPlayerChatEvent event) {
		if (mutedPlayers.contains(event.getPlayer())) {
			event.setCancelled(true);
		}
		event.setFormat(event.getPlayer().getDisplayName() + aio.colorize("&5&l > &r") + aio.colorize(event.getMessage()));
	}
}

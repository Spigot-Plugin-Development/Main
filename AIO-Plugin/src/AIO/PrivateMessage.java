package AIO;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PrivateMessage implements Listener {

	private Plugin plugin;
	Map<String, String> replyList = new HashMap<String, String>();
	
	PrivateMessage(Plugin plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		if (replyList.containsKey(event.getPlayer().getName())) {
			replyList.remove(event.getPlayer());
		}
	}
	
	public void message(Player sender, Player target, String message) {
		target.sendMessage(sender.getName() + ": " + message);
		sender.sendMessage(sender.getName() + ": " + message);
		replyList.put(target.getName(), sender.getName());
	}
	
	public void reply(Player sender, String message) {
		Player target = Bukkit.getServer().getPlayer((String)replyList.get(sender));
		target.sendMessage(sender.getName() + ": " + message);
		sender.sendMessage(sender.getName() + ": " + message);
	}
	
}

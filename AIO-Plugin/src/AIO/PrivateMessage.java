package AIO;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PrivateMessage {

	private Plugin plugin;
	Map<String, String> replyList = new HashMap<String, String>();
	
	PrivateMessage(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public void message(Player sender, Player target, String message) {
		target.sendMessage(sender.getName() + ": " + message);
		sender.sendMessage(sender.getName() + ": " + message);
	}
	
	public void reply(Player sender, String message) {
		
	}
	
}

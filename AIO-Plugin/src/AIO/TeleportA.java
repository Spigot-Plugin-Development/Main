package AIO;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class TeleportA {
	private Plugin plugin;
	
	TeleportA(Plugin plugin) {
		this.plugin = plugin;
	}
	
	private List<Player> teleporter = new ArrayList<Player>();
	private List<Player> teleport = new ArrayList<Player>();
	private List<Boolean> teleportType = new ArrayList<Boolean>();
	
	public void request(Player sender, Player target, boolean toSelf) {
		if (teleporter.contains(sender)) {
			int remove = teleporter.indexOf(sender);
			teleporter.remove(remove);
			teleport.remove(remove);
			teleportType.remove(remove);
		}
		if (teleport.contains(target)) {
			int remove = teleport.indexOf(target);
			teleporter.remove(remove);
			teleport.remove(remove);
			teleportType.remove(remove);
		}
		
		teleporter.add(sender);
		teleport.add(target);
		teleportType.add(toSelf);
		
		sender.sendMessage("Request sent to " + target.getName());
		if (toSelf) {
			target.sendMessage(sender.getName() + " wants to teleport you to them.");
		} else {
			target.sendMessage(sender.getName() + " wants to teleport to you.");
		}
	}
	
	public void decide(Player player, boolean decision) {
		if (!teleport.contains(player)) {
			player.sendMessage("You have no pending requests.");
			return;
		} 
		int tp = teleport.indexOf(player);
		if (!decision) {
			teleporter.get(tp).sendMessage(player.getName() + " denied your request.");
			return;
		}
		if (teleportType.get(tp)) {
			teleporter.get(tp).sendMessage("Teleporting to " + player.getName());
			player.sendMessage("Teleporting..");
			teleporter.get(tp).teleport(player.getLocation());
		} else {
			player.sendMessage("Teleporting to " + player.getName());
			teleporter.get(tp).sendMessage("Teleporting..");
			player.teleport(teleporter.get(tp).getLocation());
		}
		teleporter.remove(tp);
		teleport.remove(tp);
		teleportType.remove(tp);

	}
}

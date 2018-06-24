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
	private Map<String, String> replyList = new HashMap<String, String>();
	
	PrivateMessage(Plugin plugin) {
		this.plugin = plugin;
	}

	/* @EventHandler
	public void onLeave(PlayerQuitEvent event) {
		if(replyList.containsKey(event.getPlayer().getName())) {
			replyList.remove(event.getPlayer().getName());
		}
	} */

	//Send private message
	public void message(Player sender, String[] args) {
		if(args.length > 1 && plugin.getServer().getPlayer(args[0]) != null) {
		    Player target = plugin.getServer().getPlayer(args[0]);
		    String message = String.join(" ", aio.allButFirst(args));
		    sender.sendMessage("[ " + sender.getName() + " ] >> [ " + target.getName() + " ] : " + message);
		    target.sendMessage("[ " + sender.getName() + " ] >> [ " + target.getName() + " ] : " + message);
            if(replyList.containsKey(target.getName())) {
                replyList.replace(target.getName(), sender.getName());
                plugin.getLogger().info("Replace: " + target.getName() + " > " + replyList.get(target.getName()));
            } else {
                replyList.put(target.getName(), sender.getName());
                plugin.getLogger().info("Put: " + target.getName() + " > " + replyList.get(target.getName()));
            }
            if(replyList.containsKey(sender.getName())) {
                replyList.replace(sender.getName(), target.getName());
                plugin.getLogger().info("Replace: " + sender.getName() + " > " + replyList.get(sender.getName()));
            } else {
                replyList.put(sender.getName(), target.getName());
                plugin.getLogger().info("Put: " + sender.getName() + " > " + replyList.get(sender.getName()));
            }
		} else if(args.length == 1 && plugin.getServer().getPlayer(args[0]) != null) {
			sender.sendMessage("You have to enter a message.");
		} else if(args.length == 1 && plugin.getServer().getPlayer(args[0]) == null) {
		    sender.sendMessage("Player not found.");
        }
	}

	//Reply to player who previously sent a message to you
	public void reply(Player sender, String[] args) {

	    plugin.getLogger().info("Reply: sender is " + sender.getName() + ", pair in list is " + replyList.get(sender.getName()));
        plugin.getLogger().info("Number of elements in hashmap: " + replyList.size());

	    if(replyList.containsKey(sender.getName()) && args.length > 0) {
            Player target = plugin.getServer().getPlayer(replyList.get(sender.getName()));
            String message = String.join(" ", aio.allButFirst(args));
            target.sendMessage("[ " + sender.getName() + " ] >> [ " + target.getName() + " ] : " + message);
            sender.sendMessage("[ " + sender.getName() + " ] >> [ " + target.getName() + " ] : " + message);
        } else if(args.length == 0) {
	        sender.sendMessage("You have to enter a message.");
        } else {
	        sender.sendMessage("You don't have anyone to reply to.");
        }
	}
	
}

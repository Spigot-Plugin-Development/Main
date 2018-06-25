package AIO;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class PrivateMessage implements Listener, CommandExecutor {

	private Plugin plugin;
	private Map<String, String> replyList = new HashMap<String, String>();
	
	PrivateMessage(aio plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginCommand("msg").setExecutor(this);
		Bukkit.getServer().getPluginCommand("reply").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		if(replyList.containsKey(event.getPlayer().getName())) {
			replyList.remove(event.getPlayer().getName());
		}
	}

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("msg")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                if(args.length > 1 && plugin.getServer().getPlayer(args[0]) != null) {
                    Player target = plugin.getServer().getPlayer(args[0]);
                    String message = String.join(" ", aio.allButFirst(args));
                    player.sendMessage("[ " + player.getName() + " ] >> [ " + target.getName() + " ] : " + message);
                    target.sendMessage("[ " + player.getName() + " ] >> [ " + target.getName() + " ] : " + message);
                    if(replyList.containsKey(target.getName())) {
                        replyList.replace(target.getName(), player.getName());
                        plugin.getLogger().info("Replace: " + target.getName() + " > " + replyList.get(target.getName()));
                    } else {
                        replyList.put(target.getName(), player.getName());
                        plugin.getLogger().info("Put: " + target.getName() + " > " + replyList.get(target.getName()));
                    }
                    if(replyList.containsKey(player.getName())) {
                        replyList.replace(player.getName(), target.getName());
                        plugin.getLogger().info("Replace: " + player.getName() + " > " + replyList.get(player.getName()));
                    } else {
                        replyList.put(player.getName(), target.getName());
                        plugin.getLogger().info("Put: " + player.getName() + " > " + replyList.get(player.getName()));
                    }
                } else if(args.length == 1 && plugin.getServer().getPlayer(args[0]) != null) {
                    player.sendMessage("You have to enter a message.");
                } else if(args.length == 1 && plugin.getServer().getPlayer(args[0]) == null) {
                    player.sendMessage("Player not found.");
                }
            } else {
                sender.sendMessage("Only players can execute this command.");
            }
        }

        if(cmd.getName().equalsIgnoreCase("reply")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                plugin.getLogger().info("Reply: sender is " + player.getName() + ", pair in list is " + replyList.get(player.getName()));
                plugin.getLogger().info("Number of elements in hashmap: " + replyList.size());

                if(replyList.containsKey(player.getName()) && args.length > 0) {
                    Player target = plugin.getServer().getPlayer(replyList.get(player.getName()));
                    String message = String.join(" ", aio.allButFirst(args));
                    target.sendMessage("[ " + player.getName() + " ] >> [ " + target.getName() + " ] : " + message);
                    player.sendMessage("[ " + player.getName() + " ] >> [ " + target.getName() + " ] : " + message);
                } else if(args.length == 0) {
                    player.sendMessage("You have to enter a message.");
                } else {
                    player.sendMessage("You don't have anyone to reply to.");
                }
            } else {
                sender.sendMessage("Only players can execute this command.");
            }
        }

        return false;

    }
	
}

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

public class PrivateMessage implements Listener, CommandExecutor {
    private aio plugin;

    private Map<String, String> replyList = new HashMap<>();
	
	PrivateMessage(aio plugin) {
		this.plugin = plugin;
		Bukkit.getServer().getPluginCommand("msg").setExecutor(this);
		Bukkit.getServer().getPluginCommand("reply").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("msg")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                if(!player.hasPermission("aio.msg")) {
                    player.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                if(args.length > 1 && plugin.getServer().getPlayer(args[0]) != null) {
                    Player target = plugin.getServer().getPlayer(args[0]);
                    String message = String.join(" ", aio.allButFirst(args));
                    player.sendMessage("[ " + player.getName() + " ] >> [ " + target.getName() + " ] : " + message);
                    target.sendMessage("[ " + player.getName() + " ] >> [ " + target.getName() + " ] : " + message);
                    if(replyList.containsKey(target.getName())) {
                        replyList.replace(target.getName(), player.getName());
                    } else {
                        replyList.put(target.getName(), player.getName());
                    }
                    if(replyList.containsKey(player.getName())) {
                        replyList.replace(player.getName(), target.getName());
                    } else {
                        replyList.put(player.getName(), target.getName());
                    }
                    return false;
                } else if(args.length == 1 && plugin.getServer().getPlayer(args[0]) != null) {
                    player.sendMessage("You have to enter a message.");
                    return false;
                } else if(args.length == 1 && plugin.getServer().getPlayer(args[0]) == null) {
                    player.sendMessage("Player not found.");
                    return false;
                }
            } else {
                sender.sendMessage("Only players can execute this command.");
                return false;
            }
        }

        if(command.getName().equalsIgnoreCase("reply")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                if(!player.hasPermission("aio.reply")) {
                    player.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                if(replyList.containsKey(player.getName()) && args.length > 0) {
                    Player target = plugin.getServer().getPlayer(replyList.get(player.getName()));
                    String message = String.join(" ", aio.allButFirst(args));
                    target.sendMessage("[ " + player.getName() + " ] >> [ " + target.getName() + " ] : " + message);
                    player.sendMessage("[ " + player.getName() + " ] >> [ " + target.getName() + " ] : " + message);
                    return false;
                } else if(args.length == 0) {
                    player.sendMessage("You have to enter a message.");
                    return false;
                } else {
                    player.sendMessage("You don't have anyone to reply to.");
                    return false;
                }
            } else {
                sender.sendMessage("Only players can execute this command.");
                return false;
            }
        }

        return false;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        if(replyList.containsKey(event.getPlayer().getName())) {
            replyList.remove(event.getPlayer().getName());
        }
    }
}

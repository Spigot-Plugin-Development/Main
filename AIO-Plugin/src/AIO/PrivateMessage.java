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

	    //Send private message
        if(command.getName().equalsIgnoreCase("msg")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("messages.player_only"));
                return true;
            }
            if(!sender.hasPermission("aio.msg")) {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
                return true;
            }
            if(args.length < 2) {
                sender.sendMessage(plugin.getMessage("privatemsg.usage_msg"));
                return true;
            }
            if(plugin.getServer().getPlayer(args[0]) == null) {
                sender.sendMessage(plugin.getMessage("messages.player_not_found", args[0]));
                return true;
            }
            Player player = (Player)sender;
            Player target = plugin.getServer().getPlayer(args[0]);
            String message = String.join(" ", aio.allButFirst(args));
            String msg = plugin.getMessage("privatemsg.format", aio.getPlayerName(player), aio.getPlayerName(target), message);
            player.sendMessage(msg);
            target.sendMessage(msg);
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
            return true;
        }

        //Reply to private message
        if(command.getName().equalsIgnoreCase("reply")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("messages.player_only"));
                return true;
            }
            if(!sender.hasPermission("aio.reply")) {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
                return true;
            }
            if(!replyList.containsKey(sender.getName()) || plugin.getServer().getPlayer(replyList.get(sender.getName())) == null) {
                sender.sendMessage(plugin.getMessage("privatemsg.cant_reply"));
                return true;
            }
            if(args.length == 0) {
                sender.sendMessage(plugin.getMessage("privatemsg.usage_reply"));
                return true;
            }
            Player player = (Player)sender;
            Player target = plugin.getServer().getPlayer(replyList.get(player.getName()));
            String message = String.join(" ", args);
            String msg = plugin.getMessage("privatemsg.format", aio.getPlayerName(player), aio.getPlayerName(target), message);
            target.sendMessage(msg);
            player.sendMessage(msg);
            return true;
        }

        return false;
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
	    if(replyList.containsKey(event.getPlayer().getName())) {
	        plugin.getLogger().info("Containts key: " + event.getPlayer().getName());
	        replyList.remove(event.getPlayer().getName());
        }
        if(replyList.containsValue(event.getPlayer().getName())) {
            plugin.getLogger().info("Containts value: " + event.getPlayer().getName());
            for(String player : replyList.keySet()) {
                if(replyList.get(player).equals(event.getPlayer().getName())) {
                    replyList.remove(player);
                }
            }
        }
    }
}

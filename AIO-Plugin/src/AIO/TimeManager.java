package AIO;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class TimeManager implements CommandExecutor {

    private aio plugin;

    private HashMap<String, Integer> timeCommands = new HashMap<>();

    TimeManager(aio plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("dawn").setExecutor(this);
        Bukkit.getServer().getPluginCommand("morning").setExecutor(this);
        Bukkit.getServer().getPluginCommand("day").setExecutor(this);
        Bukkit.getServer().getPluginCommand("noon").setExecutor(this);
        Bukkit.getServer().getPluginCommand("afternoon").setExecutor(this);
        Bukkit.getServer().getPluginCommand("dusk").setExecutor(this);
        Bukkit.getServer().getPluginCommand("night").setExecutor(this);
        Bukkit.getServer().getPluginCommand("midnight").setExecutor(this);

        timeCommands.put("dawn", 0);
        timeCommands.put("morning", 450);
        timeCommands.put("day", 1000);
        timeCommands.put("noon", 6000);
        timeCommands.put("afternoon", 10000);
        timeCommands.put("dusk", 12000);
        timeCommands.put("night", 13000);
        timeCommands.put("midnight", 18000);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(timeCommands.containsKey(command.getName())) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.time")) {
                    sender.sendMessage(plugin.getMessage("messages.no_permissions"));
                    return true;
                }
                if(args.length == 0) {
                    ((Player) sender).getLocation().getWorld().setTime(timeCommands.get(command.getName()));
                    sender.sendMessage(plugin.getMessage("time.time_set", command.getName()));
                    return true;
                }
            }
            if(args.length == 0) {
                sender.sendMessage(plugin.getMessage("time.time_usage", command.getName()));
                return true;
            }
            if(plugin.getServer().getWorld(args[0]) == null) {
                sender.sendMessage(plugin.getMessage("time.world_not_found", args[0]));
                return true;
            } else {
                plugin.getServer().getWorld(args[0]).setTime(timeCommands.get(command.getName()));
                sender.sendMessage(plugin.getMessage("time.time_set_world", command.getName(), args[0]));
                return true;
            }
        }

        return false;
    }

}

package AIO;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WeatherManager implements CommandExecutor {
    private aio plugin;

    WeatherManager(aio plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("sun").setExecutor(this);
        Bukkit.getServer().getPluginCommand("rain").setExecutor(this);
        Bukkit.getServer().getPluginCommand("storm").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Set weather to sunny
        if(command.getName().equalsIgnoreCase("sun")) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.weather")) {
                    sender.sendMessage(plugin.getMessage("messages.no_permission"));
                    return true;
                }
                if(args.length == 0) {
                    ((Player) sender).getLocation().getWorld().setThundering(false);
                    ((Player) sender).getLocation().getWorld().setStorm(false);
                    sender.sendMessage(plugin.getMessage("weather.set1", "sunny"));
                    return true;
                }
            }
            if(args.length == 0) {
                sender.sendMessage(plugin.getMessage("weather.usage", "sun"));
                return true;
            }
            if(plugin.getServer().getWorld(args[0]) == null) {
                sender.sendMessage(plugin.getMessage("weather.world_not_found", args[0]));
                return true;
            }
            plugin.getServer().getWorld(args[0]).setThundering(false);
            plugin.getServer().getWorld(args[0]).setStorm(false);
            sender.sendMessage(plugin.getMessage("weather.set2", "sunny", args[0]));
            return true;
        }

        //Set weather to rainy
        if(command.getName().equalsIgnoreCase("rain")) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.weather")) {
                    sender.sendMessage(plugin.getMessage("messages.no_permission"));
                    return true;
                }
                if(args.length == 0) {
                    ((Player) sender).getLocation().getWorld().setThundering(false);
                    ((Player) sender).getLocation().getWorld().setStorm(true);
                    sender.sendMessage(plugin.getMessage("weather.set1", "rainy"));
                    return true;
                }
            }
            if(args.length == 0) {
                sender.sendMessage(plugin.getMessage("weather.usage", "rain"));
                return true;
            }
            if(plugin.getServer().getWorld(args[0]) == null) {
                sender.sendMessage(plugin.getMessage("weather.world_not_found", args[0]));
                return true;
            }
            plugin.getServer().getWorld(args[0]).setThundering(false);
            plugin.getServer().getWorld(args[0]).setStorm(true);
            sender.sendMessage(plugin.getMessage("weather.set2", "rainy", args[0]));
            return true;
        }

        //Set weather to stormy
        if(command.getName().equalsIgnoreCase("storm")) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.weather")) {
                    sender.sendMessage(plugin.getMessage("messages.no_permission"));
                    return true;
                }
                if(args.length == 0) {
                    ((Player) sender).getLocation().getWorld().setThundering(true);
                    ((Player) sender).getLocation().getWorld().setStorm(true);
                    sender.sendMessage(plugin.getMessage("weather.set1", "stormy"));
                    return true;
                }
            }
            if(args.length == 0) {
                sender.sendMessage(plugin.getMessage("weather.usage", "storm"));
                return true;
            }
            if(plugin.getServer().getWorld(args[0]) == null) {
                sender.sendMessage(plugin.getMessage("weather.world_not_found", args[0]));
                return true;
            }
            plugin.getServer().getWorld(args[0]).setThundering(true);
            plugin.getServer().getWorld(args[0]).setStorm(true);
            sender.sendMessage(plugin.getMessage("weather.set2", "stormy", args[0]));
            return true;
        }

        return false;
    }
}

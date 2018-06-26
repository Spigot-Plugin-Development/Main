package AIO;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class WeatherManager implements CommandExecutor {

    aio plugin;

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
                    sender.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                if(args.length == 0) {
                    ((Player) sender).getLocation().getWorld().setThundering(false);
                    ((Player) sender).getLocation().getWorld().setStorm(false);
                    sender.sendMessage("Weather set to sunny!");
                    return false;
                }
            }
            if(args.length == 1) {
                if(plugin.getServer().getWorld(args[0]) != null) {
                    plugin.getServer().getWorld(args[0]).setThundering(false);
                    plugin.getServer().getWorld(args[0]).setStorm(false);
                    sender.sendMessage("Weather set to sunny in " + plugin.getServer().getWorld(args[0]) + "!");
                    return false;
                } else {
                    sender.sendMessage("World not found.");
                    return false;
                }
            } else if(args.length == 0) {
                sender.sendMessage("World not given.");
                return false;
            } else {
                sender.sendMessage("Too many arguments.");
                return false;
            }
        }

        //Set weather to rainy
        if(command.getName().equalsIgnoreCase("rain")) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.weather")) {
                    sender.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                if(args.length == 0) {
                    ((Player) sender).getLocation().getWorld().setThundering(true);
                    ((Player) sender).getLocation().getWorld().setStorm(false);
                    sender.sendMessage("Weather set to sunny!");
                    return false;
                }
            }
            if(args.length == 1) {
                if(plugin.getServer().getWorld(args[0]) != null) {
                    plugin.getServer().getWorld(args[0]).setThundering(true);
                    plugin.getServer().getWorld(args[0]).setStorm(false);
                    sender.sendMessage("Weather set to sunny in " + plugin.getServer().getWorld(args[0]) + "!");
                    return false;
                } else {
                    sender.sendMessage("World not found.");
                    return false;
                }
            } else if(args.length == 0) {
                sender.sendMessage("World not given.");
                return false;
            } else {
                sender.sendMessage("Too many arguments.");
                return false;
            }
        }

        //Set weather to stormy
        if(command.getName().equalsIgnoreCase("storm")) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.weather")) {
                    sender.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                if(args.length == 0) {
                    ((Player) sender).getLocation().getWorld().setThundering(true);
                    ((Player) sender).getLocation().getWorld().setStorm(true);
                    sender.sendMessage("Weather set to sunny!");
                    return false;
                }
            }
            if(args.length == 1) {
                if(plugin.getServer().getWorld(args[0]) != null) {
                    plugin.getServer().getWorld(args[0]).setThundering(true);
                    plugin.getServer().getWorld(args[0]).setStorm(true);
                    sender.sendMessage("Weather set to sunny in " + plugin.getServer().getWorld(args[0]) + "!");
                    return false;
                } else {
                    sender.sendMessage("World not found.");
                    return false;
                }
            } else if(args.length == 0) {
                sender.sendMessage("World not given.");
                return false;
            } else {
                sender.sendMessage("Too many arguments.");
                return false;
            }
        }

        return false;
    }

}

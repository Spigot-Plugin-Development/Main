package AIO;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TimeManager implements CommandExecutor {

    aio plugin;

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
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Set time to dawn, 0
        if(command.getName().equalsIgnoreCase("dawn")) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.time")) {
                    sender.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                if(args.length == 0) {
                    ((Player)sender).getLocation().getWorld().setTime(0);
                    sender.sendMessage("Time set to dawn!");
                    return false;
                }
            }
            if(args.length == 1) {
                if(plugin.getServer().getWorld(args[0]) != null) {
                    plugin.getServer().getWorld(args[0]).setTime(0);
                    sender.sendMessage("Time set to dawn in " + plugin.getServer().getWorld(args[0]) + "!");
                    return false;
                } else {
                    sender.sendMessage("World not found.");
                    return false;
                }
            }
            if(args.length == 0) {
                sender.sendMessage("World not given.");
                return false;
            } else {
                sender.sendMessage("Too many arguments.");
                return false;
            }
        }

        //Set time to morning, 450
        if(command.getName().equalsIgnoreCase("morning")) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.time")) {
                    sender.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                if(args.length == 0) {
                    ((Player)sender).getLocation().getWorld().setTime(450);
                    sender.sendMessage("Time set to morning!");
                    return false;
                }
            }
            if(args.length == 1) {
                if(plugin.getServer().getWorld(args[0]) != null) {
                    plugin.getServer().getWorld(args[0]).setTime(450);
                    sender.sendMessage("Time set to morning in " + plugin.getServer().getWorld(args[0]) + "!");
                    return false;
                } else {
                    sender.sendMessage("World not found.");
                    return false;
                }
            }
            if(args.length == 0) {
                sender.sendMessage("World not given.");
                return false;
            } else {
                sender.sendMessage("Too many arguments.");
                return false;
            }
        }

        //Set time to day, 1000
        if(command.getName().equalsIgnoreCase("day")) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.time")) {
                    sender.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                if(args.length == 0) {
                    ((Player)sender).getLocation().getWorld().setTime(1000);
                    sender.sendMessage("Time set to day!");
                    return false;
                }
            }
            if(args.length == 1) {
                if(plugin.getServer().getWorld(args[0]) != null) {
                    plugin.getServer().getWorld(args[0]).setTime(1000);
                    sender.sendMessage("Time set to day in " + plugin.getServer().getWorld(args[0]) + "!");
                    return false;
                } else {
                    sender.sendMessage("World not found.");
                    return false;
                }
            }
            if(args.length == 0) {
                sender.sendMessage("World not given.");
                return false;
            } else {
                sender.sendMessage("Too many arguments.");
                return false;
            }
        }

        //Set time to noon, 6000
        if(command.getName().equalsIgnoreCase("noon")) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.time")) {
                    sender.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                if(args.length == 0) {
                    ((Player)sender).getLocation().getWorld().setTime(6000);
                    sender.sendMessage("Time set to noon!");
                    return false;
                }
            }
            if(args.length == 1) {
                if(plugin.getServer().getWorld(args[0]) != null) {
                    plugin.getServer().getWorld(args[0]).setTime(6000);
                    sender.sendMessage("Time set to noon in " + plugin.getServer().getWorld(args[0]) + "!");
                    return false;
                } else {
                    sender.sendMessage("World not found.");
                    return false;
                }
            }
            if(args.length == 0) {
                sender.sendMessage("World not given.");
                return false;
            } else {
                sender.sendMessage("Too many arguments.");
                return false;
            }
        }

        //Set time to afternoon, 10000
        if(command.getName().equalsIgnoreCase("afternoon")) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.time")) {
                    sender.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                if(args.length == 0) {
                    ((Player)sender).getLocation().getWorld().setTime(10000);
                    sender.sendMessage("Time set to afternoon!");
                    return false;
                }
            }
            if(args.length == 1) {
                if(plugin.getServer().getWorld(args[0]) != null) {
                    plugin.getServer().getWorld(args[0]).setTime(10000);
                    sender.sendMessage("Time set to afternoon in " + plugin.getServer().getWorld(args[0]) + "!");
                    return false;
                } else {
                    sender.sendMessage("World not found.");
                    return false;
                }
            }
            if(args.length == 0) {
                sender.sendMessage("World not given.");
                return false;
            } else {
                sender.sendMessage("Too many arguments.");
                return false;
            }
        }

        //Set time to dusk, 12500
        if(command.getName().equalsIgnoreCase("dusk")) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.time")) {
                    sender.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                if(args.length == 0) {
                    ((Player)sender).getLocation().getWorld().setTime(12500);
                    sender.sendMessage("Time set to dusk!");
                    return false;
                }
            }
            if(args.length == 1) {
                if(plugin.getServer().getWorld(args[0]) != null) {
                    plugin.getServer().getWorld(args[0]).setTime(12500);
                    sender.sendMessage("Time set to dusk in " + plugin.getServer().getWorld(args[0]) + "!");
                    return false;
                } else {
                    sender.sendMessage("World not found.");
                    return false;
                }
            }
            if(args.length == 0) {
                sender.sendMessage("World not given.");
                return false;
            } else {
                sender.sendMessage("Too many arguments.");
                return false;
            }
        }

        //Set time to night, 13000
        if(command.getName().equalsIgnoreCase("night")) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.time")) {
                    sender.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                if(args.length == 0) {
                    ((Player)sender).getLocation().getWorld().setTime(13000);
                    sender.sendMessage("Time set to night!");
                    return false;
                }
            }
            if(args.length == 1) {
                if(plugin.getServer().getWorld(args[0]) != null) {
                    plugin.getServer().getWorld(args[0]).setTime(13000);
                    sender.sendMessage("Time set to night in " + plugin.getServer().getWorld(args[0]) + "!");
                    return false;
                } else {
                    sender.sendMessage("World not found.");
                    return false;
                }
            }
            if(args.length == 0) {
                sender.sendMessage("World not given.");
                return false;
            } else {
                sender.sendMessage("Too many arguments.");
                return false;
            }
        }

        //Set time to midnight, 18000
        if(command.getName().equalsIgnoreCase("midnight")) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.time")) {
                    sender.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                if(args.length == 0) {
                    ((Player)sender).getLocation().getWorld().setTime(18000);
                    sender.sendMessage("Time set to midnight!");
                    return false;
                }
            }
            if(args.length == 1) {
                if(plugin.getServer().getWorld(args[0]) != null) {
                    plugin.getServer().getWorld(args[0]).setTime(18000);
                    sender.sendMessage("Time set to midnight in " + plugin.getServer().getWorld(args[0]) + "!");
                    return false;
                } else {
                    sender.sendMessage("World not found.");
                    return false;
                }
            }
            if(args.length == 0) {
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

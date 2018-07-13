package AIO;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class WeatherManager implements CommandExecutor {
    private aio plugin;

    private HashMap<String, String> weatherList = new HashMap<>();

    WeatherManager(aio plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("sun").setExecutor(this);
        Bukkit.getServer().getPluginCommand("rain").setExecutor(this);
        Bukkit.getServer().getPluginCommand("storm").setExecutor(this);

        weatherList.put("sun", "sunny");
        weatherList.put("rain", "rainy");
        weatherList.put("storm", "stormy");
    }

    private void setWeather(String weather, String world) {
        if(weather.equalsIgnoreCase("sun")) {
            plugin.getServer().getWorld(world).setThundering(false);
            plugin.getServer().getWorld(world).setStorm(false);
        }
        if(weather.equalsIgnoreCase("rain")) {
            plugin.getServer().getWorld(world).setThundering(false);
            plugin.getServer().getWorld(world).setStorm(true);
        }
        if(weather.equalsIgnoreCase("storm")) {
            plugin.getServer().getWorld(world).setThundering(true);
            plugin.getServer().getWorld(world).setStorm(true);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(weatherList.containsKey(command.getName())) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.weather")) {
                    sender.sendMessage(plugin.getMessage("aio.no_permission"));
                    return true;
                }
                if(args.length == 0) {
                    setWeather(command.getName(), ((Player) sender).getWorld().getName());
                    sender.sendMessage(plugin.getMessage("weathermanager.set_self", weatherList.get(command.getName())));
                    return true;
                }
            }
            if(args.length == 0) {
                sender.sendMessage(plugin.getMessage("weathermanager.usage", command.getName()));
                return true;
            }
            if(plugin.getServer().getWorld(args[0]) == null) {
                sender.sendMessage(plugin.getMessage("aio.world_not_found", args[0]));
                return true;
            }
            setWeather(command.getName(), args[0]);
            sender.sendMessage(plugin.getMessage("weathermanager.set_world", weatherList.get(command.getName()), args[0]));
            return true;
        }

        return false;
    }
}

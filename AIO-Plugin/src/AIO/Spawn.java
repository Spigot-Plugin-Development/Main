package AIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

public class Spawn implements Listener, CommandExecutor {

    aio plugin;
    static Location spawnLocation;

    Spawn(aio plugin) {
        this.plugin = plugin;
        plugin.getCommand("spawn").setExecutor(this);
        plugin.getCommand("setspawn").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        spawnLocation = Convert.StringToLocation(plugin.getConfig().getString("spawn"));
    }

    @EventHandler
    private void playerDeath(PlayerRespawnEvent event) {
        event.setRespawnLocation(spawnLocation);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("spawn")) {
            if (sender instanceof Player) {
                if (!sender.hasPermission("aio.spawn")) {
                    sender.sendMessage(aio.colorize("&cYou don't have permission to execute this command."));
                    return false;
                }
                if (spawnLocation == null) {
                    sender.sendMessage(aio.colorize("&cUnable to reach spawn location."));
                    return false;
                }
                ((Player)sender).teleport(spawnLocation);
                return false;
            } else {
                sender.sendMessage(aio.colorize("&cOnly players can execute this command."));
                return false;
            }
        }

        if (command.getName().equalsIgnoreCase("setspawn")) {
            if (sender instanceof Player) {
                if (!sender.hasPermission("aio.spawn.set")) {
                    sender.sendMessage(aio.colorize("&cYou don't have permission to execute this command."));
                    return false;
                }
                plugin.getConfig().set("spawn", Convert.LocationToString(((Player)sender).getLocation()));
                plugin.saveConfig();
                spawnLocation = ((Player)sender).getLocation();
                sender.sendMessage(aio.colorize("&aSpawn location set."));
                return false;
            } else {
                sender.sendMessage(aio.colorize("&cOnly players can execute this command."));
                return false;
            }
        }
        return false;
    }
}

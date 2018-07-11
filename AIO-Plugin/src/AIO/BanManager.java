package AIO;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class BanManager implements Listener, CommandExecutor {
    private aio plugin;

    BanManager(aio plugin) {
        this.plugin = plugin;
        plugin.getCommand("kick").setExecutor(this);
        plugin.getCommand("kickall").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("kickall")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("aio.kick.everyone")) {
                    for (Player player : plugin.getServer().getOnlinePlayers()) {
                        if (player.equals((Player)sender)) {
                            continue;
                        }
                        player.kickPlayer(String.join(" ", args));
                    }
                    sender.sendMessage("You kicked all players.");
                } else {
                    sender.sendMessage("You don't have permission to execute this command.");
                }
            } else {
                for (Player player: plugin.getServer().getOnlinePlayers()) {
                    player.kickPlayer(String.join(" ", args));
                }
            }
            plugin.getLogger().info(sender.getName() + " kicked all players.");
        }

        if (command.getName().equalsIgnoreCase("kick")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("aio.kick")) {
                    if(args.length > 0 && plugin.getServer().getPlayer(args[0]) != null) {
                        plugin.getServer().getPlayer(args[0]).kickPlayer(String.join(" ", aio.allButFirst(args)));
                    } else {
                        sender.sendMessage("Player not found.");
                    }
                }
            } else {
                if (args.length > 0 && plugin.getServer().getPlayer(args[0]) != null) {
                    plugin.getServer().getPlayer(args[0]).kickPlayer(String.join(" ", aio.allButFirst(args)));
                } else {
                    sender.sendMessage("Player not found.");
                }
            }
        }
        return false;
    }
}

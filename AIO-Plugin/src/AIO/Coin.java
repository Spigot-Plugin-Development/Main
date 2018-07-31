package AIO;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class Coin implements CommandExecutor {

    private aio plugin;

    Coin(aio plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("coin").setExecutor(this);
    }

    public int getCoins(UUID player) {
        if(plugin.cacheManager.containsPlayer(player)) {
            return plugin.cacheManager.getPlayer(player).coins;
        }
        System.out.println("Player not found in getCoins.");
        return -1;
    }

    public void transferCoins(UUID player, int amount) {
        if(plugin.cacheManager.containsPlayer(player)) {
            plugin.cacheManager.getPlayer(player).coins = getCoins(player) + amount;
            return;
        }
        System.out.println("player not found in transferCoins.");
    }

    public boolean onCommand(CommandSender sender, Command command, String labels, String[] args) {
        if(command.getName().equalsIgnoreCase("coin")) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.coin")) {
                    sender.sendMessage(plugin.getMessage("aio.no_permission"));
                    return true;
                }
                if(args.length == 0) {
                    sender.sendMessage("Balance: " + getCoins(((Player) sender).getUniqueId()));
                    return true;
                }
                if(args.length < 3 && !sender.hasPermission("aio.coin.admin")) {
                    sender.sendMessage("Usage: /coin send <playername> <amount>");
                    return true;
                }
                if(args.length == 2 && sender.hasPermission("aio.coin.admin")) {
                    sender.sendMessage("Usage: /coin <give | take | send> <playername> <amount>");
                    return true;
                }
                if(args[0].equalsIgnoreCase("send")) {
                    if(plugin.getServer().getPlayer(args[1]) == null) {
                        sender.sendMessage("Player not found");
                        return true;
                    }
                    if(!args[2].matches("\\d+")) {
                        sender.sendMessage("Invalid amount.");
                        return true;
                    }
                    if(Integer.parseInt(args[2]) > getCoins(((Player) sender).getUniqueId())) {
                        sender.sendMessage("You don't have enough coins.");
                        return true;
                    }
                    transferCoins(((Player) sender).getUniqueId(), -Integer.parseInt(args[2]));
                    transferCoins(plugin.getServer().getPlayer(args[1]).getUniqueId(), +Integer.parseInt(args[2]));
                    sender.sendMessage("You transfered " + args[2] + " coins to " + args[1] + ".");
                    plugin.getServer().getPlayer(args[1]).sendMessage("You recieved " + args[2] + " coins from " + aio.getPlayerName((Player)sender) + ".");
                    return true;
                }
            }
            if(args.length == 0) {
                sender.sendMessage("Usage: /coin <playername>");
                return true;
            }
            if(args.length == 1 && (!args[0].equalsIgnoreCase("give") && !args[0].equalsIgnoreCase("take") && !args[0].equalsIgnoreCase("send"))) {
                if(plugin.getServer().getPlayer(args[0]) == null) {
                    sender.sendMessage("Player not found.");
                    return true;
                }
                sender.sendMessage("Balance of " + plugin.getServer().getPlayer(args[0]).getName() + " is " + getCoins(plugin.getServer().getPlayer(args[0]).getUniqueId()));
                return true;
            }
            if(args.length < 3) {
                sender.sendMessage("Usage: /coin " + args[0] + " <playername> <amount>");
                return true;
            }
            if(args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("take")) {
                if(plugin.getServer().getPlayer(args[1]) == null) {
                    sender.sendMessage("Player not found");
                    return true;
                }
                if(!args[2].matches("\\d+")) {
                    sender.sendMessage("Invalid amount.");
                    return true;
                }
                if(args[0].equalsIgnoreCase("give")) {
                    transferCoins(plugin.getServer().getPlayer(args[1]).getUniqueId(), +Integer.parseInt(args[2]));
                    sender.sendMessage(args[2] + " coins given to " + args[1] + ".");
                    return true;
                }
                if(args[0].equalsIgnoreCase("take")) {
                    transferCoins(plugin.getServer().getPlayer(args[1]).getUniqueId(), -Integer.parseInt(args[2]));
                    sender.sendMessage(args[2] + " coins taken from " + args[1] + ".");
                    return true;
                }
            }
        }

        return false;
    }

}

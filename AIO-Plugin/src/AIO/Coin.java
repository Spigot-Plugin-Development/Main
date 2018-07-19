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

    public int getCoins(UUID uuid) {
        if(plugin.cacheManager.containsPlayer(uuid)) {
            PlayerInfo info = plugin.cacheManager.getPlayer(uuid);
            return info.coins;
        }
        return -1;
    }

    public void transferCoins(UUID sender, UUID target, int amount) {
        if(!plugin.cacheManager.containsPlayer(sender) || !plugin.cacheManager.containsPlayer(target)) {
            return;
        }
        PlayerInfo senderInfo = plugin.cacheManager.getPlayer(sender);
        PlayerInfo targetInfo = plugin.cacheManager.getPlayer(target);

        senderInfo.coins = senderInfo.coins - amount;
        targetInfo.coins = targetInfo.coins + amount;
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
                if(args[0].equalsIgnoreCase("transfer") || args[0].equalsIgnoreCase("send")) {
                    if(plugin.getServer().getPlayer(args[1]) == null) {
                        sender.sendMessage("Player not found");
                        return true;
                    }
                    if(!args[2].matches("0-9+")) {
                        sender.sendMessage("Invalid amount.");
                        return true;
                    }
                    transferCoins(((Player) sender).getUniqueId(), plugin.getServer().getPlayer(args[1]).getUniqueId(), Integer.parseInt(args[2]));
                    return true;
                }
                sender.sendMessage("Usage: /coin send <playername> <amount>");
            }
        }

        return false;
    }

}

package AIO;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public class AntiSwear implements Listener, CommandExecutor {

    private aio plugin;

    AntiSwear(aio plugin) {
        this.plugin = plugin;
        //Bukkit.getServer().getPluginCommand("antiswear").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("antiswear")) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.antiswear")) {
                    sender.sendMessage(aio.colorize("&cYou don't have permission to execute this command"));
                    return false;
                }
            }
            if(args.length == 0 || args.length > 2) {
                sender.sendMessage("&c/antiswear <add | remove | list>");
                return false;
            }
            if(args[0].equalsIgnoreCase("add")) {
                if(args.length != 2) {
                    sender.sendMessage("&c/antiswear <add> <swearword>");
                    return false;
                }

            }
        }

        return false;
    }

}
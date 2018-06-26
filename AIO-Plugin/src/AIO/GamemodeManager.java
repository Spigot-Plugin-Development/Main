package AIO;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class GamemodeManager implements CommandExecutor {

    aio plugin;

    GamemodeManager(aio plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("survival").setExecutor(this);
        Bukkit.getServer().getPluginCommand("creative").setExecutor(this);
        Bukkit.getServer().getPluginCommand("adventure").setExecutor(this);
        Bukkit.getServer().getPluginCommand("spectator").setExecutor(this);
        Bukkit.getServer().getPluginCommand("gm").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Survival
        if(command.getName().equalsIgnoreCase("survival")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                if(!player.hasPermission("aio.gamemode")) {
                    player.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                player.setGameMode(GameMode.SURVIVAL);
                player.sendMessage("Gamemode set to survival!");
                return false;
            } else {
                sender.sendMessage("Only players can execute this command.");
                return false;
            }
        }

        //Creative
        if(command.getName().equalsIgnoreCase("creative")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                if(!player.hasPermission("aio.gamemode")) {
                    player.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                player.setGameMode(GameMode.CREATIVE);
                player.sendMessage("Gamemode set to creative!");
                return false;
            } else {
                sender.sendMessage("Only players can execute this command.");
                return false;
            }
        }

        //Adventure
        if(command.getName().equalsIgnoreCase("adventure")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                if(!player.hasPermission("aio.gamemode")) {
                    player.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                player.setGameMode(GameMode.ADVENTURE);
                player.sendMessage("Gamemode set to adventure!");
                return false;
            } else {
                sender.sendMessage("Only players can execute this command.");
                return false;
            }
        }

        //Spectator
        if(command.getName().equalsIgnoreCase("spectator")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                if(!player.hasPermission("aio.gamemode")) {
                    player.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                player.setGameMode(GameMode.SPECTATOR);
                player.sendMessage("Gamemode set to spectator!");
                return false;
            } else {
                sender.sendMessage("Only players can execute this command.");
                return false;
            }
        }

        //Gamemode
        if(command.getName().equalsIgnoreCase("gm")) {
            String[] gms = {"survival", "s", "0"};
            String[] gmc = {"creative", "c", "1"};
            String[] gma = {"adventure", "a", "2"};
            String[] gmsp = {"spectator", "sp", "3"};

            if(sender instanceof Player) {
                Player player = (Player)sender;
                if(!player.hasPermission("aio.gamemode")) {
                    player.sendMessage("You don't have permission to execute this command.");
                    return false;
                }
                if(args.length == 1) {
                    if(Arrays.asList(gms).contains(args[0])) {
                        player.setGameMode(GameMode.SURVIVAL);
                        player.sendMessage("Gamemode set to survival!");
                        return false;
                    } else if(Arrays.asList(gmc).contains(args[0])) {
                        player.setGameMode(GameMode.CREATIVE);
                        player.sendMessage("Gamemode set to creative!");
                        return false;
                    } else if(Arrays.asList(gma).contains(args[0])) {
                        player.setGameMode(GameMode.ADVENTURE);
                        player.sendMessage("Gamemode set to adventure!");
                        return false;
                    } else if(Arrays.asList(gmsp).contains(args[0])) {
                        player.setGameMode(GameMode.SPECTATOR);
                        player.sendMessage("Gamemode set to spectator!");
                        return false;
                    } else {
                        sender.sendMessage("Invalid gamemode.");
                        return false;
                    }
                }
            } else {
                if(args.length == 2) {
                    if(Arrays.asList(gms).contains(args[0]) && plugin.getServer().getPlayer(args[1]) != null) {
                        plugin.getServer().getPlayer(args[1]).setGameMode(GameMode.SURVIVAL);
                        sender.sendMessage("Gamemode set to survival for " + args[1] + ".");
                        return false;
                    } else if(Arrays.asList(gmc).contains(args[0]) && plugin.getServer().getPlayer(args[1]) != null) {
                        plugin.getServer().getPlayer(args[1]).setGameMode(GameMode.CREATIVE);
                        sender.sendMessage("Gamemode set to creative for " + args[1] + ".");
                        return false;
                    } else if(Arrays.asList(gma).contains(args[0]) && plugin.getServer().getPlayer(args[1]) != null) {
                        plugin.getServer().getPlayer(args[1]).setGameMode(GameMode.ADVENTURE);
                        sender.sendMessage("Gamemode set to adventure for " + args[1] + ".");
                        return false;
                    } else if(Arrays.asList(gmsp).contains(args[0]) && plugin.getServer().getPlayer(args[1]) != null) {
                        plugin.getServer().getPlayer(args[1]).setGameMode(GameMode.SPECTATOR);
                        sender.sendMessage("Gamemode set to spectator for " + args[1] + ".");
                        return false;
                    } else if(plugin.getServer().getPlayer(args[1]) == null) {
                        sender.sendMessage("Player not found.");
                        return false;
                    } else {
                        sender.sendMessage("Invalid gamemode.");
                        return false;
                    }
                } else if(args.length > 2) {
                    sender.sendMessage("Too many arguments.");
                    return false;
                } else {
                    sender.sendMessage("Not enough arguments");
                    return false;
                }
            }
        }

        return false;
    }

}

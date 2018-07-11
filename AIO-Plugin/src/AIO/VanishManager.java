package AIO;

import de.myzelyam.api.vanish.VanishAPI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class VanishManager implements Listener, CommandExecutor {
    private aio plugin;

    List<Player> a = new ArrayList<>();

    VanishManager(aio plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("vanish").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean isVanished(Player player) {
        return VanishAPI.isInvisible(player);
    }

    public void changeVanished(Player player) {
        if (isVanished(player)) {
            VanishAPI.showPlayer(player);
        } else {
            VanishAPI.hidePlayer(player);
        }
    }

    public void addVanish(Player player) {
        if (!isVanished(player)) {
            VanishAPI.hidePlayer(player);
        }
    }

    public void removeVanish(Player player) {
        if (isVanished(player)) {
            VanishAPI.showPlayer(player);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("vanish")) {
            if (sender instanceof Player) {
                if (!((Player)sender).hasPermission("aio.vanish")) {
                    sender.sendMessage("You don't have permission to execute that command.");
                    return false;
                }
                if (args.length == 0) {
                    changeVanished((Player)sender);
                    sender.sendMessage("God mode " + (isVanished((Player)sender) ? "enabled" : "disabled") + ".");
                    return false;
                }
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("yes") || args[0].equalsIgnoreCase("y") || args[0].equalsIgnoreCase("true")) {
                        addVanish((Player)sender);
                        sender.sendMessage("You have vanished.");
                        return false;
                    }

                    if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("no") || args[0].equalsIgnoreCase("n") || args[0].equalsIgnoreCase("false")) {
                        removeVanish((Player)sender);
                        sender.sendMessage("You have unvanished.");
                        return false;
                    }
                }

                if (!((Player)sender).hasPermission("aio.vanish.others")) {
                    sender.sendMessage("You don't have permission to change vanish for other players.");
                    return false;
                }

                if (args.length == 1) {
                    if (plugin.getServer().getPlayer(args[0]) != null) {
                        changeVanished(plugin.getServer().getPlayer(args[0]));
                        sender.sendMessage("Vanish " + (isVanished(plugin.getServer().getPlayer(args[0])) ? "enbaled" : "disabled") + " for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
                        return false;
                    } else {
                        sender.sendMessage("Player not found.");
                        return false;
                    }
                }

                if (args.length == 2) {
                    if (plugin.getServer().getPlayer(args[1]) != null) {
                        if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("yes") || args[0].equalsIgnoreCase("y") || args[0].equalsIgnoreCase("true")) {
                            addVanish(plugin.getServer().getPlayer(args[1]));
                            sender.sendMessage("Vanish enabled for " + plugin.getServer().getPlayer(args[1]).getDisplayName() + ".");
                            return false;
                        }

                        if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("no") || args[0].equalsIgnoreCase("n") || args[0].equalsIgnoreCase("false")) {
                            removeVanish(plugin.getServer().getPlayer(args[1]));
                            sender.sendMessage("Vanish disabled for " + plugin.getServer().getPlayer(args[1]).getDisplayName() + ".");
                            return false;
                        }
                    }

                    if (plugin.getServer().getPlayer(args[0]) != null) {
                        if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("yes") || args[1].equalsIgnoreCase("y") || args[1].equalsIgnoreCase("true")) {
                            addVanish(plugin.getServer().getPlayer(args[0]));
                            sender.sendMessage("Vanish enabled for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
                            return false;
                        }

                        if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("no") || args[1].equalsIgnoreCase("n") || args[1].equalsIgnoreCase("false")) {
                            removeVanish(plugin.getServer().getPlayer(args[0]));
                            sender.sendMessage("Vanish disabled for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
                            return false;
                        }
                    }
                    sender.sendMessage("Invalid arguments.");
                    return false;
                }
                sender.sendMessage("Too many arguments.");
                return false;
            } else {
                if (args.length == 0) {
                    sender.sendMessage("No player given.");
                    return false;
                }
                if (args.length == 1) {
                    if (plugin.getServer().getPlayer(args[0]) != null) {
                        changeVanished(plugin.getServer().getPlayer(args[0]));
                        sender.sendMessage("Vanish " + (isVanished(plugin.getServer().getPlayer(args[0])) ? "enabled" : "disabled") + " for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
                        return false;
                    }
                }
                if (args.length == 2) {
                    if (plugin.getServer().getPlayer(args[1]) != null) {
                        if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("yes") || args[0].equalsIgnoreCase("y") || args[0].equalsIgnoreCase("true")) {
                            addVanish(plugin.getServer().getPlayer(args[1]));
                            sender.sendMessage("Vanish enabled for " + plugin.getServer().getPlayer(args[1]).getDisplayName() + ".");
                            return false;
                        }

                        if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("no") || args[0].equalsIgnoreCase("n") || args[0].equalsIgnoreCase("false")) {
                            removeVanish(plugin.getServer().getPlayer(args[1]));
                            sender.sendMessage("Vanish disabled for " + plugin.getServer().getPlayer(args[1]).getDisplayName() + ".");
                            return false;
                        }
                    }

                    if (plugin.getServer().getPlayer(args[0]) != null) {
                        if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("yes") || args[1].equalsIgnoreCase("y") || args[1].equalsIgnoreCase("true")) {
                            addVanish(plugin.getServer().getPlayer(args[0]));
                            sender.sendMessage("Vanish enabled for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
                            return false;
                        }

                        if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("no") || args[1].equalsIgnoreCase("n") || args[1].equalsIgnoreCase("false")) {
                            removeVanish(plugin.getServer().getPlayer(args[0]));
                            sender.sendMessage("Vanish disabled for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
                            return false;
                        }
                    }
                    sender.sendMessage("Invalid arguments.");
                    return false;
                }
                sender.sendMessage("Too many arguments.");
                return false;
            }
        }
        return false;
    }
}

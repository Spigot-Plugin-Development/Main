package AIO;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;
import java.util.List;

public class GodManager implements Listener, CommandExecutor {
    aio plugin;

    List<Player> godPlayers = new ArrayList<Player>();

    GodManager(aio plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("god").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean isGod(Player player) {
        return godPlayers.contains(player);
    }

    public void changeGod(Player player) {
        if (isGod(player)) {
            removeGod(player);
        } else {
            addGod(player);
        }
    }

    public void addGod(Player player) {
        if (!isGod(player)) {
            maxPlayer(player);
            godPlayers.add(player);
        }
    }

    public void removeGod(Player player) {
        if (isGod(player)) {
            godPlayers.remove(player);
        }
    }

    private void maxPlayer(Player player) {
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setRemainingAir(player.getMaximumAir());
        player.setFireTicks(0);
    }

    @EventHandler
    private void playerHarm(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            if (isGod((Player)event.getEntity())) {
                event.setCancelled(true);
                maxPlayer((Player)event.getEntity());
            }
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("god")) {
            if (sender instanceof Player) {
                if (!((Player)sender).hasPermission("aio.god")) {
                    sender.sendMessage("You don't have permission to execute that command.");
                    return false;
                }
                if (args.length == 0) {
                    changeGod((Player)sender);
                    sender.sendMessage("God mode " + (isGod((Player)sender) ? "enabled" : "disabled") + ".");
                    return false;
                }
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("yes") || args[0].equalsIgnoreCase("y") || args[0].equalsIgnoreCase("true")) {
                        addGod((Player)sender);
                        sender.sendMessage("God mode enabled.");
                        return false;
                    }

                    if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("no") || args[0].equalsIgnoreCase("n") || args[0].equalsIgnoreCase("false")) {
                        removeGod((Player)sender);
                        sender.sendMessage("God mode disabled.");
                        return false;
                    }
                }

                if (!((Player)sender).hasPermission("aio.god.others")) {
                    sender.sendMessage("You don't have permission to change god mode for other players.");
                    return false;
                }

                if (args.length == 1) {
                    if (plugin.getServer().getPlayer(args[0]) != null) {
                        changeGod(plugin.getServer().getPlayer(args[0]));
                        sender.sendMessage("God mode enabled for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
                        return false;
                    } else {
                        sender.sendMessage("Player not found.");
                        return false;
                    }
                }

                if (args.length == 2) {
                    if (plugin.getServer().getPlayer(args[1]) != null) {
                        if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("yes") || args[0].equalsIgnoreCase("y") || args[0].equalsIgnoreCase("true")) {
                            addGod(plugin.getServer().getPlayer(args[1]));
                            sender.sendMessage("God mode enabled for " + plugin.getServer().getPlayer(args[1]).getDisplayName() + ".");
                            return false;
                        }

                        if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("no") || args[0].equalsIgnoreCase("n") || args[0].equalsIgnoreCase("false")) {
                            removeGod(plugin.getServer().getPlayer(args[1]));
                            sender.sendMessage("God mode disabled for " + plugin.getServer().getPlayer(args[1]).getDisplayName() + ".");
                            return false;
                        }
                    }

                    if (plugin.getServer().getPlayer(args[0]) != null) {
                        if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("yes") || args[1].equalsIgnoreCase("y") || args[1].equalsIgnoreCase("true")) {
                            addGod(plugin.getServer().getPlayer(args[0]));
                            sender.sendMessage("God mode enabled for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
                            return false;
                        }

                        if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("no") || args[1].equalsIgnoreCase("n") || args[1].equalsIgnoreCase("false")) {
                            removeGod(plugin.getServer().getPlayer(args[0]));
                            sender.sendMessage("God mode disabled for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
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
                        changeGod(plugin.getServer().getPlayer(args[0]));
                        sender.sendMessage("God mode " + (isGod(plugin.getServer().getPlayer(args[0])) ? "enabled" : "disabled") + " for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
                        return false;
                    }
                }
                if (args.length == 2) {
                    if (plugin.getServer().getPlayer(args[1]) != null) {
                        if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("yes") || args[0].equalsIgnoreCase("y") || args[0].equalsIgnoreCase("true")) {
                            addGod(plugin.getServer().getPlayer(args[1]));
                            sender.sendMessage("God mode enabled for " + plugin.getServer().getPlayer(args[1]).getDisplayName() + ".");
                            return false;
                        }

                        if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("no") || args[0].equalsIgnoreCase("n") || args[0].equalsIgnoreCase("false")) {
                            removeGod(plugin.getServer().getPlayer(args[1]));
                            sender.sendMessage("God mode disabled for " + plugin.getServer().getPlayer(args[1]).getDisplayName() + ".");
                            return false;
                        }
                    }

                    if (plugin.getServer().getPlayer(args[0]) != null) {
                        if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("yes") || args[1].equalsIgnoreCase("y") || args[1].equalsIgnoreCase("true")) {
                            addGod(plugin.getServer().getPlayer(args[0]));
                            sender.sendMessage("God mode enabled for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
                            return false;
                        }

                        if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("no") || args[1].equalsIgnoreCase("n") || args[1].equalsIgnoreCase("false")) {
                            removeGod(plugin.getServer().getPlayer(args[0]));
                            sender.sendMessage("God mode disabled for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
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

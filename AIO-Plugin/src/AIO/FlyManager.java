package AIO;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.ArrayList;
import java.util.List;

public class FlyManager implements Listener, CommandExecutor {

    aio plugin;

    List<Player> flyingPlayers = new ArrayList<>();

    FlyManager(aio plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("fly").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean canFly(Player player) {
        return flyingPlayers.contains(player);
    }

    public void changeFly(Player player) {
        if (canFly(player)) {
            removeFly(player);
        } else {
            addFly(player);
        }
    }

    public void addFly(Player player) {
        if (!canFly(player)) {
            flyingPlayers.add(player);
        }
    }

    public void removeFly(Player player) {
        if (canFly(player)) {
            flyingPlayers.remove(player);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.getPlayer().setAllowFlight(true);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("fly")) {
            if (sender instanceof Player) {
                if (!((Player)sender).hasPermission("aio.fly")) {
                    sender.sendMessage("You don't have permission to execute that command.");
                    return false;
                }
                if (args.length == 0) {
                    changeFly((Player)sender);
                    sender.sendMessage("Fly " + (canFly((Player)sender) ? "enabled" : "disabled") + ".");
                    return false;
                }
                if (args.length == 1) {
                    if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("yes") || args[0].equalsIgnoreCase("y") || args[0].equalsIgnoreCase("true")) {
                        addFly((Player)sender);
                        sender.sendMessage("Fly enabled.");
                        return false;
                    }

                    if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("no") || args[0].equalsIgnoreCase("n") || args[0].equalsIgnoreCase("false")) {
                        removeFly((Player)sender);
                        sender.sendMessage("Fly disabled.");
                        return false;
                    }
                }

                if (!((Player)sender).hasPermission("aio.fly.others")) {
                    sender.sendMessage("You don't have permission to change fly for other players.");
                    return false;
                }

                if (args.length == 1) {
                    if (plugin.getServer().getPlayer(args[0]) != null) {
                        changeFly(plugin.getServer().getPlayer(args[0]));
                        sender.sendMessage("Fly enabled for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
                        return false;
                    } else {
                        sender.sendMessage("Player not found.");
                        return false;
                    }
                }

                if (args.length == 2) {
                    if (plugin.getServer().getPlayer(args[1]) != null) {
                        if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("yes") || args[0].equalsIgnoreCase("y") || args[0].equalsIgnoreCase("true")) {
                            addFly(plugin.getServer().getPlayer(args[1]));
                            sender.sendMessage("Fly enabled for " + plugin.getServer().getPlayer(args[1]).getDisplayName() + ".");
                            return false;
                        }

                        if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("no") || args[0].equalsIgnoreCase("n") || args[0].equalsIgnoreCase("false")) {
                            removeFly(plugin.getServer().getPlayer(args[1]));
                            sender.sendMessage("Fly disabled for " + plugin.getServer().getPlayer(args[1]).getDisplayName() + ".");
                            return false;
                        }
                    }

                    if (plugin.getServer().getPlayer(args[0]) != null) {
                        if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("yes") || args[1].equalsIgnoreCase("y") || args[1].equalsIgnoreCase("true")) {
                            addFly(plugin.getServer().getPlayer(args[0]));
                            sender.sendMessage("Fly enabled for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
                            return false;
                        }

                        if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("no") || args[1].equalsIgnoreCase("n") || args[1].equalsIgnoreCase("false")) {
                            removeFly(plugin.getServer().getPlayer(args[0]));
                            sender.sendMessage("Fly disabled for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
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
                        changeFly(plugin.getServer().getPlayer(args[0]));
                        sender.sendMessage("Fly " + (canFly(plugin.getServer().getPlayer(args[0])) ? "enabled" : "disabled") + " for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
                        return false;
                    }
                }
                if (args.length == 2) {
                    if (plugin.getServer().getPlayer(args[1]) != null) {
                        if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("yes") || args[0].equalsIgnoreCase("y") || args[0].equalsIgnoreCase("true")) {
                            removeFly(plugin.getServer().getPlayer(args[1]));
                            sender.sendMessage("Fly enabled for " + plugin.getServer().getPlayer(args[1]).getDisplayName() + ".");
                            return false;
                        }

                        if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("no") || args[0].equalsIgnoreCase("n") || args[0].equalsIgnoreCase("false")) {
                            removeFly(plugin.getServer().getPlayer(args[1]));
                            sender.sendMessage("Fly disabled for " + plugin.getServer().getPlayer(args[1]).getDisplayName() + ".");
                            return false;
                        }
                    }

                    if (plugin.getServer().getPlayer(args[0]) != null) {
                        if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("yes") || args[1].equalsIgnoreCase("y") || args[1].equalsIgnoreCase("true")) {
                            addFly(plugin.getServer().getPlayer(args[0]));
                            sender.sendMessage("Fly enabled for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
                            return false;
                        }

                        if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("no") || args[1].equalsIgnoreCase("n") || args[1].equalsIgnoreCase("false")) {
                            removeFly(plugin.getServer().getPlayer(args[0]));
                            sender.sendMessage("Fly disabled for " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ".");
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


    @EventHandler
    public void playerToggleFlight(PlayerToggleFlightEvent event) {
        if (flyingPlayers.contains(event.getPlayer())) {
            return;
        }
        if (!event.getPlayer().hasPermission("aio.doublejump")) {
            return;
        }
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        event.setCancelled(true);
        event.getPlayer().setFlying(false);
        event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().multiply(6.0).setY(2.0));
    }

    @EventHandler
    public void playerToggleSneak(PlayerToggleSneakEvent event) {
        if (flyingPlayers.contains(event.getPlayer())) {
            return;
        }
        if (!event.getPlayer().hasPermission("aio.doublejump")) {
            return;
        }
        if (event.getPlayer().getGameMode() == GameMode.CREATIVE || event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
            return;
        }
        event.setCancelled(true);
        event.getPlayer().setFlying(false);
        event.getPlayer().setVelocity(event.getPlayer().getLocation().getDirection().setX(0).setY(-10.0).setZ(0));
    }
}

package AIO;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;

public class FreezeManager implements Listener, CommandExecutor {
    aio plugin;

    List<Player> frozenPlayers = new ArrayList<Player>();

    FreezeManager(aio plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("freeze").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean isFrozen(Player player) {
        return frozenPlayers.contains(player);
    }

    public void changeFrozen(Player player) {
        if (isFrozen(player)) {
            removeFrozen(player);
        } else {
            addFrozen(player);
        }
    }

    public void addFrozen(Player player) {
        if (!isFrozen(player)) {
            frozenPlayers.add(player);
        }
    }

    public void removeFrozen(Player player) {
        if (isFrozen(player)) {
            frozenPlayers.remove(player);
        }
    }

    @EventHandler
    private void playerMove(PlayerMoveEvent event) {
        if (frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
            event.setTo(event.getFrom());
            event.getPlayer().sendMessage("You are frozen and can not move.");
        }
    }

    @EventHandler
    private void playerTeleport(PlayerTeleportEvent event) {
        if (frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("You are frozen and can not teleport.");
        }
    }

    @EventHandler
    private void playerKick(PlayerKickEvent event) {
        if (frozenPlayers.contains(event.getPlayer()) && event.getReason().equals("Flying is not enabled on this server")) {
            event.setCancelled(true);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("freeze")) {
            if (sender instanceof Player) {
                if (((Player) sender).hasPermission("aio.freeze")) {
                    if (args.length < 1) {
                        sender.sendMessage("Player not given");
                        return false;
                    } else if (args.length > 1) {
                        sender.sendMessage("Too many arguments.");
                        return false;
                    }
                    if (plugin.getServer().getPlayer(args[0]) != null) {
                        changeFrozen(plugin.getServer().getPlayer(args[0]));
                        sender.sendMessage((isFrozen(plugin.getServer().getPlayer(args[0])) ?  "Frozen " : "Unfrozen ") + plugin.getServer().getPlayer(args[0]).getDisplayName());
                        return false;
                    } else {
                        sender.sendMessage("Player not found.");
                        return false;
                    }
                } else {
                    sender.sendMessage("You don't have permission to execute that command.");
                    return false;
                }
            } else {
                if (args.length < 1) {
                    sender.sendMessage("Player not given");
                    return false;
                } else if (args.length > 1) {
                    sender.sendMessage("Too many arguments.");
                    return false;
                }
                if (plugin.getServer().getPlayer(args[0]) != null) {
                    changeFrozen(plugin.getServer().getPlayer(args[0]));
                    sender.sendMessage((isFrozen(plugin.getServer().getPlayer(args[0])) ? "Frozen " : "Unfrozen ") + plugin.getServer().getPlayer(args[0]).getDisplayName());
                    return false;
                } else {
                    sender.sendMessage("Player not found.");
                    return false;
                }
            }
        }
        return false;
    }
}

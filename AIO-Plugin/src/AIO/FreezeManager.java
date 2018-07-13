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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class FreezeManager implements Listener, CommandExecutor {
    private aio plugin;

    private List<Player> frozenPlayers = new ArrayList<>();
    private List<Player> unfrozenPlayers = new ArrayList<>();

    FreezeManager(aio plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("freeze").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean isFrozen(Player player) { return frozenPlayers.contains(player); }

    public void changeFrozen(Player player) {
        if (isFrozen(player)) {
            removeFrozen(player);
        } else {
            addFrozen(player);
        }
    }

    public void addFrozen(Player player) {
        if (!isFrozen(player)) { frozenPlayers.add(player); }
    }

    public void removeFrozen(Player player) {
        if (isFrozen(player)) {
            frozenPlayers.remove(player);
            unfrozenPlayers.add(player);
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnGround() || !player.isOnline()) {
                        unfrozenPlayers.remove(player);
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 1, 2);
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("freeze")) {
            if(sender instanceof Player && !sender.hasPermission("aio.freeze")) {
                sender.sendMessage(plugin.getMessage("aio.no_permission"));
                return true;
            }
            if(args.length != 1) {
                sender.sendMessage(plugin.getMessage("freezemanager.usage"));
                return true;
            }
            if (plugin.getServer().getPlayer(args[0]) == null) {
                sender.sendMessage(plugin.getMessage("aio.player_not_found_1", args[0]));
                return true;
            }
            changeFrozen(plugin.getServer().getPlayer(args[0]));
            String status = (isFrozen(plugin.getServer().getPlayer(args[0])) ?  "Frozen" : "Unfrozen");
            sender.sendMessage(plugin.getMessage("freezemanager.set", status , aio.getPlayerName(plugin.getServer().getPlayer(args[0]))));
            return true;
        }

        return false;
    }

    @EventHandler
    private void playerMove(PlayerMoveEvent event) {
        if (frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
            event.setTo(event.getFrom());
            event.getPlayer().sendMessage(plugin.getMessage("freezemanager.move"));
        }
    }

    @EventHandler
    private void playerTeleport(PlayerTeleportEvent event) {
        if (frozenPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
            event.getPlayer().sendMessage(plugin.getMessage("freezemanager.teleport"));
        }
    }

    @EventHandler
    private void playerKick(PlayerKickEvent event) {
        if ((unfrozenPlayers.contains(event.getPlayer()) || frozenPlayers.contains(event.getPlayer())) && event.getReason().equals(plugin.getMessage("freezemanager.kick_reason"))) {
            event.setCancelled(true);
        }
    }
}

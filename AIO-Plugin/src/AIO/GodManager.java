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
    private aio plugin;

    private List<Player> godPlayers = new ArrayList<>();

    GodManager(aio plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("god").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean isGod(Player player) { return godPlayers.contains(player); }

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
        if (isGod(player)) { godPlayers.remove(player); }
    }

    private void maxPlayer(Player player) {
        player.setHealth(20.0);
        player.setFoodLevel(20);
        player.setRemainingAir(player.getMaximumAir());
        player.setFireTicks(0);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("god")) {
            if (!sender.hasPermission("aio.god")) {
                sender.sendMessage(plugin.getMessage("aio.no_permission"));
                return true;
            }
            if (args.length == 0) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(plugin.getMessage("godmanager.usage_admin_1"));
                    return true;
                }
                changeGod((Player)sender);
                sender.sendMessage(plugin.getMessage("godmanager.set_self", (isGod((Player)sender) ? "enabled" : "disabled")));
                return true;
            }
            if (args.length == 1 && sender instanceof Player) {
                if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("yes") || args[0].equalsIgnoreCase("y") || args[0].equalsIgnoreCase("true")) {
                    addGod((Player)sender);
                    sender.sendMessage(plugin.getMessage("godmanager.set_self", "enabled"));
                    return true;
                }
                if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("no") || args[0].equalsIgnoreCase("n") || args[0].equalsIgnoreCase("false")) {
                    removeGod((Player)sender);
                    sender.sendMessage(plugin.getMessage("godmanager.set_self", "disabled"));
                    return true;
                }
            }
            if (!sender.hasPermission("aio.god.admin")) {
                sender.sendMessage(plugin.getMessage("godmanager.usage_player"));
                return true;
            }
            if (args.length == 1) {
                if (plugin.getServer().getPlayer(args[0]) == null) {
                    sender.sendMessage(plugin.getMessage("aio.player_not_found_1", args[0]));
                    return true;
                }
                changeGod(plugin.getServer().getPlayer(args[0]));
                String status = (isGod(plugin.getServer().getPlayer(args[0])) ? "enabled" : "disabled");
                sender.sendMessage(plugin.getMessage("godmanager.set_player", status, aio.getPlayerName(plugin.getServer().getPlayer(args[0]))));
                plugin.getServer().getPlayer(args[0]).sendMessage(plugin.getMessage("godmanager.set_self", status));
                return true;
            }
            if (args.length == 2) {
                if (plugin.getServer().getPlayer(args[0]) != null) {
                    if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("yes") || args[1].equalsIgnoreCase("y") || args[1].equalsIgnoreCase("true")) {
                        addGod(plugin.getServer().getPlayer(args[0]));
                        sender.sendMessage(plugin.getMessage("godmanager.set_player", "enabled", aio.getPlayerName(plugin.getServer().getPlayer(args[0]))));
                        plugin.getServer().getPlayer(args[0]).sendMessage(plugin.getMessage("godmanager.set_self", "enabled"));
                        return true;
                    }
                    if (args[1].equalsIgnoreCase("off") || args[1].equalsIgnoreCase("no") || args[1].equalsIgnoreCase("n") || args[1].equalsIgnoreCase("false")) {
                        removeGod(plugin.getServer().getPlayer(args[0]));
                        sender.sendMessage(plugin.getMessage("godmanager.set_player", "disabled", aio.getPlayerName(plugin.getServer().getPlayer(args[0]))));
                        plugin.getServer().getPlayer(args[0]).sendMessage(plugin.getMessage("godmanager.set_self", "disabled"));
                        return true;
                    }
                    sender.sendMessage(plugin.getMessage("godmanager.usage_admin_2"));
                    return true;
                } else if (plugin.getServer().getPlayer(args[1]) != null) {
                    if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("yes") || args[0].equalsIgnoreCase("y") || args[0].equalsIgnoreCase("true")) {
                        addGod(plugin.getServer().getPlayer(args[1]));
                        sender.sendMessage(plugin.getMessage("godmanager.set_player", "enabled", aio.getPlayerName(plugin.getServer().getPlayer(args[1]))));
                        plugin.getServer().getPlayer(args[1]).sendMessage(plugin.getMessage("godmanager.set_self", "enabled"));
                        return true;
                    }
                    if (args[0].equalsIgnoreCase("off") || args[0].equalsIgnoreCase("no") || args[0].equalsIgnoreCase("n") || args[0].equalsIgnoreCase("false")) {
                        removeGod(plugin.getServer().getPlayer(args[1]));
                        sender.sendMessage(plugin.getMessage("godmanager.set_player", "disabled", aio.getPlayerName(plugin.getServer().getPlayer(args[1]))));
                        plugin.getServer().getPlayer(args[1]).sendMessage(plugin.getMessage("godmanager.set_self", "disabled"));
                        return true;
                    }
                    sender.sendMessage(plugin.getMessage("godmanager.usage_admin_3"));
                    return true;
                } else {
                    sender.sendMessage(plugin.getMessage("aio.player_not_found_2"));
                    return true;
                }
            }
        }

        return false;
    }

    @EventHandler
    private void playerHarm(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) { return; }
        if (!isGod((Player)event.getEntity())) { return; }
        event.setCancelled(true);
        maxPlayer((Player)event.getEntity());
    }
}

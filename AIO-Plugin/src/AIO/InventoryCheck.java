package AIO;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;

import java.util.HashMap;
import java.util.Map;

public class InventoryCheck implements Listener, CommandExecutor {

    aio plugin;

    Map<Player, Player> inventories = new HashMap<>();
    Map<Player, Player> enderchests = new HashMap<>();

    InventoryCheck(aio plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("invsee").setExecutor(this);
        plugin.getServer().getPluginCommand("endersee").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("invsee")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("aio.invsee")) {
                    if (args.length == 0) {
                        sender.sendMessage("No player given.");
                    } else {
                        if (plugin.getServer().getPlayer(args[0]) != null) {
                            inventories.put(plugin.getServer().getPlayer(args[0]), (Player) sender);
                            ((Player) sender).openInventory(plugin.getServer().getPlayer(args[0]).getInventory());
                        } else {
                            sender.sendMessage("Player not found.");
                        }
                    }
                } else {
                    sender.sendMessage("You don't have permission to execute that command.");
                }
            } else {
                sender.sendMessage("Only players can execute this command.");
            }
        }

        if (command.getName().equalsIgnoreCase("endersee")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("aio.endersee")) {
                    if (args.length == 0) {
                        sender.sendMessage("No player given.");
                    } else {
                        if (plugin.getServer().getPlayer(args[0]) != null) {
                            enderchests.put(plugin.getServer().getPlayer(args[0]), (Player)sender);
                            ((Player)sender).openInventory(plugin.getServer().getPlayer(args[0]).getEnderChest());
                        } else {
                            sender.sendMessage("Player not found.");
                        }
                    }
                } else {
                    sender.sendMessage("You don't have permission to execute that command.");
                }
            } else {
                sender.sendMessage("Only players can execute this command.");
            }
        }
        return false;
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent event) {
        if (enderchests.containsValue((Player)event.getPlayer())) {
            for (Player player: enderchests.keySet()) {
                if (enderchests.get(player) == (Player)event.getPlayer()) {
                    enderchests.remove(player);
                }
            }
        }
        if (inventories.containsValue((Player)event.getPlayer())) {
            for (Player player: inventories.keySet()) {
                if (inventories.get(player) == (Player)event.getPlayer()) {
                    inventories.remove(player);
                }
            }
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {
        if (inventories.containsKey((Player)event.getWhoClicked())) {
            inventories.get((Player)event.getWhoClicked()).openInventory(event.getWhoClicked().getInventory());
            return;
        }
        if (inventories.containsValue((Player)event.getWhoClicked())) {
            for (Player player: inventories.keySet()) {
                if (inventories.get(player) == (Player)event.getWhoClicked()) {
                    player.getInventory().setStorageContents(event.getInventory().getStorageContents());
                }
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void inventoryDrag(InventoryDragEvent event) {
        if (inventories.containsKey((Player)event.getWhoClicked())) {
            inventories.get((Player)event.getWhoClicked()).openInventory(event.getWhoClicked().getInventory());
            inventories.get((Player)event.getWhoClicked()).updateInventory();
            return;
        }
        if (inventories.containsValue((Player)event.getWhoClicked())) {
            for (Player player: inventories.keySet()) {
                if (inventories.get(player) == (Player)event.getWhoClicked()) {
                    player.getInventory().setStorageContents(event.getInventory().getStorageContents());
                }
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void inventoryInteract(InventoryInteractEvent event) {
        if (inventories.containsKey((Player)event.getWhoClicked())) {
            inventories.get((Player)event.getWhoClicked()).openInventory(event.getWhoClicked().getInventory());
            inventories.get((Player)event.getWhoClicked()).updateInventory();
            return;
        }
        if (inventories.containsValue((Player)event.getWhoClicked())) {
            for (Player player: inventories.keySet()) {
                if (inventories.get(player) == (Player)event.getWhoClicked()) {
                    player.getInventory().setStorageContents(event.getInventory().getStorageContents());
                }
                player.updateInventory();
            }
        }
    }
}

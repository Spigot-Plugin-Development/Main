package AIO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DropParty implements Listener, CommandExecutor {
    aio plugin;

    List<ItemStack> items = new ArrayList<>();
    List<Player> dropping = new ArrayList<>();
    boolean paused;

    BukkitRunnable dropTask;

    DropParty(aio plugin) {
        this.plugin = plugin;
        plugin.getCommand("dropparty").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        paused = true;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
        if (command.getName().equalsIgnoreCase("dropparty")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("aio.dropparty")) {
                    if (args.length == 0) {
                        items.forEach((item) -> {
                            sender.sendMessage(item.getAmount() + "x " + item.getType().toString());
                        });
                        return false;
                    }
                    if (args[0].equalsIgnoreCase("add")) {
                        if (sender.hasPermission("aio.dropparty.add")) {
                            dropping.add((Player)sender);
                            ((Player)sender).openInventory(Bukkit.createInventory(null, 3 * 9));
                        } else {
                            sender.sendMessage("You don't have permission for that.");
                        }
                    }
                    if (args[0].equalsIgnoreCase("start")) {
                        if (sender.hasPermission("aio.dropparty.start")) {
                            plugin.getServer().broadcastMessage("A drop party will begin soon!");
                            new BukkitRunnable() {
                                @Override
                                public void run() {
                                    if (!paused) {
                                        Player player = (Player)plugin.getServer().getOnlinePlayers().toArray()[new Random().nextInt(plugin.getServer().getOnlinePlayers().size())];
                                        int itemID = new Random().nextInt(items.size());
                                        ItemStack item = items.get(itemID);
                                        plugin.getServer().broadcastMessage(player.getDisplayName() + " won the next item:" + item.getAmount() + "x " + item.getType().toString().toLowerCase());
                                        player.getInventory().addItem(item);
                                        items.remove(item);
                                    }
                                    if (items.isEmpty()) {
                                        plugin.getServer().broadcastMessage("The drop party has ended.");
                                        cancel();
                                    }
                                }
                            }.runTaskTimer(plugin, 15 * 20, 5 * 20);
                            paused = false;
                        } else {
                            sender.sendMessage("You don't have permission for that.");
                        }
                    }
                    if (args[0].equalsIgnoreCase("pause")) {
                        if (sender.hasPermission("aio.dropparty.pause")) {
                            paused = true;
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
        if (dropping.contains(event.getPlayer())) {
            for (ItemStack item: event.getInventory().getContents()) {
                if (item == null || item.getType() == Material.AIR) {
                    continue;
                }
                items.add(item);
            }
        }
    }
}

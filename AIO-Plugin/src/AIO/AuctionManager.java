package AIO;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class AuctionManager implements CommandExecutor {
    private aio plugin;

    ItemStack auctionedItem;
    Double time = 0.0d;
    Double highestBid = 0.0d;
    UUID bidder;
    Double increment = 0.0d;

    AuctionManager(aio plugin) {
        this.plugin = plugin;
        plugin.getCommand("auction").setExecutor(this);
        plugin.getCommand("bid").setExecutor(this);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (auctionedItem != null) {
                    if (time <= 0) {
                        if (bidder == null) {
                            plugin.getServer().broadcastMessage("No one bid for the item.");
                        } else {
                            plugin.getServer().broadcastMessage(plugin.getServer().getPlayer(bidder).getDisplayName() + " won the auction!");
                            plugin.getServer().getPlayer(bidder).getInventory().addItem(auctionedItem);
                        }
                        auctionedItem = null;
                        highestBid = 0.0d;
                        bidder = null;
                        increment = 0.0d;
                    } else {
                        time--;
                        if (time == 60) {
                            plugin.getServer().broadcastMessage("1 minute remaining from auction!");
                        }
                        if (time == 30) {
                            plugin.getServer().broadcastMessage("30 seconds remaining from auction!");
                        }
                        if (time == 10) {
                            plugin.getServer().broadcastMessage("10 seconds remaining from auction!");
                        }
                        if (time == 5) {
                            plugin.getServer().broadcastMessage("5 seconds remaining from auction!");
                        }
                        if (time == 4) {
                            plugin.getServer().broadcastMessage("4 seconds remaining from auction!");
                        }
                        if (time == 3) {
                            plugin.getServer().broadcastMessage("3 seconds remaining from auction!");
                        }
                        if (time == 2) {
                            plugin.getServer().broadcastMessage("2 seconds remaining from auction!");
                        }
                        if (time == 1) {
                            plugin.getServer().broadcastMessage("1 second remaining from auction!");
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    public void startAuction(Double time, Double startBid, ItemStack item, Double increment) {
        auctionedItem = item;
        highestBid = startBid;
        this.time = time;
        plugin.getServer().broadcastMessage("A new auction is starting!");
        plugin.getServer().broadcastMessage(item.getAmount() + "x " + item.getType().toString());
        plugin.getServer().broadcastMessage("Starting price: $" + startBid);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("auction")) {
            if (args.length == 0) {
                if (sender.hasPermission("aio.auction")) {
                    if (auctionedItem != null) {
                        sender.sendMessage("Current auction:");
                        sender.sendMessage(auctionedItem.getAmount() + "x" + auctionedItem.getItemMeta().getDisplayName() + " (" + auctionedItem.getType().toString() + ")");
                        if (bidder == null) {
                            sender.sendMessage("No bids so far.");
                        } else {
                            sender.sendMessage("Highest bid: $" + highestBid + " by " + plugin.getServer().getPlayer(bidder).getDisplayName());
                        }
                        sender.sendMessage("Time left: " + time);
                        return false;
                    } else {
                        sender.sendMessage("There is no auction running.");
                        return false;
                    }
                } else {
                    sender.sendMessage("You don't have permission to execute that command.");
                    return false;
                }
            }
            if (args[0].equalsIgnoreCase("start")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage("Only players can execute this command.");
                    return false;
                }
                if (!sender.hasPermission("aio.auction.create")) {
                    sender.sendMessage("You don't have permission for that.");
                    return false;
                }
                if (auctionedItem != null) {
                    sender.sendMessage("An auction is already running.");
                    return false;
                }
                if (((Player)sender).getInventory().getItemInMainHand() == null || ((Player)sender).getInventory().getItemInMainHand().getType() == Material.AIR) {
                    sender.sendMessage("You must be holding an item to auction.");
                    return false;
                }
                if (args.length != 4) {
                    sender.sendMessage("Usage: /" + label + " start <time> <starting price> <incement>");
                    return false;
                }
                startAuction(Double.parseDouble(args[1]), Double.parseDouble(args[2]), ((Player)sender).getInventory().getItemInMainHand(), Double.parseDouble(args[3]));
            }
            if (args[0].equalsIgnoreCase("end")) {
                if (!sender.hasPermission("aio.auction.end")) {
                    sender.sendMessage("You don't have permission for that.");
                    return false;
                }
                if (auctionedItem == null) {
                    sender.sendMessage("No auction is currently running.");
                    return false;
                }
                time = 0.0d;
            }
            if (args[0].equalsIgnoreCase("cancel")) {
                if (!sender.hasPermission("aio.auction.cancel")) {
                    sender.sendMessage("You don't have permission for that.");
                    return false;
                }
                if (auctionedItem == null) {
                    sender.sendMessage("No auction is currently running.");
                    return false;
                }
                auctionedItem = null;
                highestBid = 0.0d;
                bidder = null;
                increment = 0.0d;
            }
        }

        if (command.getName().equalsIgnoreCase("bid")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can execute this command.");
                return false;
            }
            if (!sender.hasPermission("aio.auction.bid")) {
                sender.sendMessage("You don't have permission to execute this command.");
                return false;
            }
            if (auctionedItem == null) {
                sender.sendMessage("No item is being currently auctioned.");
                return false;
            }
            if (((Player)sender).getUniqueId().equals(bidder)) {
                sender.sendMessage("You are already the highest bidder.");
                return false;
            }
            if (plugin.economyManager.getBalance(((Player)sender).getUniqueId()) < highestBid + increment) {
                sender.sendMessage("Insufficient funds.");
                return false;
            }
            if (bidder != null) {
                plugin.economyManager.add(bidder, highestBid);
            }
            bidder = ((Player)sender).getUniqueId();
            highestBid = highestBid + increment;
            plugin.economyManager.add(bidder, -highestBid);
            plugin.getServer().broadcastMessage(sender.getName() + " bid $" + highestBid);
        }
        return false;
    }
}

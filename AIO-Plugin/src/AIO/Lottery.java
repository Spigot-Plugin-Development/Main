package AIO;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.util.*;
import java.util.Map;

public class Lottery implements Listener,CommandExecutor {
    aio plugin;
    double untilDraw;
    int count;
    Map<UUID, Integer> tickets = new HashMap<>();
    Map<UUID, Integer> prizes = new HashMap<>();

    Lottery(aio plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("lottery").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        untilDraw = 20 * 60 * 60;
        plugin.sqlconnector.query("SELECT * FROM minecraft_lottery", new SQLCallback() {
            @Override
            public void callback(ResultSet result) {
                try {
                    while (result.next()) {
                        if (result.getInt("minecraft_lottery_prize") > 0) {
                            prizes.put(UUID.fromString(result.getString("minecraft_lottery_uuid")), result.getInt("minecraft_lottery_prize"));
                        }
                        if (result.getInt("minecraft_lottery_tickets") > 0) {
                            prizes.put(UUID.fromString(result.getString("minecraft_lottery_uuid")), result.getInt("minecraft_lottery_tickets"));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                untilDraw -= 20;
                if (untilDraw <= 0) {
                    untilDraw = 20 * 60 * 60;
                    if (tickets.size() > 0) {
                        List<UUID> allTickets = new ArrayList<>();
                        tickets.forEach((uuid, count) -> {
                            for (int i = 0; i < count; i++) {
                                allTickets.add(uuid);
                            }
                        });
                        OfflinePlayer winner = plugin.getServer().getOfflinePlayer(allTickets.get(new Random().nextInt(allTickets.size())));
                        plugin.getServer().broadcastMessage(winner.getName() + " won $" + 1000 * totalTickets() + " in the lottery with " + ticketCount(winner.getUniqueId()) + " tickets!");
                        if (prizes.containsKey(winner.getUniqueId())) {
                            prizes.replace(winner.getUniqueId(), prizes.get(winner.getUniqueId()) + 1000 * totalTickets());
                        } else {
                            prizes.put(winner.getUniqueId(), 1000 * totalTickets());
                        }
                        tickets.clear();
                    } else {
                        plugin.getServer().broadcastMessage("Nobody bought tickets this round.");
                    }
                }
            }
        }.runTaskTimer(plugin, 20, 20);
    }

    public void disable() {
        plugin.sqlconnector.update("TRUNCATE TABLE minecraft_lottery;", new SQLCallback());
        tickets.forEach((uuid, tickets) -> {
            plugin.sqlconnector.update("INSERT INTO minecraft_lottery (minecraft_lottery_uuid, minecraft_lottery_tickets) VALUES (" +
                    "'" + uuid + "', " +
                    "'" + tickets + "' " +
                    ") ON DUPLICATE KEY UPDATE minecraft_lottery_uuid = '" + uuid + "', minecraft_lottery_tickets = '" + tickets + "';"
                    , new SQLCallback());
        });
        prizes.forEach((uuid, prize) -> {
            plugin.sqlconnector.update("INSERT INTO minecraft_lottery (minecraft_lottery_uuid, minecraft_lottery_prize) VALUES (" +
                            "'" + uuid + "', " +
                            "'" + prize + "' " +
                            ") ON DUPLICATE KEY UPDATE minecraft_lottery_uuid = '" + uuid + "', minecraft_lottery_prize = '" + prize + "';"
                    , new SQLCallback());
        });
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (prizes.containsKey(event.getPlayer().getUniqueId())) {
            event.getPlayer().sendMessage("You have unclaimed rewards from lottery! /lot claim");
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("lottery")) {
            if (args.length == 0) {
                if (!sender.hasPermission("aio.lottery")) {
                    sender.sendMessage("You don't have permission to execute that command.");
                    return false;
                }
                sender.sendMessage(aio.colorize("&7------------------ &5&lLottery &7------------------"));
                sender.sendMessage(aio.colorize("&bDraw in: " + (int) untilDraw / 20 / 60 + "m " + untilDraw / 20 % 60 + "s"));
                if (sender instanceof Player && ticketCount(((Player)sender).getUniqueId()) > 0) {
                    sender.sendMessage(aio.colorize("&bYour tickets: " + ticketCount(((Player)sender).getUniqueId())));
                } else {
                    sender.sendMessage("You don't have any tickets. Buy with /lot buy");
                }
                sender.sendMessage("Total tickets: " + totalTickets());
                return false;
            }
            if (args[0].equalsIgnoreCase("buy")) {
                if (sender instanceof Player) {
                    if (!sender.hasPermission("aio.lottery.buy")) {
                        sender.sendMessage("You don't have permission for that.");
                        return false;
                    }
                    if (args.length == 1) {
                        if (plugin.economyManager.getBalance(((Player)sender).getUniqueId()) >= 1000) {
                            plugin.economyManager.add(((Player)sender).getUniqueId(), -1000);
                            buyTicket((Player)sender, 1);
                        } else {
                            sender.sendMessage("Insufficient funds.");
                        }
                    } else {
                        if (plugin.economyManager.getBalance(((Player)sender).getUniqueId()) >= 1000 * Integer.parseInt(args[1])) {
                            plugin.economyManager.add(((Player)sender).getUniqueId(), -1000 * Integer.parseInt(args[1]));
                            buyTicket((Player)sender, Integer.parseInt(args[1]));
                        } else {
                            sender.sendMessage("Insufficient funds.");
                        }
                    }
                } else {
                    sender.sendMessage("Only players can execute this command.");
                }
            }
            if (args[0].equalsIgnoreCase("draw")) {
                if (sender.hasPermission("aio.lottery.draw")) {
                    untilDraw = 20;
                } else {
                    sender.sendMessage("You don't have permission for that");
                }
            }
            if (args[0].equalsIgnoreCase("claim")) {
                if (sender instanceof Player) {
                    if (sender.hasPermission("aio.lottery.claim")) {
                        if (prizes.containsKey(((Player)sender).getUniqueId())) {
                            plugin.economyManager.add(((Player)sender).getUniqueId(), prizes.get(((Player)sender).getUniqueId()));
                            sender.sendMessage("Successfully claimed $" + prizes.get(((Player) sender).getUniqueId()));
                            prizes.remove(((Player)sender).getUniqueId());
                        } else {
                            sender.sendMessage("You have nothing unclaimed.");
                        }
                    } else {
                        sender.sendMessage("You don't have permission for that.");
                    }
                } else {
                    sender.sendMessage("Only players can execute this command.");
                }
            }
        }
        return false;
    }

    public void buyTicket(Player player, int amount) {
        if (tickets.containsKey(player.getUniqueId())) {
            tickets.replace(player.getUniqueId(), ticketCount(player.getUniqueId()) + amount);
        } else {
            tickets.put(player.getUniqueId(), amount);
        }
    }

    public int totalTickets() {
        count = 0;
        tickets.forEach((uuid, ticketCount) -> {
            count += ticketCount;
        });
        return count;
    }

    public int ticketCount(UUID uuid) {
        if (tickets.containsKey(uuid)) {
            return tickets.get(uuid);
        } else {
            return 0;
        }
    }
}

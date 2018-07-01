package AIO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class ChatGames implements Listener {

    aio plugin;
    String goal = "";
    String[] words = {"glasses", "toothbrush", "bracelet", "cupcake", "apple", "key", "desk", "melody", "card", "flower", "pencil", "bucket", "industry"};

    ChatGames(aio plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        new BukkitRunnable() {
            @Override
            public void run() {
                switch (new Random().nextInt(4)) {
                    case 0:
                        int o1 = new Random().nextInt(900) + 100;
                        int o2 = new Random().nextInt(900) + 100;
                        goal = "" + (o1 + o2);
                        plugin.getServer().broadcastMessage(aio.colorize("The first player to correctly type the result of " + o1 + " + " + o2 + " wins $100!"));
                        break;
                    case 1:
                        goal = words[new Random().nextInt(words.length)];
                        plugin.getServer().broadcastMessage(aio.colorize("The first player to type " + goal + " into chat wins $100!"));
                        break;
                    case 2:
                        goal = words[new Random().nextInt(words.length)];
                        JSONMessage message = JSONMessage.create("The first player to type ").then("the word hidden here").tooltip(goal).then(" in chat wins $100!");
                        sendAll(message);
                        break;
                    case 3:
                        int ox1 = new Random().nextInt(40) + 10;
                        int ox2 = new Random().nextInt(40) + 10;
                        goal = "" + (ox1 * ox2);
                        plugin.getServer().broadcastMessage("The first player to correctly type the result of " + ox1 + " * " + ox2 + " wins $100!");
                    case 4:
                        goal = words[new Random().nextInt(words.length)];
                        List<String> scrambled = Arrays.asList(goal.split(""));
                        Collections.shuffle(scrambled);
                        String scramble = "";
                        for (String letter: scrambled) {
                            scramble += letter;
                        }
                        JSONMessage message1 = JSONMessage.create("The first player to unscramble the characters ").then(scramble).color(ChatColor.GREEN).then(" wins $100!");
                        sendAll(message1);
                    default: break;
                }
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!goal.isEmpty()) {
                            plugin.getServer().broadcastMessage("No one got it right this time. The solution was " + goal);
                            goal = "";
                        }
                    }
                }.runTaskLater(plugin, 20 * 20);
            }
        }.runTaskTimer(plugin, 5 * 60 * 20, 10 * 60 * 20);
    }

    public void sendAll(JSONMessage message) {
        for (Player player: plugin.getServer().getOnlinePlayers()) {
            message.send(player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void playerChat(AsyncPlayerChatEvent event) {
        if (!goal.isEmpty()) {
            if (event.getMessage().equals(goal)) {
                goal = "";
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        plugin.getServer().broadcastMessage(event.getPlayer().getDisplayName() + " won the game and $100!");
                        plugin.economyManager.add(event.getPlayer().getUniqueId(), 100);
                    }
                }.runTask(plugin);
            }
        }
    }
}

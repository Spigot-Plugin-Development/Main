package AIO;

import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class ChatGames implements Listener {

    aio plugin;
    String goal;
    String[] words = {"apple", "key", "desk", "melody", "card", "flower", "pencil", "bucket", "industry"};

    ChatGames(aio plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);

        new BukkitRunnable() {
            @Override
            public void run() {
                switch (new Random().nextInt(2)) {
                    case 0:
                        int os1 = new Random().nextInt(900) + 100;
                        int os2 = new Random().nextInt(900) + 100;
                        goal = "" + (os1 + os2);
                        plugin.getServer().broadcastMessage(aio.colorize("The first player to correctly type the result of " + os1 + " + " + os2 + " wins $100!"));
                        break;
                    case 1:
                        goal = words[new Random().nextInt(words.length)];
                        plugin.getServer().broadcastMessage(aio.colorize("The first player to type " + goal + " into chat wins $100!"));
                    case 2:
                        goal = words[new Random().nextInt(words.length)];
                        JSONMessage message = JSONMessage.create("The first player to type ").then("the word hidden here").tooltip(goal).then(" in chat wins $100!");
                        sendAll(message);
                    default: break;
                }
            }
        }.runTaskTimer(plugin, 60 * 20, 60 * 20);
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

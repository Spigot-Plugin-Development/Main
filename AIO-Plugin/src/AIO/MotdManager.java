package AIO;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

import java.util.Random;

public class MotdManager implements Listener {
    private aio plugin;

    MotdManager(aio plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void ping(ServerListPingEvent event) {
        event.setMaxPlayers(event.getNumPlayers() + 1);
        String[] adj = {"amazing", "awesome", "lag-free", "brand new", "cool", "great"};
        event.setMotd(aio.colorize("Join our &5&l" + adj[new Random().nextInt(adj.length)] + "&r server!"));
    }
}

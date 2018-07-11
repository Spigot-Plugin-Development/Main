package AIO;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class CustomSigns implements Listener {
    private aio plugin;

    CustomSigns(aio plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void signColor(SignChangeEvent event) {
        for (int i = 0; i < 4; i++) {
            event.setLine(i, aio.colorize(event.getLine(i)));
        }
    }
}

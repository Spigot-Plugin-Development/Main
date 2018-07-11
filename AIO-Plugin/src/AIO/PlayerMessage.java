package AIO;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerMessage implements Listener {
    private aio plugin;

	private List<Player> mutedPlayers = new ArrayList<>();

	public void mutePlayer(Player player) {
		mutedPlayers.add(player);
	}

	public void unmutePlayer(Player player) {
		mutedPlayers.remove(player);
	}
	
	PlayerMessage(aio plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onMessage(AsyncPlayerChatEvent event) {
		if (mutedPlayers.contains(event.getPlayer())) {
            event.setCancelled(true);
        }

        String playerName;

        if(event.getPlayer().getDisplayName().equalsIgnoreCase("")) {
            playerName = event.getPlayer().getName();
        } else {
            playerName = event.getPlayer().getDisplayName();
        }

        //GROUP NAMES ARE TEMPORARY
        if(plugin.permission.getPrimaryGroup(event.getPlayer()).equalsIgnoreCase("Owner")) {
            event.setFormat(aio.colorize("&7[&9Owner&7] &9" + playerName + "&7 » &f" + event.getMessage()));
        } else if(plugin.permission.getPrimaryGroup(event.getPlayer()).equalsIgnoreCase("Admin")) {
            event.setFormat(aio.colorize("&7[&cAdmin&7] &c" + playerName + "&7 » &f" + event.getMessage()));
        } else if(plugin.permission.getPrimaryGroup(event.getPlayer()).equalsIgnoreCase("Legend")) {
            event.setFormat(aio.colorize("&7[&6Legend&7] &6" + playerName + "&7 » &f" + event.getMessage()));
        } else if(plugin.permission.getPrimaryGroup(event.getPlayer()).equalsIgnoreCase("King")) {
            event.setFormat(aio.colorize("&7[&3King&7] &3" + playerName + "&7 » &f" + event.getMessage()));
        } else if(plugin.permission.getPrimaryGroup(event.getPlayer()).equalsIgnoreCase("Premium")) {
            event.setFormat(aio.colorize("&7[&5Premium&7] &5" + playerName + "&7 » &f" + event.getMessage()));
        } else if(plugin.permission.getPrimaryGroup(event.getPlayer()).equalsIgnoreCase("Vip")) {
            event.setFormat(aio.colorize("&7[&aV.I.P.&7] &a" + playerName + "&7 » &f" + event.getMessage()));
        } else {
            event.setFormat(aio.colorize("&7[&fDefault&7] &f" + playerName + "&7 » &f" + event.getMessage()));
        }
	}
}

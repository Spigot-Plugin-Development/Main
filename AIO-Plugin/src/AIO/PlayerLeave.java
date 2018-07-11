package AIO;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Date;

public class PlayerLeave implements Listener {
	private aio plugin;
	
	PlayerLeave(aio plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	public void onKick(PlayerKickEvent event) {
		plugin.sqlconnector.update("UPDATE minecraft_player SET " +
				"minecraft_player_name = '" + event.getPlayer().getName() + "', " +
				"minecraft_player_last_quit = '" + Convert.DateToString(new Date()) + "', " +
				"minecraft_player_last_ip = '" + event.getPlayer().getAddress().toString() + "', " +
				"minecraft_player_server = '" + plugin.getServer().getName() + "', " +
				"minecraft_player_location = '" + Convert.LocationToString(event.getPlayer().getLocation()) + "', " +
				"minecraft_player_gamemode = '" + Convert.GamemodeToInt(event.getPlayer().getGameMode()) + "', " +
				"minecraft_player_god_mode = '" + Convert.BooleanToInt(plugin.godManager.isGod(event.getPlayer())) + "', " +
				"minecraft_player_fly = '" + Convert.BooleanToInt(plugin.flyManager.canFly(event.getPlayer())) + "', " +
				"minecraft_player_frozen = '" + Convert.BooleanToInt(plugin.freezeManager.isFrozen(event.getPlayer())) + "', " +
				"minecraft_player_vanished = '" + Convert.BooleanToInt(plugin.vanishManager.isVanished(event.getPlayer())) + "', " +
				"minecraft_player_muted = '" + Convert.DateToString(plugin.cacheManager.getPlayer(event.getPlayer().getUniqueId()).muted) + "', " +
				"minecraft_player_banned = '" + Convert.DateToString(plugin.cacheManager.getPlayer(event.getPlayer().getUniqueId()).banned) + "', " +
				"minecraft_player_coins = '" + plugin.cacheManager.getPlayer(event.getPlayer().getUniqueId()).coins + "', " +
				"minecraft_player_nick = '" + event.getPlayer().getDisplayName() + "', " +
				"minecraft_player_balance = '" + plugin.economyManager.getBalance(event.getPlayer().getUniqueId()) + "', " +
				"minecraft_player_warnings = '" + plugin.cacheManager.getPlayer(event.getPlayer().getUniqueId()).warnings + "'" +
				" WHERE minecraft_player_UUID = '" + event.getPlayer().getUniqueId() + "';", new SQLCallback() {
			@Override
			public void callback() {
				plugin.cacheManager.removePlayer(event.getPlayer().getUniqueId());
			}
		});
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		if (!plugin.vanishManager.isVanished(event.getPlayer())) {
			event.setQuitMessage(event.getPlayer().getDisplayName().toString() + " has left the game.");
		} else {
			event.setQuitMessage("");
		}
		plugin.sqlconnector.update("UPDATE minecraft_player SET " +
				"minecraft_player_name = '" + event.getPlayer().getName() + "', " +
				"minecraft_player_last_quit = '" + Convert.DateToString(new Date()) + "', " +
				"minecraft_player_last_ip = '" + event.getPlayer().getAddress().toString() + "', " +
				"minecraft_player_server = '" + plugin.getServer().getName() + "', " +
				"minecraft_player_location = '" + Convert.LocationToString(event.getPlayer().getLocation()) + "', " +
				"minecraft_player_gamemode = '" + Convert.GamemodeToInt(event.getPlayer().getGameMode()) + "', " +
				"minecraft_player_god_mode = '" + Convert.BooleanToInt(plugin.godManager.isGod(event.getPlayer())) + "', " +
				"minecraft_player_fly = '" + Convert.BooleanToInt(plugin.flyManager.canFly(event.getPlayer())) + "', " +
				"minecraft_player_frozen = '" + Convert.BooleanToInt(plugin.freezeManager.isFrozen(event.getPlayer())) + "', " +
				"minecraft_player_vanished = '" + Convert.BooleanToInt(plugin.vanishManager.isVanished(event.getPlayer())) + "', " +
				"minecraft_player_muted = '" + Convert.DateToString(plugin.cacheManager.getPlayer(event.getPlayer().getUniqueId()).muted) + "', " +
				"minecraft_player_banned = '" + Convert.DateToString(plugin.cacheManager.getPlayer(event.getPlayer().getUniqueId()).banned) + "', " +
				"minecraft_player_coins = '" + plugin.cacheManager.getPlayer(event.getPlayer().getUniqueId()).coins + "', " +
				"minecraft_player_nick = '" + event.getPlayer().getDisplayName() + "', " +
				"minecraft_player_balance = '" + plugin.economyManager.getBalance(event.getPlayer().getUniqueId()) + "', " +
				"minecraft_player_warnings = '" + plugin.cacheManager.getPlayer(event.getPlayer().getUniqueId()).warnings + "'" +
				" WHERE minecraft_player_UUID = '" + event.getPlayer().getUniqueId() + "';", new SQLCallback() {
			@Override
			public void callback() {
				plugin.cacheManager.removePlayer(event.getPlayer().getUniqueId());
			}
		});
	}
}

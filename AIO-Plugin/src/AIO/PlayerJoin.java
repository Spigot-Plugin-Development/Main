package AIO;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.util.UUID;

public class PlayerJoin implements Listener {
	
	private aio plugin;
	
	PlayerJoin(aio plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if (plugin.cacheManager.containsPlayer(event.getPlayer().getUniqueId())) {
			PlayerInfo info = plugin.cacheManager.getPlayer(event.getPlayer().getUniqueId());
			if (info.godMode) {
				plugin.godPlayers.add(event.getPlayer());
			}

		}
		new BukkitRunnable() {
			@Override
			public void run() {

			}
		}.runTaskTimer(plugin, 1, 1);
		event.setJoinMessage(event.getPlayer().getDisplayName().toString() + " has joined the game. Welcome!");
		event.getPlayer().sendMessage("Welcome " + event.getPlayer().getDisplayName().toString() + " to the game");
	}

	@EventHandler
	public void beforeJoin(AsyncPlayerPreLoginEvent event) {
		UUID uuid = event.getUniqueId();
		plugin.sqlconnector.query("SELECT * FROM minecraft_player WHERE minecraft_player_UUID = '" + uuid + "';", new SQLCallback() {
			@Override
			public void callback(ResultSet result) {
				try {
					if (result.next()) {
						plugin.cacheManager.addPlayer(new PlayerInfo(uuid,
								result.getString("minecraft_player_name"),
								result.getTimestamp("minecraft_player_last_join"),
								result.getTimestamp("minecraft_player_last_quit"),
								result.getString("minecraft_player_last_ip"),
								result.getString("minecraft_player_server"),
								Convert.StringToLocation(result.getString("minecraft_player_location")),
								result.getInt("minecraft_player_gamemode"),
								result.getBoolean("minecraft_player_god_mode"),
								result.getBoolean("minecraft_player_fly"),
								result.getBoolean("minecraft_player_frozen"),
								result.getBoolean("minecraft_player_vanished"),
								result.getDate("minecraft_player_muted"),
								result.getDate("minecraft_player_banned"),
								result.getInt("minecraft_player_coins"),
								result.getString("minecraft_player_nick"),
								result.getInt("minecraft_player_balance"),
								result.getString("minecaft_player_warnings")));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

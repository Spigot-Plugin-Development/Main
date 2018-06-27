package AIO;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.util.Date;
import java.util.UUID;

public class PlayerJoin implements Listener {
	
	private aio plugin;
	
	PlayerJoin(aio plugin) {
        Bukkit.getPluginManager().registerEvents(this, plugin);
	    this.plugin = plugin;
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		event.setJoinMessage("");
		new BukkitRunnable() {
			@Override
			public void run() {
				if (plugin.cacheManager.containsPlayer(event.getPlayer().getUniqueId())) {
					PlayerInfo info = plugin.cacheManager.getPlayer(event.getPlayer().getUniqueId());
					if (new Date().before(info.banned)) {
						event.getPlayer().kickPlayer("You are banned!");
					}
					event.getPlayer().teleport(info.location);
					event.getPlayer().setGameMode(Convert.IntToGamemode(info.gamemode));
					event.getPlayer().setDisplayName(info.nick);
					if (info.godMode) {
						plugin.godManager.addGod(event.getPlayer());
					}
					if (info.fly) {
						plugin.flyManager.addFly(event.getPlayer());
					}
					if (info.frozen) {
						plugin.freezeManager.addFrozen(event.getPlayer());
					}
					if (info.vanished) {
						plugin.vanishManager.addVanish(event.getPlayer());
					} else {
						event.setJoinMessage(event.getPlayer().getDisplayName() + " has joined the game.");
					}
					if (new Date().before(info.muted)) {
						plugin.playerMessage.mutePlayer(event.getPlayer());
					}
					plugin.economyManager.set(event.getPlayer().getUniqueId(), info.balance);
					if (info.lastQuit.equals(info.lastJoin)) {
						event.setJoinMessage("Welcome " + event.getPlayer().getDisplayName() + " to the server!");
					}
					cancel();
				}
			}
		}.runTaskTimer(plugin, 1, 1);
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
								result.getString("minecraft_player_warnings")));
					} else {
					    plugin.cacheManager.addPlayer(new PlayerInfo(uuid));
					    PlayerInfo info = plugin.cacheManager.getPlayer(event.getUniqueId());
                        plugin.sqlconnector.update("INSERT INTO minecraft_player (" +
                                "minecraft_player_UUID, minecraft_player_name, minecraft_player_last_join, minecraft_player_last_quit, minecraft_player_last_ip, minecraft_player_server, minecraft_player_location, " +
                                "minecraft_player_gamemode, minecraft_player_god_mode, minecraft_player_fly, minecraft_player_frozen, minecraft_player_vanished, minecraft_player_muted, " +
                                "minecraft_player_banned, minecraft_player_coins, minecraft_player_nick, minecraft_player_balance, minecraft_player_warnings) VALUES (" +
                                "'" + info.uuid + "', " +
                                "'" + info.name + "', " +
                                "'" + Convert.DateToString(new Date()) + "', " +
                                "'" + Convert.DateToString(new Date()) + "', " +
                                "'" + event.getAddress().toString() + "', " +
                                "'" + plugin.getServer().getName() + "', " +
                                "'" + Convert.LocationToString(info.location) + "', " +
                                "'" + info.gamemode + "',  " +
                                "'" + Convert.BooleanToInt(info.godMode) + "', " +
                                "'" + Convert.BooleanToInt(info.fly) + "', " +
                                "'" + Convert.BooleanToInt(info.frozen) + "', " +
                                "'" + Convert.BooleanToInt(info.vanished) + "', " +
                                "'" + Convert.DateToString(info.muted) + "', " +
                                "'" + Convert.DateToString(info.banned) + "', " +
                                "'" + info.coins + "', " +
                                "'" + info.nick + "', " +
                                "'" + info.balance + "', " +
                                "'" + info.warnings + "');", new SQLCallback());
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
}

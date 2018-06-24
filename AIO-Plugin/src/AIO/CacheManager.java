package AIO;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class CacheManager extends Callable<PlayerInfo{

    aio plugin;

    Map<Player, PlayerInfo> cachedPlayers = new HashMap<Player, PlayerInfo>();

    CacheManager(aio plugin) {
        this.plugin = plugin;
    }

    public PlayerInfo getPlayer(UUID uuid) {
        plugin.sqlconnector.query("SELECT * FROM minecraft_player WHERE minecraft_player_UUID='" + uuid.toString() + "';", new SQLCallback() {
            @Override
            public Object callback(ResultSet result) {
                try {
                    if (result.next()) {
                        cachedPlayers.put(getServer().getPlayer(uuid), new PlayerInfo(uuid,
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
                            result.getString("minecaft_player_warnings")
                        ));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return null;
    }
}

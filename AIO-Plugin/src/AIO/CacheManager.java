package AIO;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class CacheManager {

    aio plugin;

    Map<UUID, PlayerInfo> cachedPlayers = new HashMap<UUID, PlayerInfo>();

    CacheManager(aio plugin) {
        this.plugin = plugin;
    }

    public void addPlayer(PlayerInfo info) {
        cachedPlayers.put(info.uuid, info);
    }

    public boolean containsPlayer(UUID uuid) {
        return cachedPlayers.containsKey(uuid);
    }

    public PlayerInfo getPlayer(UUID uuid) {
        return cachedPlayers.get(uuid);
    }

    public void setPlayer(UUID uuid, PlayerInfo info) {
        cachedPlayers.replace(uuid, info);
    }

    public void removePlayer(UUID uuid) {
        cachedPlayers.remove(uuid);
    }

    public void playerJoin(UUID uuid) {
        plugin.sqlconnector.query("SELECT * FROM minecraft_player WHERE minecraft_player_UUID='" + uuid.toString() + "';", new SQLCallback() {
            @Override
            public void callback(ResultSet result) {
                try {
                    if (result.next()) {
                        cachedPlayers.put(uuid, new PlayerInfo(uuid,
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
    }

    public void playerQuit(UUID uuid) {
        plugin.sqlconnector.update("SELECT * FROM minecraft_player WHERE minecraft_player_UUID='" + uuid.toString() + "';", new SQLCallback() {
            @Override
            public void callback(ResultSet result) {
                try {
                    if (result.next()) {
                        cachedPlayers.put(uuid, new PlayerInfo(uuid,
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
    }
}

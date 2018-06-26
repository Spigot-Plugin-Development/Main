package AIO;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CacheManager {

    aio plugin;

    Map<UUID, PlayerInfo> cachedPlayers = new HashMap<>();

    CacheManager(aio plugin) {
        this.plugin = plugin;
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player: plugin.getServer().getOnlinePlayers()) {
                    updatePlayer(player.getUniqueId());
                    savePlayer(getPlayer(player.getUniqueId()));
                }
            }
        }.runTaskTimerAsynchronously(plugin, 60 * 20, 60 * 20);
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

    public void removePlayer(UUID uuid) {
        cachedPlayers.remove(uuid);
    }

    public void updatePlayer(UUID uuid) {
        Player player = plugin.getServer().getPlayer(uuid);
        getPlayer(uuid).ip = player.getAddress().toString();
        getPlayer(uuid).server = plugin.getServer().getName();
        getPlayer(uuid).location = player.getLocation();
        getPlayer(uuid).gamemode = Convert.GamemodeToInt(player.getGameMode());
        getPlayer(uuid).godMode = plugin.godManager.isGod(player);
        getPlayer(uuid).fly = plugin.flyManager.canFly(player);
        getPlayer(uuid).frozen = plugin.freezeManager.isFrozen(player);
        getPlayer(uuid).vanished = plugin.vanishManager.isVanished(player);
        getPlayer(uuid).nick = player.getDisplayName();
        getPlayer(uuid).balance = plugin.economyManager.getBalance(uuid);
    }

    public void savePlayer(PlayerInfo info) {
        plugin.sqlconnector.update("UPDATE minecraft_player SET " +
                "minecraft_player_name = '" + info.name + "'," +
                "minecraft_player_last_join = '" + Convert.DateToString(info.lastJoin) + "', " +
                "minecraft_player_last_quit = '" + Convert.DateToString(info.lastQuit) + "', " +
                "minecraft_player_last_ip = '" + info.ip + "'," +
                "minecraft_player_server = '" + info.server + "'," +
                "minecraft_player_location = '" + Convert.LocationToString(info.location) + "'," +
                "minecraft_player_gamemode = '" + info.gamemode + "'," +
                "minecraft_player_god_mode = '" + Convert.BooleanToInt(info.godMode) + "'," +
                "minecraft_player_fly = '" + Convert.BooleanToInt(info.fly) + "'," +
                "minecraft_player_frozen = '" + Convert.BooleanToInt(info.frozen) + "'," +
                "minecraft_player_vanished = '" + Convert.BooleanToInt(info.vanished) + "'," +
                "minecraft_player_muted = '" + Convert.DateToString(info.muted) + "'," +
                "minecraft_player_banned = '" + Convert.DateToString(info.banned) + "'," +
                "minecraft_player_coins = '" + info.coins + "'," +
                "minecraft_player_nick = '" + info.nick + "'," +
                "minecraft_player_balance = '" + info.balance + "'," +
                "minecraft_player_warnings = '" + info.warnings + "'" +
                " WHERE minecraft_player_UUID = '" + info.uuid.toString() + "';", new SQLCallback() {});
    }
}

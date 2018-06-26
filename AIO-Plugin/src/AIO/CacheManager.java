package AIO;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CacheManager {

    aio plugin;

    Map<UUID, PlayerInfo> cachedPlayers = new HashMap<>();

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

    public void removePlayer(UUID uuid) {
        cachedPlayers.remove(uuid);
    }
}

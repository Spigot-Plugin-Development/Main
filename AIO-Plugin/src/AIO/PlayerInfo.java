package AIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.joptsimple.util.DateConverter;

import java.util.Date;
import java.util.UUID;

public class PlayerInfo {
    UUID uuid;
    String name;
    Date lastJoin;
    Date lastQuit;
    String ip;
    String server;
    Location location;
    int gamemode;
    boolean godMode;
    boolean fly;
    boolean frozen;
    boolean vanished;
    Date muted;
    Date banned;
    int coins;
    String nick;
    double balance;
    String warnings;

    PlayerInfo(UUID uuid) {
        this.uuid = uuid;
        this.name = "";
        this.lastJoin = new Date();
        this.lastQuit = new Date();
        this.ip = "";
        this.server = Bukkit.getServerName();
        this.location = Spawn.spawnLocation;
        this.gamemode = 0;
        this.godMode = false;
        this.fly = false;
        this.frozen = false;
        this.vanished = false;
        this.muted = new Date();
        this.banned = new Date();
        this.coins = 0;
        this.nick = "";
        this.balance = 0;
        this.warnings = "";
    }

    PlayerInfo(UUID uuid, String name, Date lastJoin, Date lastQuit, String ip, String server, Location location, int gamemode, boolean godMode, boolean fly, boolean frozen, boolean vanished, Date muted, Date banned, int coins, String nick, double balance, String warnings) {
        this.uuid = uuid;
        this.name = name;
        this.lastJoin = lastJoin;
        this.lastQuit = lastQuit;
        this.ip = ip;
        this.server = server;
        this.location = location;
        this.gamemode = gamemode;
        this.godMode = godMode;
        this.fly = fly;
        this.frozen = frozen;
        this.vanished = vanished;
        this.muted = muted;
        this.banned = banned;
        this.coins = coins;
        this.nick = nick;
        this.balance = balance;
        this.warnings = warnings;
    }
}

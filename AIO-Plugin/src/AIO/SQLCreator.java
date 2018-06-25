package AIO;

public class SQLCreator {
    public static String create() {
        return "CREATE DATABASE minecraft IF NOT EXISTS" +
                "CHARACTER SET utf8" +
                "COLLATION utf8mb4_unicode_ci;" +
                "CREATE TABLE `minecraft_player` (\n" +
                " `minecraft_player_ID` int(11) NOT NULL AUTO_INCREMENT,\n" +
                " `minecraft_player_UUID` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,\n" +
                " `minecraft_player_names` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,\n" +
                " `minecraft_player_last_login` datetime NOT NULL,\n" +
                " `minecraft_player_last_logout` datetime NOT NULL,\n" +
                " `minecraft_player_last_ip` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,\n" +
                " `minecraft_player_server` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,\n" +
                " `minecraft_player_location` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,\n" +
                " `minecraft_player_gamemode` tinyint(4) NOT NULL,\n" +
                " `minecraft_player_god_mode` tinyint(1) NOT NULL,\n" +
                " `minecraft_player_fly` tinyint(1) NOT NULL,\n" +
                " `minecraft_player_frozen` tinyint(1) NOT NULL,\n" +
                " `minecraft_player_vanished` tinyint(1) NOT NULL,\n" +
                " `minecraft_player_muted` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',\n" +
                " `minecraft_player_banned` timestamp NOT NULL DEFAULT '0000-00-00 00:00:00',\n" +
                " `minecraft_player_coins` int(11) NOT NULL,\n" +
                " `minecraft_player_nick` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,\n" +
                " `minecraft_player_balance` decimal(10,2) NOT NULL,\n" +
                " `minecraft_player_warnings` text COLLATE utf8mb4_unicode_ci NOT NULL,\n" +
                " PRIMARY KEY (`minecraft_player_ID`)\n" +
                ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;";
    }
}

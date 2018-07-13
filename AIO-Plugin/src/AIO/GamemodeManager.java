package AIO;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class GamemodeManager implements CommandExecutor {
    private aio plugin;

    private Map<String, GameMode> gamemodeList = new HashMap<>();

    GamemodeManager(aio plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("survival").setExecutor(this);
        Bukkit.getServer().getPluginCommand("creative").setExecutor(this);
        Bukkit.getServer().getPluginCommand("adventure").setExecutor(this);
        Bukkit.getServer().getPluginCommand("spectator").setExecutor(this);
        Bukkit.getServer().getPluginCommand("gm").setExecutor(this);

        gamemodeList.put("survival", GameMode.SURVIVAL);
        gamemodeList.put("creative", GameMode.CREATIVE);
        gamemodeList.put("adventure", GameMode.ADVENTURE);
        gamemodeList.put("spectator", GameMode.SPECTATOR);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(gamemodeList.containsKey(command.getName())) {
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.gamemode." + command.getName())) {
                    sender.sendMessage(plugin.getMessage("messages.no_permission"));
                    return true;
                }
                if(args.length == 0 || !sender.hasPermission("aio.gamemode.admin")) {
                    ((Player) sender).setGameMode(gamemodeList.get(command.getName()));
                    sender.sendMessage(plugin.getMessage("gamemode.set", command.getName()));
                    return true;
                }
            }
            if(args.length == 0) {
                sender.sendMessage(plugin.getMessage("gamemode.usage", command.getName()));
                return true;
            }
            if(plugin.getServer().getPlayer(args[0]) == null) {
                sender.sendMessage(plugin.getMessage("gamemode.player_not_found", args[0]));
                return true;
            }
            plugin.getServer().getPlayer(args[0]).setGameMode(gamemodeList.get(command.getName()));
            plugin.getServer().getPlayer(args[0]).sendMessage(plugin.getMessage("gamemode.set", command.getName()));
            sender.sendMessage(plugin.getMessage("gamemode.set_player", command.getName(), args[0]));
            return true;
        }

        return false;
    }
}

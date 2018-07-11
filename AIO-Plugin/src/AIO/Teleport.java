package AIO;

import java.util.*;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Teleport implements CommandExecutor {

    private aio plugin;

    private HashMap<Player, Player> tpaList = new HashMap<>();
    private HashMap<Player, Player> tpahereList = new HashMap<>();

    Teleport(aio plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("tpa").setExecutor(this);
        Bukkit.getServer().getPluginCommand("tpaccept").setExecutor(this);
        Bukkit.getServer().getPluginCommand("tpdeny").setExecutor(this);
        Bukkit.getServer().getPluginCommand("tpahere").setExecutor(this);
        Bukkit.getServer().getPluginCommand("tptoggle").setExecutor(this);
        Bukkit.getServer().getPluginCommand("tp").setExecutor(this);
        Bukkit.getServer().getPluginCommand("tphere").setExecutor(this);
        Bukkit.getServer().getPluginCommand("tpall").setExecutor(this);
    }

    //Requesting teleport
    private void request(Player sender, Player target, boolean toSelf) {

        //Remove previous teleport requests
        for(Player player : tpaList.keySet()) {
            if(tpaList.get(player) == target || player == sender) {
                tpaList.remove(player);
            }
        }
        for(Player player : tpahereList.keySet()) {
            if(tpahereList.get(player) == target || player == sender) {
                tpahereList.remove(player);
            }
        }

        //Add new teleport request
        if(toSelf) {
            tpahereList.put(sender, target);
            target.sendMessage(plugin.getMessage("teleport.tpahere_request", aio.getPlayerName(sender)));
        } else {
            tpaList.put(sender, target);
            target.sendMessage(plugin.getMessage("teleport.tpa_request", aio.getPlayerName(sender)));
        }

        sender.sendMessage(plugin.getMessage("teleport.requested", aio.getPlayerName(target)));
    }

    //Accepting or denying request
    private void decide(Player player, boolean decision) {
        if(!tpaList.containsValue(player) && !tpahereList.containsValue(player)) {
            player.sendMessage(plugin.getMessage("teleport.no_request"));
            return;
        }
        Player requester = null;
        Boolean toSelf = false;
        for(Player tpaPlayer : tpaList.keySet()) {
            if(tpaList.get(tpaPlayer) == player) {
                requester = tpaPlayer;
                toSelf = false;
            }
        }
        for(Player tpaherePlayer : tpahereList.keySet()) {
            if(tpahereList.get(tpaherePlayer) == player) {
                requester = tpaherePlayer;
                toSelf = true;
            }
        }
        assert requester != null;
        if(decision) {
            if(toSelf) {
                requester.sendMessage(plugin.getMessage("teleport.accepted2", aio.getPlayerName(player)));
                player.sendMessage(plugin.getMessage("teleport.accepted1", aio.getPlayerName(requester)));
                player.teleport(requester.getLocation());
            } else {
                requester.sendMessage(plugin.getMessage("teleport.accepted2", aio.getPlayerName(player)));
                player.sendMessage(plugin.getMessage("teleport.accepted1", aio.getPlayerName(requester)));
                requester.teleport(player.getLocation());
            }
        } else {
            requester.sendMessage(plugin.getMessage("teleport.denied2", aio.getPlayerName(player)));
            player.sendMessage(plugin.getMessage("teleport.denied1", aio.getPlayerName(requester)));
        }
        if(toSelf) { tpahereList.remove(requester); return; }
        tpaList.remove(requester);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        //Send teleport request
        if(command.getName().equalsIgnoreCase("tpa")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("messages.player_only"));
                return true;
            }
            if(!sender.hasPermission("aio.teleport.tpa")) {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
                return true;
            }
            if(args.length == 0) {
                sender.sendMessage(plugin.getMessage("teleport.tpa_usage"));
                return true;
            }
            if(plugin.getServer().getPlayer(args[0]) == null) {
                sender.sendMessage(plugin.getMessage("messages.player_not_found", args[0]));
                return true;
            }
            if(plugin.getServer().getPlayer(args[0]) == sender) {
                sender.sendMessage(plugin.getMessage("teleport.teleport_self"));
                return true;
            }
            request((Player)sender, plugin.getServer().getPlayer(args[0]), false);
            return true;
        }

        //Send teleport-here request
        if(command.getName().equalsIgnoreCase("tpahere")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("messages.player_only"));
                return true;
            }
            if(!sender.hasPermission("aio.teleport.tpahere")) {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
                return true;
            }
            if(args.length == 0) {
                sender.sendMessage(plugin.getMessage("teleport.tpahere_usage"));
                return true;
            }
            if(plugin.getServer().getPlayer(args[0]) == null) {
                sender.sendMessage(plugin.getMessage("messages.player_not_found", args[0]));
                return true;
            }
            if(plugin.getServer().getPlayer(args[0]) == sender) {
                sender.sendMessage(plugin.getMessage("teleport.teleport_self"));
                return true;
            }
            request((Player)sender, plugin.getServer().getPlayer(args[0]), true);
            return true;
        }

        //Accept tpa/tpahere request
        if(command.getName().equalsIgnoreCase("tpaccept")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("messages.player_only"));
                return true;
            }
            if(!sender.hasPermission("aio.teleport.tpa")) {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
                return true;
            }
            decide((Player)sender, true);
            return true;
        }

        //Deny tpa/tpahere request
        if(command.getName().equalsIgnoreCase("tpdeny")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("messages.player_only"));
                return true;
            }
            if(!sender.hasPermission("aio.teleport.tpa")) {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
                return true;
            }
            decide((Player)sender, false);
            return true;
        }

        //Allowing or denying requests to be sent to this player
        if(command.getName().equalsIgnoreCase("tptoggle")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("messages.player_only"));
                return true;
            }
            if(!sender.hasPermission("aio.teleport.tptoggle")) {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
                return true;
            }
            ///////////
            return true;
        }

        //Teleport to player without request
        if(command.getName().equalsIgnoreCase("tp")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("messages.player_only"));
                return true;
            }
            if(!sender.hasPermission("aio.teleport.admin")) {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
                return true;
            }
            ///////////
            return true;
        }

        //Teleport player to self without request
        if(command.getName().equalsIgnoreCase("tphere")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("messages.player_only"));
                return true;
            }
            if(!sender.hasPermission("aio.teleport.admin")) {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
                return true;
            }
            ///////////
            return true;
        }

        //Teleport all players to self without request
        if(command.getName().equalsIgnoreCase("tpall")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("messages.player_only"));
                return true;
            }
            if(!sender.hasPermission("aio.teleport.admin")) {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
                return true;
            }
            ///////////
            return true;
        }

        return false;
    }

}

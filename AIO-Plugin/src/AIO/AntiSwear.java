package AIO;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public class AntiSwear implements Listener, CommandExecutor {

    private aio plugin;
    private File asfile;
    private FileConfiguration asconfig;

    AntiSwear(aio plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("antiswear").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        File antiswear = new File(plugin.getDataFolder(), "antiswear.yml");
        if(!antiswear.exists()) {
            try {
                antiswear.createNewFile();
                PrintWriter pw = new PrintWriter(new FileWriter(antiswear));
                pw.println("antiswear:");
                pw.flush();
                pw.close();
            } catch(IOException ex) {
                plugin.getLogger().log(Level.SEVERE, plugin.getMessage("messages.file_not_created", "antiswear"));
                ex.printStackTrace();
            }
        }
    }

    //Reload antiswear file
    private void reloadAntiswear() {
        if(asfile == null) {
            asfile = new File(plugin.getDataFolder(), "antiswear.yml");
        }
        asconfig = YamlConfiguration.loadConfiguration(asfile);
    }

    //FileConfiguration of antiswear file
    private FileConfiguration getAntiswear() {
        if(asconfig == null) {
            reloadAntiswear();
        }
        return asconfig;
    }

    //Save antiswear file
    private void saveAntiswear() {
        if(asconfig == null || asfile == null) {
            return;
        }
        try {
            getAntiswear().save(asfile);
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    //Antiswear command
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equalsIgnoreCase("antiswear")) {

            boolean enabled = plugin.getConfig().getBoolean("antiswear-enabled");

            //Check for permission
            if(sender instanceof Player) {
                if(!sender.hasPermission("aio.antiswear")) {
                    sender.sendMessage(plugin.getMessage("messages.no_permission"));
                    return false;
                }
            }

            //Print usage
            if(args.length == 0) {
                if(enabled) {
                    sender.sendMessage(plugin.getMessage("antiswear.usage_enabled"));
                    return false;
                }
                sender.sendMessage(plugin.getMessage("antiswear.usage_disabled"));
                return false;
            }

            //Add to list
            if(args[0].equalsIgnoreCase("add")) {
                if(args.length != 2) {
                    sender.sendMessage(plugin.getMessage("antiswear.add_usage"));
                    return false;
                }
                List<String> swearList = getAntiswear().getStringList("antiswear");
                if(swearList.contains(args[1].toLowerCase())) {
                    sender.sendMessage(plugin.getMessage("antiswear.cannot_add", args[1].toLowerCase()));
                    return false;
                }
                swearList.add(args[1].toLowerCase());
                getAntiswear().set("antiswear", swearList);
                saveAntiswear();
                sender.sendMessage(plugin.getMessage("antiswear.added", args[1].toLowerCase()));
                return false;
            }

            //Remove from list
            if(args[0].equalsIgnoreCase("remove")) {
                if(args.length != 2) {
                    sender.sendMessage(plugin.getMessage("antiswear.remove_usage"));
                    return false;
                }
                List<String> swearList = getAntiswear().getStringList("antiswear");
                if(!swearList.contains(args[1].toLowerCase())) {
                    sender.sendMessage(plugin.getMessage("antiswear.cannot_remove", args[1].toLowerCase()));
                    return false;
                }
                swearList.remove(args[1].toLowerCase());
                getAntiswear().set("antiswear", swearList);
                saveAntiswear();
                sender.sendMessage(plugin.getMessage("antiswear.removed", args[1].toLowerCase()));
                return false;
            }

            //Print list
            if(args[0].equalsIgnoreCase("list")) {
                List<String> swearList = getAntiswear().getStringList("antiswear");
                StringBuilder swearwords = new StringBuilder();
                for(String word : swearList) { swearwords.append(word).append(plugin.getMessage("antiswear.list_separator")); }
                if(swearList.isEmpty()) {
                    sender.sendMessage(plugin.getMessage("antiswear.list_empty"));
                    return false;
                }
                sender.sendMessage(plugin.getMessage("antiswear.list", String.valueOf(swearList.size()), swearwords.substring(0, swearwords.length() - 6)));
                return false;
            }

            //Enable antiswear
            if(args[0].equalsIgnoreCase("enable")) {
                if(enabled) {
                    sender.sendMessage(plugin.getMessage("antiswear.enabled_already"));
                    return false;
                }
                plugin.getConfig().set("antiswear-enabled", true);
                plugin.saveConfig();
                sender.sendMessage(plugin.getMessage("antiswear.enabled"));
                return false;
            }

            //Disable antiswear
            if(args[0].equalsIgnoreCase("disable")) {
                if(!enabled) {
                    sender.sendMessage(plugin.getMessage("antiswear.disabled_already"));
                    return false;
                }
                plugin.getConfig().set("antiswear-enabled", false);
                plugin.saveConfig();
                sender.sendMessage(plugin.getMessage("antiswear.disabled"));
            }
        }

        return false;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if(event.getPlayer().hasPermission("aio.antiswear.bypass")) {
            return;
        }

        List<String> message = Arrays.asList(event.getMessage().toLowerCase().split(" "));
        List<String> swearList = getAntiswear().getStringList("antiswear");

        for(String swearword : swearList) {
            if(message.contains(swearword)) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(plugin.getMessage("antiswear.no_swear"));
                return;
            }
        }
    }

}

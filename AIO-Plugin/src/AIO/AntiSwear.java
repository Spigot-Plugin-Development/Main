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
    private String prefix;
    private File asfile;
    private FileConfiguration asconfig;

    AntiSwear(aio plugin) {
        this.plugin = plugin;
        this.prefix = aio.colorize("&7[&fAIO » &cAntiswear&7]&r ");
        Bukkit.getServer().getPluginCommand("antiswear").setExecutor(this);
        File antiswear = new File(plugin.getDataFolder(), "antiswear.yml");
        if(!antiswear.exists()) {
            try {
                antiswear.createNewFile();
                PrintWriter pw = new PrintWriter(new FileWriter(antiswear));
                pw.println("antiswear:");
                pw.flush();
                pw.close();
            } catch(IOException ex) {
                plugin.getLogger().log(Level.SEVERE, "Unable to create antiswear file!");
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
                    sender.sendMessage(aio.colorize("&cYou don't have permission to execute this command."));
                    return false;
                }
            }

            //Print usage
            if(args.length == 0) {
                if(enabled) {
                    sender.sendMessage(aio.colorize(prefix + "&aAntiswear enabled.\n" + prefix + "&c/antiswear <add | remove | list | disable>"));
                    return false;
                }
                sender.sendMessage(aio.colorize(prefix + "&4Antiswear disabled.\n" + prefix + "&c/antiswear <add | remove | list | enable>"));
                return false;
            }

            //Add to list
            if(args[0].equalsIgnoreCase("add")) {
                if(args.length != 2) {
                    sender.sendMessage(aio.colorize(prefix + "&c/antiswear add <swearword>"));
                    return false;
                }
                List<String> swearList = getAntiswear().getStringList("antiswear");
                if(swearList.contains(args[1].toLowerCase())) {
                    sender.sendMessage(aio.colorize(prefix + "&cThe word '&f" + args[1].toLowerCase() + "&c' is already on the antiswear list."));
                    return false;
                }
                swearList.add(args[1].toLowerCase());
                getAntiswear().set("antiswear", swearList);
                saveAntiswear();
                sender.sendMessage(aio.colorize(prefix + "&aYou added the word '&f" + args[1].toLowerCase() + "&a' to the antiswear list."));
                return false;
            }

            //Remove from list
            if(args[0].equalsIgnoreCase("remove")) {
                if(args.length != 2) {
                    sender.sendMessage(aio.colorize(prefix + "&c/antiswear remove <swearword>"));
                    return false;
                }
                List<String> swearList = getAntiswear().getStringList("antiswear");
                if(!swearList.contains(args[1].toLowerCase())) {
                    sender.sendMessage(aio.colorize(prefix + "&cThe word '&f" + args[1].toLowerCase() + "&c' is not on the antiswear list."));
                    return false;
                }
                swearList.remove(args[1].toLowerCase());
                getAntiswear().set("antiswear", swearList);
                saveAntiswear();
                sender.sendMessage(aio.colorize(prefix + "&aYou removed the word '&f" + args[1].toLowerCase() + "&a' from the antiswear list."));
                return false;
            }

            //Print list
            if(args[0].equalsIgnoreCase("list")) {
                List<String> swearList = getAntiswear().getStringList("antiswear");
                StringBuilder swearwords = new StringBuilder();
                int count = 0;
                for(String word : swearList) {
                    swearwords.append(word).append("&7, &f");
                    count += 1;
                }
                if(count == 0) {
                    sender.sendMessage(aio.colorize(prefix + "&7There are no words in the antiswear list."));
                    return false;
                }
                sender.sendMessage(aio.colorize(prefix + "&7Antiswear list: &f" + swearwords.substring(0, swearwords.length() - 6) + "&7."));
                sender.sendMessage(aio.colorize(prefix + "&7Total of &f" + count + "&7 words."));
                return false;
            }

            //Enable antiswear
            if(args[0].equalsIgnoreCase("enable")) {
                if(enabled) {
                    sender.sendMessage(aio.colorize(prefix + "&cAntiswear is already enabled."));
                    return false;
                }
                plugin.getConfig().set("antiswear-enabled", true);
                plugin.saveConfig();
                sender.sendMessage(aio.colorize(prefix + "&aAntiswear enabled, the Minecraft Gods are watching now!"));
                return false;
            }

            //Disable antiswear
            if(args[0].equalsIgnoreCase("disable")) {
                if(!enabled) {
                    sender.sendMessage(aio.colorize(prefix + "&cAntiswear is already disabled."));
                    return false;
                }
                plugin.getConfig().set("antiswear-enabled", false);
                plugin.saveConfig();
                sender.sendMessage(aio.colorize(prefix + "&4Antiswear disabled!"));
            }
        }

        return false;
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        List<String> message = Arrays.asList(event.getMessage().split(" "));
        List<String> swearList = getAntiswear().getStringList("antiswear");

        for(String swearword : swearList) {
            if(message.contains(swearword) && !event.getPlayer().hasPermission("aio.antiswear.exception")) {
                event.setCancelled(true);
                event.getPlayer().sendMessage(aio.colorize(prefix + "&cYou are not allowed to swear on this server."));
            }
        }
    }

}

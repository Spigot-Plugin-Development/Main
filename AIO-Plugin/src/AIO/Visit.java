package AIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Visit implements CommandExecutor, Listener {
    private aio plugin;

    private File file;
    private FileConfiguration configuration;

    Visit(aio plugin) {
        this.plugin = plugin;
        plugin.getCommand("visit").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        File locations = new File(plugin.getDataFolder(), "visit-locations.yml");
        if (!locations.exists()) {
            try {
                plugin.getLogger().warning(plugin.getMessage("messages.file_not_found", "visit-locations.yml"));
                locations.createNewFile();
                PrintWriter pw = new PrintWriter(new FileWriter(locations));
                pw.println("warps: {}");
                pw.flush();
                pw.close();
            } catch (IOException ex) {
                plugin.getLogger().severe(plugin.getMessage("message.file_not_created", "visit-locations.yml"));
                ex.printStackTrace();
            }
        }

    }

    private void reloadWarps() {
        if (file == null) {
            file = new File(plugin.getDataFolder(), "visit-locations.yml");
        }
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    private FileConfiguration getLocations() {
        if (configuration == null) {
            reloadWarps();
        }
        return configuration;
    }

    private void saveLocations() {
        if (configuration == null || file == null) {
            return;
        }
        try {
            getLocations().save(file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private Location getLocation(String name) {
        if (getLocations().contains("locations." + name + ".world") && getLocations().contains("locations." + name + ".x") && getLocations().contains("locations." + name + ".y") && getLocations().contains("locations." + name + ".z") && getLocations().contains("locations." + name + ".yaw") && getLocations().contains("locations." + name + ".pitch")) {
            World w = Bukkit.getWorld(getLocations().getString("locations." + name + ".world"));
            double x = getLocations().getDouble("locations." + name + ".x");
            double y = getLocations().getDouble("locations." + name + ".y");
            double z = getLocations().getDouble("locations." + name + ".z");
            float yaw = (float)getLocations().getDouble("locations." + name + ".yaw");
            float pitch = (float)getLocations().getDouble("locations." + name + ".pitch");
            return new Location(w, x, y, z, yaw, pitch);
        }
        return null;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("visit")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can execute this command.");
                return false;
            }
            if (!sender.hasPermission("aio.visit")) {
                sender.sendMessage("You don't have permission to execute this command.");
                return false;
            }
            if (args.length == 0) {
                sender.sendMessage("Usage: /visit <player>");
                return false;
            }
            if (getLocation(args[0]) == null) {
                sender.sendMessage("This player does not have a welcome sign!");
                return false;
            }
            ((Player)sender).teleport(getLocation(args[0]));
        }
        return false;
    }

    @EventHandler
    public void signUpdate(SignChangeEvent event) {
        if (event.getBlock().getType() != Material.SIGN_POST) {
            return;
        }
        if (getLocation(event.getPlayer().getName()) != null) {
            return;
        }
        Sign signData = (Sign)event.getBlock().getState();
        System.out.println(signData.getLine(1));
        System.out.println(signData.getLines()[1]);
        if (!signData.getLines()[1].equals("[Welcome]")) {
            return;
        }
        event.getPlayer().sendMessage("A");
        signData.setLine(0, "");
        signData.setLine(1, aio.colorize("&5&l[&a&lWelcome&5&l]"));
        signData.setLine(2, aio.getPlayerName(event.getPlayer()));
        signData.setLine(3, "");
        signData.update();

        getLocations().set("locations." + event.getPlayer().getName() + ".world", event.getBlock().getLocation().getWorld().getName());
        getLocations().set("locations." + event.getPlayer().getName() + ".x", event.getBlock().getLocation().getX());
        getLocations().set("locations." + event.getPlayer().getName() + ".y", event.getBlock().getLocation().getY());
        getLocations().set("locations." + event.getPlayer().getName() + ".z", event.getBlock().getLocation().getZ());
        getLocations().set("locations." + event.getPlayer().getName() + ".yaw", event.getBlock().getLocation().getYaw());
        getLocations().set("locations." + event.getPlayer().getName() + ".pitch", event.getBlock().getLocation().getPitch());
        saveLocations();
    }

    @EventHandler
    public void signBreak(BlockBreakEvent event) {
        if (event.getBlock().getType() != Material.SIGN_POST) {
            return;
        }
        if (getLocation(event.getPlayer().getName()) == null) {
            return;
        }
        Block sign = event.getBlock();
        Sign signData = (Sign)sign.getState();
        if (!signData.getLine(1).equals(aio.colorize("&5&l[&a&lWelcome&5&l]"))) {
            return;
        }
        if (!signData.getLine(2).equals(event.getPlayer().getName())) {
            event.setCancelled(true);
            return;
        }
        getLocations().set("locations." + event.getPlayer().getName(), null);
        saveLocations();
    }
}

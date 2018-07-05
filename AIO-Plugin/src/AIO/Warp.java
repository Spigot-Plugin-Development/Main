package AIO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.*;
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
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class Warp implements Listener, CommandExecutor {

	private aio plugin;
	private File warpfile;
	private FileConfiguration warpconfig;

	Warp(aio plugin) {
		this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("warp").setExecutor(this);
        Bukkit.getServer().getPluginCommand("setwarp").setExecutor(this);
        Bukkit.getServer().getPluginCommand("delwarp").setExecutor(this);
        Bukkit.getServer().getPluginCommand("warplist").setExecutor(this);
        Bukkit.getServer().getPluginCommand("warpreload").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        File warps = new File(plugin.getDataFolder(), "warps.yml");
		if(!warps.exists()) {
			try {
				warps.createNewFile();
				PrintWriter pw = new PrintWriter(new FileWriter(warps));
				pw.println("warps:");
				pw.flush();
				pw.close();
			} catch(IOException ex) {
				plugin.getLogger().log(Level.SEVERE, plugin.getMessage("message.file_not_created", "warps"));
				ex.printStackTrace();
			}
		}
	}
	
	//Reload warp file
	private void reloadWarps() {
		if(warpfile == null) {
			warpfile = new File(plugin.getDataFolder(), "warps.yml");
		}
		warpconfig = YamlConfiguration.loadConfiguration(warpfile);
	}
	
	//FileConfiguration of warp file
	private FileConfiguration getWarps() {
		if(warpconfig == null) {
			reloadWarps();
		}
		return warpconfig;
	}
	
	//Save warp file
    public void saveWarps() {
		if(warpconfig == null || warpfile == null) {
			return;
		}
		try {
			getWarps().save(warpfile);
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}
	
	//Get location of warp
	private Location getWarpLocation(String name) {
		if(getWarps().contains("warps." + name + ".world") && getWarps().contains("warps." + name + ".x") && getWarps().contains("warps." + name + ".y") && getWarps().contains("warps." + name + ".z") && getWarps().contains("warps." + name + ".yaw") && getWarps().contains("warps." + name + ".pitch")){
			World w = Bukkit.getWorld(getWarps().getString("warps." + name + ".world"));
			double x = getWarps().getDouble("warps." + name + ".x");
			double y = getWarps().getDouble("warps." + name + ".y");
			double z = getWarps().getDouble("warps." + name + ".z");
			float yaw = (float)getWarps().getDouble("warps." + name + ".yaw");
			float pitch = (float)getWarps().getDouble("warps." + name + ".pitch");
			return new Location(w, x, y, z, yaw, pitch);
		}
		return null;
	}
	
	//Warp signs
	@EventHandler
	public void signChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		Sign sign = (Sign)event.getBlock().getState();
		if(event.getLine(0).equalsIgnoreCase("[warp]") && player.hasPermission("aio.sign.warp")) {
			if(event.getLine(1) == null || getWarpLocation(event.getLine(1)) == null) {
				event.setLine(0, aio.colorize("&4&l[Warp]"));
				event.setLine(1, "warp name");
				sign.update();
				return;
			}
			event.setLine(0, aio.colorize("&1&l[Warp]"));
			event.setLine(1, event.getLine(1));
			sign.update();
		}
	}
	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if(event.getAction().equals(Action.LEFT_CLICK_BLOCK) && isSign(block)) {
			Sign sign = (Sign)block.getState();
			if(!player.hasPermission("aio.sign.warp") && sign.getLine(0).equals(aio.colorize("&1&l[Warp]"))) {
				player.sendMessage(plugin.getMessage("message.no_permission_sign"));
				event.setCancelled(true);
			}
		}
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && isSign(block)) {
			Sign sign = (Sign)block.getState();
			if(player.hasPermission("aio.warp") && sign.getLine(0).equals(aio.colorize("&1&l[Warp]")) && getWarpLocation(sign.getLine(1)) != null) {
				player.teleport(getWarpLocation(sign.getLine(1)));
				player.sendMessage(plugin.getMessage("warp.warping", sign.getLine(1)));
			}
		}
	}
	
	private boolean isSign(Block block) {
	    return block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //Set new warp
        if(command.getName().equalsIgnoreCase("setwarp")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("messages.player_only"));
                return false;
            }
            if(!sender.hasPermission("aio.setwarp")) {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
                return false;
            }
            if(args.length < 1) {
                sender.sendMessage(plugin.getMessage("warp.setwarp_usage"));
                return false;
            }
            if(getWarpLocation(args[0]) != null) {
                sender.sendMessage(plugin.getMessage("warp.warp_exists"));
                return false;
            }
            Player player = (Player)sender;
            getWarps().set("warps." + args[0] + ".world", player.getWorld().getName());
            getWarps().set("warps." + args[0] + ".x", player.getLocation().getX());
            getWarps().set("warps." + args[0] + ".y", player.getLocation().getY());
            getWarps().set("warps." + args[0] + ".z", player.getLocation().getZ());
            getWarps().set("warps." + args[0] + ".yaw", player.getLocation().getYaw());
            getWarps().set("warps." + args[0] + ".pitch", player.getLocation().getPitch());
            saveWarps();
            player.sendMessage(plugin.getMessage("warp.warp_set", args[0]));
            return true;
        }

	    //Delete warp
        if(command.getName().equalsIgnoreCase("delwarp")) {
            if(sender instanceof Player && !sender.hasPermission("aio.delwarp")) {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
                return false;
            }
            if(args.length < 1) {
                sender.sendMessage(plugin.getMessage("warp.delwarp_usage"));
                return false;
            }
            if(getWarpLocation(args[0]) == null) {
                sender.sendMessage(plugin.getMessage("warp.warp_not_exists"));
                return false;
            }
            getWarps().set("warps." + args[0], null);
            saveWarps();
            sender.sendMessage(plugin.getMessage("warp.warp_deleted", args[0]));
            return true;
        }

        //Warp
        if(command.getName().equalsIgnoreCase("warp")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(plugin.getMessage("messages.player_only"));
                return false;
            }
            if(!sender.hasPermission("aio.warp")) {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
                return false;
            }
            Player player = (Player)sender;
            if(args.length < 1) {
                player.sendMessage(plugin.getMessage("warp.warp_usage"));
                return false;
            }
            if(getWarpLocation(args[0]) == null) {
                player.sendMessage(plugin.getMessage("warp.warp_not_found", args[0]));
                return false;
            }
            if(getWarpLocation(args[0]).getWorld() == null) {
                player.sendMessage(plugin.getMessage("warp.world_not_found", args[0]));
                return false;
            }
            player.sendMessage(plugin.getMessage("warp.warping", args[0]));
            player.teleport(getWarpLocation(args[0]));
            return true;
        }

        //Reload warp file
        if(command.getName().equalsIgnoreCase("warpreload")) {
            if(sender instanceof Player && !sender.hasPermission("aio.warpreload")) {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
                return false;
            }
            reloadWarps();
            saveWarps();
            sender.sendMessage(plugin.getMessage("warp.reloaded"));
            return true;
        }

        //List all warps
        if(command.getName().equalsIgnoreCase("warplist")) {
            if(sender instanceof Player && !sender.hasPermission("aio.warplist")) {
                sender.sendMessage(plugin.getMessage("messages.no_permission"));
                return false;
            }
            if(!getWarps().isConfigurationSection("warps")) {
                sender.sendMessage(plugin.getMessage("warp.list_empty"));
                return false;
            }
            Set<String> warps = getWarps().getConfigurationSection("warps").getKeys(false);
            Iterator<String> itr = warps.iterator();
            StringBuilder list = new StringBuilder();
            while(itr.hasNext()) {
                list.append(itr.next()).append(plugin.getMessage("warp.list_separator"));
            }
            sender.sendMessage(plugin.getMessage("warp.list", String.valueOf(warps.size()), list.substring(0, list.length() - 6)));
            return true;
        }

	    return false;
    }

}
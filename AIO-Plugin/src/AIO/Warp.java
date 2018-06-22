package AIO;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.Plugin;

public class Warp implements Listener {

	private Plugin plugin;
	private File warpfile;
	private FileConfiguration warpconfig;
	
	//Message variables
	String noperm;
	String warp_del;
	String warp_set;
	String warping;
	String warp_not_found;
	String no_world;
	String reload;
	String warp_list;
	String no_warp;
	String warp_already;
	String player_only;
	
	//Constructor
	Warp(Plugin plugin) {
		this.plugin = plugin;
		File warps = new File(plugin.getDataFolder(), "warps.yml");
		if(!warps.exists()){
			try{
				warps.createNewFile();
				PrintWriter pw = new PrintWriter(new FileWriter(warps));
				pw.println("warps:");
				pw.flush();
				pw.close();
			}catch(IOException ex){
				plugin.getLogger().log(Level.SEVERE, "Unable to create warp file! Disabling...");
				ex.printStackTrace();
			}
		}
		
		this.load();
	}
	
	//Reload warp file
	public void reloadWarps() {
		if(warpfile == null){
			warpfile = new File(plugin.getDataFolder(), "warps.yml");
		}
		warpconfig = YamlConfiguration.loadConfiguration(warpfile);
	}
	
	//FileConfiguration of warp file
	public FileConfiguration getWarps() {
		if(warpconfig == null){
			reloadWarps();
		}
		return warpconfig;
	}
	
	//Save warp file
	public void saveWarps() {
		if(warpconfig == null || warpfile == null){
			return;
		}
		try{
			getWarps().save(warpfile);
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	//Load message string values from config file
	public void load() {
		this.warp_not_found = plugin.getConfig().getString("messages.warp_not_found");
		this.no_world = plugin.getConfig().getString("messages.world_not_exists");
		this.noperm = plugin.getConfig().getString("messages.no_permission");
		this.warp_del = plugin.getConfig().getString("messages.warp_del");
		this.warp_set = plugin.getConfig().getString("messages.warp_set");
		this.warp_list = plugin.getConfig().getString("messages.warp_list");
		this.warping = plugin.getConfig().getString("messages.warping");
		this.no_warp = plugin.getConfig().getString("messages.no_warp");
		this.reload = plugin.getConfig().getString("messages.reload");
		this.warp_already = plugin.getConfig().getString("messages.warp_already_exists");
		this.player_only = plugin.getConfig().getString("messages.player_only");
	}
	
	//Get location of warp
	public Location getWarpLocation(String name) {
		if(this.getWarps().contains("warps." + name + ".world") && this.getWarps().contains("warps." + name + ".x") && this.getWarps().contains("warps." + name + ".y") && this.getWarps().contains("warps." + name + ".z") && this.getWarps().contains("warps." + name + ".yaw") && this.getWarps().contains("warps." + name + ".pitch")){
			World w = Bukkit.getWorld(this.getWarps().getString("warps." + name + ".world"));
			double x = this.getWarps().getDouble("warps." + name + ".x");
			double y = this.getWarps().getDouble("warps." + name + ".y");
			double z = this.getWarps().getDouble("warps." + name + ".z");
			float yaw = (float)this.getWarps().getDouble("warps." + name + ".yaw");
			float pitch = (float)this.getWarps().getDouble("warps." + name + ".pitch");
			return new Location(w, x, y, z, yaw, pitch);
		}
		return null;
	}
	
	//Set warp
	public void setWarp(Player player, String name) {
		this.getWarps().set("warps." + name + ".world", player.getWorld().getName());
		this.getWarps().set("warps." + name + ".x", player.getLocation().getX());
		this.getWarps().set("warps." + name + ".y", player.getLocation().getY());
		this.getWarps().set("warps." + name + ".z", player.getLocation().getZ());
		this.getWarps().set("warps." + name + ".yaw", player.getLocation().getYaw());
		this.getWarps().set("warps." + name + ".pitch", player.getLocation().getPitch());
		this.saveWarps();
		player.sendMessage(this.warp_set.replace("{warp}", name));
	}
	
	//Delete warp
	public void delWarp(Player player, String name) {
		this.getWarps().set("warps." + name, null);
		this.saveWarps();
		player.sendMessage(this.warp_del.replace("{warp}", name));
	}
	
	//Warp signs
	@EventHandler
	public void signChange(SignChangeEvent event) {
		Player player = event.getPlayer();
		Sign sign = (Sign)event.getBlock().getState();
		if(event.getLine(0).equalsIgnoreCase("[warp]") && player.hasPermission("aio.sign.create")) {
			if(event.getLine(1) == null || this.getWarpLocation(event.getLine(1)) == null) {
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
		if(event.getAction().equals(Action.LEFT_CLICK_BLOCK) && this.isSign(block)) {
			Sign sign = (Sign)block.getState();
			if(!player.hasPermission("aio.sign.destroy") && sign.getLine(0).equals(aio.colorize("&1&l[Warp]"))) {
				player.sendMessage(this.noperm);
				event.setCancelled(true);
			}
		}
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && this.isSign(block)) {
			Sign sign = (Sign)block.getState();
			if(player.hasPermission("aio.sign.use") && sign.getLine(0).equals(aio.colorize("&1&l[Warp]")) && this.getWarpLocation(sign.getLine(1)) != null) {
				player.teleport(this.getWarpLocation(sign.getLine(1)));
			}
		}
	}
	
	private boolean isSign(Block block) {
		if(block.getType() == Material.WALL_SIGN || block.getType() == Material.SIGN_POST) {
			return true;
		}
		return false;
	}
}
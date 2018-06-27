package AIO;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.permission.*;
import java.sql.ResultSet;
import java.util.*;

public class aio extends JavaPlugin implements Listener {
	
	private Chat chat;
	private Economy economy;
	private Permission permission;

	SQLConnector sqlconnector;
	Enchant enchant;
	AntiItemlag antiItemlag;
	PrivateMessage privateMessage;
	Advertisements advertisements;
	TeleportA teleporta;
	BannerCreator bannerCreator;
	AntiSpambot antiSpambot;
	Warp warp;
	GodManager godManager;
	FlyManager flyManager;
	WeatherManager weatherManager;
	GamemodeManager gamemodeManager;
	TimeManager timeManager;
	Commands commands;
	EconomyManager economyManager;
	VanishManager vanishManager;
	PlayerJoin playerJoin;
	PlayerLeave playerLeave;
	SpecialChests specialChests;

	Location spawn;
	PlayerMessage playerMessage;
	FreezeManager freezeManager;

	CacheManager cacheManager;
	
	@Override
	public void onEnable() {
		getLogger().info("Starting All-In-One Plugin");

		sqlconnector = new SQLConnector(this);
		//retrieve server id
		//enable necessary parts
		sqlconnector.connect("127.0.0.1:8889", "minecraft", "root", "root", false);

		spawn = new Location(getServer().getWorld(getConfig().getString("spawn-world")), getConfig().getDouble("spawn-x"), getConfig().getDouble("spawn-y"), getConfig().getDouble("spawn-z"), (float)getConfig().getDouble("spawn-yaw"), (float)getConfig().getDouble("spawn-pitch"));

		bannerCreator = new BannerCreator(this);
		advertisements = new Advertisements(this);
		antiItemlag = new AntiItemlag(this);
		enchant = new Enchant(this);
		warp = new Warp(this);
		commands = new Commands(this);
		economyManager = new EconomyManager(this);
		godManager = new GodManager(this);
		flyManager = new FlyManager(this);
		teleporta = new TeleportA(this);
		privateMessage = new PrivateMessage(this);
		antiSpambot = new AntiSpambot(this);
		playerMessage = new PlayerMessage(this);
		vanishManager = new VanishManager(this);
		weatherManager = new WeatherManager(this);
		gamemodeManager = new GamemodeManager(this);
		timeManager = new TimeManager(this);
		teleporta = new TeleportA(this);
		privateMessage = new PrivateMessage(this);
		freezeManager = new FreezeManager(this);
		cacheManager = new CacheManager(this);
		playerJoin = new PlayerJoin(this);
		playerLeave = new PlayerLeave(this);
		specialChests = new SpecialChests(this);

		Bukkit.getPluginManager().registerEvents(this, this);
		setupChat();
		setupEconomy();
		setupPermissions();
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Stopping All-In-One Plugin");
		advertisements.removeBar();
		saveConfig();
		warp.saveWarps();
		for (Player player: getServer().getOnlinePlayers()) {
			cacheManager.updatePlayer(player.getUniqueId());
			cacheManager.savePlayer(cacheManager.getPlayer(player.getUniqueId()));
		}
		sqlconnector.disconnect();
	}

	private boolean setupChat() {
		RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
		if (chatProvider != null) {
			chat = chatProvider.getProvider();
		}
		return (chat != null);
	}

	private boolean setupEconomy() {
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

	private boolean setupPermissions() {
		RegisteredServiceProvider<Permission> permissionProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.permission.Permission.class);
		if (permissionProvider != null) {
			permission = permissionProvider.getProvider();
		}
		return (permission != null);
	}

	public static String[] allButFirst(String[] input) {
		return Arrays.copyOfRange(input, 1, input.length);
	}

	public static String colorize(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}

	@EventHandler
	private void signColor(SignChangeEvent event) {
		for (int i = 0; i < 4; i++) {
			event.setLine(i, aio.colorize(event.getLine(i)));
		}
	}
	
	@EventHandler
	private void treeFeller(BlockBreakEvent event) {
		if (event.getPlayer().getInventory().getItem(event.getPlayer().getInventory().getHeldItemSlot()) == null) {
			return;
		}
		if (event.getPlayer().getInventory().getItem(event.getPlayer().getInventory().getHeldItemSlot()).getType() != Material.DIAMOND_AXE) {
			return;
		}
		if (event.getBlock().getType() != Material.LOG && event.getBlock().getType() != Material.LOG_2) {
			return;
		}
		int i = 0;
		while (event.getBlock().getRelative(0, i, 0).getType() == event.getBlock().getType() && i < 32) {
			i++;
		}
		for (int j = 0; j < i; j++) {
			event.getBlock().getRelative(0, j, 0).breakNaturally(event.getPlayer().getInventory().getItem(event.getPlayer().getInventory().getHeldItemSlot()));
		}
	}
	
	@EventHandler
	private void playerDeath(PlayerRespawnEvent event) {
		event.setRespawnLocation(spawn);
	}
	
	@EventHandler
	private void rightClickBlock(PlayerInteractEvent event) {
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getItem() == null || event.getItem().getType() != Material.BUCKET) {
			return;
		}
		if (!event.getClickedBlock().getType().equals(Material.OBSIDIAN)) {
			return;
		}
		event.getClickedBlock().setType(Material.AIR);
		event.getItem().setAmount(event.getItem().getAmount() - 1);
		event.getPlayer().getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
		event.setCancelled(true);
	}

}

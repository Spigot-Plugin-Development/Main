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

	Location spawn;
	PlayerMessage playerMessage;
	List<Player> frozenPlayers = new ArrayList<Player>();

	CacheManager cacheManager;
	
	@Override
	public void onEnable() {
		getLogger().info("Starting All-In-One Plugin");

		sqlconnector = new SQLConnector(this);
		//retrieve server id
		//enable necessary parts

		spawn = new Location(getServer().getWorld(getConfig().getString("spawn-world")), getConfig().getDouble("spawn-x"), getConfig().getDouble("spawn-y"), getConfig().getDouble("spawn-z"), (float)getConfig().getDouble("spawn-yaw"), (float)getConfig().getDouble("spawn-pitch"));

		cacheManager = new CacheManager(this);
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

		Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerLeave(this), this);
		Bukkit.getPluginManager().registerEvents(this, this);
		teleporta = new TeleportA(this);
		privateMessage = new PrivateMessage(this);
		Bukkit.getPluginManager().registerEvents(privateMessage, this);
		setupChat();
		setupEconomy();
		setupPermissions();
		sqlconnector.connect("127.0.0.1:8889", "minecraft", "root", "root");

		getCommand("kickall").setExecutor(commands);
		getCommand("kick").setExecutor(commands);

		for (Player player: getServer().getOnlinePlayers()) {
			new AsyncPlayerPreLoginEvent(player.getName(), player.getAddress().getAddress(), player.getUniqueId());
		}
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Stopping All-In-One Plugin");
		advertisements.removeBar();
		saveConfig();
		warp.saveWarps();
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
	private void playerMove(PlayerMoveEvent event) {
		if (frozenPlayers.contains(event.getPlayer())) {
			event.setCancelled(true);
			event.setTo(event.getFrom());
			event.getPlayer().sendMessage("You are frozen and can not move.");
			sqlconnector.query("SELECT * FROM PLAYERS WHERE player_UUID = '" + event.getPlayer().getUniqueId() + "';", new SQLCallback() {
				@Override
				public void callback(ResultSet result) {
					try {
						while (result.next()) {
							System.out.println(result.getDouble("balance"));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	@EventHandler
	private void signColor(SignChangeEvent event) {
		for (int i = 0; i < 4; i++) {
			event.setLine(i, aio.colorize(event.getLine(i)));
		}
	}
	
	@EventHandler
	private void playerTeleport(PlayerTeleportEvent event) {
		if (frozenPlayers.contains(event.getPlayer())) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("You are frozen and can not teleport.");
		}
	}
	
	@EventHandler
	private void playerKick(PlayerKickEvent event) {
		if (frozenPlayers.contains(event.getPlayer()) && event.getReason().equals("Flying is not enabled on this server")) {
			event.setCancelled(true);
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
	
	@EventHandler
	private void trashChest(InventoryCloseEvent event) {
		if (event.getInventory().getLocation() == null) {
			return;
		}
		if (event.getInventory().getLocation().getBlock().getType() != Material.CHEST) {
			return;
		}
		if (event.getInventory().getType() == InventoryType.CHEST) {
			Block chest = event.getInventory().getLocation().getBlock();
			if (chest.getRelative(BlockFace.EAST).getType() == Material.WALL_SIGN) {
				Block sign = (Block)chest.getRelative(BlockFace.EAST);
				if (((Sign)sign.getState()).getLines()[1].equals("[Trash]") && sign.getData() == (byte)5) {
					event.getInventory().clear();
				}
			}
			if (chest.getRelative(BlockFace.WEST).getType() == Material.WALL_SIGN) {
				Block sign = (Block)chest.getRelative(BlockFace.WEST);
				if (((Sign)sign.getState()).getLines()[1].equals("[Trash]") && sign.getData() == (byte)4) {
					event.getInventory().clear();
				}
			}
			if (chest.getRelative(BlockFace.NORTH).getType() == Material.WALL_SIGN) {
				Block sign = (Block)chest.getRelative(BlockFace.NORTH);
				if (((Sign)sign.getState()).getLines()[1].equals("[Trash]") && sign.getData() == (byte)2) {
					event.getInventory().clear();
				}
			}
			if (chest.getRelative(BlockFace.SOUTH).getType() == Material.WALL_SIGN) {
				Block sign = (Block)chest.getRelative(BlockFace.SOUTH);
				if (((Sign)sign.getState()).getLines()[1].equals("[Trash]") && sign.getData() == (byte)3) {
					event.getInventory().clear();
				}
			}
		}
	}
	
	@EventHandler
	private void freeChest(InventoryOpenEvent event) {
		if (event.getInventory().getLocation() == null) {
			return;
		}
		if (event.getInventory().getLocation().getBlock().getType() != Material.CHEST) {
			return;
		}
		if (event.getInventory().getType() == InventoryType.CHEST) {
			Block chest = event.getInventory().getLocation().getBlock();
			if (chest.getRelative(BlockFace.EAST).getType() == Material.WALL_SIGN) {
				Block sign = (Block)chest.getRelative(BlockFace.EAST);
				if (((Sign)sign.getState()).getLines()[1].equals("[Free]") && sign.getData() == (byte)5) {
					Material material = Material.matchMaterial(((Sign)sign.getState()).getLines()[2]);
					for (int i = 0; i < 27; i++) {
						event.getInventory().setItem(i, new ItemStack(material, material.getMaxStackSize()));
					}
				}
			}
			if (chest.getRelative(BlockFace.WEST).getType() == Material.WALL_SIGN) {
				Block sign = (Block)chest.getRelative(BlockFace.WEST);
				if (((Sign)sign.getState()).getLines()[1].equals("[Free]") && sign.getData() == (byte)4) {
					Material material = Material.matchMaterial(((Sign)sign.getState()).getLines()[2]);
					for (int i = 0; i < 27; i++) {
						event.getInventory().setItem(i, new ItemStack(material, material.getMaxStackSize()));
					}
				}
			}
			if (chest.getRelative(BlockFace.NORTH).getType() == Material.WALL_SIGN) {
				Block sign = (Block)chest.getRelative(BlockFace.NORTH);
				if (((Sign)sign.getState()).getLines()[1].equals("[Free]") && sign.getData() == (byte)2) {
					Material material = Material.matchMaterial(((Sign)sign.getState()).getLines()[2]);
					for (int i = 0; i < 27; i++) {
						event.getInventory().setItem(i, new ItemStack(material, material.getMaxStackSize()));
					}
				}
			}
			if (chest.getRelative(BlockFace.SOUTH).getType() == Material.WALL_SIGN) {
				Block sign = (Block)chest.getRelative(BlockFace.SOUTH);
				if (((Sign)sign.getState()).getLines()[1].equals("[Free]") && sign.getData() == (byte)3) {
					Material material = Material.matchMaterial(((Sign)sign.getState()).getLines()[2]);
					for (int i = 0; i < 27; i++) {
						event.getInventory().setItem(i, new ItemStack(material, material.getMaxStackSize()));
					}
				}
			}
		}
	}
}

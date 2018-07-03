package AIO;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.permission.*;

import java.io.File;
import java.util.*;
import java.util.logging.Level;

public class aio extends JavaPlugin implements Listener {
	
	Chat chat;
	Economy economy;
	Permission permission;

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
	EntityRename entityRename;
	Lottery lottery;
	DropParty dropParty;
	InventoryCheck inventoryCheck;
	ChatGames chatGames;
	AntiSwear antiSwear;
	Crates crates;
	Spawn spawn;

	PlayerMessage playerMessage;
	FreezeManager freezeManager;

	CacheManager cacheManager;

	private File messageFile;
	private FileConfiguration messageConfig;
	
	@Override
	public void onEnable() {
		getLogger().info("Starting All-In-One Plugin");

        saveDefaultConfig();

		File messages = new File(getDataFolder(), "messages.yml");
		if(!messages.exists()) {
			getLogger().log(Level.SEVERE, "Unable to read messages file! Server shutdown initiated.");
			getServer().shutdown();
		}

        sqlconnector = new SQLConnector(this);
        sqlconnector.connect(getConfig().getString("mysql.server"), "minecraft", getConfig().getString("mysql.username"), getConfig().getString("mysql.password"), false);

		antiSwear = new AntiSwear(this);
		bannerCreator = new BannerCreator(this);
		advertisements = new Advertisements(this);
		antiItemlag = new AntiItemlag(this);
		enchant = new Enchant(this);
		entityRename = new EntityRename(this);
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
		freezeManager = new FreezeManager(this);
		cacheManager = new CacheManager(this);
		playerJoin = new PlayerJoin(this);
		playerLeave = new PlayerLeave(this);
		specialChests = new SpecialChests(this);
		lottery = new Lottery(this);
		dropParty = new DropParty(this);
		inventoryCheck = new InventoryCheck(this);
		chatGames = new ChatGames(this);
		crates = new Crates(this);
		spawn = new Spawn(this);

		Bukkit.getPluginManager().registerEvents(this, this);
		setupChat();
		setupEconomy();
		setupPermissions();
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Stopping All-In-One Plugin");
		advertisements.removeBar();
		lottery.disable();
		warp.saveWarps();
		saveConfig();
		for(Player player: getServer().getOnlinePlayers()) {
			cacheManager.updatePlayer(player.getUniqueId());
			cacheManager.savePlayer(cacheManager.getPlayer(player.getUniqueId()));
		}
		sqlconnector.disconnect();
	}

    public FileConfiguration getMessages() {
        if(messageConfig == null) {
            if(messageFile == null) {
                messageFile = new File(getDataFolder(), "messages.yml");
            }
            messageConfig = YamlConfiguration.loadConfiguration(messageFile);
        }
        return messageConfig;
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
		return ChatColor.translateAlternateColorCodes('&', input + "&r");
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

package AIO;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;

public class aio extends JavaPlugin {
	Chat chat;
	Economy economy;
	Permission permission;
	WorldEditPlugin worldEdit;
	WorldGuardPlugin worldGuard;

	SQLConnector sqlconnector;
	Enchant enchant;
	AntiItemlag antiItemlag;
	PrivateMessage privateMessage;
	Advertisements advertisements;
	Teleport teleport;
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
	PrisonCells prisonCells;
	AuctionManager auctionManager;
	Holograms holograms;
	PlayerMessage playerMessage;
	FreezeManager freezeManager;
	CacheManager cacheManager;
	TreeFeller treeFeller;
	Obsidiantolava obsidiantolava;
	CustomSigns customSigns;
	MotdManager motdManager;
	BanManager banManager;
	Visit visit;
	SpecialPapers specialPapers;
	VoteSystem voteSystem;
	Coin coin;

	private File messageFile;
	private FileConfiguration messageConfig;
	
	@Override
	public void onEnable() {
		getLogger().info("Starting All-In-One Plugin");

        saveDefaultConfig();

		setupChat();
		setupEconomy();
		setupPermissions();
		setupWorldEdit();
		setupWorldGuard();

		File messages = new File(getDataFolder(), "messages.yml");
		if(!messages.exists()) {
			getLogger().severe("Unable to read messages file! server is shutting down.");
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
		teleport = new Teleport(this);
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
		prisonCells = new PrisonCells(this);
		auctionManager = new AuctionManager(this);
		holograms = new Holograms(this);
		treeFeller = new TreeFeller(this);
		obsidiantolava = new Obsidiantolava(this);
		customSigns = new CustomSigns(this);
		motdManager = new MotdManager(this);
		banManager = new BanManager(this);
		visit = new Visit(this);
		specialPapers = new SpecialPapers(this);
		voteSystem = new VoteSystem(this);
		coin = new Coin(this);
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Stopping All-In-One Plugin");

		advertisements.removeBar();
		lottery.disable();
		prisonCells.save();
		saveConfig();
		for(Player player: getServer().getOnlinePlayers()) {
			cacheManager.updatePlayer(player.getUniqueId());
			cacheManager.savePlayer(cacheManager.getPlayer(player.getUniqueId()));
		}
		sqlconnector.disconnect();
	}

    String getMessage(String path, String... strings) {
		if (messageConfig == null) {
			if (messageFile == null) {
				messageFile = new File(getDataFolder(), "messages.yml");
			}
			messageConfig = YamlConfiguration.loadConfiguration(messageFile);
		}
	    String message = messageConfig.getString(path);
	    if (messageConfig.getString(path.split("\\.")[0] + ".prefix") != null && message.contains("{prefix}")) {
            message = message.replace("{prefix}", messageConfig.getString(path.split("\\.")[0] + ".prefix"));
        }
        for (String newSubString : strings) {
            String oldSubString = message.substring(message.indexOf("{"), message.indexOf("}")+1);
            message = message.replace(oldSubString, newSubString);
        }
        return aio.colorize(message);
    }

    public static String getPlayerName(Player player) {
		if (Bukkit.getServer().getPlayer(player.getUniqueId()).getDisplayName().equalsIgnoreCase("")) {
			return player.getName();
		}
		return aio.colorize(player.getDisplayName());
	}

    public static String[] allButFirst(String[] input) {
        return Arrays.copyOfRange(input, 1, input.length);
    }

    public static String colorize(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    private void setupWorldEdit() {
		worldEdit = (WorldEditPlugin)getServer().getPluginManager().getPlugin("WorldEdit");
	}

	private void setupWorldGuard() {
		worldGuard = (WorldGuardPlugin)getServer().getPluginManager().getPlugin("WorldGuard");
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
}

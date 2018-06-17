package AIO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

public class aio extends JavaPlugin implements Listener {
	
	private Chat chat;
	private Economy economy;
	
	PrivateMessage privateMessage;
	Advertisements advertisements;
	TeleportA teleporta;
	
	Location spawn;
	
	List<Player> godPlayers = new ArrayList<Player>();
	
	@Override
	public void onEnable() {
		getLogger().info("Starting All-In-One Plugin");
		
		//retrieve server id
		//connect to mysql
		//enable necessary parts
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		
		spawn = new Location(getServer().getWorld(getConfig().getString("spawn-world")), getConfig().getDouble("spawn-x"), getConfig().getDouble("spawn-y"), getConfig().getDouble("spawn-z"), (float)getConfig().getDouble("spawn-yaw"), (float)getConfig().getDouble("spawn-pitch"));
		
		advertisements = new Advertisements(this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerLeave(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerMessage(this), this);
		Bukkit.getPluginManager().registerEvents(this, this);
		teleporta = new TeleportA(this);
		privateMessage = new PrivateMessage(this);
		Bukkit.getPluginManager().registerEvents(privateMessage, this);
		setupChat();
		setupEconomy();
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Stopping All-In-One Plugin");
		advertisements.removeBar();
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("kickall")) {
			if (sender instanceof Player) {
				for (Player player: getServer().getOnlinePlayers()) {
					if (sender == player) {
						continue;
					}
					player.kickPlayer(String.join(" ", args));
				}
			} else {
				for (Player player: getServer().getOnlinePlayers()) {
					player.kickPlayer(String.join(" ", args));
				}
			}
			getLogger().info(sender.getName() + " kicked all players.");
		}
		
		if (command.getName().equalsIgnoreCase("kick")) {
			if (args.length > 0 && getServer().getPlayer(args[0]) != null) {
				getServer().getPlayer(args[0]).kickPlayer(String.join(" ", allButFirst(args)));
			} else {
				sender.sendMessage("Player not found.");
			}
		}
		
		if (command.getName().equalsIgnoreCase("nick")) {
			if (sender instanceof Player) {
				Player player = (Player)sender;
				player.setDisplayName(colorize(args[0]));
			} else {
				sender.sendMessage("Only players can execute this command.");
			}
		}
		
		if (command.getName().equalsIgnoreCase("ping")) {
			sender.sendMessage("Pong!");
		}
		
		//Send private message to another player
		if (command.getName().equalsIgnoreCase("msg")) {
			if (args.length > 1 && getServer().getPlayer(args[0]) != null) {
				if (sender instanceof Player) {
					Player player = (Player)sender;
					privateMessage.message(player, getServer().getPlayer(args[0]), String.join(" ", allButFirst(args)));
				} else {
					sender.sendMessage("Only players can execute this command.");
				}
			} else if (args.length == 1 && getServer().getPlayer(args[0]) == null) {
				sender.sendMessage("Player not found.");
			}
		}
		
		//Reply to message
		if (command.getName().equalsIgnoreCase("reply")) {
			if (args.length > 0) {
				if (sender instanceof Player) {
					Player player = (Player)sender;
					privateMessage.reply(player, String.join(" ", args));
				} else {
					sender.sendMessage("Only players can execute this command.");
				}
			}
		}
		
		if (command.getName().equalsIgnoreCase("sun")) {
			if (sender instanceof Player && args.length == 0) {
				((Player)sender).getLocation().getWorld().setThundering(false);
				((Player)sender).getLocation().getWorld().setStorm(false);
			} else if (args.length == 0) {
				sender.sendMessage("World not given");
			} else if (getServer().getWorld(args[0]) != null) {
				getServer().getWorld(args[0]).setThundering(false);
				getServer().getWorld(args[0]).setStorm(false);
			}
		}
		
		if (command.getName().equalsIgnoreCase("rain")) {
			if (sender instanceof Player && args.length == 0) {
				((Player)sender).getLocation().getWorld().setThundering(true);
				((Player)sender).getLocation().getWorld().setStorm(false);
			} else if (args.length == 0) {
				sender.sendMessage("World not given");
			} else if (getServer().getWorld(args[0]) != null) {
				getServer().getWorld(args[0]).setThundering(true);
				getServer().getWorld(args[0]).setStorm(false);
			}
		}
		
		if (command.getName().equalsIgnoreCase("storm")) {
			if (sender instanceof Player) {
				((Player)sender).getLocation().getWorld().setThundering(true);
				((Player)sender).getLocation().getWorld().setStorm(true);
			} else if (args.length == 0) {
				sender.sendMessage("World not given");
			} else if (getServer().getWorld(args[0]) != null) {
				getServer().getWorld(args[0]).setThundering(true);
				getServer().getWorld(args[0]).setStorm(true);
			}
		}
		
		if (command.getName().equalsIgnoreCase("worlds")) {
			sender.sendMessage("Worlds: " + getServer().getWorlds().toString());
		}
		
		if (command.getName().equalsIgnoreCase("dawn")) {
			if (sender instanceof Player) {
				((Player)sender).getLocation().getWorld().setTime(0);
			} else if (args.length == 0) {
				sender.sendMessage("World not given");
			} else if (getServer().getWorld(args[0]) != null) {
				getServer().getWorld(args[0]).setTime(0);
			}
		}
		
		if (command.getName().equalsIgnoreCase("morning")) {
			if (sender instanceof Player) {
				((Player)sender).getLocation().getWorld().setTime(450);
			} else if (args.length == 0) {
				sender.sendMessage("World not given");
			} else if (getServer().getWorld(args[0]) != null) {
				getServer().getWorld(args[0]).setTime(450);
			}
		}
		
		if (command.getName().equalsIgnoreCase("day")) {
			if (sender instanceof Player) {
				((Player)sender).getLocation().getWorld().setTime(1000);
			} else if (args.length == 0) {
				sender.sendMessage("World not given");
			} else if (getServer().getWorld(args[0]) != null) {
				getServer().getWorld(args[0]).setTime(1000);
			}
		}
		
		if (command.getName().equalsIgnoreCase("noon")) {
			if (sender instanceof Player) {
				((Player)sender).getLocation().getWorld().setTime(6000);
			} else if (args.length == 0) {
				sender.sendMessage("World not given");
			} else if (getServer().getWorld(args[0]) != null) {
				getServer().getWorld(args[0]).setTime(6000);
			}
		}
		
		if (command.getName().equalsIgnoreCase("afternoon")) {
			if (sender instanceof Player) {
				((Player)sender).getLocation().getWorld().setTime(10000);
			} else if (args.length == 0) {
				sender.sendMessage("World not given");
			} else if (getServer().getWorld(args[0]) != null) {
				getServer().getWorld(args[0]).setTime(10000);
			}
		}
		
		if (command.getName().equalsIgnoreCase("dusk")) {
			if (sender instanceof Player) {
				((Player)sender).getLocation().getWorld().setTime(12500);
			} else if (args.length == 0) {
				sender.sendMessage("World not given");
			} else if (getServer().getWorld(args[0]) != null) {
				getServer().getWorld(args[0]).setTime(12500);
			}
		}
		
		if (command.getName().equalsIgnoreCase("night")) {
			if (sender instanceof Player) {
				((Player)sender).getLocation().getWorld().setTime(13000);
			} else if (args.length == 0) {
				sender.sendMessage("World not given");
			} else if (getServer().getWorld(args[0]) != null) {
				getServer().getWorld(args[0]).setTime(13000);
			}
		}
		
		if (command.getName().equalsIgnoreCase("midnight")) {
			if (sender instanceof Player) {
				((Player)sender).getLocation().getWorld().setTime(18000);
			} else if (args.length == 0) {
				sender.sendMessage("World not given");
			} else if (getServer().getWorld(args[0]) != null) {
				getServer().getWorld(args[0]).setTime(18000);
			}
		}
		
		if (command.getName().equalsIgnoreCase("survival")) {
			if (sender instanceof Player) {
				Player player = (Player)sender;
				player.setGameMode(GameMode.SURVIVAL);
			} else {
				sender.sendMessage("Only players can execute this command.");
			}
		}
		
		if (command.getName().equalsIgnoreCase("ad")) {
			advertisements.addAd(sender, String.join(" ", args));
		}
		
		if (command.getName().equalsIgnoreCase("creative")) {
			if (sender instanceof Player) {
				Player player = (Player)sender;
				player.setGameMode(GameMode.CREATIVE);
			} else {
				sender.sendMessage("Only players can execute this command.");
			}
		}
		
		if (command.getName().equalsIgnoreCase("adventure")) {
			if (sender instanceof Player) {
				Player player = (Player)sender;
				player.setGameMode(GameMode.ADVENTURE);
			} else {
				sender.sendMessage("Only players can execute this command.");
			}
		}
		
		if (command.getName().equalsIgnoreCase("spectator")) {
			if (sender instanceof Player) {
				Player player = (Player)sender;
				player.setGameMode(GameMode.SPECTATOR);
			} else {
				sender.sendMessage("Only players can execute this command.");
			}
		}
		
		if (command.getName().equalsIgnoreCase("gm")) {
			if (sender instanceof Player) {
				Player player = (Player)sender;
				String[] gms = {"survival", "s", "0"};
				String[] gmc = {"creative", "c", "1"};
				String[] gma = {"adventure", "a", "2"};
				String[] gmsp = {"spectator", "sp", "3"};
				if (args.length > 0 && Arrays.asList(gms).contains(args[0])) {
					player.setGameMode(GameMode.SURVIVAL);
				}
				if (args.length > 0 && Arrays.asList(gmc).contains(args[0])) {
					player.setGameMode(GameMode.CREATIVE);
				}
				if (args.length > 0 && Arrays.asList(gma).contains(args[0])) {
					player.setGameMode(GameMode.ADVENTURE);
				}
				if (args.length > 0 && Arrays.asList(gmsp).contains(args[0])) {
					player.setGameMode(GameMode.SPECTATOR);
				}
			} else {
				sender.sendMessage("Only players can execute this command.");
			}
		}
		
		if (command.getName().equalsIgnoreCase("balance")) {
			if (sender instanceof Player) {
				Player player = (Player)sender;
				economy.depositPlayer(player, 10);
				player.sendMessage("Current balance: " + economy.getBalance(player));
			}
		}
		
		if (command.getName().equalsIgnoreCase("heal")) {
			if (args.length == 0 && !(sender instanceof Player)) {
				sender.sendMessage("No playername given.");
			} else {
				if (args.length == 0) {
					((Player)sender).setHealth(20.0);
				} else {
					if (getServer().getPlayer(args[0]) != null) {
						getServer().getPlayer(args[0]).setHealth(20.0);
					} else {
						sender.sendMessage("Player not found.");
					}
				}
			}
		}
		
		if (command.getName().equalsIgnoreCase("feed")) {
			if (args.length == 0 && !(sender instanceof Player)) {
				sender.sendMessage("No playername given.");
			} else {
				if (args.length == 0) {
					((Player)sender).setFoodLevel(20);
					((Player)sender).setExhaustion(0.0f);
				} else {
					if (getServer().getPlayer(args[0]) != null) {
						getServer().getPlayer(args[0]).setFoodLevel(20);
						getServer().getPlayer(args[0]).setExhaustion(0.0f);
					} else {
						sender.sendMessage("Player not found.");
					}
				}
			}
		}
		
		if (command.getName().equalsIgnoreCase("fly")) {
			if (sender instanceof Player) {
				if (args.length == 0) {
					((Player)sender).setAllowFlight(!((Player)sender).getAllowFlight());
				} else {
					getServer().getPlayer(args[0]).setAllowFlight(!getServer().getPlayer(args[0]).getAllowFlight());
				}
			} else if (args.length == 0) {
				sender.sendMessage("Player not found.");
			} else {
				getServer().getPlayer(args[0]).setAllowFlight(!getServer().getPlayer(args[0]).getAllowFlight());
			}
		}
		
		if (command.getName().equalsIgnoreCase("god")) {
			if (sender instanceof Player) {
				if (args.length == 0) {
					if (godPlayers.contains((Player)sender)) {
						godPlayers.remove((Player)sender);
					} else {
						godPlayers.add((Player)sender);
					}
				} else {
					if (godPlayers.contains(getServer().getPlayer(args[0]))) {
						godPlayers.remove(getServer().getPlayer(args[0]));
					} else {
						godPlayers.add(getServer().getPlayer(args[0]));
					}
				}
			} else if (args.length == 0) {
				sender.sendMessage("Player not found.");
			} else {
				if (godPlayers.contains(getServer().getPlayer(args[0]))) {
					godPlayers.remove(getServer().getPlayer(args[0]));
				} else {
					godPlayers.add(getServer().getPlayer(args[0]));
				}
			}
		}
		
		if (command.getName().equalsIgnoreCase("kittycannon")) {
			if (sender instanceof Player) {
				Ocelot ocelot = (Ocelot)((Player)sender).getWorld().spawnEntity(((Player)sender).getLocation(), EntityType.OCELOT);
				ocelot.setBaby();
				ocelot.setCatType(Ocelot.Type.BLACK_CAT);
				ocelot.setVelocity(((Player)sender).getEyeLocation().getDirection().multiply(2.0));
				
				new BukkitRunnable() {
					
					@Override
					public void run() {
						if (ocelot.isOnGround()) {
							ocelot.getLocation().getWorld().createExplosion(ocelot.getLocation(), 0.0f);
							ocelot.remove();
							cancel();
						}
						
					}
				}.runTaskTimer(this, 10, 2);
			}
		}
		
		if (command.getName().equalsIgnoreCase("giveself")) {
			if (sender instanceof Player) {
				ItemStack toGive = new ItemStack(Material.matchMaterial(args[0]));
				if (args.length > 1) {
					toGive.setAmount(Integer.parseInt(args[1]));
				} else {
					toGive.setAmount(1);
				}
				((Player)sender).getInventory().addItem(toGive);
			}
		}
		
		if (command.getName().equalsIgnoreCase("tpa")) {
			if (sender instanceof Player && args.length > 0 && getServer().getPlayer(args[0]) != null) {
				teleporta.request((Player)sender, getServer().getPlayer(args[0]), false);
			}
		}
		
		if (command.getName().equalsIgnoreCase("tpahere")) {
			if (sender instanceof Player && args.length > 0 && getServer().getPlayer(args[0]) != null) {
				teleporta.request((Player)sender, getServer().getPlayer(args[0]), true);
			}
		}
		
		if (command.getName().equalsIgnoreCase("tpaccept")) {
			if (sender instanceof Player) {
				teleporta.decide((Player)sender, true);
			}
		}
		
		if (command.getName().equalsIgnoreCase("tpdeny")) {
			if (sender instanceof Player) {
				teleporta.decide((Player)sender, false);
			}
		}
		
		if (command.getName().equalsIgnoreCase("spawn")) {
			if (sender instanceof Player) {
				((Player)sender).teleport(spawn);
			} else {
				sender.sendMessage("Only players can execute this command");
			}
		}
		
		if (command.getName().equalsIgnoreCase("setspawn")) {
			if (sender instanceof Player) {
				getConfig().set("spawn-world", ((Player)sender).getLocation().getWorld().getName());
				getConfig().set("spawn-x", ((Player)sender).getLocation().getX());
				getConfig().set("spawn-y", ((Player)sender).getLocation().getY());
				getConfig().set("spawn-z", ((Player)sender).getLocation().getZ());
				getConfig().set("spawn-yaw", ((Player)sender).getLocation().getYaw());
				getConfig().set("spawn-pitch", ((Player)sender).getLocation().getPitch());
				saveConfig();
				spawn = ((Player)sender).getLocation();
			} else {
				sender.sendMessage("Only players can execute this command");
			}
		}
		
		if (command.getName().equalsIgnoreCase("more")) {
			if (sender instanceof Player) {
				((Player)sender).getInventory().getItemInMainHand().setAmount(((Player)sender).getInventory().getItemInMainHand().getMaxStackSize());
			} else {
				sender.sendMessage("Only players can execute this command.");
			}
		}
		
		if (command.getName().equalsIgnoreCase("repair")) {
			if (sender instanceof Player) {
				((Player)sender).getInventory().getItemInMainHand().setDurability((short)0);
			}
 		}
		
		if (command.getName().equalsIgnoreCase("skull")) {
			if (sender instanceof Player) {
				if (args.length == 0) {
					ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
					SkullMeta meta = (SkullMeta)head.getItemMeta();
					meta.setOwningPlayer((Player)sender);
					head.setItemMeta(meta);
					((Player)sender).getInventory().addItem(head);
				} else if (getServer().getPlayer(args[0]) != null) {
					ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
					SkullMeta meta = (SkullMeta)head.getItemMeta();
					meta.setOwningPlayer(getServer().getPlayer(args[0]));
					head.setItemMeta(meta);
					((Player)sender).getInventory().addItem(head);
				}
			}
		}
		
		if (command.getName().equalsIgnoreCase("craftbench")) {
			if (sender instanceof Player) {
				((Player)sender).openWorkbench(null, true);
			}
		}
		
		return false;
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
	
	public static String[] allButFirst(String[] input) {
		return Arrays.copyOfRange(input, 1, input.length);
	}
	
	public static String colorize(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
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
	private void playerHarm(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (godPlayers.contains((Player)event.getEntity())) {
				event.setCancelled(true);
				((Player)event.getEntity()).setHealth(20.0);
				((Player)event.getEntity()).setFoodLevel(20);
			}
		}
	}
	
	List<Item> items = new ArrayList<Item>();
	
	@EventHandler
	private void playerDeath(PlayerRespawnEvent event) {
		event.setRespawnLocation(spawn);
	}
	
	@EventHandler
	private void itemDrop(ItemSpawnEvent event) {
		items.add(event.getEntity());
		if (items.size() >= 1000) {
			for (int i= 0; i < 50; i++) {
				items.get(0).remove();
				items.remove(0);
			}
			getServer().broadcastMessage("Warning: 50 dropped items have been removed to prevent lag!");
		}
		System.out.println(items.size());
	}
	
	@EventHandler
	private void itemPickup(EntityPickupItemEvent event) {
		items.remove(event.getItem());
	}
	
	@EventHandler
	private void itemMerge(ItemMergeEvent event) {
		items.remove(event.getEntity());
	}
	
	@EventHandler
	private void itemDespawn(ItemDespawnEvent event) {
		items.remove(event.getEntity());
	}
}

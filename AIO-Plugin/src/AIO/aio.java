package AIO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Iterator;
import java.util.Set;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.block.Sign;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Ocelot;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

public class aio extends JavaPlugin implements Listener {
	
	private Chat chat;
	private Economy economy;
	
	Enchant enchant;
	AntiItemlag antiItemlag;
	PrivateMessage privateMessage;
	Advertisements advertisements;
	TeleportA teleporta;
	BannerCreator bannerCreator;
	AntiSpambot antiSpambot;
	Warp warp;
	
	Location spawn;
	
	List<Player> godPlayers = new ArrayList<Player>();
	List<Player> frozenPlayers = new ArrayList<Player>();
	
	@Override
	public void onEnable() {
		getLogger().info("Starting All-In-One Plugin");
		
		//retrieve server id
		//connect to mysql
		//enable necessary parts
		getConfig().options().copyDefaults(true);
		saveDefaultConfig();
		
		spawn = new Location(getServer().getWorld(getConfig().getString("spawn-world")), getConfig().getDouble("spawn-x"), getConfig().getDouble("spawn-y"), getConfig().getDouble("spawn-z"), (float)getConfig().getDouble("spawn-yaw"), (float)getConfig().getDouble("spawn-pitch"));
		bannerCreator = new BannerCreator(this);
		advertisements = new Advertisements(this);
		antiItemlag = new AntiItemlag(this);
		enchant = new Enchant(this);
		warp = new Warp(this);

		getServer().getPluginManager().registerEvents(new Warp(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerLeave(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerMessage(this), this);
		Bukkit.getPluginManager().registerEvents(bannerCreator, this);
		Bukkit.getPluginManager().registerEvents(this, this);
		teleporta = new TeleportA(this);
		privateMessage = new PrivateMessage(this);
		Bukkit.getPluginManager().registerEvents(enchant, this);
		Bukkit.getPluginManager().registerEvents(privateMessage, this);
		setupChat();
		setupEconomy();
		antiSpambot = new AntiSpambot(this);
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Stopping All-In-One Plugin");
		advertisements.removeBar();
		saveConfig();
		warp.saveWarps();
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

		if (command.getName().equalsIgnoreCase("whois")) {
			if (args.length == 0) {
				sender.sendMessage("Player not given.");
			} else if (getServer().getPlayer(args[0]) == null) {
				sender.sendMessage("Player not found.");
			} else {
				sender.sendMessage("Information about " + getServer().getPlayer(args[0]).getName());
				sender.sendMessage("IP Address: " + getServer().getPlayer(args[0]).getAddress().toString());
				sender.sendMessage("OP: " + getServer().getPlayer(args[0]).isOp());
				sender.sendMessage("Fly mode: " + getServer().getPlayer(args[0]).getAllowFlight());
				sender.sendMessage("God mode: " + godPlayers.contains(getServer().getPlayer(args[0])));
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
			if (sender instanceof Player) {
				advertisements.adCommand((Player)sender, args);
			}
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
						if (ocelot.isOnGround() || ocelot.isDead() || ocelot.getLocation().getBlockY() < 0) {
							ocelot.getLocation().getWorld().createExplosion(ocelot.getLocation(), 0.0f);
							ocelot.remove();
							cancel();
						}
						
					}
				}.runTaskTimer(this, 5, 2);
			}
		}
		
		if (command.getName().equalsIgnoreCase("tntcannon")) {
			if (sender instanceof Player) {
				TNTPrimed tnt = (TNTPrimed)((Player)sender).getWorld().spawnEntity(((Player)sender).getLocation(), EntityType.PRIMED_TNT);
				tnt.setVelocity(((Player)sender).getEyeLocation().getDirection().multiply(2.0));
				tnt.setFuseTicks(300);
				new BukkitRunnable() {
					
					@Override
					public void run() {
						if (tnt.isOnGround() || tnt.getLocation().getBlockY() < 0) {
							tnt.getLocation().getWorld().createExplosion(tnt.getLocation(), 0.0f);
							tnt.remove();
							cancel();
						}
						
					}
				}.runTaskTimer(this, 5, 2);
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
		
		if (command.getName().equalsIgnoreCase("banner")) {
			if (sender instanceof Player) {
				if (args.length == 0) {
					bannerCreator.createBanner((Player)sender);	
				} else if (args[0].equalsIgnoreCase("get")) {
					if (BannerCreator.getByName(String.join(" ", allButFirst(args))) != null) {
						((Player)sender).getInventory().addItem(BannerCreator.getByName(String.join(" ", allButFirst(args))));
					}
				} else if (args[0].equalsIgnoreCase("letter")) {
					String[] borderWanted = {"yes", "y", "true", "border", "bordered"};
					boolean bordered = false;
					if (args.length > 4) {
						for (String border: borderWanted) {
							if (args[4].equals(border)) {
								bordered = true;
							}
						}
					}
					if (BannerCreator.getCharacter(args[1], args[2], args[3], bordered) != null) {
						((Player)sender).getInventory().addItem(BannerCreator.getCharacter(args[1], args[2], args[3], bordered));
					}
				}
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
					meta.setOwningPlayer(getServer().getOfflinePlayer(args[0]));
					head.setItemMeta(meta);
					((Player)sender).getInventory().addItem(head);
				}
			}
		}
		
		if (command.getName().equalsIgnoreCase("flyspeed")) {
			if (sender instanceof Player && args.length == 1) {
				((Player)sender).setFlySpeed(Float.parseFloat(args[0]));
			} else {
				getServer().getPlayer(args[1]).setFlySpeed(Float.parseFloat(args[0]) / 10f);
			}
		}
		
		if (command.getName().equalsIgnoreCase("enchanttable")) {
			if (sender instanceof Player) {
				((Player)sender).openEnchanting(new Location(getServer().getWorld("world"), -278.0, 69.0, 296.0), true);
			}
		}
		
		if (command.getName().equalsIgnoreCase("enderchest")) {
			if (sender instanceof Player) {
				((Player)sender).openInventory(((Player)sender).getEnderChest());
			}
		}
		
		if (command.getName().equalsIgnoreCase("craftbench")) {
			if (sender instanceof Player) {
				((Player)sender).openWorkbench(null, true);
			}
		}
		
		if (command.getName().equalsIgnoreCase("nearby")) {
			if (sender instanceof Player) {
				for (Entity entity: ((Player)sender).getNearbyEntities(100, 100, 100)) {
					if (entity instanceof Player) {
						if (((Player)entity).getLocation().distance(((Player)sender).getLocation()) <= 100.0) {
							((Player)sender).sendMessage(((Player)entity).getName() + ": " + ((Player)entity).getLocation().distance(((Player)sender).getLocation()) + "m");
						}
					}
				}
			}
		}
		
		if (command.getName().equalsIgnoreCase("news")) {
			sender.sendMessage("Server news:");
			sender.sendMessage("Server now working");
		}
		
		if (command.getName().equalsIgnoreCase("motd")) {
			sender.sendMessage("Best server ever!");
		}
		
		if (command.getName().equalsIgnoreCase("rules")) {
			sender.sendMessage("Eat, sleep, code, repeat");
		}
		
		if (command.getName().equalsIgnoreCase("freeze")) {
			if (args.length == 0) {
				sender.sendMessage("Player not given.");
			} else {
				if (getServer().getPlayer(args[0]) == null) {
					sender.sendMessage("Player not found.");
				} else {
					if (frozenPlayers.contains(getServer().getPlayer(args[0]))) {
						frozenPlayers.remove(getServer().getPlayer(args[0]));
					} else {
						frozenPlayers.add(getServer().getPlayer(args[0]));
					}
				}
			}
		}
		
		if (command.getName().equalsIgnoreCase("unsafeenchant")) {
			if (sender instanceof Player) {
				((Player)sender).getInventory().getItemInMainHand().addUnsafeEnchantment(Enchant.Translate(args[0]), Integer.parseInt(args[1]));
			}
		}
		
		//Delete warp
		if(command.getName().equalsIgnoreCase("delwarp")) {
			/* if(!sender.hasPermission("aio.command.delwarp")) {
				sender.sendMessage(colorize(warp.noperm));
				return true;
			} */
			if(args.length < 1) {
				sender.sendMessage(ChatColor.RED + "/delwarp <warp name>");
				return true;
			}
			if(warp.getWarpLocation(args[0]) == null) {
				sender.sendMessage(colorize(warp.warp_not_found));
				return true;
			}
			warp.delWarp((Player)sender, args[0]);
			return false;
		}
		
		//Set new warp
		if(command.getName().equalsIgnoreCase("setwarp")) {
			if(!(sender instanceof Player)) {
				sender.sendMessage(colorize(warp.player_only));
				return true;
			}
			/* if(!sender.hasPermission("aio.command.setwarp")) {
				sender.sendMessage(colorize(warp.noperm));
				return true;
			} */
			if(args.length < 1) {
				sender.sendMessage(ChatColor.RED + "/setwarp <warp name>");
				return true;
			}
			if(warp.getWarpLocation(args[0]) != null) {
				sender.sendMessage(colorize(warp.warp_already.replace("{warp}", args[0])));
				return true;
			}
			warp.setWarp((Player)sender, args[0]);
			return false;
		}
		
		//Warping
		if(command.getName().equalsIgnoreCase("warp")) {
			if(!(sender instanceof Player)){
				sender.sendMessage(colorize(warp.player_only));
				return true;
			}
			/* if(!sender.hasPermission("aio.command.warp")) {
				sender.sendMessage(colorize(warp.noperm));
				return true;
			} */
			Player player = (Player)sender;
			if(args.length < 1) {
				player.sendMessage(ChatColor.RED + "/warp <warp name>");
				return true;
			}
			if(warp.getWarpLocation(args[0]) == null) {
				player.sendMessage(colorize(warp.warp_not_found).replace("{warp}", args[0]));
				return true;
			}
			if(warp.getWarpLocation(args[0]).getWorld() == null) {
				player.sendMessage(colorize(warp.no_world));
				return true;
			}
			player.sendMessage(colorize(warp.warping).replace("{warp}", args[0]));
			player.teleport(warp.getWarpLocation(args[0]));
			return false;
		}
		
		//Reloading warp file
		if(command.getName().equalsIgnoreCase("warpreload")) {
			/* if(!sender.hasPermission("aio.command.warpreload")) {
				sender.sendMessage(colorize(warp.noperm));
				return true;
			} */
			reloadConfig();
			warp.reloadWarps();
			saveConfig();
			warp.saveWarps();
			warp.load();
			sender.sendMessage(colorize(warp.reload));
			return false;
		}
		
		//Warplist
		if(command.getName().equalsIgnoreCase("warplist")) {
			/* if(!sender.hasPermission("aio.command.warplist")) {
				sender.sendMessage(colorize(warp.noperm));
				return true;
			} */
			if(!warp.getWarps().isConfigurationSection("warps")) {
				sender.sendMessage(colorize(warp.no_warp));
				return true;
			}
			Set<String> warps = warp.getWarps().getConfigurationSection("warps").getKeys(false);
			Iterator<String> itr = warps.iterator();
			String list = "";
			while(itr.hasNext()) {
				list = list + itr.next() + ", ";
			}
			sender.sendMessage(colorize(warp.warp_list.replace("{list}", list.substring(0, list.length() - 2)).replace("{amount}", String.valueOf(warps.size()))));
			return false;
		}
		
		if (command.getName().equalsIgnoreCase("lightning")) {
			if (sender instanceof Player && args.length == 0) {
				((Player)sender).getWorld().strikeLightning(((Player)sender).getTargetBlock(null, 600).getLocation());
			} else if (args.length == 0) {
				sender.sendMessage("Player not given.");
			} else if (getServer().getPlayer(args[0]) == null) {
				sender.sendMessage("Player not found.");
			} else {
				getServer().getPlayer(args[0]).getWorld().strikeLightning(getServer().getPlayer(args[0]).getLocation());
			}
		}
		
		if (command.getName().equalsIgnoreCase("spawner")) {
			if (sender instanceof Player) {
				if (args.length == 0) {
					sender.sendMessage("No mob type given");
				} else if (EntityType.valueOf(args[0].toUpperCase()) == null) {
					sender.sendMessage("Invalid mob type given.");
				} else {
					if (!((Player)sender).getTargetBlock(null, 10).getType().equals(Material.MOB_SPAWNER)) {
						sender.sendMessage("You must be looking at a mob spawner to change its type");
					} else {
						CreatureSpawner spawner = (CreatureSpawner)((Player)sender).getTargetBlock(null, 10).getState();
						spawner.setSpawnedType(EntityType.valueOf(args[0].toUpperCase()));
						spawner.update();
					}
				}
			} else {
				sender.sendMessage("Only players can execute this command.");
			}
		}
		
		return false;
	}
	
	@EventHandler
	private void playerMove(PlayerMoveEvent event) {
		if (frozenPlayers.contains(event.getPlayer())) {
			event.setCancelled(true);
			event.setTo(event.getFrom());
			event.getPlayer().sendMessage("You are frozen and can not move.");
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
				((Player)event.getEntity()).setRemainingAir(((Player)event.getEntity()).getMaximumAir());
				((Player)event.getEntity()).setFireTicks(0);
			}
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

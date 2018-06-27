package AIO;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.TabCompleteEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Advertisements implements Listener, CommandExecutor {

	private aio plugin;
	
	BossBar bossBar;
	
	List<Player> creatingAds = new ArrayList<Player>();
	List<Player> namingAds = new ArrayList<Player>();
	List<Advertisement> createdAds = new ArrayList<Advertisement>();
	
	List<Advertisement> playerAd = new ArrayList<Advertisement>();
	List<Advertisement> serverAd = new ArrayList<Advertisement>();

	List<Advertisement> reportedAds = new ArrayList<>();
	
	Advertisements(aio plugin) {
		this.plugin = plugin;

		Bukkit.getPluginManager().registerEvents(this, plugin);
		Bukkit.getPluginCommand("ad").setExecutor(this);
		
		serverAd.add(new Advertisement(null, "&aAdvertise your shop: &b/ad create", BarColor.WHITE, BarStyle.SOLID, 60.0));
		serverAd.add(new Advertisement(null, "&aCheck out server news: &b/news", BarColor.WHITE, BarStyle.SOLID, 60.0));
		serverAd.add(new Advertisement(null, "&aGet lava by right clicking &5Obsidian &bwith a &7bucket&b!", BarColor.WHITE, BarStyle.SOLID, 60.0));
		
		bossBar = Bukkit.createBossBar("Loading", BarColor.BLUE, BarStyle.SOLID);
		bossBar.setProgress(1.0);
		bossBar.setVisible(true);
		
		for (Player player: Bukkit.getServer().getOnlinePlayers()) {
			bossBar.addPlayer(player);
		}
		
		new BukkitRunnable() {
			
			@Override
			public void run() {
				if (!playerAd.isEmpty()) {
					bossBar.setTitle(aio.colorize(playerAd.get(0).getText()));
					bossBar.setStyle(playerAd.get(0).getStyle());
					bossBar.setColor(playerAd.get(0).getColor());
					bossBar.setProgress(playerAd.get(0).getTime() / playerAd.get(0).getMaxTime());
					playerAd.get(0).setTime(playerAd.get(0).getTime() - 0.05d);
					if (playerAd.get(0).getTime() <= 0) {
						playerAd.remove(0);
					}
				} else {
					if (serverAd.get(0).getTime() <= 0) {
						cycleServerCommands();
					} else {
						bossBar.setTitle(aio.colorize(serverAd.get(0).getText()));
						bossBar.setStyle(serverAd.get(0).getStyle());
						bossBar.setColor(serverAd.get(0).getColor());
						bossBar.setProgress(serverAd.get(0).getTime() / serverAd.get(0).getMaxTime());
						serverAd.get(0).setTime(serverAd.get(0).getTime() -0.05d);
					}
				}
				
			}
		}.runTaskTimer(plugin, 20, 1);

		plugin.sqlconnector.query("SELECT * FROM minecraft_ad", new SQLCallback() {
			@Override
			public void callback(ResultSet result) {
				try {
					while (result.next()) {
						Advertisement ad = new Advertisement(plugin.getServer().getPlayer(result.getString("minecraft_ad_player")), result.getString("minecraft_ad_text"), Convert.StringToColor(result.getString("minecraft_ad_color")), Convert.StringToStyle(result.getString("minecraft_ad_style")), result.getDouble("minecraft_ad_time"));
						ad.setMaxTime(result.getDouble("minecraft_ad_maxtime"));
						if (result.getBoolean("minecraft_ad_reported")) {
							reportedAds.add(ad);
						} else {
							playerAd.add(ad);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(command.getName().equalsIgnoreCase("ad")) {
			if (sender.hasPermission("aio.ad")) {
				if (sender instanceof Player) {
					adCommand((Player) sender, args);
				}
			} else {
				sender.sendMessage("You don't have permission to execute that command.");
			}
		}
		return false;
	}
	
	@EventHandler
	private void joinEvent(PlayerJoinEvent event) {
		bossBar.addPlayer(event.getPlayer());
	}
	
	private void cycleServerCommands() {
		serverAd.get(0).setTime(serverAd.get(0).getMaxTime());
		serverAd.add(serverAd.get(0));
		serverAd.remove(0);
	}
	
	private void showHelp(Player sender) {
		if (sender.hasPermission("aio.ad")) {
			sender.sendMessage(aio.colorize("&7---------- &bCommand usage:&7 ----------"));
			if (sender.hasPermission("aio.ad.create")) {
				sender.sendMessage(aio.colorize("&b/ad create&5: Opens the advertiesment creating GUI."));
			}
			//sender.sendMessage(aio.colorize("&b/ad edit&5: Edits the current advertisement that you have in queue."));
			if (sender.hasPermission("aio.ad.time")) {
				sender.sendMessage(aio.colorize("&b/ad time&5: Shows you the time left until your ad appears."));
			}
			if (sender.hasPermission("aio.ad.cancel")) {
				sender.sendMessage(aio.colorize("&b/ad cancel&5: Cancels your advertisement."));
			}
			if (sender.hasPermission("aio.ad.report")) {
				sender.sendMessage(aio.colorize("&b/ad report&5: Reports an inappropriate advertisement."));
			}
			if (sender.hasPermission("aio.ad.reports")) {
				sender.sendMessage(aio.colorize("&b/ad reports&5: See reported advertisements."));
			}
			if (sender.hasPermission("aio.ad")) {
				sender.sendMessage(aio.colorize("&b/ad help&5: Shows this help message."));
			}
		}
	}
	
	private double getQueueLength() {
		double length = 0d;
		for (Advertisement ad: playerAd) {
			length += ad.getTime();
		}
		return length;
	}
	
	private double getQueueLengthUntil(Player player) {
		double length = 0d;
		for (Advertisement ad: playerAd) {
			if (ad.getPlayer().equals(player)) {
				return length;
			}
			length += ad.getTime();
		}
		return length;
	}
	
	public void adCommand(Player sender, String[] command) {
		if (command.length == 0) {
			showHelp(sender);
			return;
		}
		if (command[0].equalsIgnoreCase("help")) {
			showHelp(sender);
			return;
		}
		if (command[0].equalsIgnoreCase("create")) {
			if (!sender.hasPermission("aio.ad.create")) {
				sender.sendMessage("You don't have permission for that.");
				return;
			}
			if (getQueueLength() > 600d) {
				sender.sendMessage(aio.colorize("&c&lError: &bThe queue is too long, please try again later."));
				return;
			}
			if (!playerAd.isEmpty() && playerAd.get(0).getPlayer().equals(sender)) {
				sender.sendMessage(aio.colorize("&c&lError: &bYour advertisement is currently running."));
				return;
			}
			for (Advertisement ad: playerAd) {
				if (ad.getPlayer().equals(sender)) {
					sender.sendMessage(aio.colorize("&c&lError: &bYou already have an advertisement in the queue."));
					return;
				}
			}
			creatingAds.add(sender);
			createdAds.add(new Advertisement());
			createdAds.get(createdAds.size() - 1).setPlayer(sender);
			openInventory(sender);
			return;
		}
		if (command[0].equalsIgnoreCase("cancel")) {
			if (command.length == 1) {
				if (!sender.hasPermission("aio.ad.cancel")) {
					sender.sendMessage("You don't have permission for that.");
					return;
				}
				for (Advertisement ad : playerAd) {
					if (ad.getPlayer().equals(sender)) {
						sender.sendMessage(aio.colorize("&bYou have successfully cancelled your advertisement."));
						playerAd.remove(ad);
						return;
					}
				}
				sender.sendMessage(aio.colorize("&c&lError: &bYou don't have an advertisement in the queue."));
				return;
			} else {
				if (!sender.hasPermission("aio.ad.cancel.others")) {
					sender.sendMessage("You don't have permission for that.");
					return;
				}
				for (Advertisement ad : playerAd) {
					if (ad.getPlayer().equals(plugin.getServer().getPlayer(command[1]))) {
						sender.sendMessage(aio.colorize("&bYou have successfully cancelled " + ad.getPlayer() + "'s advertisement."));
						playerAd.remove(ad);
						return;
					}
				}
				sender.sendMessage(aio.colorize("&c&lError: &b " + plugin.getServer().getPlayer(command[1]).getDisplayName() + " doesn't have an advertisement in the queue."));
				return;
			}
		}
		if (command[0].equalsIgnoreCase("time")) {
			if (!sender.hasPermission("aio.ad.time")) {
				sender.sendMessage("You don't have permission for that.");
				return;
			}
			if (!playerAd.isEmpty() && playerAd.get(0).getPlayer().equals(sender)) {
				sender.sendMessage(aio.colorize("&bYour advertisement is currently running."));
				return;
			}
			if (getQueueLength() == getQueueLengthUntil(sender)) {
				sender.sendMessage(aio.colorize("&c&lError: &bYou don't have an advertisement in the queue."));
			} else {
				sender.sendMessage(aio.colorize("&bThere are approximately " + (int)getQueueLengthUntil(sender) + " seconds until your advertisement appears."));
			}
			return;
		}
		if (command[0].equalsIgnoreCase("report")) {
			if (!sender.hasPermission("aio.ad.report")) {
				sender.sendMessage("You don't have permission for that.");
				return;
			}
			if (playerAd.isEmpty()) {
				sender.sendMessage("You can't report server ads.");
				return;
			}
			if (reportedAds.contains(playerAd.get(0))) {
				sender.sendMessage("This ad has already been reported.");
				return;
			} else {
				reportedAds.add(playerAd.get(0));
				plugin.sqlconnector.update("INSERT INTO minecraft_ad (minecraft_ad_player, minecraft_ad_color, minecraft_ad_style, minecraft_ad_text, minecraft_ad_time, minecraft_ad_maxtime, minecraft_ad_reported) VALUES (" +
						"'" + playerAd.get(0).getPlayer().getName() + "', " +
						"'" + Convert.ColorToString(playerAd.get(0).getColor()) + "', " +
						"'" + Convert.StyleToString(playerAd.get(0).getStyle()) + "', " +
						"'" + playerAd.get(0).getText() + "', " +
						"'" + playerAd.get(0).getTime() + "', " +
						"'" + playerAd.get(0).getMaxTime() + "', " +
						"'1'" +
						");", new SQLCallback());
				sender.sendMessage("You have successfully reported this advertisement.");
				return;
			}
		}
		if (command[0].equalsIgnoreCase("reports")) {
			if (!sender.hasPermission("aio.ad.reports")) {
				sender.sendMessage("You don't have permission for that.");
				return;
			}
			if (reportedAds.isEmpty()) {
				sender.sendMessage("There are no reported advertisements.");
				return;
			}
			if (command.length > 2) {
				if (command[1].equalsIgnoreCase("clear")) {
					if (reportedAds.size() > Integer.parseInt(command[2])) {
						plugin.sqlconnector.update("DELETE FROM minecraft_ad WHERE minecraft_ad_reported = '1' AND minecraft_ad_player ='" + reportedAds.get(Integer.parseInt(command[2])).getPlayer().getName() + "' AND minecraft_ad_text ='" + reportedAds.get(Integer.parseInt(command[2])).getText() + "';", new SQLCallback());
						reportedAds.remove(Integer.parseInt(command[2]));
						return;
					}
				}
			}
			for (int i = 0; i < reportedAds.size(); i++) {
				sender.sendMessage("Reported ad #" + i + ": <" + reportedAds.get(i).getPlayer().getName() + "> :" + reportedAds.get(i).getText());
			}
			return;
		}
		/*
		 * edit, report, reports?
		 */
		showHelp(sender);
	}
	
	public void removeBar() {
		bossBar.removeAll();
	}
	
	private void openInventory(Player player) {
		Inventory inventory = Bukkit.createInventory(null, 6 * 9);
		updateInventory(player, inventory);
		player.openInventory(inventory);
	}
	
	private void showInventory(Player player) {
		Inventory inventory = player.getOpenInventory().getTopInventory();
		updateInventory(player, inventory);
	}
	
	private void updateInventory(Player player, Inventory inventory) {
		Advertisement ad = createdAds.get(creatingAds.indexOf(player));
		
		ItemStack confirmBanner = BannerCreator.getByName("check-disabled");
		ItemMeta confirmbannerMeta = confirmBanner.getItemMeta();
		confirmbannerMeta.setDisplayName(aio.colorize("&7&lConfirm"));
		
		if (!ad.getText().equals("") && ad.getPlayer() != null) {
			confirmBanner = BannerCreator.getByName("check");
			confirmbannerMeta = confirmBanner.getItemMeta();
			confirmbannerMeta.setDisplayName(aio.colorize("&a&lConfirm"));
		}
		confirmBanner.setItemMeta(confirmbannerMeta);
		inventory.setItem(8, confirmBanner);
		
		ItemStack cancelBanner = BannerCreator.getByName("cross");
		ItemMeta cancelbannerMeta = cancelBanner.getItemMeta();
		cancelbannerMeta.setDisplayName(aio.colorize("&c&lCancel"));
		cancelBanner.setItemMeta(cancelbannerMeta);
		inventory.setItem(0, cancelBanner);
		
		ItemStack sign = new ItemStack(Material.SIGN);
		ItemMeta signMeta = sign.getItemMeta();
		if (ad.getText().equals("")) {
			signMeta.setDisplayName(aio.colorize("&b&lSet Advertisement Text"));
		} else {
			signMeta.setDisplayName(aio.colorize("&b&lChange Advertisement Text"));
		}
		sign.setItemMeta(signMeta);
		inventory.setItem(10, sign);
		
		ItemStack stone = new ItemStack(Material.STONE);
		ItemMeta stoneMeta = stone.getItemMeta();
		if (ad.getStyle() == BarStyle.SOLID) {
			stoneMeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
		}
			stoneMeta.setDisplayName(aio.colorize("&r&lSolid Line"));
		stone.setItemMeta(stoneMeta);
		inventory.setItem(12, stone);
		
		ItemStack stonebrick = new ItemStack(Material.SMOOTH_BRICK);
		ItemMeta stonebrickMeta = stonebrick.getItemMeta();
		if (ad.getStyle() == BarStyle.SEGMENTED_6) {
			stonebrickMeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
		}
		stonebrickMeta.setDisplayName(aio.colorize("&r&l6 Segments"));
		stonebrick.setItemMeta(stonebrickMeta);
		inventory.setItem(13, stonebrick);
		
		ItemStack crackedstonebrick = new ItemStack(Material.SMOOTH_BRICK, 1, (byte)2);
		ItemMeta crackedstonebrickMeta = crackedstonebrick.getItemMeta();
		if (ad.getStyle() == BarStyle.SEGMENTED_10) {
			crackedstonebrickMeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
		}
		crackedstonebrickMeta.setDisplayName(aio.colorize("&r&l10 Segments"));
		crackedstonebrick.setItemMeta(crackedstonebrickMeta);
		inventory.setItem(14, crackedstonebrick);
		
		ItemStack chiseledstonebrick = new ItemStack(Material.SMOOTH_BRICK, 1, (byte)3);
		ItemMeta chiseledstonebrickMeta = chiseledstonebrick.getItemMeta();
		if (ad.getStyle() == BarStyle.SEGMENTED_12) {
			chiseledstonebrickMeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
		}
		chiseledstonebrickMeta.setDisplayName(aio.colorize("&r&l12 Segments"));
		chiseledstonebrick.setItemMeta(chiseledstonebrickMeta);
		inventory.setItem(15, chiseledstonebrick);
		
		ItemStack cobblestone = new ItemStack(Material.COBBLESTONE);
		ItemMeta cobblestoneMeta = cobblestone.getItemMeta();
		if (ad.getStyle() == BarStyle.SEGMENTED_20) {
			cobblestoneMeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
		}
		cobblestoneMeta.setDisplayName(aio.colorize("&r&l20 Segments"));
		cobblestone.setItemMeta(cobblestoneMeta);
		inventory.setItem(16, cobblestone);
		
		ItemStack whiteconcrete = new ItemStack(Material.CONCRETE);
		ItemMeta whiteconcretemeta = whiteconcrete.getItemMeta();
		if (ad.getColor() == BarColor.WHITE) {
			whiteconcretemeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
		}
		whiteconcretemeta.setDisplayName(aio.colorize("&r&lWhite Bar"));
		whiteconcrete.setItemMeta(whiteconcretemeta);
		inventory.setItem(28, whiteconcrete);
		
		ItemStack yellowconcrete = new ItemStack(Material.CONCRETE, 1, (byte)4);
		ItemMeta yellowconcretemeta = yellowconcrete.getItemMeta();
		if (ad.getColor() == BarColor.YELLOW) {
			yellowconcretemeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
		}
		yellowconcretemeta.setDisplayName(aio.colorize("&e&lYellow Bar"));
		yellowconcrete.setItemMeta(yellowconcretemeta);
		inventory.setItem(29, yellowconcrete);
		
		ItemStack pinkconcrete = new ItemStack(Material.CONCRETE, 1, (byte)6);
		ItemMeta pinkconcretemeta = pinkconcrete.getItemMeta();
		if (ad.getColor() == BarColor.PINK) {
			pinkconcretemeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
		}
		pinkconcretemeta.setDisplayName(aio.colorize("&d&lPink Bar"));
		pinkconcrete.setItemMeta(pinkconcretemeta);
		inventory.setItem(30, pinkconcrete);
		
		ItemStack redconcrete = new ItemStack(Material.CONCRETE, 1, (byte)14);
		ItemMeta redconcretemeta = redconcrete.getItemMeta();
		if (ad.getColor() == BarColor.RED) {
			redconcretemeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
		}
		redconcretemeta.setDisplayName(aio.colorize("&c&lRed Bar"));
		redconcrete.setItemMeta(redconcretemeta);
		inventory.setItem(31, redconcrete);
		
		ItemStack greenconcrete = new ItemStack(Material.CONCRETE, 1, (byte)13);
		ItemMeta greenconcretemeta = greenconcrete.getItemMeta();
		if (ad.getColor() == BarColor.GREEN) {
			greenconcretemeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
		}
		greenconcretemeta.setDisplayName(aio.colorize("&2&lGreen Bar"));
		greenconcrete.setItemMeta(greenconcretemeta);
		inventory.setItem(32, greenconcrete);
		
		ItemStack blueconcrete = new ItemStack(Material.CONCRETE, 1, (byte)11);
		ItemMeta blueconcretemeta = blueconcrete.getItemMeta();
		if (ad.getColor() == BarColor.BLUE) {
			blueconcretemeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
		}
		blueconcretemeta.setDisplayName(aio.colorize("&9&lBlue Bar"));
		blueconcrete.setItemMeta(blueconcretemeta);
		inventory.setItem(33, blueconcrete);
		
		ItemStack purpleconcrete = new ItemStack(Material.CONCRETE, 1, (byte)10);
		ItemMeta purpleconcretemeta = purpleconcrete.getItemMeta();
		if (ad.getColor() == BarColor.PURPLE) {
			purpleconcretemeta.addEnchant(Enchantment.SILK_TOUCH, 1, true);
		}
		purpleconcretemeta.setDisplayName(aio.colorize("&5&lPurple Bar"));
		purpleconcrete.setItemMeta(purpleconcretemeta);
		inventory.setItem(34, purpleconcrete);
		
		ItemStack plusTime = BannerCreator.getByName("plus");
		if (ad.getTime() > 110d) {
			plusTime = BannerCreator.getByName("plus-disabled");
		}
		ItemMeta plustimeMeta = plusTime.getItemMeta();
		plustimeMeta.setDisplayName(aio.colorize("&b&lAdd 10 Seconds"));
		plusTime.setItemMeta(plustimeMeta);
		inventory.setItem(52, plusTime);
			
		ItemStack minusTime = BannerCreator.getByName("minus");
		if (ad.getTime() < 20d) {
			minusTime = BannerCreator.getByName("minus-disabled");
		}
		ItemMeta minustimeMeta = minusTime.getItemMeta();
		minustimeMeta.setDisplayName(aio.colorize("&b&lSubtract 10 Seconds"));
		minusTime.setItemMeta(minustimeMeta);
		inventory.setItem(46, minusTime);
		
		ItemStack x0sec = BannerCreator.getCharacter(String.valueOf(((int)ad.getTime() % 60) / 10), "black", "white", true);
		ItemStack xysec = BannerCreator.getCharacter("0", "black", "white", true);
		ItemStack sec = BannerCreator.getCharacter("s", "black", "white", true);
		ItemMeta x0secmeta = x0sec.getItemMeta();
		x0secmeta.setDisplayName(aio.colorize("&f"));
		x0sec.setItemMeta(x0secmeta);
		ItemMeta xysecmeta = xysec.getItemMeta();
		xysecmeta.setDisplayName(aio.colorize("&f"));
		xysec.setItemMeta(xysecmeta);
		ItemMeta secmeta = sec.getItemMeta();
		secmeta.setDisplayName(aio.colorize("&f"));
		sec.setItemMeta(secmeta);
		
		if (ad.getTime() < 60d) {
			inventory.setItem(47, new ItemStack(Material.AIR));
			inventory.setItem(48, x0sec);
			inventory.setItem(49, xysec);
			inventory.setItem(50, sec);
			inventory.setItem(51,  new ItemStack(Material.AIR));
		} else {
			ItemStack xmin = BannerCreator.getCharacter(String.valueOf((int)ad.getTime() / 60), "black", "white", true);
			ItemMeta xminmeta = xmin.getItemMeta();
			xminmeta.setDisplayName(aio.colorize("&f"));
			xmin.setItemMeta(xminmeta);
			ItemStack min = BannerCreator.getCharacter("m", "black", "white", true);
			ItemMeta minMeta = min.getItemMeta();
			minMeta.setDisplayName(aio.colorize("&f"));
			min.setItemMeta(minMeta);
			inventory.setItem(47, xmin);
			inventory.setItem(48, min);
			inventory.setItem(49, x0sec);
			inventory.setItem(50, xysec);
			inventory.setItem(51, sec);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void playerTabEvent(TabCompleteEvent event) {
		if (event.getSender() instanceof Player) {
			if (namingAds.contains((Player)event.getSender())) {
				if (!createdAds.get(creatingAds.indexOf((Player)event.getSender())).getText().equals("")) {
					List<String> completions = new ArrayList<String>();
					completions.add(createdAds.get(creatingAds.indexOf((Player)event.getSender())).getText());
					event.setCompletions(completions);
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void playerChatEvent(AsyncPlayerChatEvent event) {
		if (event.getPlayer() instanceof Player) {
			if (namingAds.contains(event.getPlayer())) {
				event.setCancelled(true);
				new BukkitRunnable() {
					
					@Override
					public void run() {
						namingAds.remove(event.getPlayer());
						createdAds.get(creatingAds.indexOf(event.getPlayer())).setText(event.getMessage().trim());
						openInventory(event.getPlayer());
					}
				}.runTask(plugin);
				
			}
		}
	}
	
	@EventHandler
	public void inventoryClickEvent(InventoryClickEvent event) {
		if (creatingAds.contains(event.getWhoClicked()) && !namingAds.contains(event.getWhoClicked())) {
			if (event.getClickedInventory() == event.getWhoClicked().getInventory()) {
				event.setCancelled(true);
				return;
			}
			if (event.getCurrentItem() == null) {
				event.setCancelled(true);
				return;
			}
			if (event.getCurrentItem().getItemMeta() == null) {
				event.setCancelled(true);
				return;
			}
			if (event.getCurrentItem().getItemMeta().getDisplayName() == null) {
				event.setCancelled(true);
				return;
			}
			switch (ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName())) {
			case "Confirm":
				if (!createdAds.get(creatingAds.indexOf(event.getWhoClicked())).getText().equals("") && createdAds.get(creatingAds.indexOf(event.getWhoClicked())).getPlayer() != null) {
					createdAds.get(creatingAds.indexOf(event.getWhoClicked())).setMaxTime(createdAds.get(creatingAds.indexOf(event.getWhoClicked())).getTime());
					playerAd.add(createdAds.get(creatingAds.indexOf(event.getWhoClicked())));
					createdAds.remove(creatingAds.indexOf(event.getWhoClicked()));
					creatingAds.remove(event.getWhoClicked());
					event.getWhoClicked().closeInventory();
				}
				break;
			case "Cancel":
				createdAds.remove(creatingAds.indexOf(event.getWhoClicked()));
				creatingAds.remove(event.getWhoClicked());
				event.getWhoClicked().closeInventory();
				break;
			case "White Bar":
				createdAds.get(creatingAds.indexOf(event.getWhoClicked())).setColor(BarColor.WHITE);
				showInventory((Player)event.getWhoClicked());
				break;
			case "Yellow Bar":
				createdAds.get(creatingAds.indexOf(event.getWhoClicked())).setColor(BarColor.YELLOW);
				showInventory((Player)event.getWhoClicked());
				break;
			case "Pink Bar":
				createdAds.get(creatingAds.indexOf(event.getWhoClicked())).setColor(BarColor.PINK);
				showInventory((Player)event.getWhoClicked());
				break;
			case "Red Bar":
				createdAds.get(creatingAds.indexOf(event.getWhoClicked())).setColor(BarColor.RED);
				showInventory((Player)event.getWhoClicked());
				break;
			case "Green Bar":
				createdAds.get(creatingAds.indexOf(event.getWhoClicked())).setColor(BarColor.GREEN);
				showInventory((Player)event.getWhoClicked());
				break;
			case "Blue Bar":
				createdAds.get(creatingAds.indexOf(event.getWhoClicked())).setColor(BarColor.BLUE);
				showInventory((Player)event.getWhoClicked());
				break;
			case "Purple Bar":
				createdAds.get(creatingAds.indexOf(event.getWhoClicked())).setColor(BarColor.PURPLE);
				showInventory((Player)event.getWhoClicked());
				break;
			case "Solid Line":
				createdAds.get(creatingAds.indexOf(event.getWhoClicked())).setStyle(BarStyle.SOLID);
				showInventory((Player)event.getWhoClicked());
				break;
			case "6 Segments":
				createdAds.get(creatingAds.indexOf(event.getWhoClicked())).setStyle(BarStyle.SEGMENTED_6);
				showInventory((Player)event.getWhoClicked());
				break;
			case "10 Segments":
				createdAds.get(creatingAds.indexOf(event.getWhoClicked())).setStyle(BarStyle.SEGMENTED_10);
				showInventory((Player)event.getWhoClicked());
				break;
			case "12 Segments":
				createdAds.get(creatingAds.indexOf(event.getWhoClicked())).setStyle(BarStyle.SEGMENTED_12);
				showInventory((Player)event.getWhoClicked());
				break;
			case "20 Segments":
				createdAds.get(creatingAds.indexOf(event.getWhoClicked())).setStyle(BarStyle.SEGMENTED_20);
				showInventory((Player)event.getWhoClicked());
				break;
			case "Add 10 Seconds":
				if (createdAds.get(creatingAds.indexOf(event.getWhoClicked())).getTime() < 120d) {
					createdAds.get(creatingAds.indexOf(event.getWhoClicked())).setTime(createdAds.get(creatingAds.indexOf(event.getWhoClicked())).getTime() + 10.0);
				}
				showInventory((Player)event.getWhoClicked());
				break;
			case "Subtract 10 Seconds":
				if (createdAds.get(creatingAds.indexOf(event.getWhoClicked())).getTime() > 10d) {
					createdAds.get(creatingAds.indexOf(event.getWhoClicked())).setTime(createdAds.get(creatingAds.indexOf(event.getWhoClicked())).getTime() - 10.0);
				}
				showInventory((Player)event.getWhoClicked());
			default:
				break;
			}
			if (event.getCurrentItem().getType() == Material.SIGN) {
				namingAds.add((Player)event.getWhoClicked());
				event.getWhoClicked().closeInventory();
				event.getWhoClicked().sendMessage(aio.colorize("&7-------------------------------------------------"));
				event.getWhoClicked().sendMessage(aio.colorize("&b&lEnter the desired advertisement text:"));
				if (!createdAds.get(creatingAds.indexOf((Player)event.getWhoClicked())).getText().equals("")) {
					event.getWhoClicked().sendMessage(aio.colorize("&b&lTo retrieve the current text, type anything and press &5&l[TAB]"));
				}
				event.getWhoClicked().sendMessage(aio.colorize("&7-------------------------------------------------"));
			}
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void inventoryCloseEvent(InventoryCloseEvent event) {
		if (creatingAds.contains(event.getPlayer()) && !namingAds.contains(event.getPlayer())) {
			openInventory((Player)event.getPlayer());
		}
	}
}

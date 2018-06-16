package AIO;

import java.util.Arrays;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.*;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;

public class aio extends JavaPlugin implements Listener {
	
	private Chat chat;
	private Economy economy;
	
	private PrivateMessage privateMessage;
	Advertisements advertisements;
	
	@Override
	public void onEnable() {
		getLogger().info("Starting All-In-One Plugin");
		
		//retrieve server id
		//connect to mysql
		//enable necessary parts
		advertisements = new Advertisements(this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerLeave(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerMessage(this), this);
		Bukkit.getPluginManager().registerEvents(this, this);
		privateMessage = new PrivateMessage(this);
		Bukkit.getPluginManager().registerEvents(privateMessage, this);
		setupChat();
		setupEconomy();
	}
	
	@Override
	public void onDisable() {
		getLogger().info("Stopping All-In-One Plugin");
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
}

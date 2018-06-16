package AIO;

import java.util.Arrays;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.plugin.Plugin;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

public class aio extends JavaPlugin implements Listener {
	
	private Chat chat;
	
	@Override
	public void onEnable() {
		getLogger().info("Starting All-In-One Plugin");
		Advertisements advertisements = new Advertisements(this);
		
		//retrieve server id
		//connect to mysql
		//enable necessary parts
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerLeave(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerMessage(this), this);
		setupChat();
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
		if (command.getName().equalsIgnoreCase("privatemsg")) {
			if (args.length > 1 && getServer().getPlayer(args[0]) != null) {
				getServer().getPlayer(args[0]).sendMessage(sender.getName() + ": " + String.join(" ", allButFirst(args)));
			} else if (args.length == 1 && getServer().getPlayer(args[0]) == null) {
				sender.sendMessage("Player not found.");
			}
		}
		
		return false;
		
	}
	
	 private boolean setupChat()
	    {
	        RegisteredServiceProvider<Chat> chatProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.chat.Chat.class);
	        if (chatProvider != null) {
	            chat = chatProvider.getProvider();
	        }

	        return (chat != null);
	    }
	
	public static String[] allButFirst(String[] input) {
		return Arrays.copyOfRange(input, 1, input.length);
	}
	
	public static String colorize(String input) {
		return ChatColor.translateAlternateColorCodes('&', input);
	}
}

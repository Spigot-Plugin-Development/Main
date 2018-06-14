package AIO;

import java.util.Arrays;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.player.PlayerQuitEvent;

public class aio extends JavaPlugin implements Listener {
	
	@Override
	public void onEnable() {
		getLogger().info("Starting All-In-One Plugin");
		
		//retrieve server id
		//connect to mysql
		//enable necessary parts
		Bukkit.getPluginManager().registerEvents(new PlayerJoin(this), this);
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
		return false;
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		event.setQuitMessage(event.getPlayer().getDisplayName().toString() + "has joined the game. Welcome!");
	}
	
	private String[] allButFirst(String[] input) {
		return Arrays.copyOfRange(input, 1, input.length);
	}
}

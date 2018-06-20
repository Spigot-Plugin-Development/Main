package AIO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.plugin.Plugin;

public class Economy {
	
	private Plugin plugin;
	private File economyFile;
	
	private final HashMap<UUID, Double> balance = new HashMap<>();
	
	Economy(Plugin plugin) {
		this.plugin = plugin;
			try {
			economyFile = new File(plugin.getDataFolder(), "economy.yml");
			BufferedReader reader = new BufferedReader(new FileReader(economyFile));
			String line;
			while ((line = reader.readLine()) != null) {
				balance.put(UUID.fromString(line.split(":")[0]), Double.parseDouble(line.split(":")[1]));
			}
			reader.close();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public Double loadBalance(String player) {
		return balance.get(plugin.getServer().getPlayer(player).getUniqueId());
	}
	
	public void saveBalance(String player, double amount) {
		
	}
}

package AIO;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;

public class AntiItemlag implements Listener, CommandExecutor {
	aio plugin;
	
	List<Item> items = new ArrayList<>();
	
	AntiItemlag(aio plugin) {
		this.plugin = plugin;
		plugin.getCommand("clearlag").setExecutor(this);
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	public void clearItems(int count) {
		boolean all = count == items.size();
		for (int i = 0; i < count; i++) {
			items.get(0).remove();
			items.remove(0);
		}
		if (all) {
			plugin.getServer().broadcastMessage("Warning: All dropped items have been removed to prevent lag!");
		} else {
			plugin.getServer().broadcastMessage("Warning: " + count + " dropped items have been removed to prevent lag!");
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("clearlag")) {
			if (!sender.hasPermission("aio.clearlag")) {
				sender.sendMessage("You don't have permission to execute this command.");
				return false;
			}
			clearItems(items.size());
		}
		return false;
	}
	
	@EventHandler
	private void itemDrop(ItemSpawnEvent event) {
		items.add(event.getEntity());
		if (items.size() >= 1000) {
			clearItems(50);
		}
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

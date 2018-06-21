package AIO;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.ItemDespawnEvent;
import org.bukkit.event.entity.ItemMergeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.plugin.Plugin;

public class AntiItemlag implements Listener {
	
	List<Item> items = new ArrayList<Item>();
	Plugin plugin;
	
	AntiItemlag(Plugin plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	private void itemDrop(ItemSpawnEvent event) {
		items.add(event.getEntity());
		if (items.size() >= 1000) {
			for (int i= 0; i < 50; i++) {
				items.get(0).remove();
				items.remove(0);
			}
			plugin.getServer().broadcastMessage("Warning: 50 dropped items have been removed to prevent lag!");
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

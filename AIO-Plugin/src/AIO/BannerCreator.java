package AIO;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.plugin.Plugin;

public class BannerCreator implements Listener {
	
	private Plugin plugin;
	
	List<Player> creatingBanners = new ArrayList<Player>();
	List<ItemStack> createdBanners = new ArrayList<ItemStack>();
	
	BannerCreator(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public void createBanner(Player player) {
		creatingBanners.add(player);
		createdBanners.add(new ItemStack(Material.BANNER));
		openBaseColor(player);
	}
	
	private void openBaseColor(Player player) {
		Inventory inventory = Bukkit.createInventory(null, 4 * 9);
		for (int i = 0; i < 16; i++) {
			ItemStack wool = new ItemStack(Material.WOOL, 1, (byte)i);
			inventory.setItem(9 + i + i / 8, wool);
		}
		player.openInventory(inventory);
	}
	
	private void openDyeColor(Player player) {
		Inventory inventory = Bukkit.createInventory(null, 4 * 9);
		for (int i = 0; i < 16; i++) {
			ItemStack wool = new ItemStack(Material.INK_SACK, 1, (byte)(15 -i));
			inventory.setItem(9 + i + i / 8, wool);
		}
		player.openInventory(inventory);
	}
	
	private void openPattern(Player player, DyeColor color) {
		Inventory inventory = Bukkit.createInventory(null, 6 * 9);
		ItemStack banner = createdBanners.get(creatingBanners.indexOf(player));
		for (int i = 0; i < PatternType.values().length; i++) {
			ItemStack bannerCopy = banner.clone();
			BannerMeta meta = (BannerMeta)bannerCopy.getItemMeta();
			meta.removePattern(meta.getPatterns().size() - 1);
			meta.addPattern(new Pattern(color, PatternType.values()[i]));
			bannerCopy.setItemMeta(meta);
			inventory.setItem(i, bannerCopy);
		}
		player.openInventory(inventory);
	}
	
	@EventHandler 
	public void playerCloseInventory(InventoryCloseEvent event) {
		/*if (creatingBanners.contains(event.getPlayer())) {
			creatingBanners.remove(event.getPlayer());
		}*/
	}
	
	@EventHandler
	public void playerClickInventory(InventoryClickEvent event) {
		if (creatingBanners.contains((Player)event.getWhoClicked())) {
			System.out.println(event.getCurrentItem().getType());
			switch (event.getCurrentItem().getType()) {
			case WOOL:
				ItemStack newBanner = createdBanners.get(creatingBanners.indexOf(event.getWhoClicked()));
				BannerMeta newMeta = (BannerMeta)newBanner.getItemMeta();
				newMeta.setBaseColor(DyeColor.getByWoolData(event.getCurrentItem().getData().getData()));
				newBanner.setItemMeta(newMeta);
				createdBanners.set(creatingBanners.indexOf(event.getWhoClicked()), newBanner);
				openDyeColor((Player)event.getWhoClicked());
				break;
			case INK_SACK:
				ItemStack banner = createdBanners.get(creatingBanners.indexOf(event.getWhoClicked()));
				BannerMeta meta = (BannerMeta)banner.getItemMeta();
				meta.addPattern(new Pattern(DyeColor.getByDyeData(event.getCurrentItem().getData().getData()), PatternType.BORDER));
				banner.setItemMeta(meta);
				createdBanners.set(creatingBanners.indexOf(event.getWhoClicked()), banner);
				openPattern((Player)event.getWhoClicked(), DyeColor.getByDyeData(event.getCurrentItem().getData().getData()));
				System.out.println(DyeColor.getByDyeData(event.getCurrentItem().getData().getData()));
				break;
			case BANNER:
				ItemStack progressBanner = createdBanners.get(creatingBanners.indexOf(event.getWhoClicked()));
				BannerMeta progressMeta = (BannerMeta)progressBanner.getItemMeta();
				ItemStack clickedBanner = event.getCurrentItem();
				BannerMeta clickedMeta = (BannerMeta)clickedBanner.getItemMeta();
				progressMeta.setPattern(progressMeta.getPatterns().size() - 1, new Pattern(progressMeta.getPattern(progressMeta.getPatterns().size() - 1).getColor(), clickedMeta.getPattern(clickedMeta.getPatterns().size() - 1).getPattern()));
				progressBanner.setItemMeta(progressMeta);
				createdBanners.set(creatingBanners.indexOf(event.getWhoClicked()), progressBanner);
				event.getWhoClicked().getInventory().addItem(progressBanner);
				openDyeColor((Player)event.getWhoClicked());
				break;
			default:
				break;
			}
			event.setCancelled(true);
		}
	}
	
	public static ItemStack getByName(String string) {
		switch (string) {
		case "plus": 
			ItemStack plus = new ItemStack(Material.BANNER, 1, (byte)15);
			BannerMeta plusMeta = (BannerMeta)plus.getItemMeta();
			List<Pattern> plusPatterns = new ArrayList<Pattern>();
			plusPatterns.add(new Pattern(DyeColor.GREEN, PatternType.STRIPE_CENTER));
			plusPatterns.add(new Pattern(DyeColor.GREEN, PatternType.STRIPE_MIDDLE));
			plusPatterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			plusPatterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
			plusPatterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
			plusMeta.setPatterns(plusPatterns);
			plus.setItemMeta(plusMeta);
			return plus;
		case "minus":
			ItemStack minus = new ItemStack(Material.BANNER, 1, (byte)15);
			BannerMeta minusMeta = (BannerMeta)minus.getItemMeta();
			List<Pattern> minusPatterns = new ArrayList<Pattern>();
			minusPatterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_MIDDLE));
			minusPatterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			minusMeta.setPatterns(minusPatterns);
			minus.setItemMeta(minusMeta);
			return minus;
		default: return null;
		}
	}
}

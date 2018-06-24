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
		Bukkit.getPluginManager().registerEvents(this, plugin);
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
		if (creatingBanners.contains(event.getWhoClicked())) {
			switch (event.getCurrentItem().getType()) {
			case WOOL:
				ItemStack newBanner = new ItemStack(Material.BANNER, 1, Convert.WoolToDye(event.getCurrentItem().getData().getData()));
				createdBanners.set(creatingBanners.indexOf(event.getWhoClicked()), newBanner);
				openDyeColor((Player)event.getWhoClicked());
				break;
			case INK_SACK:
				ItemStack banner = createdBanners.get(creatingBanners.indexOf(event.getWhoClicked()));
				BannerMeta meta = (BannerMeta)banner.getItemMeta();
				meta.addPattern(new Pattern(Convert.ByteToDye(event.getCurrentItem().getData().getData()), PatternType.BORDER));
				banner.setItemMeta(meta);
				createdBanners.set(creatingBanners.indexOf(event.getWhoClicked()), banner);
				openPattern((Player)event.getWhoClicked(), Convert.ByteToDye(event.getCurrentItem().getData().getData()));
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
		ItemStack banner = new ItemStack(Material.BANNER, 1, (byte)15);
		BannerMeta bannerMeta = (BannerMeta)banner.getItemMeta();
		List<Pattern> bannerPatterns = new ArrayList<Pattern>();
		switch (string) {
		case "plus": 
			bannerPatterns.add(new Pattern(DyeColor.GREEN, PatternType.STRIPE_CENTER));
			bannerPatterns.add(new Pattern(DyeColor.GREEN, PatternType.STRIPE_MIDDLE));
			bannerPatterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			bannerPatterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
			bannerPatterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
			break;
		case "plus-disabled": 
			bannerPatterns.add(new Pattern(DyeColor.SILVER, PatternType.STRIPE_CENTER));
			bannerPatterns.add(new Pattern(DyeColor.SILVER, PatternType.STRIPE_MIDDLE));
			bannerPatterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			bannerPatterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
			bannerPatterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
			break;
		case "minus":
			bannerPatterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_MIDDLE));
			bannerPatterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		case "minus-disabled":
			bannerPatterns.add(new Pattern(DyeColor.SILVER, PatternType.STRIPE_MIDDLE));
			bannerPatterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		case "check":
			bannerPatterns.add(new Pattern(DyeColor.LIME, PatternType.STRIPE_LEFT));
			bannerPatterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
			bannerPatterns.add(new Pattern(DyeColor.LIME, PatternType.STRIPE_DOWNLEFT));
			bannerPatterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
			bannerPatterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		case "check-disabled":
			bannerPatterns.add(new Pattern(DyeColor.SILVER, PatternType.STRIPE_LEFT));
			bannerPatterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_TOP));
			bannerPatterns.add(new Pattern(DyeColor.SILVER, PatternType.STRIPE_DOWNLEFT));
			bannerPatterns.add(new Pattern(DyeColor.WHITE, PatternType.STRIPE_BOTTOM));
			bannerPatterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		case "cross":
			bannerPatterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_DOWNRIGHT));
			bannerPatterns.add(new Pattern(DyeColor.RED, PatternType.STRIPE_DOWNLEFT));
			bannerPatterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		case "cross-disabled":
			bannerPatterns.add(new Pattern(DyeColor.SILVER, PatternType.STRIPE_DOWNRIGHT));
			bannerPatterns.add(new Pattern(DyeColor.SILVER, PatternType.STRIPE_DOWNLEFT));
			bannerPatterns.add(new Pattern(DyeColor.WHITE, PatternType.BORDER));
			break;
		default:
			return null;
		}
		bannerMeta.setPatterns(bannerPatterns);
		banner.setItemMeta(bannerMeta);
		return banner;
	}
	
	public static ItemStack getCharacter(String string, String foregroundColor, String backgroundColor, boolean border) {
		DyeColor fgColor = Convert.StringtoDye(foregroundColor);
		DyeColor bgColor = Convert.StringtoDye(backgroundColor);
		if (border) {
			ItemStack banner = new ItemStack(Material.BANNER, 1, Convert.DyeToByte(bgColor));
			BannerMeta bannerMeta = (BannerMeta)banner.getItemMeta();
			List<Pattern> patterns = new ArrayList<Pattern>();
			switch (string.toLowerCase()) {
			case "a":
			case "b":
			case "c":
			case "e":
			case "f":
			case "g":
			case "h":
			case "i":
			case "j":
			case "k":
			case "l":
			case "m":
			case "n":
			case "o":
			case "p":
			case "r":
			case "s":
			case "t":
			case "u":
			case "v":
			case "w":
			case "x":
			case "y":
			case "z":
			case "3":
			case "4":
			case "6":
			case "8":
			case "9":
			case "0":
			case "!":
			case "?":
			case ".":
			case ":":
			case ";":
			case "/":
			case "\\":
				banner = getCharacter(string, foregroundColor, backgroundColor, false);
				bannerMeta = (BannerMeta)banner.getItemMeta();
				patterns = bannerMeta.getPatterns();
				patterns.add(new Pattern(bgColor, PatternType.BORDER));
				break;
			case "d":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				patterns.add(new Pattern(bgColor, PatternType.CURLY_BORDER));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(bgColor, PatternType.BORDER));
				break;
			case "q":
				banner = new ItemStack(Material.BANNER, 1, Convert.DyeToByte(fgColor));
				patterns.add(new Pattern(bgColor, PatternType.RHOMBUS_MIDDLE));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_BOTTOM_RIGHT));
				patterns.add(new Pattern(bgColor, PatternType.BORDER));
				break;
			case "1":
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_TOP_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_CENTER));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				patterns.add(new Pattern(bgColor, PatternType.BORDER));
				break;
			case "2":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(bgColor, PatternType.RHOMBUS_MIDDLE));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNLEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				patterns.add(new Pattern(bgColor, PatternType.BORDER));
				break;
			case "5":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNRIGHT));
				patterns.add(new Pattern(bgColor, PatternType.CURLY_BORDER));
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_BOTTOM_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(bgColor, PatternType.BORDER));
				break;
			case "7":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(bgColor, PatternType.DIAGONAL_RIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNLEFT));
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_BOTTOM_LEFT));
				patterns.add(new Pattern(bgColor, PatternType.BORDER));
				break;
			default:
				break;
			}
			bannerMeta.setPatterns(patterns);
			banner.setItemMeta(bannerMeta);
			return banner;
		} else {
			ItemStack banner = new ItemStack(Material.BANNER, 1,Convert.DyeToByte(bgColor));
			BannerMeta bannerMeta = (BannerMeta)banner.getItemMeta();
			List<Pattern> patterns = new ArrayList<Pattern>();
			switch (string.toLowerCase()) {
			case "a":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_MIDDLE));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				break;
			case "b":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_MIDDLE));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				break;
			case "c":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				break;
			case "d":
				banner = new ItemStack(Material.BANNER, 1, Convert.DyeToByte(fgColor));
				patterns.add(new Pattern(bgColor, PatternType.RHOMBUS_MIDDLE));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				break;
			case "e":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_MIDDLE));
				patterns.add(new Pattern(bgColor, PatternType.STRIPE_RIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				break;
			case "f":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_MIDDLE));
				patterns.add(new Pattern(bgColor, PatternType.STRIPE_RIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				break;
			case "g":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				patterns.add(new Pattern(bgColor, PatternType.HALF_HORIZONTAL));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				break;
			case "h":
				banner = new ItemStack(Material.BANNER, 1, Convert.DyeToByte(fgColor));
				patterns.add(new Pattern(bgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(bgColor, PatternType.STRIPE_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				break;
			case "i":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_CENTER));
				break;
			case "j":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(bgColor, PatternType.HALF_HORIZONTAL));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				break;
			case "k":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_MIDDLE));
				patterns.add(new Pattern(bgColor, PatternType.HALF_VERTICAL_MIRROR));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNLEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNRIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				break;
			case "l":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				break;
			case "m":
				patterns.add(new Pattern(fgColor, PatternType.TRIANGLE_TOP));
				patterns.add(new Pattern(bgColor, PatternType.TRIANGLES_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				break;
			case "n":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(bgColor, PatternType.TRIANGLE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNRIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				break;
			case "o":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				break;
			case "p":
				patterns.add(new Pattern(fgColor, PatternType.HALF_HORIZONTAL_MIRROR));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				patterns.add(new Pattern(bgColor, PatternType.STRIPE_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				break;
			case "q":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNRIGHT));
				patterns.add(new Pattern(bgColor, PatternType.HALF_HORIZONTAL));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				break;
			case "r":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNRIGHT));
				patterns.add(new Pattern(fgColor, PatternType.HALF_HORIZONTAL));
				patterns.add(new Pattern(bgColor, PatternType.DIAGONAL_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_MIDDLE));
				break;
			case "s":
				patterns.add(new Pattern(fgColor, PatternType.TRIANGLE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.TRIANGLE_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_TOP_RIGHT));
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_BOTTOM_LEFT));
				patterns.add(new Pattern(bgColor, PatternType.RHOMBUS_MIDDLE));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNRIGHT));
				break;
			case "t":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_CENTER));
				break;
			case "u":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				break;
			case "v":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(bgColor, PatternType.TRIANGLE_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNLEFT));
				break;
			case "w":
				patterns.add(new Pattern(fgColor, PatternType.TRIANGLE_BOTTOM));
				patterns.add(new Pattern(bgColor, PatternType.TRIANGLES_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				break;
			case "x":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNRIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNLEFT));
				break;
			case "y":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNRIGHT));
				patterns.add(new Pattern(bgColor, PatternType.DIAGONAL_RIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNLEFT));
				break;
			case "z":
				patterns.add(new Pattern(fgColor, PatternType.TRIANGLE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.TRIANGLE_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_TOP_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_BOTTOM_RIGHT));
				patterns.add(new Pattern(bgColor, PatternType.RHOMBUS_MIDDLE));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNLEFT));
				break;
			case "0":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNLEFT));
				break;
			case "1":
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_TOP_LEFT));
				patterns.add(new Pattern(bgColor, PatternType.BORDER));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_CENTER));
				break;
			case "2":
				patterns.add(new Pattern(fgColor, PatternType.TRIANGLE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.TRIANGLE_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_TOP_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_BOTTOM_RIGHT));
				patterns.add(new Pattern(bgColor, PatternType.RHOMBUS_MIDDLE));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNLEFT));
				break;
			case "3":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_MIDDLE));
				patterns.add(new Pattern(bgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				break;
			case "4":
				patterns.add(new Pattern(fgColor, PatternType.HALF_HORIZONTAL_MIRROR));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(bgColor, PatternType.STRIPE_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_MIDDLE));
				break;
			case "5":
				banner = new ItemStack(Material.BANNER, 1, Convert.DyeToByte(fgColor));
				patterns.add(new Pattern(bgColor, PatternType.HALF_VERTICAL_MIRROR));
				patterns.add(new Pattern(bgColor, PatternType.HALF_HORIZONTAL_MIRROR));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				patterns.add(new Pattern(bgColor, PatternType.DIAGONAL_RIGHT_MIRROR));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNRIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				break;
			case "6":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				patterns.add(new Pattern(bgColor, PatternType.HALF_HORIZONTAL));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_MIDDLE));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				break;
			case "7":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(bgColor, PatternType.DIAGONAL_RIGHT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNLEFT));
				break;
			case "8":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_BOTTOM));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_MIDDLE));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				break;
			case "9":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_LEFT));
				patterns.add(new Pattern(bgColor, PatternType.HALF_HORIZONTAL_MIRROR));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_MIDDLE));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				break;
			case "?":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_RIGHT));
				patterns.add(new Pattern(bgColor, PatternType.HALF_HORIZONTAL_MIRROR));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_TOP));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_MIDDLE));
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_BOTTOM_LEFT));
				break;
			case "!":
				patterns.add(new Pattern(fgColor, PatternType.HALF_HORIZONTAL));
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_MIDDLE));
				patterns.add(new Pattern(bgColor, PatternType.HALF_VERTICAL_MIRROR));
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_BOTTOM_LEFT));
				break;
			case ".":
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_BOTTOM_LEFT));
				break;
			case ":":
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_BOTTOM_LEFT));
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_TOP_LEFT));
				break;
			case ";":
				patterns.add(new Pattern(fgColor, PatternType.HALF_VERTICAL));
				patterns.add(new Pattern(bgColor, PatternType.HALF_HORIZONTAL_MIRROR));
				patterns.add(new Pattern(fgColor, PatternType.SQUARE_BOTTOM_LEFT));
				break;
			case "/":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNLEFT));
				break;
			case "\\":
				patterns.add(new Pattern(fgColor, PatternType.STRIPE_DOWNRIGHT));
				break;
			default:
				break;
			}
			bannerMeta.setPatterns(patterns);
			banner.setItemMeta(bannerMeta);
			return banner;
		}
	}
}

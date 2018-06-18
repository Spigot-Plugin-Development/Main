package AIO;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.plugin.Plugin;

public class Enchant implements Listener {
	
	Plugin plugin;
	
	Enchant(Plugin plugin) {
		this.plugin = plugin;
	}
	public static Enchantment Translate(String str) {
		if (Enchantment.getByName(str) != null) {
			return Enchantment.getByName(str);
		}
		String enchantment = str.trim().toLowerCase();
		enchantment = enchantment.replace("_", "");
		enchantment = enchantment.replace("-", "");
		if (Enchantment.getByName(enchantment) != null) {
			return Enchantment.getByName(enchantment);
		}
		switch (enchantment) {
		case "aquaaffinity": return Enchantment.WATER_WORKER;
		case "baneofathropods": return Enchantment.DAMAGE_ARTHROPODS;
		case "blastprotection": return Enchantment.PROTECTION_EXPLOSIONS;
		case "cureofbinding": return Enchantment.BINDING_CURSE;
		case "curseofvanishing": return Enchantment.VANISHING_CURSE;
		case "depthstrider": return Enchantment.DEPTH_STRIDER;
		case "efficiency": return Enchantment.DIG_SPEED;
		case "featherfalling": return Enchantment.PROTECTION_FALL;
		case "fireaspect": return Enchantment.FIRE_ASPECT;
		case "fireprotection": return Enchantment.PROTECTION_FIRE;
		case "flame": return Enchantment.ARROW_FIRE;
		case "fortune": return Enchantment.LUCK;
		case "frostwalker": return Enchantment.FROST_WALKER;
		case "infinity": return Enchantment.ARROW_INFINITE;
		case "knockback": return Enchantment.KNOCKBACK;
		case "looting": return Enchantment.LOOT_BONUS_MOBS;
		case "luckofthesea": return Enchantment.LUCK;
		case "lure": return Enchantment.LURE;
		case "mending": return Enchantment.MENDING;
		case "power": return Enchantment.ARROW_DAMAGE;
		case "projectileprotection": return Enchantment.PROTECTION_PROJECTILE;
		case "protection": return Enchantment.PROTECTION_ENVIRONMENTAL;
		case "punch": return Enchantment.ARROW_KNOCKBACK;
		case "respiration": return Enchantment.OXYGEN;
		case "sharpness": return Enchantment.DAMAGE_ALL;
		case "silktouch": return Enchantment.SILK_TOUCH;
		case "smite": return Enchantment.DAMAGE_UNDEAD;
		case "sweepingedge": return Enchantment.SWEEPING_EDGE;
		case "thorns": return Enchantment.THORNS;
		case "unbreaking": return Enchantment.DURABILITY;
		}
		return null;
	}
	
	@EventHandler
	private void onTabComplete(PlayerChatTabCompleteEvent event) {
		System.out.println(event.getChatMessage());
	}
}

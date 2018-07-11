package AIO;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class Obsidiantolava implements Listener {
    private aio plugin;

    Obsidiantolava(aio plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    private void rightClickBlock(PlayerInteractEvent event) {
        if (!event.getPlayer().hasPermission("aio.obsidiantolava")) {
            return;
        }
        if (event.isCancelled()) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }
        if (event.getItem().getType() != Material.BUCKET) {
            return;
        }
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.OBSIDIAN) {
            return;
        }
        event.getClickedBlock().setType(Material.AIR);
        event.getItem().setAmount(event.getItem().getAmount() - 1);
        event.getPlayer().getInventory().addItem(new ItemStack(Material.LAVA_BUCKET));
        event.setCancelled(true);
    }
}

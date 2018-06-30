package AIO;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Crates implements Listener {

    aio plugin;

    Crates(aio plugin) {
        this.plugin = plugin;
    }

    public void giveKey(Player player, String crate) {
        player.getInventory().addItem(crateKey(crate));
    }

    ItemStack crateKey(String crate) {
        ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta keyMeta = key.getItemMeta();
        keyMeta.setDisplayName(aio.colorize("&5" + crate + " Key"));
        key.setItemMeta(keyMeta);
        net.minecraft.server.v1_12_R1.ItemStack nmsKey = CraftItemStack.asNMSCopy(key);
        NBTTagCompound tag = nmsKey.hasTag() ? nmsKey.getTag() : new NBTTagCompound();
        tag.set("CrateType", new NBTTagString(crate));
        key = CraftItemStack.asBukkitCopy(nmsKey);
        return key;
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void rightClick(PlayerInteractEvent event) {
        if (event.getClickedBlock().getType() != Material.CHEST) {
            return;
        }
        if (event.getItem().getType() != Material.TRIPWIRE_HOOK) {
            return;
        }
        if (event.getItem().getItemMeta().getDisplayName() == null) {
            return;
        }
        net.minecraft.server.v1_12_R1.ItemStack nmsKey = CraftItemStack.asNMSCopy(event.getItem());
        if (nmsKey.hasTag()) {
            switch (nmsKey.getTag().getString("CrateType")) {
                case "Vote":
            }
        }
    }
}

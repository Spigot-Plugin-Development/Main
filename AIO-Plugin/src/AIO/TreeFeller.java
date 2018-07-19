package AIO;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TreeFeller implements Listener, CommandExecutor {
    private aio plugin;

    TreeFeller(aio plugin) {
        this.plugin = plugin;
        plugin.getCommand("axe").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void giveAxe(Player player) {
        ItemStack axe = new ItemStack(Material.DIAMOND_AXE);
        ItemMeta axeMeta = axe.getItemMeta();
        axeMeta.setDisplayName(aio.colorize("&b&lMagic axe"));
        axe.setItemMeta(axeMeta);
        net.minecraft.server.v1_12_R1.ItemStack nmsAxe = CraftItemStack.asNMSCopy(axe);
        NBTTagCompound tag = nmsAxe.hasTag() ? nmsAxe.getTag() : new NBTTagCompound();
        tag.setString("axe", "magic");
        nmsAxe.setTag(tag);
        axe = CraftItemStack.asBukkitCopy(nmsAxe);
        player.getInventory().addItem(axe);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("axe")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can execute this command.");
                return false;
            }
            if (!sender.hasPermission("aio.treefeller.axe")) {
                sender.sendMessage("You don't have permission to execute this command.");
                return false;
            }
            giveAxe((Player)sender);
        }
        return false;
    }

    @EventHandler
    private void treeFeller(BlockBreakEvent event) {
        if (!event.getPlayer().hasPermission("aio.treefeller")) {
            return;
        }
        if (event.getPlayer().getInventory().getItemInMainHand() == null) {
            return;
        }
        if (event.getPlayer().getInventory().getItemInMainHand().getType() != Material.DIAMOND_AXE) {
            return;
        }
        if (event.getBlock().getType() != Material.LOG && event.getBlock().getType() != Material.LOG_2) {
            return;
        }
        if (event.getPlayer().getInventory().getItemInMainHand().getItemMeta().getDisplayName() == null) {
            return;
        }
        net.minecraft.server.v1_12_R1.ItemStack axe = CraftItemStack.asNMSCopy(event.getPlayer().getInventory().getItemInMainHand());
        if (!axe.hasTag()) {
            return;
        }
        NBTTagCompound tag = axe.getTag();
        if (tag.getString("axe") == null) {
            return;
        }
        if (!tag.getString("axe").equals("magic")) {
            return;
        }
        int i = 0;
        while (event.getBlock().getRelative(0, i, 0).getType() == event.getBlock().getType() && i < 32) {
            i++;
        }
        for (int j = 0; j < i; j++) {
            event.getBlock().getRelative(0, j, 0).breakNaturally(event.getPlayer().getInventory().getItem(event.getPlayer().getInventory().getHeldItemSlot()));
        }
    }
}

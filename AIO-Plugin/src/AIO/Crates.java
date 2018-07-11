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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Crates implements Listener, CommandExecutor {
    private aio plugin;

    Crates(aio plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("crates").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
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
        tag.setString("CrateType", crate);
        key = CraftItemStack.asBukkitCopy(nmsKey);
        return key;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("crates")) {
            if (args.length == 0) {
                if (sender.hasPermission("aio.crates")) {
                    sender.sendMessage("/" + label + " chest <Crate>: Create a chest where you're looking");
                    sender.sendMessage("/" + label + " give <Player> <Crate>");
                } else {
                    sender.sendMessage("You don't have permission to execeute that command.");
                }
            } else {
                if (args[0].equalsIgnoreCase("chest")) {

                }
                if (args[0].equalsIgnoreCase("give")) {
                    if (sender.hasPermission("aio.crates.give")) {
                        if (args.length != 3) {
                            sender.sendMessage("/" + label + " give <Player> <Crate>");
                        } else {
                            if (plugin.getServer().getPlayer(args[1]) != null) {
                                giveKey(plugin.getServer().getPlayer(args[1]), args[2]);
                            } else {
                                sender.sendMessage("Player not found.");
                            }
                        }
                    } else {
                        sender.sendMessage("You don't have permission for that.");
                    }
                }
            }
        }
        return false;
    }

    @EventHandler(priority=EventPriority.HIGH)
    public void rightClick(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.CHEST) {
            return;
        }
        if (event.getItem() == null) {
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
        event.setCancelled(true);
    }
}

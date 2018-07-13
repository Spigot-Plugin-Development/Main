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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class SpecialPapers implements Listener, CommandExecutor {
    private aio plugin;

    SpecialPapers(aio plugin) {
        this.plugin = plugin;
        plugin.getCommand("withdrawxp").setExecutor(this);
        plugin.getCommand("banknote").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public void giveBanknote(Player player, Double amount) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.setDisplayName(aio.colorize("&b&l$" + amount + " BANKNOTE"));
        paper.setItemMeta(paperMeta);
        net.minecraft.server.v1_12_R1.ItemStack nmsPaper = CraftItemStack.asNMSCopy(paper);
        NBTTagCompound tag = nmsPaper.hasTag() ? nmsPaper.getTag() : new NBTTagCompound();
        tag.setDouble("MONEY-AMOUNT", amount);
        nmsPaper.setTag(tag);
        paper = CraftItemStack.asBukkitCopy(nmsPaper);
        player.getInventory().addItem(paper);
    }

    public void giveExppaper(Player player, Float amount) {
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta paperMeta = paper.getItemMeta();
        paperMeta.setDisplayName(aio.colorize("&5&l" + amount + " XP"));
        paper.setItemMeta(paperMeta);
        net.minecraft.server.v1_12_R1.ItemStack nmsPaper = CraftItemStack.asNMSCopy(paper);
        NBTTagCompound tag = nmsPaper.hasTag() ? nmsPaper.getTag() : new NBTTagCompound();
        tag.setFloat("XP-AMOUNT", amount);
        nmsPaper.setTag(tag);
        paper = CraftItemStack.asBukkitCopy(nmsPaper);
        player.getInventory().addItem(paper);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("withdrawxp")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can execute this command.");
                return false;
            }
            if (!sender.hasPermission("aio.withdrawxp")) {
                sender.sendMessage("You don't have permission to execute this command.");
                return false;
            }
            if (args.length == 0) {
                sender.sendMessage("Usage: /withdrawxp <amount>");
                return false;
            }
            if (((Player)sender).getExp() < Float.parseFloat(args[0]) && !((Player)sender).hasPermission("aio.withdrawxp.bypass")) {
                sender.sendMessage("You don't have this much experience");
                return false;
            }
            ((Player)sender).setExp(((Player)sender).getExp() - Float.parseFloat(args[0]));
            giveExppaper((Player)sender, Float.parseFloat(args[0]));
        }
        if (command.getName().equalsIgnoreCase("banknote")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can execute this command.");
                return false;
            }
            if (!sender.hasPermission("aio.banknote")) {
                sender.sendMessage("You don't have permission to execute this command.");
                return false;
            }
            if (args.length == 0) {
                sender.sendMessage("Usage: /banknote <amount>");
                return false;
            }
            if (plugin.economyManager.getBalance(((Player)sender).getUniqueId()) < Float.parseFloat(args[0]) && !((Player)sender).hasPermission("aio.banknote.bypass")) {
                sender.sendMessage("Insufficient funds.");
                return false;
            }
            plugin.economyManager.add(((Player)sender).getUniqueId(), -Double.parseDouble(args[0]));
            giveBanknote((Player)sender, Double.parseDouble(args[0]));
        }
        return false;
    }

    @EventHandler
    public void paperUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }
        if (event.getItem().getType() != Material.PAPER) {
            return;
        }
        net.minecraft.server.v1_12_R1.ItemStack nmsPaper = CraftItemStack.asNMSCopy(event.getItem());
        if (!nmsPaper.hasTag()) {
            return;
        }
        if (nmsPaper.getTag().getDouble("MONEY-AMOUNT") > 0) {
            event.getItem().setAmount(event.getItem().getAmount() - 1);
            plugin.economyManager.add(event.getPlayer().getUniqueId(), nmsPaper.getTag().getDouble("MONEY-AMOUNT"));
            return;
        }
        if (nmsPaper.getTag().getFloat("XP-AMOUNT") > 0) {
            event.getPlayer().getInventory().remove(event.getItem());
            event.getPlayer().setExp(event.getPlayer().getExp() + nmsPaper.getTag().getFloat("XP-AMOUNT"));
            return;
        }
    }
}

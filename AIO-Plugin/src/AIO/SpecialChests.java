package AIO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class SpecialChests implements Listener {

    private aio plugin;

    SpecialChests(aio plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    private void trashChest(InventoryCloseEvent event) {
        if (event.getInventory().getLocation() == null) {
            return;
        }
        if (event.getInventory().getLocation().getBlock().getType() != Material.CHEST) {
            return;
        }
        if (event.getInventory().getType() == InventoryType.CHEST) {
            Block chest = event.getInventory().getLocation().getBlock();
            if (chest.getRelative(BlockFace.EAST).getType() == Material.WALL_SIGN) {
                Block sign = (Block)chest.getRelative(BlockFace.EAST);
                if (((Sign)sign.getState()).getLines()[1].equals("[Trash]") && sign.getData() == (byte)5) {
                    event.getInventory().clear();
                }
            }
            if (chest.getRelative(BlockFace.WEST).getType() == Material.WALL_SIGN) {
                Block sign = (Block)chest.getRelative(BlockFace.WEST);
                if (((Sign)sign.getState()).getLines()[1].equals("[Trash]") && sign.getData() == (byte)4) {
                    event.getInventory().clear();
                }
            }
            if (chest.getRelative(BlockFace.NORTH).getType() == Material.WALL_SIGN) {
                Block sign = (Block)chest.getRelative(BlockFace.NORTH);
                if (((Sign)sign.getState()).getLines()[1].equals("[Trash]") && sign.getData() == (byte)2) {
                    event.getInventory().clear();
                }
            }
            if (chest.getRelative(BlockFace.SOUTH).getType() == Material.WALL_SIGN) {
                Block sign = (Block)chest.getRelative(BlockFace.SOUTH);
                if (((Sign)sign.getState()).getLines()[1].equals("[Trash]") && sign.getData() == (byte)3) {
                    event.getInventory().clear();
                }
            }
        }
    }

    @EventHandler
    private void freeChest(InventoryOpenEvent event) {
        if (event.getInventory().getLocation() == null) {
            return;
        }
        if (event.getInventory().getLocation().getBlock().getType() != Material.CHEST) {
            return;
        }
        if (event.getInventory().getType() == InventoryType.CHEST) {
            Block chest = event.getInventory().getLocation().getBlock();
            if (chest.getRelative(BlockFace.EAST).getType() == Material.WALL_SIGN) {
                Block sign = (Block)chest.getRelative(BlockFace.EAST);
                if (((Sign)sign.getState()).getLines()[1].equals("[Free]") && sign.getData() == (byte)5) {
                    Material material = Material.matchMaterial(((Sign)sign.getState()).getLines()[2]);
                    for (int i = 0; i < 27; i++) {
                        event.getInventory().setItem(i, new ItemStack(material, material.getMaxStackSize()));
                    }
                }
            }
            if (chest.getRelative(BlockFace.WEST).getType() == Material.WALL_SIGN) {
                Block sign = (Block)chest.getRelative(BlockFace.WEST);
                if (((Sign)sign.getState()).getLines()[1].equals("[Free]") && sign.getData() == (byte)4) {
                    Material material = Material.matchMaterial(((Sign)sign.getState()).getLines()[2]);
                    for (int i = 0; i < 27; i++) {
                        event.getInventory().setItem(i, new ItemStack(material, material.getMaxStackSize()));
                    }
                }
            }
            if (chest.getRelative(BlockFace.NORTH).getType() == Material.WALL_SIGN) {
                Block sign = (Block)chest.getRelative(BlockFace.NORTH);
                if (((Sign)sign.getState()).getLines()[1].equals("[Free]") && sign.getData() == (byte)2) {
                    Material material = Material.matchMaterial(((Sign)sign.getState()).getLines()[2]);
                    for (int i = 0; i < 27; i++) {
                        event.getInventory().setItem(i, new ItemStack(material, material.getMaxStackSize()));
                    }
                }
            }
            if (chest.getRelative(BlockFace.SOUTH).getType() == Material.WALL_SIGN) {
                Block sign = (Block)chest.getRelative(BlockFace.SOUTH);
                if (((Sign)sign.getState()).getLines()[1].equals("[Free]") && sign.getData() == (byte)3) {
                    Material material = Material.matchMaterial(((Sign)sign.getState()).getLines()[2]);
                    for (int i = 0; i < 27; i++) {
                        event.getInventory().setItem(i, new ItemStack(material, material.getMaxStackSize()));
                    }
                }
            }
        }
    }

}

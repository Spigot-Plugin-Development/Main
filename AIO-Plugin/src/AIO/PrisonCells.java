package AIO;

import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.*;

public class PrisonCells implements Listener, CommandExecutor {
    aio plugin;

    List<PrisonCell> cells = new ArrayList<>();
    List<Player> cellAbandon = new ArrayList<>();

    PrisonCells(aio plugin) {
        this.plugin = plugin;
        plugin.getCommand("cell").setExecutor(this);
        plugin.getCommand("key").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
        plugin.sqlconnector.query("SELECT * FROM minecraft_prisoncell", new SQLCallback() {
            @Override
            public void callback(ResultSet result) {
                try {
                    while (result.next()) {
                        cells.add(new PrisonCell(result.getString("minecraft_prisoncell_owner_name"),
                                UUID.fromString(result.getString("minecraft_prisoncell_owner_uuid")),
                                result.getDate("minecraft_prisoncell_owned_until"),
                                Convert.StringToLocationNoPY(result.getString("minecraft_prisoncell_coordinate1")),
                                Convert.StringToLocationNoPY(result.getString("minecraft_prisoncell_coordinate2")),
                                result.getInt("minecraft_prisoncell_orientation"),
                                result.getInt("minecraft_prisoncell_size"),
                                result.getString("minecraft_prisoncell_name")
                                ));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        new BukkitRunnable() {
            @Override
            public void run() {
                for (PrisonCell cell: cells) {
                    if (new Date().after(cell.getOwnedUntil())) {
                        if (plugin.getServer().getPlayer(cell.getUuid()) != null) {
                            plugin.getServer().getPlayer(cell.getUuid()).sendMessage("");
                        }
                        cell.setOwnedUntil(new Date());
                        cell.setUuid(UUID.randomUUID());
                        cell.setOwner("");
                        updateSign(cell);

                    }
                }
                save();
            }
        }.runTaskTimer(plugin, 60 * 20, 60 * 20);
    }

    public void save() {
        for (PrisonCell cell: cells) {
            plugin.sqlconnector.update("UPDATE minecraft_prisoncell SET " +
                "minecraft_prisoncell_owner_name = '" + cell.getOwner() + "', " +
                "minecraft_prisoncell_owner_uuid = '" + cell.getUuid() + "', " +
                "minecraft_prisoncell_owned_until = '" + Convert.DateToString(cell.getOwnedUntil()) + "', " +
                "minecraft_prisoncell_coordinate1 = '" + Convert.LocationToStringNoPY(cell.getCoordinate1()) + "', " +
                "minecraft_prisoncell_coordinate2 = '" + Convert.LocationToStringNoPY(cell.getCoordinate2()) + "', " +
                "minecraft_prisoncell_orientation = '" + cell.getOrientation() + "', " +
                "minecraft_prisoncell_size = '" + cell.getSize() + "' " +
                "WHERE minecraft_prisoncell_name = '" + cell.getName() + "';", new SQLCallback());
        }
    }

    public void giveKey(Player player, int size) {
        player.getInventory().addItem(cellKey(size));
    }

    ItemStack cellKey(int size) {
        ItemStack key = new ItemStack(Material.TRIPWIRE_HOOK);
        ItemMeta keyMeta = key.getItemMeta();
        keyMeta.setDisplayName(aio.colorize("&5" + size + "x" + size + " Key"));
        key.setItemMeta(keyMeta);
        net.minecraft.server.v1_12_R1.ItemStack nmsKey = CraftItemStack.asNMSCopy(key);
        NBTTagCompound tag = nmsKey.hasTag() ? nmsKey.getTag() : new NBTTagCompound();
        tag.set("CELL_SIZE", new NBTTagInt(size));
        key = CraftItemStack.asBukkitCopy(nmsKey);
        return key;
    }

    public Location getEntranceCorner(PrisonCell cell) {
        switch (cell.getOrientation()) {
            case 0:
                return new Location(cell.getCoordinate1().getWorld(), cell.getCoordinate1().getX(), cell.getCoordinate2().getY(), cell.getCoordinate2().getZ());
            case 1:
                return new Location(cell.getCoordinate1().getWorld(), cell.getCoordinate1().getX(), cell.getCoordinate2().getY(), cell.getCoordinate1().getZ());
            case 2:
                return new Location(cell.getCoordinate1().getWorld(), cell.getCoordinate2().getX(), cell.getCoordinate2().getY(), cell.getCoordinate1().getZ());
            case 3:
                return new Location(cell.getCoordinate1().getWorld(), cell.getCoordinate2().getX(), cell.getCoordinate2().getY(), cell.getCoordinate2().getZ());
            case 4:
                return new Location(cell.getCoordinate1().getWorld(), cell.getCoordinate1().getX(), cell.getCoordinate2().getY(), cell.getCoordinate1().getZ());
            case 5:
                return new Location(cell.getCoordinate1().getWorld(), cell.getCoordinate2().getX(), cell.getCoordinate2().getY(), cell.getCoordinate1().getZ());
            case 6:
                return new Location(cell.getCoordinate1().getWorld(), cell.getCoordinate2().getX(), cell.getCoordinate2().getY(), cell.getCoordinate2().getZ());
            case 7:
                return new Location(cell.getCoordinate1().getWorld(), cell.getCoordinate1().getX(), cell.getCoordinate2().getY(), cell.getCoordinate2().getZ());
        }
        return null;
    }

    public PrisonCell getCellOf(Player player) {
        for (PrisonCell cell: cells) {
            if (cell.getUuid().equals(player.getUniqueId())) {
                return cell;
            }
        }
        return null;
    }

    public Location getSign(PrisonCell cell) {
        switch (cell.getOrientation()) {
            case 0:
                return getEntranceCorner(cell).add(2, 1, 1);
            case 1:
                return getEntranceCorner(cell).add(-1, 1, 2);
            case 2:
                return getEntranceCorner(cell).add(-2, 1, -1);
            case 3:
                return getEntranceCorner(cell).add(1, 1, -2);
            case 4:
                return getEntranceCorner(cell).add(2, 1, -1);
            case 5:
                return getEntranceCorner(cell).add(1, 1, 2);
            case 6:
                return getEntranceCorner(cell).add(-2, 1, 1);
            case 7:
                return getEntranceCorner(cell).add(-1, 1, -2);
        }
        return null;
    }

    public BlockFace SignData(PrisonCell cell) {
        switch (cell.getOrientation()) {
            case 0:
                return BlockFace.EAST;
            case 1:
                return BlockFace.SOUTH;
            case 2:
                return BlockFace.WEST;
            case 3:
                return BlockFace.NORTH;
            case 4:
                return BlockFace.EAST;
            case 5:
                return BlockFace.SOUTH;
            case 6:
                return BlockFace.WEST;
            case 7:
                return BlockFace.NORTH;
        }
        return BlockFace.WEST;
    }

    public Location getDoor(PrisonCell cell) {
        switch (cell.getOrientation()) {
            case 0:
                return getEntranceCorner(cell).add(1, 0, 0);
            case 1:
                return getEntranceCorner(cell).add(0, 0, 1);
            case 2:
                return getEntranceCorner(cell).add(-1, 0, 0);
            case 3:
                return getEntranceCorner(cell).add(1, 0, -1);
            case 4:
                return getEntranceCorner(cell).add(1, 0, 0);
            case 5:
                return getEntranceCorner(cell).add(0, 0, 1);
            case 6:
                return getEntranceCorner(cell).add(-1, 0, 0);
            case 7:
                return getEntranceCorner(cell).add(0, 0, -1);
        }
        return null;
    }

    public void updateSign(PrisonCell cell) {
        Block sign = cell.getCoordinate1().getWorld().getBlockAt(getSign(cell));
        sign.setType(Material.WALL_SIGN);
        Sign signData = (Sign)sign.getState();
        if (cell.getOwner().equals("")) {
            signData.setLine(0, aio.colorize("&5&l[&a&l" + cell.getName() + "&5&l]"));
            signData.setLine(1, aio.colorize("&b&lRentable"));
            signData.setLine(2, aio.colorize("&a&lSize: &b&l" + cell.getSize() + "&a&lx&b&l" + cell.getSize()));
            signData.setLine(3, aio.colorize("&5Right click with key"));
        } else {
            SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
            signData.setLine(0, aio.colorize("&5&l[&a&l" + cell.getName() + "&5&l]"));
            signData.setLine(1, aio.colorize("&aOwned by:"));
            signData.setLine(2, cell.getOwner());
            signData.setLine(3, aio.colorize("Until " + format.format(cell.getOwnedUntil())));
        }
        org.bukkit.material.Sign signMaterial = new org.bukkit.material.Sign(Material.WALL_SIGN);
        signMaterial.setFacingDirection(SignData(cell));
        signData.setData(signMaterial);
        signData.update();
    }

    public Location getTP(PrisonCell cell) {
        Location x = cell.getCoordinate1();
        switch (cell.getOrientation()) {
            case 0:
                x = getEntranceCorner(cell).add(2.5, 0, 0.5); break;
            case 1:
                x = getEntranceCorner(cell).add(0.5, 0, 2.5); break;
            case 2:
                x = getEntranceCorner(cell).add(-2.5, 0, 0.5); break;
            case 3:
                x = getEntranceCorner(cell).add(0.5, 0, -2.5); break;
            case 4:
                x = getEntranceCorner(cell).add(2.5, 0, 0.5); break;
            case 5:
                x = getEntranceCorner(cell).add(0.5, 0, 2.5); break;
            case 6:
                x = getEntranceCorner(cell).add(-2.5, 0, 0.5); break;
            case 7:
                x = getEntranceCorner(cell).add(0.5, 0, -2.5); break;
        }
        x.setYaw((cell.getOrientation() % 4 - 2) * 90);
        x.setPitch(0.0f);
        return x;
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("key")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can execute this command.");
                return false;
            }
            if (!sender.hasPermission("aio.cell.key")) {
                sender.sendMessage("You don't have permission the execute this command.");
                return false;
            }
            if (args.length == 0) {
                sender.sendMessage("Usage: /key <size>");
                return false;
            }
            int size = Integer.parseInt(args[0]);
            if (size <= 10) {
                giveKey((Player)sender, size);
            } else {
                plugin.auctionManager.startAuction(120.0d, 100000.0d, cellKey(size), 1000.0d);
            }
        }
        if (command.getName().equalsIgnoreCase("cell")) {
            if (!sender.hasPermission("aio.cell")) {
                sender.sendMessage("You don't have permission to execute this command.");
                return false;
            }
            if (!(sender instanceof Player)) {
                sender.sendMessage("Only players can execute this commmand.");
                return false;
            }
            if (args.length == 0) {
                for (PrisonCell cell: cells) {
                    if (cell.getUuid().equals(((Player)sender).getUniqueId())) {
                        ((Player)sender).teleport(getTP(cell));
                        return false;
                    }
                }
                sender.sendMessage("You don't have a prison cell.");
                return false;
            }
            if (args[0].equalsIgnoreCase("create")) {
                if (!sender.hasPermission("aio.cell.create")) {
                    sender.sendMessage("You don't have permission for that.");
                    return false;
                }
                if (args.length != 3) {
                    sender.sendMessage("Usage: /cell create <name> <orientation>");
                    return false;
                }
                for (PrisonCell cell: cells) {
                    if (cell.getName().equals(args[1])) {
                        sender.sendMessage("A cell with this name already exists.");
                        return false;
                    }
                }
                Selection cell = plugin.worldEdit.getSelection((Player)sender);
                ProtectedCuboidRegion region = new ProtectedCuboidRegion(args[1], Convert.LocationToBlockVector(cell.getMinimumPoint()), Convert.LocationToBlockVector(cell.getMaximumPoint()));
                plugin.worldGuard.getRegionManager(cell.getWorld()).addRegion(region);
                int size = Math.abs(cell.getMaximumPoint().getBlockX() - cell.getMinimumPoint().getBlockX() + 1);
                int orientation = Integer.parseInt(args[2]);
                PrisonCell prisonCell = new PrisonCell("", UUID.randomUUID(), new Date(), cell.getMaximumPoint(), cell.getMinimumPoint(), orientation, size, args[1]);
                updateSign(prisonCell);
                cells.add(prisonCell);
                plugin.sqlconnector.update("INSERT INTO minecraft_prisoncell (" +
                        "minecraft_prisoncell_name," +
                        "minecraft_prisoncell_owner_name," +
                        "minecraft_prisoncell_owner_uuid," +
                        "minecraft_prisoncell_owned_until," +
                        "minecraft_prisoncell_coordinate1," +
                        "minecraft_prisoncell_coordinate2," +
                        "minecraft_prisoncell_orientation," +
                        "minecraft_prisoncell_size) VALUES (" +
                        "'" + prisonCell.getName() + "', " +
                        "'" + prisonCell.getOwner() + "', " +
                        "'" + prisonCell.getUuid().toString() + "', " +
                        "'" + Convert.DateToString(prisonCell.getOwnedUntil()) + "', " +
                        "'" + Convert.LocationToStringNoPY(prisonCell.getCoordinate1()) + "', " +
                        "'" + Convert.LocationToStringNoPY(prisonCell.getCoordinate2()) + "', " +
                        "'" + prisonCell.getOrientation() + "', " +
                        "'" + prisonCell.getSize() + "');", new SQLCallback());
            }
            if (args[0].equalsIgnoreCase("tp")) {
                if (!sender.hasPermission("aio.cell.tp")) {
                    sender.sendMessage("You don't have permission for that.");
                    return false;
                }
                for (PrisonCell cell: cells) {
                    if (cell.getOwner().equals(args[1])) {
                        ((Player)sender).teleport(getTP(cell));
                        sender.sendMessage("Teleporting to " + args[1] + "'s cell.");
                        return false;
                    }
                }
                sender.sendMessage("The given player doesn't have a prison cell.");
                return false;
            }
            if (args[0].equalsIgnoreCase("remove")) {
                if (!sender.hasPermission("remove")) {
                    sender.sendMessage("You don't have permission for that.");
                    return false;
                }
                if (getCellOf((Player)sender) == null) {
                    sender.sendMessage("You don't have a prison cell.");
                    return false;
                }
                if (args.length != 2) {
                    sender.sendMessage("Usage: /cell remove <player>");
                    return false;
                }
                PrisonCell cell = getCellOf((Player)sender);
                DefaultDomain members = plugin.worldGuard.getRegionManager(cell.getCoordinate1().getWorld()).matchRegion(cell.getName()).getMembers();
                members.removePlayer(args[1]);
                plugin.worldGuard.getRegionManager(cell.getCoordinate1().getWorld()).matchRegion(cell.getName()).setMembers(members);
            }
            if (args[0].equalsIgnoreCase("add")) {
                if (!sender.hasPermission("aio.cell.add")) {
                    sender.sendMessage("You don't have permission for that.");
                    return false;
                }
                if (getCellOf((Player)sender) == null) {
                    sender.sendMessage("You don't have a prison cell.");
                    return false;
                }
                if (args.length != 2) {
                    sender.sendMessage("Usage: /cell add <player>");
                    return false;
                }
                if (plugin.getServer().getPlayer(args[1]) == null) {
                    sender.sendMessage("Player not found.");
                }
                PrisonCell cell = getCellOf((Player)sender);
                DefaultDomain members = plugin.worldGuard.getRegionManager(cell.getCoordinate1().getWorld()).matchRegion(cell.getName()).getMembers();
                members.addPlayer(args[1]);
                plugin.worldGuard.getRegionManager(cell.getCoordinate1().getWorld()).matchRegion(cell.getName()).setMembers(members);
            }
            if (args[0].equalsIgnoreCase("delete")) {
                if (!sender.hasPermission("aio.cell.delete")) {
                    sender.sendMessage("You don't have permission for that.");
                    return false;
                }
                if (args.length != 2) {
                    sender.sendMessage("Usage: /cell delete <name>");
                    return false;
                }
                for (PrisonCell cell: cells) {
                    if (cell.getName().equals(args[1])) {
                        plugin.sqlconnector.update("DELETE FROM minecraft_prisoncell WHERE minecraft_prisoncell_name = '" + cell.getName() + "';", new SQLCallback());
                        cells.remove(cell);
                        return false;
                    }
                }
                sender.sendMessage("No cell was found with this name.");
                return false;
            }
            if (args[0].equalsIgnoreCase("abandon")) {
                if (!sender.hasPermission("aio.cell.abandon")) {
                    sender.sendMessage("You don't have permission for that.");
                    return false;
                }
                if (getCellOf((Player)sender) == null) {
                    sender.sendMessage("You don't have a prison cell");
                    return false;
                }
                if (cellAbandon.contains((Player)sender)) {
                    cellAbandon.remove((Player)sender);
                    PrisonCell cell = getCellOf((Player)sender);
                    ProtectedRegion region = plugin.worldGuard.getRegionManager(cell.getCoordinate1().getWorld()).matchRegion(cell.getName());
                    region.setOwners(new DefaultDomain());
                    cell.setOwner("");
                    cell.setOwnedUntil(new Date());
                    cell.setUuid(UUID.randomUUID());
                    updateSign(cell);
                } else {
                    sender.sendMessage("Type /cell abandon in the next 10 seconds again to confirm!");
                    cellAbandon.add((Player) sender);
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            cellAbandon.remove((Player)sender);
                        }
                    }.runTaskLater(plugin, 200);
                }
            }
        }
        return false;
    }

    @EventHandler
    public void interact(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock().getType() != Material.WALL_SIGN) {
            return;
        }
        if (event.getItem() == null) {
            return;
        }
        if (event.getItem().getType() != Material.TRIPWIRE_HOOK) {
            return;
        }
        net.minecraft.server.v1_12_R1.ItemStack key = CraftItemStack.asNMSCopy(event.getItem());
        if (!key.hasTag()) {
            return;
        }
        if (key.getTag().getInt("CELL_SIZE") <= 0) {
            return;
        }
        if (getCellOf(event.getPlayer()) != null) {
            event.getPlayer().sendMessage("You already have a prison cell.");
            return;
        }
        for (PrisonCell cell: cells) {
            if (getSign(cell).equals(event.getClickedBlock().getLocation())) {
                if (!cell.getOwner().equals("")) {
                    event.getPlayer().sendMessage("This cell is already claimed.");
                    return;
                }
                if (cell.getSize() != key.getTag().getInt("CELL_SIZE")) {
                    event.getPlayer().sendMessage("This key is not valid for this cell.");
                    return;
                }
                cell.setOwner(event.getPlayer().getName());
                cell.setUuid(event.getPlayer().getUniqueId());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                calendar.add(Calendar.DAY_OF_MONTH, 5);
                cell.setOwnedUntil(calendar.getTime());
                updateSign(cell);
                event.getPlayer().getInventory().getItemInMainHand().setAmount(0);
                event.getPlayer().sendMessage("You have successfully claimed cell " + cell.getName());
                DefaultDomain owner = new DefaultDomain();
                owner.addPlayer(event.getPlayer().getUniqueId());
                plugin.worldGuard.getRegionManager(cell.getCoordinate1().getWorld()).matchRegion(cell.getName()).setOwners(owner);
                return;
            }
        }
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        for (PrisonCell cell: cells) {
            if (cell.getUuid().equals(event.getPlayer().getUniqueId())) {
                if (!cell.getOwner().equals(event.getPlayer().getName())) {
                    cell.setOwner(event.getPlayer().getName());
                    return;
                }
            }
        }
    }
}

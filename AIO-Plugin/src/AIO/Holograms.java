package AIO;

import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class Holograms implements CommandExecutor {
    private aio plugin;

    Holograms(aio plugin) {
        this.plugin = plugin;
        plugin.getCommand("hologram").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
        if (command.getName().equalsIgnoreCase("hologram")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("aio.hologram")) {
                    if (args.length == 0) {
                        return false;
                    }
                    if (args[0].equalsIgnoreCase("create")) {
                        Player player = (Player)sender;
                        EntityArmorStand armorStand = new EntityArmorStand(((CraftWorld)player.getWorld()).getHandle(), player.getLocation().getBlockX() + 0.5d, player.getLocation().getBlockY() + 0.5d, player.getLocation().getBlockZ() + 0.5d);
                        armorStand.setCustomName(aio.colorize(String.join(" ", aio.allButFirst(args))));
                        armorStand.setCustomNameVisible(true);
                        armorStand.setNoGravity(true);
                        armorStand.setInvisible(true);
                        PacketPlayOutSpawnEntityLiving packet = new PacketPlayOutSpawnEntityLiving(armorStand);
                        ((CraftPlayer)player).getHandle().playerConnection.sendPacket(packet);
                    }
                } else {
                    sender.sendMessage("You don't have permission to execute that command.");
                }
            } else {
                sender.sendMessage("Only players can execute this command.");
            }
        }
        return false;
    }
}

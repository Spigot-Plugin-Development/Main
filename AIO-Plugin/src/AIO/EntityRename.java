package AIO;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class EntityRename implements Listener, CommandExecutor {
    aio plugin;
    Map<Player, String> renaming = new HashMap<>();

    EntityRename(aio plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginCommand("rename").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
        if (command.getName().equalsIgnoreCase("rename")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("aio.rename")) {
                    if (args.length == 0) {
                        sender.sendMessage("Entity name not specified.");
                    } else {
                        sender.sendMessage("Right click an entity in the next 5 seconds to rename it.");
                        renaming.put((Player)sender, aio.colorize(String.join(" ", args)));
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                renaming.remove((Player)sender);
                            }
                        }.runTaskLater(plugin, 100);
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

    @EventHandler
    public void playerInteract(PlayerInteractEntityEvent event) {
        if (renaming.containsKey(event.getPlayer())) {
            event.getRightClicked().setCustomName(renaming.get(event.getPlayer()));
            event.getRightClicked().setCustomNameVisible(true);
        }
    }
}

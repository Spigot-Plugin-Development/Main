package AIO;

import org.bukkit.*;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class Commands implements CommandExecutor {

    private aio plugin;

    Commands(aio plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginCommand("flyspeed").setExecutor(this);
        Bukkit.getServer().getPluginCommand("giveself").setExecutor(this);
        Bukkit.getServer().getPluginCommand("spawner").setExecutor(this);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        //Kickall - player: kick all except sender; console: kick all
        if(cmd.getName().equalsIgnoreCase("kickall")) {
            if(sender instanceof Player) {
                if(sender.hasPermission("")) {
                    for(Player player : plugin.getServer().getOnlinePlayers()) {
                        if(sender == player) { continue; }
                        player.kickPlayer(String.join(" ", args));
                    }
                    sender.sendMessage("You kicked all players.");
                } else {
                    sender.sendMessage("You don't have permission to execute this command.");
                }
            } else {
                for(Player player: plugin.getServer().getOnlinePlayers()) {
                    player.kickPlayer(String.join(" ", args));
                }
            }
            plugin.getLogger().info(sender.getName() + " kicked all players.");
        }

        //Kick - kick the specified player
        if(cmd.getName().equalsIgnoreCase("kick")) {
            if(args.length > 0 && plugin.getServer().getPlayer(args[0]) != null) {
                plugin.getServer().getPlayer(args[0]).kickPlayer(String.join(" ", aio.allButFirst(args)));
            } else {
                sender.sendMessage("Player not found.");
            }
        }

        //Nick - change the displayed name of the player
        if(cmd.getName().equalsIgnoreCase("nick")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                player.setDisplayName(aio.colorize(args[0]));
            } else {
                sender.sendMessage("Only players can execute this command.");
            }
        }

        //WhoIs - information about specified player
        if(cmd.getName().equalsIgnoreCase("whois")) {
            if(args.length == 0) {
                sender.sendMessage("Player not given.");
            } else if(plugin.getServer().getPlayer(args[0]) == null) {
                sender.sendMessage("Player not found.");
            } else {
                sender.sendMessage("Information about " + plugin.getServer().getPlayer(args[0]).getName());
                sender.sendMessage("IP Address: " + plugin.getServer().getPlayer(args[0]).getAddress().toString());
                sender.sendMessage("OP: " + plugin.getServer().getPlayer(args[0]).isOp());
                sender.sendMessage("Fly: " + plugin.flyManager.canFly(plugin.getServer().getPlayer(args[0])));
                sender.sendMessage("God mode: " + plugin.godManager.isGod(plugin.getServer().getPlayer(args[0])));
            }
        }

        //Ping - ping-pong test command
        if(cmd.getName().equalsIgnoreCase("ping")) {
            sender.sendMessage("Pong!");
        }

        //Worlds - list worlds
        if(cmd.getName().equalsIgnoreCase("worlds")) {
            sender.sendMessage("Worlds: " + plugin.getServer().getWorlds().toString());
        }

        //Heal - fill up health and food bar
        if(cmd.getName().equalsIgnoreCase("heal")) {
            if(args.length == 0 && !(sender instanceof Player)) {
                sender.sendMessage("No playername given.");
            } else {
                if(args.length == 0) {
                    ((Player)sender).setHealth(20.0);
                } else {
                    if(plugin.getServer().getPlayer(args[0]) != null) {
                        plugin.getServer().getPlayer(args[0]).setHealth(20.0);
                    } else {
                        sender.sendMessage("Player not found.");
                    }
                }
            }
        }

        //Feed - fill up food bar
        if(cmd.getName().equalsIgnoreCase("feed")) {
            if(args.length == 0 && !(sender instanceof Player)) {
                sender.sendMessage("No playername given.");
            } else {
                if(args.length == 0) {
                    ((Player)sender).setFoodLevel(20);
                    ((Player)sender).setExhaustion(0.0f);
                } else {
                    if(plugin.getServer().getPlayer(args[0]) != null) {
                        plugin.getServer().getPlayer(args[0]).setFoodLevel(20);
                        plugin.getServer().getPlayer(args[0]).setExhaustion(0.0f);
                    } else {
                        sender.sendMessage("Player not found.");
                    }
                }
            }
        }

        //Fly - turn on/off fly mode
        if(cmd.getName().equalsIgnoreCase("fly")) {
            if(sender instanceof Player) {
                if(args.length == 0) {
                    ((Player)sender).setAllowFlight(!((Player)sender).getAllowFlight());
                } else {
                    plugin.getServer().getPlayer(args[0]).setAllowFlight(!plugin.getServer().getPlayer(args[0]).getAllowFlight());
                }
            } else if(args.length == 0) {
                sender.sendMessage("Player not found.");
            } else {
                plugin.getServer().getPlayer(args[0]).setAllowFlight(!plugin.getServer().getPlayer(args[0]).getAllowFlight());
            }
        }

        //Kittycannon - shoot exploding ocelot baby
        if(cmd.getName().equalsIgnoreCase("kittycannon")) {
            if(sender instanceof Player) {
                Ocelot ocelot = (Ocelot)((Player)sender).getWorld().spawnEntity(((Player)sender).getLocation(), EntityType.OCELOT);
                ocelot.setBaby();
                ocelot.setCatType(Ocelot.Type.BLACK_CAT);
                ocelot.setVelocity(((Player)sender).getEyeLocation().getDirection().multiply(2.0));

                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if(ocelot.isOnGround() || ocelot.isDead() || ocelot.getLocation().getBlockY() < 0) {
                            ocelot.getLocation().getWorld().createExplosion(ocelot.getLocation(), 0.0f);
                            ocelot.remove();
                            cancel();
                        }

                    }
                }.runTaskTimer(plugin, 5, 2);
            }
        }

        //TNTCannon - shoot exploding TNT
        if(cmd.getName().equalsIgnoreCase("tntcannon")) {
            if(sender instanceof Player) {
                TNTPrimed tnt = (TNTPrimed)((Player)sender).getWorld().spawnEntity(((Player)sender).getLocation(), EntityType.PRIMED_TNT);
                tnt.setVelocity(((Player)sender).getEyeLocation().getDirection().multiply(2.0));
                tnt.setFuseTicks(300);
                new BukkitRunnable() {

                    @Override
                    public void run() {
                        if(tnt.isOnGround() || tnt.getLocation().getBlockY() < 0) {
                            tnt.getLocation().getWorld().createExplosion(tnt.getLocation(), 0.0f);
                            tnt.remove();
                            cancel();
                        }

                    }
                }.runTaskTimer(plugin, 5, 2);
            }
        }

        //Giveself - give items to player
        if(cmd.getName().equalsIgnoreCase("giveself")) {
            if(sender instanceof Player) {
                if (args.length == 0) {
                    sender.sendMessage("No material given.");
                } else {
                    ItemStack toGive = new ItemStack(Material.matchMaterial(args[0]));
                    if(args.length > 1) {
                        toGive.setAmount(Integer.parseInt(args[1]));
                    } else {
                        toGive.setAmount(1);
                    }
                    ((Player)sender).getInventory().addItem(toGive);
                }
            }
        }

        //Tpa - send teleport request to player
        if(cmd.getName().equalsIgnoreCase("tpa")) {
            if(sender instanceof Player && args.length > 0 && plugin.getServer().getPlayer(args[0]) != null) {
                plugin.teleporta.request((Player)sender, plugin.getServer().getPlayer(args[0]), false);
            }
        }

        //Tpahere - send teleport-here request to player
        if(cmd.getName().equalsIgnoreCase("tpahere")) {
            if(sender instanceof Player && args.length > 0 && plugin.getServer().getPlayer(args[0]) != null) {
                plugin.teleporta.request((Player)sender, plugin.getServer().getPlayer(args[0]), true);
            }
        }

        //Tpaccept - accept tpa/tpahere request
        if(cmd.getName().equalsIgnoreCase("tpaccept")) {
            if(sender instanceof Player) {
                plugin.teleporta.decide((Player)sender, true);
            }
        }

        //Tpdeny - deny tpa/tpahere request
        if(cmd.getName().equalsIgnoreCase("tpdeny")) {
            if(sender instanceof Player) {
                plugin.teleporta.decide((Player)sender, false);
            }
        }

        if (cmd.getName().equalsIgnoreCase("adminonly")) {
            if (sender instanceof Player) {
                if (((Player)sender).hasPermission("aio.ao.send")) {
                    for (Player player: plugin.getServer().getOnlinePlayers()) {
                        if (player.hasPermission("aio.ao")) {
                            player.sendMessage(aio.colorize("&c&lAdmin Only: " + sender.getName() + ": " + String.join(" ", args)));
                        }
                    }
                } else {
                    sender.sendMessage("You don't have permission to execute that command.");
                }
            } else {
                for (Player player: plugin.getServer().getOnlinePlayers()) {
                    if (player.hasPermission("aio.ao")) {
                        player.sendMessage(aio.colorize("&c&lAdmin Only: " + sender.getName() + ": " + String.join(" ", args)));
                    }
                }
            }
        }

        /* //Spawn - teleport to spawn location
        if(cmd.getName().equalsIgnoreCase("spawn")) {
            if(sender instanceof Player) {
                ((Player)sender).teleport(spawn);
            } else {
                sender.sendMessage("Only players can execute this command");
            }
        } */

        /* //Setspawn - set spawn location
        if(cmd.getName().equalsIgnoreCase("setspawn")) {
            if(sender instanceof Player) {
                plugin.getConfig().set("spawn-world", ((Player)sender).getLocation().getWorld().getName());
                plugin.getConfig().set("spawn-x", ((Player)sender).getLocation().getX());
                plugin.getConfig().set("spawn-y", ((Player)sender).getLocation().getY());
                plugin.getConfig().set("spawn-z", ((Player)sender).getLocation().getZ());
                plugin.getConfig().set("spawn-yaw", ((Player)sender).getLocation().getYaw());
                plugin.getConfig().set("spawn-pitch", ((Player)sender).getLocation().getPitch());
                plugin.saveConfig();
                spawn = ((Player)sender).getLocation();
            } else {
                sender.sendMessage("Only players can execute this command");
            }
        } */

        //More - increase amount of held item
        if(cmd.getName().equalsIgnoreCase("more")) {
            if(sender instanceof Player) {
                ((Player)sender).getInventory().getItemInMainHand().setAmount(((Player)sender).getInventory().getItemInMainHand().getMaxStackSize());
            } else {
                sender.sendMessage("Only players can execute this command.");
            }
        }

        //Repair - repair held item
        if(cmd.getName().equalsIgnoreCase("repair")) {
            if(sender instanceof Player) {
                ((Player)sender).getInventory().getItemInMainHand().setDurability((short)0);
            }
        }

        //Skull - spawn your skull
        if(cmd.getName().equalsIgnoreCase("skull")) {
            if(sender instanceof Player) {
                if(args.length == 0) {
                    ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
                    SkullMeta meta = (SkullMeta)head.getItemMeta();
                    meta.setOwningPlayer((Player)sender);
                    head.setItemMeta(meta);
                    ((Player)sender).getInventory().addItem(head);
                } else if(plugin.getServer().getPlayer(args[0]) != null) {
                    ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
                    SkullMeta meta = (SkullMeta)head.getItemMeta();
                    meta.setOwningPlayer(plugin.getServer().getOfflinePlayer(args[0]));
                    head.setItemMeta(meta);
                    ((Player)sender).getInventory().addItem(head);
                }
            }
        }

        //Flyspeed - change speed of flight
        if(cmd.getName().equalsIgnoreCase("flyspeed")) {
            if(sender instanceof Player && args.length == 1) {
                ((Player)sender).setFlySpeed(Float.parseFloat(args[0]));
            } else {
                plugin.getServer().getPlayer(args[1]).setFlySpeed(Float.parseFloat(args[0]) / 10f);
            }
        }

        //Enchanttable - open enchanttable
        if(cmd.getName().equalsIgnoreCase("enchanttable")) {
            if(sender instanceof Player) {
                ((Player)sender).openEnchanting(new Location(plugin.getServer().getWorld("world"), -278.0, 69.0, 296.0), true);
            }
        }

        //Enderchest - open enderchest
        if(cmd.getName().equalsIgnoreCase("enderchest")) {
            if(sender instanceof Player) {
                ((Player)sender).openInventory(((Player)sender).getEnderChest());
            }
        }

        //Craftbench - open craftbench
        if(cmd.getName().equalsIgnoreCase("craftbench")) {
            if(sender instanceof Player) {
                ((Player)sender).openWorkbench(null, true);
            }
        }

        //Nearby - list nearby entities
        if(cmd.getName().equalsIgnoreCase("nearby")) {
            if(sender instanceof Player) {
                for(Entity entity: ((Player)sender).getNearbyEntities(100, 100, 100)) {
                    if(entity instanceof Player) {
                        if(((Player)entity).getLocation().distance(((Player)sender).getLocation()) <= 100.0) {
                            ((Player)sender).sendMessage(((Player)entity).getName() + ": " + ((Player)entity).getLocation().distance(((Player)sender).getLocation()) + "m");
                        }
                    }
                }
            }
        }

        //News - get server news.
        if(cmd.getName().equalsIgnoreCase("news")) {
            sender.sendMessage("Server news:");
            sender.sendMessage("Server now working");
        }

        //Motd - print server motd
        if(cmd.getName().equalsIgnoreCase("motd")) {
            sender.sendMessage("Best server ever!");
        }

        //Rules - print server rules
        if(cmd.getName().equalsIgnoreCase("rules")) {
            sender.sendMessage("Eat, sleep, code, repeat");
        }

        //Unsafeenchant - put unsafe enchants on items
        if(cmd.getName().equalsIgnoreCase("unsafeenchant")) {
            if(sender instanceof Player) {
                ((Player)sender).getInventory().getItemInMainHand().addUnsafeEnchantment(Enchant.Translate(args[0]), Integer.parseInt(args[1]));
            }
        }

        //Lightning - ELECTRIC CREEPERS :D
        if(cmd.getName().equalsIgnoreCase("lightning")) {
            if(sender instanceof Player && args.length == 0) {
                ((Player)sender).getWorld().strikeLightning(((Player)sender).getTargetBlock(null, 600).getLocation());
            } else if(args.length == 0) {
                sender.sendMessage("Player not given.");
            } else if(plugin.getServer().getPlayer(args[0]) == null) {
                sender.sendMessage("Player not found.");
            } else {
                plugin.getServer().getPlayer(args[0]).getWorld().strikeLightning(plugin.getServer().getPlayer(args[0]).getLocation());
            }
        }

        //Spawner - spawn any mob
        if(cmd.getName().equalsIgnoreCase("spawner")) {
            if(sender instanceof Player) {
                if(args.length == 0) {
                    sender.sendMessage("No mob type given");
                } else if(EntityType.valueOf(args[0].toUpperCase()) == null) {
                    sender.sendMessage("Invalid mob type given.");
                } else {
                    if(!((Player)sender).getTargetBlock(null, 10).getType().equals(Material.MOB_SPAWNER)) {
                        sender.sendMessage("You must be looking at a mob spawner to change its type.");
                    } else {
                        CreatureSpawner spawner = (CreatureSpawner)((Player)sender).getTargetBlock(null, 10).getState();
                        spawner.setSpawnedType(EntityType.valueOf(args[0].toUpperCase()));
                        spawner.update();
                    }
                }
            } else {
                sender.sendMessage("Only players can execute this command.");
            }
        }

        return false;
    }

}

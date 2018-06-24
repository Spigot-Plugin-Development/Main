package AIO;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Commands implements CommandExecutor {

    private Plugin plugin;

    PrivateMessage privateMessage;
    TeleportA teleporta;
    Warp warp;
    BannerCreator bannerCreator;
    Advertisements advertisements;

    Commands(Plugin plugin) {
        this.plugin = plugin;
        privateMessage = new PrivateMessage(plugin);
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

        /* //WhoIs - information about specified player
        if(cmd.getName().equalsIgnoreCase("whois")) {
            if(args.length == 0) {
                sender.sendMessage("Player not given.");
            } else if(plugin.getServer().getPlayer(args[0]) == null) {
                sender.sendMessage("Player not found.");
            } else {
                sender.sendMessage("Information about " + plugin.getServer().getPlayer(args[0]).getName());
                sender.sendMessage("IP Address: " + plugin.getServer().getPlayer(args[0]).getAddress().toString());
                sender.sendMessage("OP: " + plugin.getServer().getPlayer(args[0]).isOp());
                sender.sendMessage("Fly mode: " + plugin.getServer().getPlayer(args[0]).getAllowFlight());
                sender.sendMessage("God mode: " + godPlayers.contains(plugin.getServer().getPlayer(args[0])));
            }
        } */

        //Ping - ping-pong test command
        if(cmd.getName().equalsIgnoreCase("ping")) {
            sender.sendMessage("Pong!");
        }

        //Msg - send private message to another player
        if(cmd.getName().equalsIgnoreCase("msg")) {
            if(sender instanceof Player) {
                privateMessage.message((Player)sender, args);
            } else {
                sender.sendMessage("Only players can execute this command.");
            }
        }

        //Reply - reply to previous message
        if(cmd.getName().equalsIgnoreCase("reply")) {
            if(sender instanceof Player) {
                privateMessage.reply((Player)sender, args);
            } else {
                sender.sendMessage("Only players can execute this command.");
            }
        }

        //Sun - set weather to sunny
        if(cmd.getName().equalsIgnoreCase("sun")) {
            if(sender instanceof Player && args.length == 0) {
                ((Player)sender).getLocation().getWorld().setThundering(false);
                ((Player)sender).getLocation().getWorld().setStorm(false);
            } else if(args.length == 0) {
                sender.sendMessage("World not given");
            } else if(plugin.getServer().getWorld(args[0]) != null) {
                plugin.getServer().getWorld(args[0]).setThundering(false);
                plugin.getServer().getWorld(args[0]).setStorm(false);
            }
        }

        //Rain - set weather to rainy
        if(cmd.getName().equalsIgnoreCase("rain")) {
            if(sender instanceof Player && args.length == 0) {
                ((Player)sender).getLocation().getWorld().setThundering(true);
                ((Player)sender).getLocation().getWorld().setStorm(false);
            } else if(args.length == 0) {
                sender.sendMessage("World not given");
            } else if(plugin.getServer().getWorld(args[0]) != null) {
                plugin.getServer().getWorld(args[0]).setThundering(true);
                plugin.getServer().getWorld(args[0]).setStorm(false);
            }
        }

        //Storm - set weather to stormy
        if(cmd.getName().equalsIgnoreCase("storm")) {
            if(sender instanceof Player) {
                ((Player)sender).getLocation().getWorld().setThundering(true);
                ((Player)sender).getLocation().getWorld().setStorm(true);
            } else if(args.length == 0) {
                sender.sendMessage("World not given");
            } else if(plugin.getServer().getWorld(args[0]) != null) {
                plugin.getServer().getWorld(args[0]).setThundering(true);
                plugin.getServer().getWorld(args[0]).setStorm(true);
            }
        }

        //Worlds - list worlds
        if(cmd.getName().equalsIgnoreCase("worlds")) {
            sender.sendMessage("Worlds: " + plugin.getServer().getWorlds().toString());
        }

        //Dawn - set time to dawn, 0
        if(cmd.getName().equalsIgnoreCase("dawn")) {
            if(sender instanceof Player) {
                ((Player)sender).getLocation().getWorld().setTime(0);
            } else if(args.length == 0) {
                sender.sendMessage("World not given");
            } else if(plugin.getServer().getWorld(args[0]) != null) {
                plugin.getServer().getWorld(args[0]).setTime(0);
            }
        }

        //Morning - set time to morning, 450
        if(cmd.getName().equalsIgnoreCase("morning")) {
            if(sender instanceof Player) {
                ((Player)sender).getLocation().getWorld().setTime(450);
            } else if(args.length == 0) {
                sender.sendMessage("World not given");
            } else if(plugin.getServer().getWorld(args[0]) != null) {
                plugin.getServer().getWorld(args[0]).setTime(450);
            }
        }

        //Day - set time to day, 1000
        if(cmd.getName().equalsIgnoreCase("day")) {
            if(sender instanceof Player) {
                ((Player)sender).getLocation().getWorld().setTime(1000);
            } else if(args.length == 0) {
                sender.sendMessage("World not given");
            } else if(plugin.getServer().getWorld(args[0]) != null) {
                plugin.getServer().getWorld(args[0]).setTime(1000);
            }
        }

        //Noon - set time to noon, 6000
        if(cmd.getName().equalsIgnoreCase("noon")) {
            if(sender instanceof Player) {
                ((Player)sender).getLocation().getWorld().setTime(6000);
            } else if(args.length == 0) {
                sender.sendMessage("World not given");
            } else if(plugin.getServer().getWorld(args[0]) != null) {
                plugin.getServer().getWorld(args[0]).setTime(6000);
            }
        }

        //Afternoon - set time to afternoon, 10000
        if(cmd.getName().equalsIgnoreCase("afternoon")) {
            if(sender instanceof Player) {
                ((Player)sender).getLocation().getWorld().setTime(10000);
            } else if(args.length == 0) {
                sender.sendMessage("World not given");
            } else if(plugin.getServer().getWorld(args[0]) != null) {
                plugin.getServer().getWorld(args[0]).setTime(10000);
            }
        }

        //Dusk - set time to dusk, 12500
        if(cmd.getName().equalsIgnoreCase("dusk")) {
            if(sender instanceof Player) {
                ((Player)sender).getLocation().getWorld().setTime(12500);
            } else if(args.length == 0) {
                sender.sendMessage("World not given");
            } else if(plugin.getServer().getWorld(args[0]) != null) {
                plugin.getServer().getWorld(args[0]).setTime(12500);
            }
        }

        //Night - set time to night, 13000
        if(cmd.getName().equalsIgnoreCase("night")) {
            if(sender instanceof Player) {
                ((Player)sender).getLocation().getWorld().setTime(13000);
            } else if(args.length == 0) {
                sender.sendMessage("World not given");
            } else if(plugin.getServer().getWorld(args[0]) != null) {
                plugin.getServer().getWorld(args[0]).setTime(13000);
            }
        }

        //Midnight - set time to midnight, 18000
        if(cmd.getName().equalsIgnoreCase("midnight")) {
            if(sender instanceof Player) {
                ((Player)sender).getLocation().getWorld().setTime(18000);
            } else if(args.length == 0) {
                sender.sendMessage("World not given");
            } else if(plugin.getServer().getWorld(args[0]) != null) {
                plugin.getServer().getWorld(args[0]).setTime(18000);
            }
        }

        //Survival - change game mode to survival
        if(cmd.getName().equalsIgnoreCase("survival")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                player.setGameMode(GameMode.SURVIVAL);
            } else {
                sender.sendMessage("Only players can execute this command.");
            }
        }

        //Creative - change game mode to creative
        if(cmd.getName().equalsIgnoreCase("creative")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                player.setGameMode(GameMode.CREATIVE);
            } else {
                sender.sendMessage("Only players can execute this command.");
            }
        }

        //Adventure - change game mode to adventure
        if(cmd.getName().equalsIgnoreCase("adventure")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                player.setGameMode(GameMode.ADVENTURE);
            } else {
                sender.sendMessage("Only players can execute this command.");
            }
        }

        //Spectator - change game mode to spectator
        if(cmd.getName().equalsIgnoreCase("spectator")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                player.setGameMode(GameMode.SPECTATOR);
            } else {
                sender.sendMessage("Only players can execute this command.");
            }
        }

        //Gm - change game mode to specified game mode
        if(cmd.getName().equalsIgnoreCase("gm")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                String[] gms = {"survival", "s", "0"};
                String[] gmc = {"creative", "c", "1"};
                String[] gma = {"adventure", "a", "2"};
                String[] gmsp = {"spectator", "sp", "3"};
                if(args.length > 0 && Arrays.asList(gms).contains(args[0])) {
                    player.setGameMode(GameMode.SURVIVAL);
                }
                if(args.length > 0 && Arrays.asList(gmc).contains(args[0])) {
                    player.setGameMode(GameMode.CREATIVE);
                }
                if(args.length > 0 && Arrays.asList(gma).contains(args[0])) {
                    player.setGameMode(GameMode.ADVENTURE);
                }
                if(args.length > 0 && Arrays.asList(gmsp).contains(args[0])) {
                    player.setGameMode(GameMode.SPECTATOR);
                }
            } else {
                sender.sendMessage("Only players can execute this command.");
            }
        }

        //Ad - create new ad
        if(cmd.getName().equalsIgnoreCase("ad")) {
            if(sender instanceof Player) {
                advertisements.adCommand((Player)sender, args);
            }
        }

        /*//Balance - get balance
        if(cmd.getName().equalsIgnoreCase("balance")) {
            if(sender instanceof Player) {
                Player player = (Player)sender;
                economy.depositPlayer(player, 10);
                player.sendMessage("Current balance: " + economy.getBalance(player));
            }
        } */

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

        /* //God - turn on/off god mode
        if(cmd.getName().equalsIgnoreCase("god")) {
            if(sender instanceof Player) {
                if(args.length == 0) {
                    if(godPlayers.contains((Player)sender)) {
                        godPlayers.remove((Player)sender);
                    } else {
                        godPlayers.add((Player)sender);
                    }
                } else {
                    if(godPlayers.contains(plugin.getServer().getPlayer(args[0]))) {
                        godPlayers.remove(plugin.getServer().getPlayer(args[0]));
                    } else {
                        godPlayers.add(plugin.getServer().getPlayer(args[0]));
                    }
                }
            } else if(args.length == 0) {
                sender.sendMessage("Player not found.");
            } else {
                if(godPlayers.contains(plugin.getServer().getPlayer(args[0]))) {
                    godPlayers.remove(plugin.getServer().getPlayer(args[0]));
                } else {
                    godPlayers.add(plugin.getServer().getPlayer(args[0]));
                }
            }
        } */

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
                ItemStack toGive = new ItemStack(Material.matchMaterial(args[0]));
                if(args.length > 1) {
                    toGive.setAmount(Integer.parseInt(args[1]));
                } else {
                    toGive.setAmount(1);
                }
                ((Player)sender).getInventory().addItem(toGive);
            }
        }

        //Tpa - send teleport request to player
        if(cmd.getName().equalsIgnoreCase("tpa")) {
            if(sender instanceof Player && args.length > 0 && plugin.getServer().getPlayer(args[0]) != null) {
                teleporta.request((Player)sender, plugin.getServer().getPlayer(args[0]), false);
            }
        }

        //Tpahere - send teleport-here request to player
        if(cmd.getName().equalsIgnoreCase("tpahere")) {
            if(sender instanceof Player && args.length > 0 && plugin.getServer().getPlayer(args[0]) != null) {
                teleporta.request((Player)sender, plugin.getServer().getPlayer(args[0]), true);
            }
        }

        //Tpaccept - accept tpa/tpahere request
        if(cmd.getName().equalsIgnoreCase("tpaccept")) {
            if(sender instanceof Player) {
                teleporta.decide((Player)sender, true);
            }
        }

        //Tpdeny - deny tpa/tpahere request
        if(cmd.getName().equalsIgnoreCase("tpdeny")) {
            if(sender instanceof Player) {
                teleporta.decide((Player)sender, false);
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

        //Banner - create banner
        if(cmd.getName().equalsIgnoreCase("banner")) {
            if(sender instanceof Player) {
                if(args.length == 0) {
                    bannerCreator.createBanner((Player)sender);
                } else if(args[0].equalsIgnoreCase("get")) {
                    if(BannerCreator.getByName(String.join(" ", aio.allButFirst(args))) != null) {
                        ((Player)sender).getInventory().addItem(BannerCreator.getByName(String.join(" ", aio.allButFirst(args))));
                    }
                } else if(args[0].equalsIgnoreCase("letter")) {
                    String[] borderWanted = {"yes", "y", "true", "border", "bordered"};
                    boolean bordered = false;
                    if(args.length > 4) {
                        for(String border: borderWanted) {
                            if(args[4].equals(border)) {
                                bordered = true;
                            }
                        }
                    }
                    if(BannerCreator.getCharacter(args[1], args[2], args[3], bordered) != null) {
                        ((Player)sender).getInventory().addItem(BannerCreator.getCharacter(args[1], args[2], args[3], bordered));
                    }
                }
            }
        }

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

        /* //Freeze - freeze a player
        if(cmd.getName().equalsIgnoreCase("freeze")) {
            if(args.length == 0) {
                sender.sendMessage("Player not given.");
            } else {
                if(plugin.getServer().getPlayer(args[0]) == null) {
                    sender.sendMessage("Player not found.");
                } else {
                    if(frozenPlayers.contains(plugin.getServer().getPlayer(args[0]))) {
                        frozenPlayers.remove(plugin.getServer().getPlayer(args[0]));
                    } else {
                        frozenPlayers.add(plugin.getServer().getPlayer(args[0]));
                    }
                }
            }
        } */

        //Unsafeenchant - put unsafe enchants on items
        if(cmd.getName().equalsIgnoreCase("unsafeenchant")) {
            if(sender instanceof Player) {
                ((Player)sender).getInventory().getItemInMainHand().addUnsafeEnchantment(Enchant.Translate(args[0]), Integer.parseInt(args[1]));
            }
        }

        //Delwarp - delete existing warp
        if(cmd.getName().equalsIgnoreCase("delwarp")) {
            if(args.length < 1) {
                sender.sendMessage(ChatColor.RED + "/delwarp <warp name>");
                return true;
            }
            if(warp.getWarpLocation(args[0]) == null) {
                sender.sendMessage(aio.colorize(warp.warp_not_found));
                return true;
            }
            warp.delWarp((Player)sender, args[0]);
            return false;
        }

        //Setwarp - set new warp
        if(cmd.getName().equalsIgnoreCase("setwarp")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(aio.colorize(warp.player_only));
                return true;
            }
            if(args.length < 1) {
                sender.sendMessage(ChatColor.RED + "/setwarp <warp name>");
                return true;
            }
            if(warp.getWarpLocation(args[0]) != null) {
                sender.sendMessage(aio.colorize(warp.warp_already.replace("{warp}", args[0])));
                return true;
            }
            warp.setWarp((Player)sender, args[0]);
            return false;
        }

        //Warp - teleport to an existing warp
        if(cmd.getName().equalsIgnoreCase("warp")) {
            if(!(sender instanceof Player)){
                sender.sendMessage(aio.colorize(warp.player_only));
                return true;
            }
            Player player = (Player)sender;
            if(args.length < 1) {
                player.sendMessage(ChatColor.RED + "/warp <warp name>");
                return true;
            }
            if(warp.getWarpLocation(args[0]) == null) {
                player.sendMessage(aio.colorize(warp.warp_not_found).replace("{warp}", args[0]));
                return true;
            }
            if(warp.getWarpLocation(args[0]).getWorld() == null) {
                player.sendMessage(aio.colorize(warp.no_world));
                return true;
            }
            player.sendMessage(aio.colorize(warp.warping).replace("{warp}", args[0]));
            player.teleport(warp.getWarpLocation(args[0]));
            return false;
        }

        //Warpreload - reloading warp file
        if(cmd.getName().equalsIgnoreCase("warpreload")) {
            plugin.reloadConfig();
            warp.reloadWarps();
            plugin.saveConfig();
            warp.saveWarps();
            warp.load();
            sender.sendMessage(aio.colorize(warp.reload));
            return false;
        }

        //Warplist - list all existing warps
        if(cmd.getName().equalsIgnoreCase("warplist")) {
            if(!warp.getWarps().isConfigurationSection("warps")) {
                sender.sendMessage(aio.colorize(warp.no_warp));
                return true;
            }
            Set<String> warps = warp.getWarps().getConfigurationSection("warps").getKeys(false);
            Iterator<String> itr = warps.iterator();
            String list = "";
            while(itr.hasNext()) {
                list = list + itr.next() + ", ";
            }
            sender.sendMessage(aio.colorize(warp.warp_list.replace("{list}", list.substring(0, list.length() - 2)).replace("{amount}", String.valueOf(warps.size()))));
            return false;
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

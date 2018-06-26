package AIO;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class EconomyManager implements CommandExecutor {
	
	private aio plugin;
	
	private final HashMap<UUID, Double> balance = new HashMap<>();
	
	EconomyManager(aio plugin) {
		this.plugin = plugin;
		Bukkit.getPluginCommand("pay").setExecutor(this);
		Bukkit.getPluginCommand("balance").setExecutor(this);
		Bukkit.getPluginCommand("economy").setExecutor(this);
	}

	public double getBalance(UUID uuid) {
		return balance.get(uuid);
	}

	public boolean has(UUID uuid, double amount) {
		return balance.get(uuid) >= amount;
	}

	public boolean pay(UUID donor, UUID recipient, double amount) {
		if (has(donor, amount)) {
			add(donor, -amount);
			add(recipient, amount);
			return true;
		} else {
			return false;
		}
	}

	public void add(UUID uuid, double amount) {
		if (balance.containsKey(uuid)) {
			balance.replace(uuid, balance.get(uuid) + amount);
		} else {
			balance.put(uuid, balance.get(uuid) + amount);
		}
	}

	public void set(UUID uuid, double amount) {
		if (balance.containsKey(uuid)) {
			balance.replace(uuid, amount);
		} else {
			balance.put(uuid, amount);
		}
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equalsIgnoreCase("pay")) {
			if (sender instanceof Player) {
				if (((Player)sender).hasPermission("aio.pay")) {
					if (args.length < 2) {
						sender.sendMessage("Not enough arguments");
						return false;
					}
					if (plugin.getServer().getPlayer(args[0]) != null) {
						if (pay(((Player)sender).getUniqueId(), plugin.getServer().getPlayer(args[0]).getUniqueId(), Double.parseDouble(args[1]))) {
							sender.sendMessage("Successfully transfered $" + args[1] + " to " + plugin.getServer().getPlayer(args[0]).getName());
							plugin.getServer().getPlayer(args[0]).sendMessage(((Player)sender).getName() + " transferred you $" + args[1]);
							return false;
						} else {
							sender.sendMessage("Insufficient funds.");
							return false;
						}
					} else {
						sender.sendMessage("Player not found.");
						return false;
					}
				} else {
					sender.sendMessage("You don't have permission to execute that command.");
					return false;
				}
			} else {
				sender.sendMessage("Only players can execute this command.");
				return false;
			}
		}

		if (command.getName().equalsIgnoreCase("balance")) {
			if (sender instanceof Player) {
				if (args.length == 0) {
					if (((Player) sender).hasPermission("aio.balance")) {
						sender.sendMessage("Your current balance: $" + getBalance(((Player) sender).getUniqueId()));
						return false;
					} else {
						sender.sendMessage("You don't have permission to execute that command.");
						return false;
					}
				}
			}
			if (args.length == 1) {
				if (sender.hasPermission("aio.balance.others")) {
					if (plugin.getServer().getPlayer(args[0]) != null) {
						sender.sendMessage("Balance of " + plugin.getServer().getPlayer(args[0]).getDisplayName() + ": $" + getBalance(plugin.getServer().getPlayer(args[0]).getUniqueId()));
						return false;
					}
				} else {
					sender.sendMessage("You don't have permission to execute that command.");
					return false;
				}
			} else {
				sender.sendMessage("Too many arguments.");
				return false;
			}
		}

		if (command.getName().equalsIgnoreCase("economy")) {
			if (!(sender instanceof Player) || sender.hasPermission("aio.economy")) {
				if (args.length < 2) {
					sender.sendMessage("Not enough arguments.");
					return false;
				} else if (args[0].equalsIgnoreCase("reset")) {
					if (plugin.getServer().getPlayer(args[1]) != null) {
						set(plugin.getServer().getPlayer(args[1]).getUniqueId(), 0);
						sender.sendMessage("The balance of " + plugin.getServer().getPlayer(args[1]).getDisplayName() + " has been reset to $0.");
						return false;
					}
				}
				if (args.length < 3) {
					sender.sendMessage("Not enough arguments.");
					return false;
				}
				if (args[0].equalsIgnoreCase("set")) {
					if (plugin.getServer().getPlayer(args[1]) != null) {
						set(plugin.getServer().getPlayer(args[1]).getUniqueId(), Double.parseDouble(args[2]));
						sender.sendMessage("Successfully set the balance of " + plugin.getServer().getPlayer(args[1]).getDisplayName() + " to $" + getBalance(plugin.getServer().getPlayer(args[1]).getUniqueId()));
						return false;
					}
				} else if (args[0].equalsIgnoreCase("add")) {
					add(plugin.getServer().getPlayer(args[1]).getUniqueId(), Double.parseDouble(args[2]));
					sender.sendMessage("Successfully set the balance of " + plugin.getServer().getPlayer(args[1]).getDisplayName() + " to $" + getBalance(plugin.getServer().getPlayer(args[1]).getUniqueId()));
					return false;
				}
			}
		}
		return false;
	}
}

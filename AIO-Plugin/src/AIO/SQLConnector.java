package AIO;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;
import java.util.logging.Level;

public class SQLConnector {
	
	Connection connection;
	Plugin plugin;

	SQLConnector(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public void connect(String ipaddress, String database, String username, String password, boolean async) {
		if (async && plugin.isEnabled()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						Class.forName("com.mysql.jdbc.Driver");
						connection = DriverManager.getConnection("jdbc:mysql://" + ipaddress + "/" + database, username, password);
					} catch (Exception e) {
                        e.printStackTrace();
                        plugin.getLogger().log(Level.SEVERE, "Unable to connect to database! Server is shutting down.");
                        plugin.getServer().shutdown();
					}
				}
			}.runTaskAsynchronously(plugin);
		} else {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection("jdbc:mysql://" + ipaddress + "/" + database, username, password);
			} catch (Exception e) {
                e.printStackTrace();
                plugin.getLogger().log(Level.SEVERE, "Unable to connect to database! Server is shutting down.");
                plugin.getServer().shutdown();
			}
		}
	}
	
	public void query(String query, SQLCallback completed) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					Statement statement = connection.createStatement();
					ResultSet result = statement.executeQuery(query);
					new BukkitRunnable() {
						public void run() {
							completed.callback(result);
						}
					}.runTask(plugin);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(plugin);
	}

	public void update(String query, SQLCallback completed) {
		if (plugin.isEnabled()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						Statement statement = connection.createStatement();
						statement.executeUpdate(query);
						new BukkitRunnable() {
							public void run() {
								completed.callback();
							}
						}.runTask(plugin);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(plugin);
		} else {
			try {
				Statement statement = connection.createStatement();
				statement.executeUpdate(query);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void disconnect() {
		if (plugin.isEnabled()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					try {
						connection.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(plugin);
		} else {
			try {
				connection.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}

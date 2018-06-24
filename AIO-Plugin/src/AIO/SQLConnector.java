package AIO;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.*;

public class SQLConnector {
	
	Connection connection;
	Plugin plugin;

	SQLConnector(Plugin plugin) {
		this.plugin = plugin;
	}
	
	public void connect(String ipaddress, String database, String username, String password) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connection = DriverManager.getConnection("jdbc:mysql://" + ipaddress + "/" + database, username, password);
			System.out.println(connection.getMetaData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void query(String query, SQLCallback completed) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					Statement statement = connection.createStatement();
					ResultSet result = statement.executeQuery(query);
					completed.callback(result);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(plugin);
	}

	public void update(String query, SQLCallback completed) {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
			completed.callback();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void disconnect() {
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

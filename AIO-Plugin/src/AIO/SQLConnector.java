package AIO;

import org.bukkit.plugin.Plugin;

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
	
	public ResultSet query(String query) {
		try {
			Statement statement = connection.createStatement();
			return statement.executeQuery(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void update(String query) {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate(query);
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

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
			System.out.println("TEST----------1");
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("TEST----------2");
			connection = DriverManager.getConnection("jdbc:mysql://" + ipaddress + "/" + database, username, password);
			System.out.println("TEST----------3");
			System.out.println(connection.getMetaData());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public ResultSet query(String query) {
		try {
			Statement statement = connection.createStatement();
			return statement.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void disconnect() {
		try {
			connection.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

package de.whitescan.serverpassword.storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.NonNull;

/**
 * 
 * @author Whitescan
 *
 */
public class WhitelistDatabase {

	@NonNull
	@Getter
	private final Logger logger;

	@NonNull
	@Getter
	private final String databaseName;

	private Connection connection;

	public WhitelistDatabase(Logger logger, String databaseName) {
		this.logger = logger;
		this.databaseName = databaseName;
		connect();
	}

	public final void close() {

		try {

			if (connection != null) {
				connection.close();
				getLogger().info("Connection to database: " + getDatabaseName() + " was closed.");
			}

		} catch (SQLException e) {
			getLogger().warning("Connection to database: " + getDatabaseName() + " could not close!");
			e.printStackTrace();
		}

	}

	public final void connect() {

		try {

			this.connection = DriverManager.getConnection("jdbc:sqlite:" + getDatabaseName() + ".db");
			getLogger().info("Connection to database: " + getDatabaseName() + " was successful!");

		} catch (Exception e) {
			getLogger().severe("Connection to database: " + getDatabaseName()
					+ " failed! Please check credentials before reporting this as an issue...");
			e.printStackTrace();
		}

	}

	public final Connection getConnection() {

		try {

			if (connection == null || connection.isClosed())
				connect();

		} catch (SQLException e) {
			getLogger().severe("Could not retreive a connection from database: " + getDatabaseName()
					+ "! This is most likely a follow up error.");
			e.printStackTrace();
		}

		return connection;

	}

}

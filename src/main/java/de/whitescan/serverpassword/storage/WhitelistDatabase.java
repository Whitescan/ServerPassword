package de.whitescan.serverpassword.storage;

import java.io.File;
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
	private final File database;

	private Connection connection;

	public WhitelistDatabase(Logger logger, File database) {
		this.logger = logger;
		this.database = database;
		connect();
	}

	public final void close() {

		try {

			if (connection != null) {
				connection.close();
				getLogger().info("Connection to database: " + getDatabase().getName() + " was closed.");
			}

		} catch (SQLException e) {
			getLogger().warning("Connection to database: " + getDatabase().getName() + " could not close!");
			e.printStackTrace();
		}

	}

	public final void connect() {

		try {

			this.connection = DriverManager.getConnection("jdbc:sqlite:" + getDatabase().getAbsolutePath());
			getLogger().info("Connection to database: " + getDatabase().getName() + " was successful!");

		} catch (Exception e) {
			getLogger().severe("Connection to database: " + getDatabase().getName()
					+ " failed! Please check credentials before reporting this as an issue...");
			e.printStackTrace();
		}

	}

	public final Connection getConnection() {

		try {

			if (connection == null || connection.isClosed())
				connect();

		} catch (SQLException e) {
			getLogger().severe("Could not retreive a connection from database: " + getDatabase().getName()
					+ "! This is most likely a follow up error.");
			e.printStackTrace();
		}

		return connection;

	}

}

package de.whitescan.serverpassword.storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.bukkit.entity.Player;

/**
 * 
 * @author Whitescan
 *
 */
public class WhitelistDAO {

	private WhitelistDatabase whitelistDatabase;

	public WhitelistDAO(WhitelistDatabase whitelistDatabase) {
		this.whitelistDatabase = whitelistDatabase;
		checkTables();
	}

	public Connection getConnection() {
		return whitelistDatabase.getConnection();
	}

	private void checkTables() {
		final String createTable = "CREATE TABLE IF NOT EXISTS whitelist (" + //
				"uniqueId TEXT NOT NULL UNIQUE," + //
				"PRIMARY KEY(uniqueId))";

		try {
			PreparedStatement ps = getConnection().prepareStatement(createTable);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public boolean isAuthenticated(UUID uniqueId) {

		final String checkSQL = "SELECT * FROM whitelist WHERE uniqueId = ?";

		try {

			PreparedStatement ps = getConnection().prepareStatement(checkSQL);
			ps.setString(1, uniqueId.toString());

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {

				UUID check = UUID.fromString(rs.getString("uniqueId"));

				if (check.equals(uniqueId))
					return true;

			}

			rs.close();
			ps.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;

	}

	public void authenticate(Player player) {

		final String insertSQL = "INSERT INTO whitelist (uniqueId) VALUES (?)";

		try {

			PreparedStatement ps = getConnection().prepareStatement(insertSQL);
			ps.setString(1, player.getUniqueId().toString());
			ps.executeUpdate();
			ps.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

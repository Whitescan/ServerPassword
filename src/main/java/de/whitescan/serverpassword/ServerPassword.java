package de.whitescan.serverpassword;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.conversations.Conversable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import de.whitescan.serverpassword.listener.ServerPasswordListener;
import de.whitescan.serverpassword.storage.WhitelistDAO;
import de.whitescan.serverpassword.storage.WhitelistDatabase;
import lombok.Getter;

/**
 * 
 * @author Whitescan
 *
 */
public class ServerPassword extends JavaPlugin {

	// Config

	@Getter
	private PasswordMode mode;

	@Getter
	private String command;

	@Getter
	private String password;

	@Getter
	private int maxAttempts;

	// Messages

	@Getter
	private String prefix;

	@Getter
	private String authenticationPromptMessage;

	@Getter
	private String authenticationFailedMessage;

	@Getter
	private String authenticationSuccessMessage;

	@Getter
	private String exceededMaxAttemptsMessage;

	// Runtime

	@Getter
	private WhitelistDatabase whitelistDatabase;

	@Getter
	private WhitelistDAO whitelistDAO;

	@Getter
	private final Map<Conversable, Integer> attempts;

	public ServerPassword() {
		this.attempts = new HashMap<>();
		getLogger().info("Initializing...");
	}

	@Override
	public void onEnable() {

		readConfigs();

		registerListeners();

		getLogger().info("Enabled...");
	}

	private void registerListeners() {

		final PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new ServerPasswordListener(this), this);

	}

	private void readConfigs() {
		getLogger().info("Reading configurations...");

		final File configFile = new File(getDataFolder(), "config.yml");

		if (!configFile.exists())
			saveDefaultConfig();

		this.mode = PasswordMode.get(getConfig().getString("config.password-mode", "default"));
		this.command = getConfig().getString("config.after-auth-command", "");
		this.password = getConfig().getString("config.password", "");
		this.maxAttempts = getConfig().getInt("config.max-password-attempts", 3);

		if (getMaxAttempts() < 1) {
			this.maxAttempts = 1;
			getConfig().set("config.max-password-attempts", 1);
			saveConfig();
			getLogger().warning("Max Password attempts have been set to 1 since it can't go lower then that...");
		}

		this.prefix = getConfig().getString("messages.prefix", "&8[&4ServerPassword&8] ").replace("&", "§");
		this.authenticationPromptMessage = getConfig()
				.getString("messages.authentification-prompt", "&ePlease enter the Server Password to authenticate!")
				.replace("&", "§");
		this.authenticationFailedMessage = getConfig()
				.getString("messages.authentification-failed", "&cFailed to verify password, please try again...")
				.replace("&", "§");
		this.authenticationSuccessMessage = getConfig()
				.getString("messages.authentification-success", "&aSuccessfully authenticated!").replace("&", "§");
		this.exceededMaxAttemptsMessage = getConfig()
				.getString("messages.exceeded-max-attempts", "&cYou exceeded the maximum amount of Password attempts!")
				.replace("&", "§");

		// Tips

		if (getConfig().getBoolean("config.tips", true)) {

			if (getPassword().isBlank())
				getLogger()
						.warning("Your Server Password is not set. This kind of defeats the purpose of this Plugin.");

			if (getMode() == PasswordMode.PERMISSION && getCommand().isBlank())
				getLogger().warning(
						"You should setup your after-auth-command so people get permission to join your server once they entered the password once!");

		}

		if (getMode() == PasswordMode.WHITELIST) {
			this.whitelistDatabase = new WhitelistDatabase(getLogger(), new File(getDataFolder(), "whitelist.db"));
			this.whitelistDAO = new WhitelistDAO(getWhitelistDatabase());
		}

	}

	@Override
	public void onDisable() {

		if (getWhitelistDatabase() != null)
			getWhitelistDatabase().close();

		getLogger().info("Disabled...");

	}

	public void handleInvalidPasswordAttempt(Player forWhom) {

		int current = getAttempts().getOrDefault(forWhom, 0) + 1;

		if (current >= getMaxAttempts()) {
			forWhom.kickPlayer(getExceededMaxAttemptsMessage());
			getAttempts().remove(forWhom);
			return;
		}

		getAttempts().put(forWhom, current);

	}

}

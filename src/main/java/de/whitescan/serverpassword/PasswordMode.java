package de.whitescan.serverpassword;

/**
 * 
 * @author Whitescan
 *
 */
public enum PasswordMode {

	DEFAULT, WHITELIST, PERMISSION;

	public static PasswordMode get(String string) {
		PasswordMode mode = PasswordMode.valueOf(string.toUpperCase());
		return mode != null ? mode : DEFAULT;
	}

}

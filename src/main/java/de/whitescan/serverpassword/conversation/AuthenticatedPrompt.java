package de.whitescan.serverpassword.conversation;

import org.bukkit.Bukkit;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.entity.Player;

import de.whitescan.serverpassword.PasswordMode;
import de.whitescan.serverpassword.ServerPassword;
import de.whitescan.serverpassword.event.ServerPasswordAuthenticatedEvent;
import lombok.AllArgsConstructor;

/**
 * 
 * @author Whitescan
 *
 */
@AllArgsConstructor
public class AuthenticatedPrompt implements Prompt {

	private final ServerPassword serverPassword;

	@Override
	public String getPromptText(ConversationContext context) {
		return serverPassword.getAuthenticationSuccessMessage();
	}

	@Override
	public boolean blocksForInput(ConversationContext context) {
		return false;
	}

	@Override
	public Prompt acceptInput(ConversationContext context, String input) {

		final Player actor = (Player) context.getForWhom();

		if (serverPassword.getMode() == PasswordMode.WHITELIST)
			serverPassword.getWhitelistDAO().authenticate(actor);

		serverPassword.getAttempts().remove(actor);

		if (!serverPassword.getCommand().isBlank())
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
					serverPassword.getCommand().replace("{PLAYER}", actor.getName()));
		
		Bukkit.getPluginManager().callEvent(new ServerPasswordAuthenticatedEvent(actor));

		return END_OF_CONVERSATION;

	}

}

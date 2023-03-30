package de.whitescan.serverpassword.conversation;

import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.Prompt;
import org.bukkit.conversations.ValidatingPrompt;
import org.bukkit.entity.Player;

import de.whitescan.serverpassword.ServerPassword;
import lombok.AllArgsConstructor;

/**
 * 
 * @author Whitescan
 *
 */
@AllArgsConstructor
public class AuthenticationPrompt extends ValidatingPrompt {

	private final ServerPassword serverPassword;

	@Override
	public String getPromptText(ConversationContext context) {
		return serverPassword.getAuthenticationPromptMessage();
	}

	@Override
	protected boolean isInputValid(ConversationContext context, String input) {

		boolean success = input.equals(serverPassword.getPassword());

		if (!success)
			serverPassword.handleInvalidPasswordAttempt((Player) context.getForWhom());

		return success;

	}

	@Override
	protected Prompt acceptValidatedInput(ConversationContext context, String input) {
		return new AuthenticatedPrompt(serverPassword);
	}

	@Override
	protected String getFailedValidationText(ConversationContext context, String invalidInput) {
		return serverPassword.getAuthenticationFailedMessage();
	}

}

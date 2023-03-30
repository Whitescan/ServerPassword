package de.whitescan.serverpassword.listener;

import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationAbandonedEvent;
import org.bukkit.conversations.ConversationAbandonedListener;
import org.bukkit.conversations.ConversationContext;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.conversations.ConversationPrefix;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.whitescan.serverpassword.PasswordMode;
import de.whitescan.serverpassword.ServerPassword;
import de.whitescan.serverpassword.conversation.AuthenticationPrompt;
import lombok.AllArgsConstructor;

/**
 * 
 * @author Whitescan
 *
 */
@AllArgsConstructor
public class ServerPasswordListener implements Listener {

	private final ServerPassword serverPassword;

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		final Player actor = e.getPlayer();
		serverPassword.getAttempts().remove(actor);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {

		if (serverPassword.getPassword().isBlank())
			return;

		final Player actor = e.getPlayer();

		if (serverPassword.getMode() == PasswordMode.WHITELIST
				&& serverPassword.getWhitelistDAO().isAuthenticated(actor.getUniqueId()))
			return;

		if (serverPassword.getMode() == PasswordMode.PERMISSION && actor.hasPermission("serverpassword.allow"))
			return;

		// Have a conversation to authenticate
		Conversation conversation = new ConversationFactory(serverPassword).withPrefix(new ConversationPrefix() {

			@Override
			public String getPrefix(ConversationContext context) {
				return serverPassword.getPrefix();
			}

		}).withFirstPrompt(new AuthenticationPrompt(serverPassword))
				.addConversationAbandonedListener(new ConversationAbandonedListener() {

					@Override
					public void conversationAbandoned(ConversationAbandonedEvent abandonedEvent) {
						abandonedEvent.gracefulExit();
					}

				}).withLocalEcho(true).buildConversation(actor);

		conversation.begin();

	}

}

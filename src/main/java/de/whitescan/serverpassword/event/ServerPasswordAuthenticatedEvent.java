package de.whitescan.serverpassword.event;

import javax.annotation.Nonnull;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;

/**
 * 
 * Fired when a Player successfully authenticated through entering the Server Password
 * 
 * @author Whitescan
 *
 */
public class ServerPasswordAuthenticatedEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();
    
    public ServerPasswordAuthenticatedEvent(@Nonnull final Player player) {
        super(player);
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
    
}

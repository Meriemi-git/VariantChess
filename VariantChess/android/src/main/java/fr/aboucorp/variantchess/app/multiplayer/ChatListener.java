package fr.aboucorp.variantchess.app.multiplayer;

import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.api.ChannelMessage;

public interface ChatListener {
    void onChannelMessage(ChannelMessage message);

    void onChannelPresence(ChannelPresenceEvent presence);
}

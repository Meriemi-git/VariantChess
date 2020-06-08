package fr.aboucorp.variantchess.app.multiplayer;

import android.util.Log;

import com.heroiclabs.nakama.ChannelPresenceEvent;
import com.heroiclabs.nakama.Error;
import com.heroiclabs.nakama.MatchData;
import com.heroiclabs.nakama.MatchPresenceEvent;
import com.heroiclabs.nakama.MatchmakerMatched;
import com.heroiclabs.nakama.StatusPresenceEvent;
import com.heroiclabs.nakama.StreamData;
import com.heroiclabs.nakama.StreamPresenceEvent;
import com.heroiclabs.nakama.api.ChannelMessage;
import com.heroiclabs.nakama.api.NotificationList;

public abstract class AbstractMatchListener implements MatchListener {
    @Override
    public void onError(Error error) {
        Log.e("fr.aboucorp.variantchess", error.getMessage());
    }

    @Override
    public void onMatchmakerMatched(MatchmakerMatched matched) {
        Log.e("fr.aboucorp.variantchess", "Matchmaking matched");
    }

    @Override
    public void onMatchData(MatchData matchData) {
        Log.e("fr.aboucorp.variantchess", "Match data received");
    }

    @Override
    public void onDisconnect(Throwable t) {
        Log.e("fr.aboucorp.variantchess", "Socket disconnected");
    }

    @Override
    public void onChannelMessage(ChannelMessage message) {
        Log.e("fr.aboucorp.variantchess", "Message received");
    }

    @Override
    public void onChannelPresence(ChannelPresenceEvent presence) {
        Log.e("fr.aboucorp.variantchess", "On channel Presence detected");
    }

    @Override
    public void onMatchPresence(MatchPresenceEvent matchPresence) {
        Log.e("fr.aboucorp.variantchess", "On match presence detected");
    }

    @Override
    public void onNotifications(NotificationList notifications) {
        Log.e("fr.aboucorp.variantchess", "Notification received");
    }

    @Override
    public void onStatusPresence(StatusPresenceEvent presence) {
        Log.e("fr.aboucorp.variantchess", "Status presence detected");
    }

    @Override
    public void onStreamPresence(StreamPresenceEvent presence) {
        Log.e("fr.aboucorp.variantchess", "Stream presence detected");
    }

    @Override
    public void onStreamData(StreamData data) {
        Log.e("fr.aboucorp.variantchess", "Stream data received");
    }
}

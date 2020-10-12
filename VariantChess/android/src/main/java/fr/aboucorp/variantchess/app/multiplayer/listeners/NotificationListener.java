package fr.aboucorp.variantchess.app.multiplayer.listeners;

import com.heroiclabs.nakama.api.NotificationList;

public interface NotificationListener {
    void onNotifications(NotificationList notifications);
}

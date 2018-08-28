package de.freddi.bananaapp.notification.firebase;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import de.freddi.bananaapp.logging.L;
import de.freddi.bananaapp.notification.NotificationHelper;
import de.freddi.bananaapp.settings.Preferences;

public class FirebaseReceiver extends FirebaseMessagingService {
    private static final String LOGGING_TAG = "FirebaseReceiver";

    @Override
    public void onNewToken(final String strToken) {
        super.onNewToken(strToken);

        final Preferences prefs = new Preferences();
        prefs.set(Preferences.PREF.NOTIFICATIONS_FIREBASE_TOKEN, strToken);

        L.log(LOGGING_TAG, "FirebaseReceiver Refreshed token: " + strToken, prefs);
    }

    @Override
    public void onMessageReceived(final RemoteMessage remoteMessage) {
        final Preferences pref = new Preferences();
        final boolean bIsDebug = new Preferences().isDebugLogging();

        L.log(LOGGING_TAG, "FirebaseReceiver message received:\n" + getMessageDebug(remoteMessage), bIsDebug);

        // Check if message contains a data payload.
        if (remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {
            final String strFrom = remoteMessage.getData().get("from_user");
            final String strTo   = remoteMessage.getData().get("to_user");
            final String strComment = remoteMessage.getData().get("comment");
            final String strSubtopic = remoteMessage.getData().get("subtopic");

            final boolean bAllow = NotificationHelper.allowNotification(strTo, strSubtopic);
            L.log(LOGGING_TAG, "allowNotification result: " + bAllow, bIsDebug);

            if (bAllow) {
                NotificationHelper.displayNotification(this, strFrom, strTo, strComment);
            }

            pref.set(Preferences.PREF.STATE_FIREBASE_RECEIVED, "true");
        }
    }

    private String getMessageDebug(final RemoteMessage remoteMessage) {
        if (remoteMessage == null) {
            return "";
        }

        StringBuilder buff = new StringBuilder("push message received\n");

        if (remoteMessage.getData().get("from_user") != null) {
            buff.append("from_user=").append(remoteMessage.getData().get("from_user")).append("\n");
        }

        if (remoteMessage.getData().get("to_user") != null) {
            buff.append("to_user=").append(remoteMessage.getData().get("to_user")).append("\n");
        }

        if (remoteMessage.getData().get("comment") != null) {
            buff.append("comment=").append(remoteMessage.getData().get("comment")).append("\n");
        }

        if (remoteMessage.getData().get("subtopic") != null) {
            buff.append("subtopic=").append(remoteMessage.getData().get("subtopic")).append("\n");
        }

        return buff.toString().trim();
    }
}

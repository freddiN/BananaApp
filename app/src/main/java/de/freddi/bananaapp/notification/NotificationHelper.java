package de.freddi.bananaapp.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.freddi.bananaapp.MainActivity;
import de.freddi.bananaapp.R;
import de.freddi.bananaapp.logging.L;
import de.freddi.bananaapp.settings.Preferences;
import de.freddi.bananaapp.settings.Preferences.PREF;

public class NotificationHelper {
    private static final String LOGGING_TAG = "NotificationHelper";
    private static final String NOTIFICATION_CHANNEL = "default_notification_channel";

    private static final SimpleDateFormat SDF = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.GERMANY);

    /**
     * none, to me, all transactions
     */
    public static boolean allowNotification(final String strFrom, final String strTo, final String strTopic) {
        final Preferences prefs = new Preferences();
        final boolean bIsDebugging = prefs.isDebugLogging();

        if (StringUtils.isBlank(prefs.getAsString(PREF.ACCOUNT_TOKEN))) {
            L.log(LOGGING_TAG, "allowNotification isLoginOk false", bIsDebugging);
            return false;
        }

        if (strFrom.equalsIgnoreCase(prefs.getAsString(PREF.ACCOUNT_DISPLAYNAME))) {
            L.log(LOGGING_TAG, "allowNotification not my own bananas false", bIsDebugging);
            return false;
        }

        final String strNotificationTopic = prefs.getAsString(PREF.NOTIFICATIONS_TOPIC);
        if (StringUtils.length(strNotificationTopic) < 3 || strTopic == null || !strTopic.equals(strNotificationTopic)) {
            L.log(LOGGING_TAG, "allowNotification strNotificationTopic false", bIsDebugging);
            return false;
        }

        final String strTokenExpiration = prefs.getAsString(PREF.ACCOUNT_TOKEN_EXPIRATION);
        L.log(LOGGING_TAG, "allowNotification strTokenExpiration " + strTokenExpiration, bIsDebugging);
        try {
            final Date dateToken = SDF.parse(strTokenExpiration);
            final Date dateNow = new Date();
            if (dateNow.compareTo(dateToken) > 0) {
                L.log(LOGGING_TAG, "allowNotification token expired", bIsDebugging);
                return false;
            }
        } catch (final ParseException e) {
            L.log(LOGGING_TAG, "allowNotification error parsing date", bIsDebugging);
            return false;
        }

        final String strNotificationSetting = prefs.getAsString(PREF.NOTIFICATIONS_SETTING);
        if (StringUtils.equalsIgnoreCase("all transactions", strNotificationSetting)) {
            L.log(LOGGING_TAG, "allowNotification strNotificationSetting 1 true", bIsDebugging);
            return true;
        }

        if ((StringUtils.equalsIgnoreCase("to me", strNotificationSetting) && StringUtils.equalsIgnoreCase(prefs.getAsString(PREF.ACCOUNT_DISPLAYNAME), strTo)) || StringUtils.equalsIgnoreCase(strTo, "everyone")) {
            L.log(LOGGING_TAG, "allowNotification strNotificationSetting 2 true", bIsDebugging);
            return true;
        }

        // "none landet" auch hier
        return false;
    }

    private static String replaceVariables(final String strIn, final String strFrom, final String strTo, final String strComment) {
        String strOut = StringUtils.replaceAll(strIn, "%from", strFrom);
        strOut = StringUtils.replaceAll(strOut, "%to", strTo);
        strOut = StringUtils.replaceAll(strOut, "%comment", strComment);
        return strOut;
    }

    public static void displayNotification(final Context ctx, final String strFrom, final String strTo, final String strComment) {
        final Preferences prefs = new Preferences();

        final boolean hasBigText = StringUtils.isNotBlank(prefs.getAsString(PREF.NOTIFICATIONS_STYLE_BIGTEXT));
        final boolean hasText = StringUtils.isNotBlank(prefs.getAsString(PREF.NOTIFICATIONS_STYLE_TEXT));
        final boolean hasTitle = StringUtils.isNotBlank(prefs.getAsString(PREF.NOTIFICATIONS_STYLE_TITLE));

        L.log(LOGGING_TAG, "displayNotification hasBigText=" + hasBigText + " hasText=" + hasText + " hasTitle=" + hasTitle, prefs);

        if (hasTitle || hasText || hasBigText) {
            /* klick startet app und öffnet transactions */
            final Intent i = new Intent(ctx, MainActivity.class);
            i.putExtra("goto", "transactions");

            final NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, NOTIFICATION_CHANNEL)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(PendingIntent.getActivity(ctx, 0, i, PendingIntent.FLAG_UPDATE_CURRENT))
                    .setAutoCancel(true)
                    .setColor(Color.YELLOW)
                    .setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher))
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setLights(Color.YELLOW, 1000, 1000);
            //.setPriority(Notification.PRIORITY_MAX);

            if (hasTitle) {
                builder.setContentTitle(NotificationHelper.replaceVariables(prefs.getAsString(PREF.NOTIFICATIONS_STYLE_TITLE), strFrom, strTo, strComment));
            }

            if (hasText) {
                builder.setContentText(NotificationHelper.replaceVariables(prefs.getAsString(PREF.NOTIFICATIONS_STYLE_TEXT), strFrom, strTo, strComment));
            }

            if (hasBigText) {
                builder.setStyle(new NotificationCompat.BigTextStyle().bigText(NotificationHelper.replaceVariables(prefs.getAsString(PREF.NOTIFICATIONS_STYLE_BIGTEXT), strFrom, strTo, strComment)));
            }

            final NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager != null) {
                L.log(LOGGING_TAG, "notify -> Icon anzeigen", prefs);

                manager.notify((int) (System.currentTimeMillis() / 1000L), builder.build());
            }
        }
    }

    public static void createNotificationChannel(final Context ctx) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL, NOTIFICATION_CHANNEL, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Banana Notification Channel");
            final NotificationManager manager = ctx.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }
    }
}

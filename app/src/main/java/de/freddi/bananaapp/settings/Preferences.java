package de.freddi.bananaapp.settings;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import de.freddi.bananaapp.App;

public class Preferences {
    /**
     * Nicht die strigs.xml nehmen, da man zum Auflösen immer nen Context braucht und man dazu in Async Threads die
     * MainAcitivity durchreichen müsste.
     * Internationalisierung brauch ich eh nicht.
     */

    public enum PREF {
        ACCOUNT_TOKEN("account_token"),
        ACCOUNT_TOKEN_EXPIRATION("account_token_expiration"),
        ACCOUNT_TOKEN_DURATION("account_token_duration"),
        ACCOUNT_DISPLAYNAME("account_displayname"),
        ACCOUNT_BANANAS_RECEIVED("account_bananas_received"),
        ACCOUNT_BANANAS_TO_SPEND("account_bananas_to_spend"),
        ACCOUNT_IS_ADMIN("account_is_admin"),
        ACCOUNT_AD_USER("account_ad_user"),
        ACCOUNT_ID("account_id"),
        ACCOUNT_TEAM_NAME("team_name"),

        CONNECTION_SERVER("connection_server"),
        CONNECTION_HTTP_USER("connection_http_user"),
        CONNECTION_HTTP_PASS("connection_http_pass"),
        CONNECTION_TIMEOUT_CONNECT("connection_timeout_connect"),
        CONNECTION_TIMEOUT_READ("connection_timeout_read"),

        DEBUG_LOGGING("debug_logging"),

        OTHER_LIMIT_TRANSACTIONS("other_limit_transactions"),

        NOTIFICATIONS_SETTING("notifications_setting"),
        NOTIFICATIONS_TOPIC("notifications_topic"),
        NOTIFICATIONS_STYLE_TITLE("notifications_style_title"),
        NOTIFICATIONS_STYLE_TEXT ("notifications_style_text"),
        NOTIFICATIONS_STYLE_BIGTEXT("notifications_style_bigtext"),
        NOTIFICATIONS_FIREBASE_TOKEN("notifications_firebase_token"),

        STATE_LAST_SYNC("state_last_sync"),
        STATE_SETTINGS_CHANGED("state_settings_changed"),
        STATE_FIREBASE_RECEIVED("state_firebase_received");

        private final String m_strKey;

        PREF(String strKey) {
            this.m_strKey = strKey;
        }

        public String value() {
            return this.m_strKey;
        }
    }

    private final SharedPreferences m_prefs = App.get().getSharedPreferences();

    public String getAsString(final PREF strKey) {
        return m_prefs.getString(strKey.value(), null);
    }

    public String getAsStringEmptyIfNull(final PREF strKey) {
        return m_prefs.getString(strKey.value(), "");
    }

    public Integer getAsInt(final PREF strKey, final int nDefault) {
        return Integer.valueOf(m_prefs.getString(strKey.value(), "" + nDefault));
    }

    @SuppressLint("ApplySharedPref")
    public void set(final PREF strKey, final String strValue) {
        SharedPreferences.Editor edit = m_prefs.edit();
        edit.putString(strKey.value(), strValue);
        //edit.apply();
        edit.commit();
    }

    public boolean isDebugLogging() {
        return m_prefs.getBoolean(PREF.DEBUG_LOGGING.value(), false);
    }
}

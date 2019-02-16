package de.freddi.bananaapp;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.provider.Settings;

import de.freddi.bananaapp.gui.GuiHelper;
import de.freddi.bananaapp.logging.L;
import de.freddi.bananaapp.notification.NotificationHelper;
import de.freddi.bananaapp.settings.Preferences;
import de.freddi.bananaapp.settings.Preferences.PREF;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentTransaction mFragmentTransaction = getFragmentManager().beginTransaction();
        mFragmentTransaction.replace(android.R.id.content, new PrefsFragment());
        mFragmentTransaction.commit();
    }

    public static class PrefsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // xml/prefs.xml laden
            addPreferencesFromResource(R.xml.prefs);

            // callback auf token/http Settings um nur bei Änderungen einen Refresh auszulösen
            Preference tokenPref = findPreference(PREF.ACCOUNT_TOKEN.value());
            tokenPref.setOnPreferenceChangeListener(this);

            Preference serverPref = findPreference(PREF.CONNECTION_SERVER.value());
            serverPref.setOnPreferenceChangeListener(this);

            Preference httpUserPref = findPreference(PREF.CONNECTION_HTTP_USER.value());
            httpUserPref.setOnPreferenceChangeListener(this);

            Preference httpPasswordPref = findPreference(PREF.CONNECTION_HTTP_PASS.value());
            httpPasswordPref.setOnPreferenceChangeListener(this);

            Preference otherSortUserlist = findPreference(PREF.OTHER_SORT_USERLIST.value());
            otherSortUserlist.setOnPreferenceChangeListener(this);

            // cacptue clicks on items
            Preference testNotificationStyle = findPreference("notifications_style_testbutton");
            testNotificationStyle.setOnPreferenceClickListener(preference -> {
                NotificationHelper.displayNotification(getActivity(), "TEST-FROM", "TEST-TO","TEST-COMMENT" );
                return true;
            });

            Preference openDozeIntent = findPreference("other_doze_intent");
            openDozeIntent.setOnPreferenceClickListener(preference -> {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    startActivity(new Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));

                    if (new Preferences().isDebugLogging()) {
                        final PowerManager pm = (PowerManager) getActivity().getSystemService(POWER_SERVICE);
                        if (pm != null) {
                            GuiHelper.doToast(getActivity(), "Currently ignoring Doze : " + pm.isIgnoringBatteryOptimizations(getActivity().getPackageName()));
                        }
                    }
                } else {
                    GuiHelper.doToast(getActivity(), "not supported below Android 6.0 Marshmallow");
                }
                return true;
            });

            Preference debug = findPreference(PREF.DEBUG_LOGGING.value());
            debug.setOnPreferenceChangeListener((preference, o) -> {
                // sofort Debugdatei loeschen wenn debug deakiviert wird
                if (o instanceof Boolean) {
                    if (!((Boolean) o)) {
                        L.deleteLogFile();
                    }
                }

                return true;
            });

            Preference tokenFirebase = findPreference(PREF.NOTIFICATIONS_FIREBASE_TOKEN.value());
            EditTextPreference editTextTokenFirebase = (EditTextPreference) tokenFirebase;
            editTextTokenFirebase.setSummary(editTextTokenFirebase.getText());    //Text updaten
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            new Preferences().set(PREF.STATE_SETTINGS_CHANGED, "yes");

            return true;
        }
    }
}

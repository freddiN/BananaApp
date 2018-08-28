package de.freddi.bananaapp.async.http;

import android.os.AsyncTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.freddi.bananaapp.http.HTTPConnector;
import de.freddi.bananaapp.settings.Preferences;
import de.freddi.bananaapp.settings.Preferences.PREF;

public class Refresh extends AsyncTask<Void, Void, Boolean> {
    private final static SimpleDateFormat SDF = new SimpleDateFormat("dd.MM HH:mm:ss", Locale.GERMANY);

    @Override
    protected Boolean doInBackground(Void... voids) {
        if (HTTPConnector.updateAccountDetails()) {
            HTTPConnector.updateUsers();
            HTTPConnector.updateTransactions();

            new Preferences().set(PREF.STATE_LAST_SYNC, SDF.format(new Date()));

            return true;
        } else {
            return false;
        }
    }
}

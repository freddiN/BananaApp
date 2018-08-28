package de.freddi.bananaapp.async.http;

import android.os.AsyncTask;

import de.freddi.bananaapp.http.HTTPConnector;

public class Logout extends AsyncTask<Void, Void, Boolean> {
    @Override
    protected Boolean doInBackground(Void... voids) {
        return HTTPConnector.doLogout();
    }
}

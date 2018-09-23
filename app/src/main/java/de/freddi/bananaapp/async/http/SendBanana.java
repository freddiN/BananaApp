package de.freddi.bananaapp.async.http;

import android.os.AsyncTask;

import de.freddi.bananaapp.http.HTTPConnector;

public class SendBanana extends AsyncTask<String, Void, Boolean> {
    @Override
    protected Boolean doInBackground(String... params) {
        return HTTPConnector.sendBananas(params[0], params[1], params[2]);
    }
}

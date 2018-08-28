package de.freddi.bananaapp.notification.firebase;

import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;

import de.freddi.bananaapp.logging.L;
import de.freddi.bananaapp.settings.Preferences;

public class AsyncFirebaseRegister extends AsyncTask<Void, Void, Void> {
    private static final String LOGGING_TAG = "AsyncFirebaseRegister";

    protected Void doInBackground(Void... params) {
        try {
            FirebaseMessaging.getInstance().subscribeToTopic("banana");

            L.log(LOGGING_TAG, "Firebasesubscription started", new Preferences());
         } catch (final Exception exc) {
            Log.e(LOGGING_TAG, "Exception = " + exc.getMessage());
        }

        return null;
    }
}

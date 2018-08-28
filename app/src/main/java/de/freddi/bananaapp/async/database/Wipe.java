package de.freddi.bananaapp.async.database;

import android.os.AsyncTask;

import de.freddi.bananaapp.App;

public class Wipe extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        App.get().getDB().databaseInterface().wipeTransactions();
        App.get().getDB().databaseInterface().wipeUsers();

        return null;
    }
}

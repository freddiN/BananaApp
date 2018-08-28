package de.freddi.bananaapp;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.freddi.bananaapp.database.LocalDatabase;

public class App extends Application  {

    private static App INSTANCE;
    private static final String DATABASE_NAME = "LocalDatabase";

    private LocalDatabase database;

    public static App get() {
        return INSTANCE;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        database = Room.databaseBuilder(getApplicationContext(), LocalDatabase.class, DATABASE_NAME).build();

        INSTANCE = this;
    }

    public LocalDatabase getDB() {
        return database;
    }

    public SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }
}

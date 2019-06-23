package de.freddi.bananaapp;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

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

        database = Room.databaseBuilder(getApplicationContext(), LocalDatabase.class, DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .addMigrations(FROM_1_TO_2)
                .addMigrations(FROM_2_TO_3)
                .build();

        INSTANCE = this;
    }

    public LocalDatabase getDB() {
        return database;
    }

    public SharedPreferences getSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(this);
    }

    private static final Migration FROM_1_TO_2 = new Migration(1, 2) {
        @Override
        public void migrate(final SupportSQLiteDatabase database)
        {
            database.execSQL("ALTER TABLE DBTransaction ADD category TEXT");
        }
    };

    private static final Migration FROM_2_TO_3 = new Migration(2, 3) {
        @Override
        public void migrate(final SupportSQLiteDatabase database)
        {
        }
    };
}

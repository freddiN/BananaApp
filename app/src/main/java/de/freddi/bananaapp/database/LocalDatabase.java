package de.freddi.bananaapp.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {DBUser.class, DBTransaction.class}, version = 3, exportSchema = false)
public abstract class LocalDatabase extends RoomDatabase {
    public abstract DatabaseInterface databaseInterface();
}

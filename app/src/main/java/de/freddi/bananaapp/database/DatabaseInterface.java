package de.freddi.bananaapp.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface DatabaseInterface {
    @Query("SELECT * FROM DBUser")
    LiveData<List<DBUser>> getAllUsers();

    @Query("SELECT * FROM DBTransaction")
    LiveData<List<DBTransaction>> getAllTransactions();

    @Insert
    void insertAllUsers(List<DBUser> users);

    @Insert
    void insertAllTransactions(List<DBTransaction> transactions);

    @Query("DELETE FROM DBUser")
    void wipeUsers();

    @Query("DELETE FROM DBTransaction")
    void wipeTransactions();
}

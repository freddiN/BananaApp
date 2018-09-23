package de.freddi.bananaapp.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity(primaryKeys = {"from_user", "to_user", "timestamp"})
public class DBTransaction {
    @ColumnInfo(name = "timestamp")
    @NonNull
    public String timestamp;

    @ColumnInfo(name = "from_user")
    @NonNull
    public String from_user;

    @ColumnInfo(name = "to_user")
    @NonNull
    public String to_user;

    @ColumnInfo(name = "comment")
    public String comment;

    @ColumnInfo(name = "source")
    public String source;

    @ColumnInfo(name = "category")
    public String category;
}

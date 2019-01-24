package de.freddi.bananaapp.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class DBUser {
    @PrimaryKey
    @ColumnInfo(name = "display_name")
    @NonNull
    public String display_name;

    @ColumnInfo(name = "bananas_to_spend")
    public int bananas_to_spend;

    @ColumnInfo(name = "bananas_received")
    public int bananas_received;

    @ColumnInfo(name = "team_name")
    public String team_name;
}

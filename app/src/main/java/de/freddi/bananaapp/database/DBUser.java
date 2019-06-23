package de.freddi.bananaapp.database;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class DBUser {
    @PrimaryKey
    @ColumnInfo(name = "display_name")
    @NonNull
    public String display_name = "";

    @ColumnInfo(name = "bananas_to_spend")
    public int bananas_to_spend;

    @ColumnInfo(name = "bananas_received")
    public int bananas_received;

    @ColumnInfo(name = "team_name")
    public String team_name;
}

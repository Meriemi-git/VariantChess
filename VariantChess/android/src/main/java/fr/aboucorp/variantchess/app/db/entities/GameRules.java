package fr.aboucorp.variantchess.app.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "gamerules")
public class GameRules {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "difficulty")
    public int difficulty;

    @ColumnInfo(name = "balance")
    public int balance;
}

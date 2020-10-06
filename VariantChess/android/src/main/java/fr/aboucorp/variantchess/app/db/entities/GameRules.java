package fr.aboucorp.variantchess.app.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "gamerules")
public class GameRules implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "icon")
    public String icon;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "description")
    public String description;

    @ColumnInfo(name = "difficulty")
    public int difficulty;

    @ColumnInfo(name = "balance")
    public int balance;
}

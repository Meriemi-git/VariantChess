package fr.aboucorp.variantchess.app.db.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;

@Entity(tableName = "variant_user", indices = {@Index(value = {"username", "user_id"},
        unique = true)})
public class VariantUser implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "username")
    public String username;
    @ColumnInfo(name = "user_id")
    public String userId;
    @ColumnInfo(name = "metadata")
    public String metadata;
    @ColumnInfo(name = "is_connected")
    public boolean isConnected;
    @ColumnInfo(name = "auth_token")
    public String authToken;
}

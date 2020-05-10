package fr.aboucorp.variantchess.app.db.user;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import fr.aboucorp.variantchess.app.utils.AuthType;

@Entity(tableName = "user")
public class User {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "display_name")
    public String displayName;

    @ColumnInfo(name = "username")
    @NonNull
    public String username;

    @NonNull
    @ColumnInfo(name = "mail")
    public String mail;

    @ColumnInfo(name = "password")
    public String password;

    @ColumnInfo(name = "user_id")
    @NonNull
    public String userId;

    @ColumnInfo(name = "is_connected")
    @NonNull
    public boolean isConnected;

    @ColumnInfo(name = "auth_type")
    @NonNull
    public AuthType authType;

    public User() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getMail() {
        return mail;
    }

    public void setMail(@NonNull String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}

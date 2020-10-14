package fr.aboucorp.variantchess.app.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.aboucorp.variantchess.app.db.entities.VariantUser;

@Dao
public interface ChessUserDao {

    @Query("SELECT * FROM variant_user")
    List<VariantUser> getAll();

    @Query("SELECT * FROM variant_user WHERE user_id IN (:userIds)")
    List<VariantUser> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM variant_user WHERE username LIKE :username LIMIT 1")
    VariantUser findByName(String username);

    @Query("UPDATE variant_user SET is_connected = 0 WHERE auth_token = :authToken")
    void disconnectUserWithAuthToken(String authToken);

    @Query("SELECT * FROM variant_user WHERE is_connected = 1")
    LiveData<VariantUser> getConnected();

    @Query("UPDATE variant_user SET is_connected = 0 WHERE username != :username")
    void disconnectAllOthers(String username);

    @Query("UPDATE variant_user SET is_connected = 0 WHERE is_connected = 1")
    void disconnectConnectedUser();

    @Insert
    void insertAll(VariantUser... users);

    @Delete
    void delete(VariantUser user);

    @Update
    void update(VariantUser user);

}

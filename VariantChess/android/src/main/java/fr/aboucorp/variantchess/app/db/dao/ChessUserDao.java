package fr.aboucorp.variantchess.app.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.aboucorp.variantchess.app.db.entities.ChessUser;

@Dao
public interface ChessUserDao {

    @Query("SELECT * FROM chessuser")
    List<ChessUser> getAll();

    @Query("SELECT * FROM chessuser WHERE user_id IN (:userIds)")
    List<ChessUser> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM chessuser WHERE username LIKE :username LIMIT 1")
    ChessUser findByName(String username);

    @Query("UPDATE chessuser SET is_connected = 0 WHERE auth_token = :authToken")
    void disconnectUserWithAuthToken(String authToken);

    @Query("SELECT * FROM chessuser WHERE is_connected = 1")
    LiveData<ChessUser> getConnected();

    @Query("UPDATE chessuser SET is_connected = 0 WHERE username != :username")
    void disconnectAllOthers(String username);

    @Query("UPDATE chessuser SET is_connected = 0 WHERE is_connected = 1")
    void disconnectConnectedUser();

    @Insert
    void insertAll(ChessUser... users);

    @Delete
    void delete(ChessUser user);

    @Update
    void update(ChessUser user);

}

package fr.aboucorp.variantchess.app.db.user;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    LiveData<List<User>> getAll();

    @Query("SELECT * FROM user WHERE id IN (:userIds)")
    LiveData<List<User>> loadAllByIds(int[] userIds);

    @Query("SELECT * FROM user WHERE id = (:userId) LIMIT 1")
    LiveData<User> findById(int userId);

    @Query("SELECT * FROM user WHERE display_name LIKE :displayName LIMIT 1")
    User findByDisplayName(String displayName);

    @Insert
    void insertAll(User... users);

    @Delete
    void delete(User user);

    @Update
    void update(User user);

    @Query("DELETE FROM user")
    void deleteAll();

    @Query("SELECT * FROM user WHERE is_connected = 1 LIMIT 1")
    LiveData<User> getConnectedUser();

    @Query("SELECT * FROM user WHERE user_id = :userId LIMIT 1")
    User findByUserId(String userId);


}

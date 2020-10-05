package fr.aboucorp.variantchess.app.db.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fr.aboucorp.variantchess.app.db.entities.GameRules;

@Dao
public interface GameRulesDao {

    @Query("SELECT * FROM gamerules")
    LiveData<List<GameRules>> getAll();

    @Query("SELECT * FROM gamerules WHERE name LIKE :name LIMIT 1")
    GameRules findByName(String name);

    @Insert
    void insertAll(GameRules... gamerules);

    @Delete
    void delete(GameRules user);

    @Query("DELETE from gamerules")
    void deleteAll();

    @Update
    void update(GameRules user);
}

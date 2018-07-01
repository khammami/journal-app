package com.khammami.imerolium.data.db;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.khammami.imerolium.model.Post;
import com.khammami.imerolium.model.SyncPostTask;

import java.util.List;

@Dao
public interface SyncPostTaskDao {

    @Query("SELECT * FROM sync_post_task WHERE userId = :userId LIMIT 500")
    List<SyncPostTask> getSyncPostTasks(String userId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertSyncPostTask(SyncPostTask syncPostTask);

    @Query("DELETE FROM sync_post_task WHERE id  == :syncTaskId")
    void deleteSyncPostTask(String syncTaskId);

    @Query("DELETE FROM sync_post_task WHERE userId  == :userId")
    void deleteUserSyncPostTasks(String userId);

}

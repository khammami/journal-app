package com.khammami.imerolium.data.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.khammami.imerolium.model.Label;

import java.util.List;

@Dao
public interface LabelDao {
    @Query("SELECT * FROM labels WHERE userId = :userId")
    LiveData<List<Label>> getUserLabelList(String userId);

    @Query("SELECT * FROM labels WHERE id = :labelId")
    LiveData<Label> getLabel(int labelId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertLabel(Label label);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateLabel(Label label);

    @Query("DELETE FROM posts WHERE id  == :labelId")
    void deleteLabel(int labelId);
}

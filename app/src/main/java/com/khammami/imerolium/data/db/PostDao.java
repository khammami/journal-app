package com.khammami.imerolium.data.db;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.khammami.imerolium.model.Post;

import java.util.Date;
import java.util.List;

@Dao
public interface PostDao {

    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY createdAt DESC")
    LiveData<List<Post>> getUserPostList(String userId);

    @Query("SELECT * FROM posts WHERE userId = :userId ORDER BY createdAt DESC")
    List<Post> getPostList(String userId);

    @Query("SELECT * FROM posts WHERE id = :postId")
    LiveData<Post> getPost(String postId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertPost(Post post);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPosts(List<Post> posts);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updatePost(Post post);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updatePosts(List<Post> posts);

    @Query("DELETE FROM posts WHERE id  == :postId")
    void deletePost(String postId);
}

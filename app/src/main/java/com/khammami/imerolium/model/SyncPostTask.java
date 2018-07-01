package com.khammami.imerolium.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "sync_post_task")
public class SyncPostTask {

    public static final String DELETE_ACTION = "delete";
    public static final String UPDATE_ACTION = "update";
    public static final String SET_ACTION = "set";

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String postId;
    private String userId;
    private String action;
    private Date createdAt;

    @Ignore
    public SyncPostTask(){}

    public SyncPostTask(int id, String postId, String userId, String action, Date createdAt){
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.action = action;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

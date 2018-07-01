package com.khammami.imerolium.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
@Entity(tableName = "labels")
public class Label {

    @PrimaryKey(autoGenerate = true)
    private int id;
    private String userId;
    private String name;
    private Date createdAt;
    private Date updatedAt;

    @Ignore
    public Label(){}

    public Label(int id, String userId, String name, Date createdAt, Date updatedAt){
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Ignore
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("userId", userId);
        result.put("name", name);
        result.put("createdAt", createdAt);
        result.put("updatedAt", updatedAt);

        return result;
    }
}

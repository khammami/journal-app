package com.khammami.imerolium.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.google.firebase.firestore.IgnoreExtraProperties;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@IgnoreExtraProperties
@Entity(tableName = "posts")
public class Post {

    @PrimaryKey
    private @NonNull String id;
    private String userId;
    private String title;
    private String content;
    private Date createdAt;
    private Date updatedAt;
    private boolean pinned;
    private String backgroundColor;

    @Ignore
    public Post(){}

    @Ignore
    public Post(Post post){
        this.id = post.getId();
        this.userId = post.getUserId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.createdAt = post.getCreatedAt();
        this.updatedAt = post.getUpdatedAt();
        this.pinned = post.isPinned();
        this.backgroundColor = post.getBackgroundColor();
    }

    public Post(String id, String userId, String title, String content, Date createdAt, Date updatedAt, boolean pinned, String backgroundColor){
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.content = content;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.pinned = pinned;
        this.backgroundColor = backgroundColor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Ignore
    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("title", title);
        result.put("content", content);
        result.put("createdAt", createdAt);
        result.put("updatedAt", updatedAt);
        result.put("pinned", pinned);
        result.put("backgroundColor", backgroundColor);

        return result;
    }

    @Override
    public boolean equals(Object o) {

        if (o == this) return true;
        if (!(o instanceof Post)) {
            return false;
        }
        Post post = (Post) o;
        return post.getId().equals(id)
                && post.getUpdatedAt().equals(updatedAt);
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + id.hashCode();
        result = 31 * result + title.hashCode();
        result = 31 * result + content.hashCode();
        result = 31 * result + (pinned ? 1 : 0);
        result = 31 * result + createdAt.hashCode();
        result = 31 * result + updatedAt.hashCode();
        result = 31 * result + backgroundColor.hashCode();
        return result;
    }
}

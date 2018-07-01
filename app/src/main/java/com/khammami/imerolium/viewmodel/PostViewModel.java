package com.khammami.imerolium.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.khammami.imerolium.BasicApplication;
import com.khammami.imerolium.data.AppRepository;
import com.khammami.imerolium.model.Post;

public class PostViewModel extends ViewModel {
    private static final String TAG = PostViewModel.class.getSimpleName();
    private final LiveData<Post> mPost;
    private final AppRepository mRepository;

    public PostViewModel(@NonNull Application application, String postId) {
        mRepository = ((BasicApplication) application).getRepository();
        mPost = mRepository.getPost(postId);
    }

    public LiveData<Post> getPost() {
        return mPost;
    }

    public void createPost(Post post){
        mRepository.insertPost(post);
    }

    public void deletePost(Post post) {
        mRepository.deletePost(post);
    }

    public void updatePost(Post post) {
        mRepository.updatePost(post);
    }
}

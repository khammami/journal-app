package com.khammami.imerolium.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.format.DateUtils;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.khammami.imerolium.BasicApplication;
import com.khammami.imerolium.data.AppRepository;
import com.khammami.imerolium.model.Post;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PostListViewModel extends AndroidViewModel {
    private final static String TAG = PostListViewModel.class.getSimpleName();

    private final MediatorLiveData<List<Post>> mObservablePosts;
    private final AppRepository mRepository;

    public PostListViewModel(@NonNull Application application) {
        super(application);

        mRepository = ((BasicApplication) application).getRepository();

        mObservablePosts = new MediatorLiveData<>();
        mObservablePosts.setValue(null);

        LiveData<List<Post>> posts = mRepository.getUserPosts();

        // observe the changes of the posts
        mObservablePosts.addSource(posts, new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> postList) {
                mObservablePosts.setValue(postList);
            }
        });


        //check remote data
        LiveData<List<Post>> remotePostsData = mRepository.fetchPostListFromFirestore();
        remotePostsData.observeForever(new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable final List<Post> remotePosts) {
                mRepository.updateLocalPost(remotePosts);
            }
        });


    }

    public LiveData<List<Post>> getPostList() {
        return mObservablePosts;
    }

    public LiveData<List<Post>> fetchRemoteData(){
        return mRepository.fetchPostListFromFirestore();
    }

}

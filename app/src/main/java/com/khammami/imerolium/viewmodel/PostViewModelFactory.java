package com.khammami.imerolium.viewmodel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class PostViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private final Application application;
    private final String postId;

    public PostViewModelFactory(Application application, String postId) {
        this.application = application;
        this.postId = postId;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new PostViewModel(application, postId);
    }
}

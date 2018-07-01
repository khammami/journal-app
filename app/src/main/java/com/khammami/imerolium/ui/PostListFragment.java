package com.khammami.imerolium.ui;


import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.khammami.imerolium.R;
import com.khammami.imerolium.model.Post;
import com.khammami.imerolium.viewmodel.PostListViewModel;

import java.util.List;

public class PostListFragment extends Fragment {
    public static final String TAG = PostListFragment.class.getSimpleName();

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;
    private PostsAdapter mAdapter;
    private PostListViewModel postListModel;
    private List<Post> mPostList;


    public PostListFragment() {
        // Required empty public constructor
    }

    public static PostListFragment newInstance() {
        return new PostListFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView =  inflater.inflate(R.layout.fragment_post_list, container, false);

        mSwipeRefreshLayout = fragmentView.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.primaryColor),
                getResources().getColor(R.color.appIconRedColor),
                getResources().getColor(R.color.appIconGreenColor),
                getResources().getColor(R.color.appIconYellowColor),
                getResources().getColor(R.color.appIconBlueColor),
                getResources().getColor(R.color.secondaryColor)
        );


        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                setLoadingState(true);
                postListModel.fetchRemoteData().observe(getActivity(), new Observer<List<Post>>() {
                    @Override
                    public void onChanged(@Nullable List<Post> posts) {
                        postListModel.fetchRemoteData().removeObserver(this);
                        setLoadingState(false);
                    }
                });
            }
        });

        mRecyclerView = fragmentView.findViewById(R.id.recyclerview_posts);
        LinearLayoutManager linearLayoutManager =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,
                        false);
//        StaggeredGridLayoutManager gridLayoutManager =
//                new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);

        mRecyclerView.setLayoutManager(linearLayoutManager);

        mRecyclerView.setHasFixedSize(true);
        mAdapter = new PostsAdapter(getContext(), mProductClickCallback);
        mRecyclerView.setAdapter(mAdapter);

        setLoadingState(true);

        return fragmentView;
    }

    private void setLoadingState(boolean isLoading) {
        mSwipeRefreshLayout.setEnabled(!isLoading);
        mSwipeRefreshLayout.setRefreshing(isLoading);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        postListModel = ViewModelProviders.of(getActivity()).get(PostListViewModel.class);

        postListModel.getPostList().observe(this, new Observer<List<Post>>() {
            @Override
            public void onChanged(@Nullable List<Post> posts) {
                mAdapter.setPostList(posts);
                mPostList = posts;
                setLoadingState(false);
            }
        });

    }

    private final PostClickCallback mProductClickCallback = new PostClickCallback() {
        @Override
        public void onClick(int postIndex) {
            if (postIndex != -1 && getLifecycle().getCurrentState().isAtLeast(Lifecycle.State.STARTED)) {
                String postId = mPostList.get(postIndex).getId();
                Intent postIntent = new Intent(getContext(), PostActivity.class);
                postIntent.putExtra(PostActivity.POST_ID_KEY, postId);
                startActivity(postIntent);
            }
        }

        @Override
        public void onLongClick(int postIndex) {
            //Toast.makeText(getContext(),"hello there",Toast.LENGTH_SHORT).show();
        }
    };

}

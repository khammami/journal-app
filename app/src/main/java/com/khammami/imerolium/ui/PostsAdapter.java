package com.khammami.imerolium.ui;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.khammami.imerolium.R;
import com.khammami.imerolium.model.Post;
import com.khammami.imerolium.utilities.AppUtils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class PostsAdapter extends RecyclerView.Adapter<PostAdapterViewHolder> {
    public static final String TAG = PostsAdapter.class.getSimpleName();
    private Context mContext;
    private List<Post> mPostList;

    @Nullable
    private final PostClickCallback mPostClickCallback;

    public PostsAdapter(Context context, @Nullable PostClickCallback clickCallback){
        mContext = context;
        mPostClickCallback = clickCallback;
    }

    @NonNull
    @Override
    public PostAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View mView = LayoutInflater.from(mContext)
                .inflate(R.layout.post_list_item, viewGroup, false);
        return new PostAdapterViewHolder(mView, mPostClickCallback);
    }

    @Override
    public void onBindViewHolder(@NonNull final PostAdapterViewHolder viewHolder, int itemPosition) {
        Post post = mPostList.get(itemPosition);

        //post date
        if (!hasSameDateAsPrevious(itemPosition) && post.getCreatedAt() != null) {
            viewHolder.mPostDateLabelView.setVisibility(View.VISIBLE);
            viewHolder.mPostDateLabelView.setText(
                    AppUtils.getRelativeTime(post.getCreatedAt().getTime())
            );
        }else viewHolder.mPostDateLabelView.setVisibility(View.GONE);

        //post time
        if (post.getCreatedAt() != null) {
            viewHolder.mPostTimeLabelView.setText(
                    AppUtils.getUserFormatedTime(mContext, post.getCreatedAt().getTime())
            );
        }

        //post title
        if (post.getTitle() != null && !post.getTitle().isEmpty()) {
            viewHolder.mPostTitleView.setVisibility(View.VISIBLE);
            viewHolder.mPostTitleView.setText(post.getTitle());
        }else viewHolder.mPostTitleView.setVisibility(View.GONE);

        //post content
        if (post.getContent() != null && !post.getContent().isEmpty()) {
            viewHolder.mPostContentView.setVisibility(View.VISIBLE);
            viewHolder.mPostContentView.setText(post.getContent());
        }else viewHolder.mPostContentView.setVisibility(View.GONE);

        //set post card color
        if (post.getBackgroundColor() != null && !post.getBackgroundColor().isEmpty()) {
            viewHolder.mPostCardView.setCardBackgroundColor(Color.parseColor(
                    post.getBackgroundColor()
            ));
        }else {
            viewHolder.mPostCardView.setCardBackgroundColor(Color.WHITE);
        }

    }

    @Override
    public int getItemCount() {
        if (mPostList == null) return 0;
        else return mPostList.size();
    }

    public void setPostList(final List<Post> posts){
        if (mPostList == null) {
            mPostList = posts;
            notifyDataSetChanged();
        }else{
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mPostList.size();
                }

                @Override
                public int getNewListSize() {
                    return posts.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mPostList.get(oldItemPosition).getId().equals(
                            posts.get(newItemPosition).getId());
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Post newPost = posts.get(newItemPosition);
                    Post oldPost = mPostList.get(oldItemPosition);
                    return newPost.getId().equals(oldPost.getId())
                            && newPost.getUpdatedAt().equals(oldPost.getUpdatedAt());
                }
            });
            mPostList = posts;
            result.dispatchUpdatesTo(this);
        }
    }

    private boolean hasSameDateAsPrevious(int position){
        if (position > 0 && mPostList.get(position) != null && mPostList.get(position-1) != null) {
            Date currentDate = mPostList.get(position).getCreatedAt();
            Date previousDate = mPostList.get(position - 1).getCreatedAt();

            Calendar curCal = Calendar.getInstance();
            curCal.setTime(currentDate);

            Calendar prevCal = Calendar.getInstance();
            prevCal.setTime(previousDate);

            return curCal.get(Calendar.DATE) == prevCal.get(Calendar.DATE);
        } else return false;
    }
}

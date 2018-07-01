package com.khammami.imerolium.ui;

import android.support.annotation.NonNull;
import android.support.design.card.MaterialCardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.khammami.imerolium.R;

public class PostAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, 
        View.OnLongClickListener{
    private static final String TAG = PostAdapterViewHolder.class.getSimpleName();
    private PostClickCallback postClickCallback;

    public MaterialCardView mPostCardView;
    public TextView mPostDateLabelView;
    public TextView mPostTitleView;
    public TextView mPostContentView;
    public TextView mPostTimeLabelView;

    public PostAdapterViewHolder(@NonNull View itemView, PostClickCallback clickCallback) {
        super(itemView);
        postClickCallback = clickCallback;
        itemView.setOnClickListener(this);

        mPostCardView = itemView.findViewById(R.id.post_card_container);
        mPostDateLabelView = itemView.findViewById(R.id.post_date_label);
        mPostTitleView = itemView.findViewById(R.id.post_title);
        mPostContentView = itemView.findViewById(R.id.post_content);
        mPostTimeLabelView = itemView.findViewById(R.id.post_time_label);


    }

    @Override
    public void onClick(View view) {
        postClickCallback.onClick(getAdapterPosition());
    }

    @Override
    public boolean onLongClick(View view) {
        postClickCallback.onClick(-1);
        postClickCallback.onLongClick(getAdapterPosition());
        return false;
    }
}
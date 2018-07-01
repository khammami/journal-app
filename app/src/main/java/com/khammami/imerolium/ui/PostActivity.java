package com.khammami.imerolium.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.card.MaterialCardView;
import android.support.design.widget.BottomNavigationView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.colorpicker.ColorPickerDialog;
import com.android.colorpicker.ColorPickerSwatch;
import com.khammami.imerolium.BaseActivity;
import com.khammami.imerolium.R;
import com.khammami.imerolium.model.Post;
import com.khammami.imerolium.utilities.AppUtils;
import com.khammami.imerolium.viewmodel.PostViewModel;
import com.khammami.imerolium.viewmodel.PostViewModelFactory;

import java.util.Calendar;
import java.util.Date;

public class PostActivity extends BaseActivity implements
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
        ColorPickerSwatch.OnColorSelectedListener{
    private static final String TAG = PostActivity.class.getSimpleName();

    public static final String POST_ID_KEY = "post_id";
    public static final String POST_DEFAULT_ID = "";

    public static final String CREATE_ACTION = "create";
    public static final String EDIT_ACTION = "edit";
    public static final String DELETE_ACTION = "delete";

    private Post mPost;
    private String mPostId;
    private PostViewModel mViewModel;
    private EditText titleEditView;
    private EditText contentEditView;
    private TextView dateEditView;

    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    private String mAction;
    private Post mSavedPost;
    private ColorPickerDialog colorPickerDialog;
    private MaterialCardView cardEditView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        //set home icon in actionbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mPostId = getIntent().getStringExtra(POST_ID_KEY);
        if (mPostId == null) mAction = CREATE_ACTION;
        else mAction = EDIT_ACTION;

        initViews();

        //bottom nav for user actions
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_bar_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setCheckable(false);
                switch (menuItem.getItemId()) {
                    case R.id.date_picker:
                        //int date picker
                        initDatePicker();
                        datePickerDialog.show();
                        break;

                    case R.id.time_picker:
                        //int time picker
                        initTimePicker();
                        timePickerDialog.show();
                        break;

                    case R.id.post_bg_color:
                        colorPickerDialog.show(getSupportFragmentManager(), TAG);
                        break;

                    case R.id.delete_post:
                        if (mPostId != null) {
                            mViewModel.deletePost(mPost);
                        }
                        mAction = DELETE_ACTION;
                        finish();
                        break;
                }
                return true;
            }
        });

        PostViewModelFactory factory = AppUtils.providePostViewModelFactory(this.getApplication(), mPostId);
        mViewModel = ViewModelProviders.of(this, factory).get(PostViewModel.class);

        mViewModel.getPost().observe(this, new Observer<Post>() {
            @Override
            public void onChanged(@Nullable Post post) {
                Log.d(TAG, "onChanged: post");
                mViewModel.getPost().removeObserver(this);
                if (post == null) mPost = new Post();
                else {
                    mPost = post;
                    if (mAction.equals(EDIT_ACTION)) mSavedPost = new Post(post);

                }
                populateUi();
            }
        });

    }

    private void initViews() {
        cardEditView  = findViewById(R.id.post_edit_card_container);
        dateEditView = findViewById(R.id.post_edit_dates);
        titleEditView = findViewById(R.id.post_edit_title);
        contentEditView = findViewById(R.id.post_edit_content);

        //init color picker
        colorPickerDialog = AppUtils.getColorPickerDialog(getApplicationContext());
        colorPickerDialog.setOnColorSelectedListener(this);

        //set post title after user input
        titleEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPost.setTitle(editable.toString());
            }
        });

        //set post content after user input
        contentEditView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mPost.setContent(editable.toString());
            }
        });
    }

    private void populateUi() {
        if (mPost != null) {
            titleEditView.setText(mPost.getTitle());
            contentEditView.setText(mPost.getContent());
            if (mPost.getCreatedAt() != null){
                dateEditView.setText(getString(
                        R.string.post_edit_date_label,
                        AppUtils.getRelativeTime(mPost.getCreatedAt().getTime()),
                        AppUtils.getFriendlyDateTime(this, mPost.getCreatedAt().getTime()))
                );
            }

            //apply color to post background
            if (mPost.getBackgroundColor() != null){
                cardEditView.setCardBackgroundColor(Color.parseColor(mPost.getBackgroundColor()));
                colorPickerDialog.setSelectedColor(Color.parseColor(mPost.getBackgroundColor()));
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mPost != null) {
            if (!mAction.equals(DELETE_ACTION)) {

                //post creation
                if (mAction.equals(CREATE_ACTION)) {
                    //insert a post
                    if (!mPost.getTitle().isEmpty() || !mPost.getContent().isEmpty()) {
                        mPost.setUserId(mAuth.getUid());
                        if (mPost.getCreatedAt() == null) {
                            mPost.setCreatedAt(new Date());
                        }
                        mPost.setUpdatedAt(new Date());

                        mViewModel.createPost(mPost);
                    }
                } else {
                    //post update
                    if (isSavePostRequired(mPost, mSavedPost)) {
                        mPost.setUpdatedAt(new Date());
                        mViewModel.updatePost(mPost);
                    }
                }
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        mPost.setCreatedAt(calendar.getTime());

        populateUi();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int min) {
        Calendar calendar = Calendar.getInstance();
        if (mPost.getCreatedAt() != null){
            calendar.setTime(mPost.getCreatedAt());
        }

        calendar.set(Calendar.HOUR, hour);
        calendar.set(Calendar.MINUTE, min);
        mPost.setCreatedAt(calendar.getTime());

        populateUi();
    }

    @Override
    public void onColorSelected(int color) {
        cardEditView.setCardBackgroundColor(color);
        colorPickerDialog.setSelectedColor(color);
        mPost.setBackgroundColor(String.format("#%06X", (0xFFFFFF & color)));
    }

    private boolean isSavePostRequired(Post postA, Post postB){
        //check colors
        String colorA = postA.getBackgroundColor();
        String colorB = postB.getBackgroundColor();
        boolean isColorDifferent = (colorA!= null && colorB == null)
                || (colorA!= null && !colorA.equals(colorB));

        return !postA.getTitle().equals(postB.getTitle())
                || !postA.getContent().equals(postB.getContent())
                || postA.getCreatedAt().getTime() != postB.getCreatedAt().getTime()
                || isColorDifferent;
    }

    private void initDatePicker(){
        if (mAction.equals(CREATE_ACTION) || mPost == null) {
            datePickerDialog = AppUtils.getDatePickerDialog(this, this);
        } else {
            //on edit action
            datePickerDialog = AppUtils.getDatePickerDialog(this, this, mPost.getCreatedAt());
        }

        datePickerDialog.getDatePicker().setMaxDate(new Date().getTime());
    }

    private void initTimePicker(){
        if (mAction.equals(CREATE_ACTION) || mPost == null) {
            timePickerDialog = AppUtils.getTimePickerDialog(this, this);
        } else {
            //on edit action
            timePickerDialog = AppUtils.getTimePickerDialog(this, this, mPost.getCreatedAt());
        }
    }
}

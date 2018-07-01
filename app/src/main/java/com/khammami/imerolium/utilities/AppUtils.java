package com.khammami.imerolium.utilities;

import android.app.Application;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.text.format.DateUtils;

import com.android.colorpicker.ColorPickerDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.khammami.imerolium.R;
import com.khammami.imerolium.data.AppPreferences;
import com.khammami.imerolium.model.Post;
import com.khammami.imerolium.model.SyncPostTask;
import com.khammami.imerolium.viewmodel.PostViewModelFactory;

import java.util.Calendar;
import java.util.Date;

public class AppUtils {

    public static GoogleSignInClient getGoogleSignInClient(Context context){
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        return GoogleSignIn.getClient(context, gso);
    }

    public static PostViewModelFactory providePostViewModelFactory(Application application, String postId) {
        return new PostViewModelFactory(application, postId);
    }

    public static DatePickerDialog getDatePickerDialog(Context context,
                                                       DatePickerDialog.OnDateSetListener listener){
        return new DatePickerDialog(context, listener,
                        Calendar.getInstance().get(Calendar.YEAR),
                        Calendar.getInstance().get(Calendar.MONTH),
                        Calendar.getInstance().get(Calendar.DAY_OF_MONTH));

    }

    public static DatePickerDialog getDatePickerDialog(Context context,
                                                       DatePickerDialog.OnDateSetListener listener,
                                                       Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return new DatePickerDialog(context, listener,
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH));

    }

    public static TimePickerDialog getTimePickerDialog(Context context,
                                                        TimePickerDialog.OnTimeSetListener listener){

        return new TimePickerDialog(context, R.style.MyDialogTheme,
                listener,
                Calendar.getInstance().get(Calendar.HOUR),
                Calendar.getInstance().get(Calendar.MINUTE),
                false);

    }

    public static TimePickerDialog getTimePickerDialog(Context context,
                                                       TimePickerDialog.OnTimeSetListener listener,
                                                       Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return new TimePickerDialog(context, R.style.MyDialogTheme,
                listener,
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                false);

    }

    public static ColorPickerDialog getColorPickerDialog(Context context){

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog();
        int[] colors = context.getResources().getIntArray(R.array.post_bg_color_list);
        int selectedColor = context.getResources().getColor(R.color.white);
        colorPickerDialog.initialize(
                R.string.color_picker_label,colors , selectedColor, 5, colors.length);

        return colorPickerDialog;
    }

    public static CharSequence getRelativeTime(long time){
        return DateUtils.getRelativeTimeSpanString(
                time,
                Calendar.getInstance().getTimeInMillis(),
                DateUtils.DAY_IN_MILLIS,
                DateUtils.FORMAT_SHOW_DATE
        );
    }

    public static CharSequence getUserFormatedTime(Context context, long time){
        int flags;
        if (AppPreferences.isTimeFormat24(context)){
            flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_24HOUR;
        }else {
            flags = DateUtils.FORMAT_SHOW_TIME;
        }

        return DateUtils.formatDateTime(
                context,
                time,
                flags
        );
    }

    public static CharSequence getFriendlyDateTime(Context context, long time){
        int flags;
        if (AppPreferences.isTimeFormat24(context)){
            flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE |
                    DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY |
                    DateUtils.FORMAT_ABBREV_ALL | DateUtils.FORMAT_24HOUR;
        }else {
            flags = DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_DATE |
                    DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY |
                    DateUtils.FORMAT_ABBREV_ALL;
        }
        return DateUtils.formatDateTime(
                context,
                time,
                flags
        );
    }

    public static SyncPostTask createSyncPostTask(Post post, String action){
        SyncPostTask newSyncPostTask = new SyncPostTask();
        newSyncPostTask.setAction(action);
        newSyncPostTask.setPostId(post.getUserId());
        newSyncPostTask.setPostId(post.getId());
        newSyncPostTask.setCreatedAt(new Date());

        return newSyncPostTask;
    }
}

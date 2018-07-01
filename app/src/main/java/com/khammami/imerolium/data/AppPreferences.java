package com.khammami.imerolium.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.khammami.imerolium.R;

public class AppPreferences {
    private static final String TAG = AppPreferences.class.getSimpleName();

    public static final String AGENDA_VIEW = "agenda";
    public static final String QUILT_VIEW = "quilt";

    public static void setPostListViewType(Context context, String viewType) {
        SharedPreferences sp = getUserSharePreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(context.getString(R.string.pref_post_list_view_type_key), viewType);
        editor.apply();
    }

    public static String getPostListViewType(Context context) {
        SharedPreferences sp = getUserSharePreferences(context);
        return sp.getString(context.getString(R.string.pref_post_list_view_type_key),
                QUILT_VIEW);
    }

    private static SharedPreferences getUserSharePreferences(Context context){
        String userId = FirebaseAuth.getInstance().getUid();
        return context.getSharedPreferences(userId, Context.MODE_PRIVATE);

    }

    public static void setUserPhotoCover(Context context, String photoUrl) {
        SharedPreferences sp = getUserSharePreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(context.getString(R.string.pref_user_cover_key), photoUrl);
        editor.apply();
    }

    public static String getUserPhotoCover(Context context) {
        SharedPreferences sp = getUserSharePreferences(context);
        return sp.getString(context.getString(R.string.pref_user_cover_key), "");
    }

    public static boolean isTimeFormat24(Context context) {
        SharedPreferences sp = getUserSharePreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_time_format_24h_key), false);
    }
}

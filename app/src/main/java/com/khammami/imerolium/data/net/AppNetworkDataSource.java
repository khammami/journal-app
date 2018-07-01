package com.khammami.imerolium.data.net;

import android.content.Context;
import android.util.Log;

import com.khammami.imerolium.AppExecutors;

public class AppNetworkDataSource {
    private static final String LOG_TAG = AppNetworkDataSource.class.getSimpleName();

    private final Context mContext;
    private final AppExecutors mExecutors;

    private static final Object LOCK = new Object();
    private static AppNetworkDataSource sInstance;

    private AppNetworkDataSource(Context context, AppExecutors executors) {
        mContext = context;
        mExecutors = executors;
    }

    public static AppNetworkDataSource getInstance(Context context, AppExecutors executors) {
        Log.d(LOG_TAG, "Getting the network data source");
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppNetworkDataSource(context.getApplicationContext(), executors);
                Log.d(LOG_TAG, "Made new network data source");
            }
        }
        return sInstance;
    }
}

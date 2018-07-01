package com.khammami.imerolium;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.khammami.imerolium.data.AppRepository;
import com.khammami.imerolium.data.db.AppDatabase;
import com.khammami.imerolium.data.net.AppNetworkDataSource;

import io.fabric.sdk.android.Fabric;

public class BasicApplication extends MultiDexApplication {
    private AppExecutors mAppExecutors;

    @Override
    public void onCreate() {
        super.onCreate();

        //init fabric
        final Fabric fabric = new Fabric.Builder(this)
                .kits(new Crashlytics())
                .debuggable(true)
                .build();
        Fabric.with(fabric);

        mAppExecutors = AppExecutors.getInstance();
    }

    public AppDatabase getDatabase() {
        return AppDatabase.getInstance(this);
    }

    public AppNetworkDataSource getNetworkResouce() {
        return AppNetworkDataSource.getInstance(this, mAppExecutors);
    }

    public AppRepository getRepository() {
        return AppRepository.getInstance(getDatabase(), getNetworkResouce(), mAppExecutors);
    }
}

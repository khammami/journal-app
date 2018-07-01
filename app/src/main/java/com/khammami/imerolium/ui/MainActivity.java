package com.khammami.imerolium.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.services.people.v1.model.Person;
import com.google.firebase.auth.FirebaseUser;
import com.khammami.imerolium.BaseActivity;
import com.khammami.imerolium.R;
import com.khammami.imerolium.data.AppPreferences;
import com.khammami.imerolium.ui.settings.SettingsActivity;
import com.khammami.imerolium.ui.view.NavHeaderLayout;
import com.khammami.imerolium.utilities.AppUtils;
import com.khammami.imerolium.utilities.CircleTransform;
import com.khammami.imerolium.utilities.PeoplesApiAsync;
import com.squareup.picasso.Picasso;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private NavigationView navigationView;
    private ImageView mNavUserPictureView;
    private NavHeaderLayout mNavUserCoverView;
    private TextView mNavUserNameView;
    private TextView mNavUserEmailView;

    private SharedPreferences.OnSharedPreferenceChangeListener prefListener;
    private PeoplesApiAsync mFetchUSerCoverTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewPost();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Add post list fragment if this is first creation
        if (savedInstanceState == null) {
            swapPostListFragment();
        }

        initViews();

        initUserPhotoCover();
    }

    private void initViews() {
        View navHeaderView = navigationView.getHeaderView(0);
        mNavUserPictureView = navHeaderView.findViewById(R.id.user_profile_picture);
        mNavUserCoverView = navHeaderView.findViewById(R.id.navHeaderLayout);
        mNavUserNameView = navHeaderView.findViewById(R.id.user_display_name);
        mNavUserEmailView = navHeaderView.findViewById(R.id.user_email);
    }

    private void updateUi(FirebaseUser user) {
        if (user != null){
            mNavUserNameView.setText(user.getDisplayName());
            mNavUserEmailView.setText(user.getEmail());
            Picasso.get().load(user.getPhotoUrl()).transform(new CircleTransform())
                    .into(mNavUserPictureView);
            if (!AppPreferences.getUserPhotoCover(this).equals("")){
                Picasso.get().load(
                        AppPreferences.getUserPhotoCover(this)
                ).into(mNavUserCoverView);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        updateUi(mAuth.getCurrentUser());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mFetchUSerCoverTask != null) mFetchUSerCoverTask.cancel(true);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_logout){
            logout();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_journal) {
            swapPostListFragment();
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void logout(){
        mAuth.signOut();

        // Google sign out
        AppUtils.getGoogleSignInClient(this).signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //reset cover url to force re-fetch next sign in
                        AppPreferences.setUserPhotoCover(getApplication(), "");
                        checkAuth();
                    }
                });

        Toast.makeText(this,
                "logged out",
                Toast.LENGTH_SHORT)
                .show();
    }

    private void startNewPost(){
        Intent postIntent = new Intent(this, PostActivity.class);
        startActivity(postIntent);
    }

    private void swapPostListFragment() {
        PostListFragment postFragment = new PostListFragment();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, postFragment, PostListFragment.TAG).commit();
    }


    //TODO use a service to fetch photo cover
    @SuppressLint("StaticFieldLeak")
    private void initUserPhotoCover() {
        if(AppPreferences.getUserPhotoCover(getApplicationContext()).isEmpty()) {
            mFetchUSerCoverTask = new PeoplesApiAsync(getApplicationContext()) {
                @Override
                protected void onPostExecute(Person person) {
                    super.onPostExecute(person);
                    if (person != null && getApplication() != null) {
                        String coverUrl = person.getCoverPhotos().get(0).getUrl();
                        AppPreferences.setUserPhotoCover(getApplicationContext(), coverUrl);
                        Picasso.get().load(coverUrl)
                                .into(mNavUserCoverView);
                    }
                }
            };

            GoogleSignInAccount gAccount = GoogleSignIn.getLastSignedInAccount(this);

            if (gAccount != null) {
                mFetchUSerCoverTask.execute(gAccount.getAccount());
            }
        }
    }
}

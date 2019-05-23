package id.ac.umn.pm_tugasakhir_v2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // Google Social Login
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private GoogleSignInAccount account;

    // Action Bar
    private Toolbar toolbar;

    // Left NavMenu
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private ActionBarDrawerToggle toggle;

    // Custom Setting For 2x Back To Close App
    private boolean doubleBackToExitPressedOnce = false;

    // UI Object
    private ImageView userImg;
    private TextView userName, userEmail;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this);

        // Show Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Find UI
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        // Setting Up Navigation Menu
        toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        // Force To Open List Of Product When App Firstly Opened
        navigationView.setCheckedItem(R.id.nav_product);
        onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_product));

        // Find UI
        userImg = navigationView.getHeaderView(0).findViewById(R.id.userImg);
        userName = navigationView.getHeaderView(0).findViewById(R.id.userName);
        userEmail = navigationView.getHeaderView(0).findViewById(R.id.userEmail);

        // Find UI
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Open Add Product Activity
                Intent intent = new Intent(getApplicationContext(), AddActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {

        // When NavMenu Opened
        if (drawer.isDrawerOpen(GravityCompat.START)) {

            // Close It First
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (doubleBackToExitPressedOnce) {

            // 2x Back Button Pressed To Close App
            super.onBackPressed();
        }
        else {

            // In Other Menu Except Menu 1
            if (!navigationView.getMenu().getItem(0).isChecked()) {

                // Back To Menu 1 First
                navigationView.setCheckedItem(R.id.nav_product);
                onNavigationItemSelected(navigationView.getMenu().findItem(R.id.nav_product));
            }
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Tekan tombol BACK kembali untuk keluar", Toast.LENGTH_SHORT).show();
        Log.i("LOG", "Tombol BACK telah ditekan!");

        // Handler Back Button Checker Can Close App Within 1 Second On 2x Press
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                // Reset Condition
                doubleBackToExitPressedOnce = false;
            }
        }, 1000);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            signOut();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();

        if (id == R.id.nav_product) {

            // Show Product Fragment
            getSupportActionBar().setTitle(R.string.menu_list);
            fragmentManager.beginTransaction().replace(R.id.content_frame, new ProductFragment()).commit();
        }
        else if (id == R.id.nav_favorite) {

            // Show Product Fragment
            getSupportActionBar().setTitle(R.string.menu_favorites);
            fragmentManager.beginTransaction().replace(R.id.content_frame, new FavoriteFragment()).commit();
        }
        else if (id == R.id.nav_add) {

            // Open Add Product Activity
            Intent intent = new Intent(getApplicationContext(), AddActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_history) {

            // Open History Activity
            Intent intent = new Intent(getApplicationContext(), HistoryActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_about) {

            // Open About Activity
            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent);
        }

        // After Selected Then Close Drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void signOut() {

        // Log Out Or Change Account
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                // Logout Success Then Go Back To Login Screen
                Toast.makeText(MainActivity.this, "Logout berhasil!", Toast.LENGTH_SHORT).show();
                Log.w("LOG", "User berhasil keluar!");
                updateUI(null);
            }
        });
    }

    private void updateUI(GoogleSignInAccount account) {

        // Already logged in
        if(account != null) {

            // Load User Login Info Into Nav Menu Profile
            if(account.getPhotoUrl() != null) {
                Glide.with(this).load(account.getPhotoUrl().toString()).apply(
                        new RequestOptions().circleCropTransform().override(85, 85)
                ).into(userImg);
            }
            userName.setText(account.getDisplayName());
            userEmail.setText(account.getEmail());
        }
        else {

            // Go Back To Login Screen
            Intent intent = new Intent(getApplicationContext(), SplashScreen.class);
            startActivity(intent);
            finish();
        }
    }

}

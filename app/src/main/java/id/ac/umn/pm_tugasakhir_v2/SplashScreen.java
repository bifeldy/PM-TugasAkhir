package id.ac.umn.pm_tugasakhir_v2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SplashScreen extends AppCompatActivity {

    private Animation animation;
    private int RC_SIGN_IN = 1;

    private ImageView imgSplashScreen;
    private TextView txtSplashScreen;
    private SignInButton signInButton;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Load Animasi Fade Out HandMade Hehe
        animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);

        // Find UI
        imgSplashScreen = findViewById(R.id.imgSplashScreen);
        txtSplashScreen = findViewById(R.id.txtSplashScreen);
        signInButton = findViewById(R.id.signInButton);

        // Run Animate
        imgSplashScreen.startAnimation(animation);
        txtSplashScreen.startAnimation(animation);

        // Hide Google Button
        signInButton.setVisibility(View.GONE);
        signInButton.setSize(SignInButton.SIZE_WIDE);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        new Thread(new Runnable() {
            public void run() {

                try {

                    // Delay 2 Second Showing Logo
                    Thread.sleep(2000);

                    // Check for existing Google Sign In account, if the user is already signed in
                    // the GoogleSignInAccount will be non-null.
                    GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                    updateUI(account);
                } catch( InterruptedException e ) {

                    // Print Error
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void signIn() {

        // Open Google Account Manager
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void updateUI(GoogleSignInAccount account) {

        // Already logged in
        if(account != null) {
            Log.w("LOG", account.getDisplayName() + " :: " + account.getEmail() + " :: " + account.getId());

            // Save Login Info
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", account.getId());
            userData.put("email", account.getEmail());
            userData.put("displayName", account.getDisplayName());

            // Update User Login Data
            DatabaseReference myRef = FirebaseDatabase.getInstance().getReference("users/"+account.getId());
            myRef.updateChildren(userData);

            // Debug Info
//            String namaUser = account.getDisplayName();
//            Toast.makeText(this.getApplicationContext(), "Sign in berhasil!", Toast.LENGTH_SHORT).show();
//            Toast.makeText(this.getApplicationContext(), "Selamat datang, " + namaUser, Toast.LENGTH_SHORT).show();
//            Log.i("LOG", "Sign in berhasil!");

            // Go To Main Apps
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else {

            // Thread Google Button
            signInButton.post(new Runnable() {
                @Override
                public void run() {

                    // Show Button
                    signInButton.startAnimation(animation);
                    signInButton.setVisibility(View.VISIBLE);
                    signInButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            signIn();
                        }
                    });
                }
            });
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {

        try {

            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
        }
        catch (ApiException e) {

            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.i("LOG", "Login gagal. Code = " + e.getStatusCode());
            updateUI(null);
        }
    }

}

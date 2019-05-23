package id.ac.umn.pm_tugasakhir_v2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    // UI Object
    private EditText aboutName;
    private TextView aboutLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        // Find UI
        aboutName = findViewById(R.id.about_name);
        aboutLibrary = findViewById(R.id.about_library);

        // Change Activity Page UI Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Change UI text
        aboutLibrary.setText(
                "com.google.firebase:firebase-auth:16.2.1\n" +
                        "com.google.android.gms:play-services-auth:16.0.1\n" +
                        "\n" +
                        "com.android.support:recyclerview-v7:28.0.0\n" +
                        "com.android.support:cardview-v7:28.0.0\n" +
                        "\n" +
                        "com.github.bumptech.glide:glide:4.9.0\n" +
                        "\n" +
                        "com.google.firebase:firebase-database:16.0.4\n" +
                        "com.firebaseui:firebase-ui-database:3.2.2\n" +
                        "com.google.firebase:firebase-storage:16.0.4"
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

//        // noinspection SimplifiableIfStatement
//        if (id == R.id.action_save_product) {
//
//            // Save Button Clicked
//            uploadFile();
//            return true;
//        }

        // Back To Parent Activity
        finish();
        return super.onOptionsItemSelected(item);
    }
}

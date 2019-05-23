package id.ac.umn.pm_tugasakhir_v2;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HistoryActivity extends AppCompatActivity {

    // For List Of Product View
    private RecyclerView recyclerView;

    // LoggedIn Google Account Info
    private GoogleSignInAccount account;

    // Firebase Realtime Database
    private DatabaseReference historyRef;

    // Firebase UI For RecyclerView
    private FirebaseRecyclerOptions options;
    private FirebaseRecyclerAdapter<History, HistoryActivity.ViewHolder>  adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Change Activity Page UI Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this);

        // Realtime Database Product Path
        historyRef = FirebaseDatabase.getInstance().getReference("users/"+account.getId()+"/history");

        // Setting Up RecyclerView
        recyclerView = findViewById(R.id.history_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        FirebaseRecyclerOptions<History> options = new FirebaseRecyclerOptions.Builder<History>().setQuery(
                historyRef, History.class
        ).build();

        adapter = new FirebaseRecyclerAdapter<History, ViewHolder>(options) {

            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history, parent, false);

                return new ViewHolder(view);
            }


            @Override
            protected void onBindViewHolder(final ViewHolder holder, final int position, History model) {

                // Get Folder Path Name
                String userId = getRef(position).getKey();

                // Get Data Inside Folder
                historyRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // If Data Have This Value Then Show It
                        if(dataSnapshot.hasChild("historyImage")) {

                            // Load Images From URL
                            Glide.with(holder.historyImage.getContext()).load(dataSnapshot.child("historyImage").getValue().toString()).into(holder.historyImage);
                        }
                        if(dataSnapshot.hasChild("historyName")) {

                            // Show Name
                            holder.historyName.setText(dataSnapshot.child("historyName").getValue().toString());
                        }
                        if(dataSnapshot.hasChild("historyDeskripsi1")) {

                            // Show Stock Count
                            holder.historyDescription1.setText(dataSnapshot.child("historyDeskripsi1").getValue().toString());
                        }
                        if(dataSnapshot.hasChild("historyDeskripsi2")) {

                            // Show Category Info
                            holder.historyDescription2.setText(dataSnapshot.child("historyDeskripsi2").getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        // Failed to read value
                        Log.w("LOG", "Gagal menarik data!", databaseError.toException());
                    }
                });
            }

        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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

        // Back To Parent Activity
        finish();

        return super.onOptionsItemSelected(item);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView historyImage;
        public TextView historyName, historyDescription1, historyDescription2;

        public ViewHolder(View itemView) {
            super(itemView);
            historyImage = itemView.findViewById(R.id.history_image);
            historyName = itemView.findViewById(R.id.history_name);
            historyDescription1 = itemView.findViewById(R.id.history_deskripsi1);
            historyDescription2 = itemView.findViewById(R.id.history_deskripsi2);
        }
    }
}

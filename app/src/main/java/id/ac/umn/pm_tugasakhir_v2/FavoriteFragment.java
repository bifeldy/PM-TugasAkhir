package id.ac.umn.pm_tugasakhir_v2;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class FavoriteFragment extends Fragment {

    // For List Of Product View
    private RecyclerView recyclerView;
    private View v;

    // LoggedIn Google Account Info
    private GoogleSignInAccount account;

    // Firebase Realtime Database
    private DatabaseReference favoritesRef;

    // Firebase UI For RecyclerView
    private FirebaseRecyclerOptions options;
    private FirebaseRecyclerAdapter<Product, FavoriteFragment.FavoritesViewHolder>  adapter;

    public FavoriteFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.favorite_layout, container, false);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this.getContext());

        // Realtime Database Product Path
        favoritesRef = FirebaseDatabase.getInstance().getReference("users/"+account.getId()+"/products");

        // Setting Up RecyclerView
        recyclerView = v.findViewById(R.id.favorite_recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Database Query With Condition `WHERE favorite = 1` With Product Class Data Structure
        options = new FirebaseRecyclerOptions.Builder<Product>().setQuery(
                favoritesRef.orderByChild("productFavorite").equalTo(1),
                Product.class
        ).build();

        // RecyclerView Adapter Setting
        adapter = new FirebaseRecyclerAdapter<Product, FavoriteFragment.FavoritesViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final FavoriteFragment.FavoritesViewHolder holder, final int position, @NonNull final Product model) {

                // Get Folder Path Name
                String userId = getRef(position).getKey();

                // Get Data Inside Folder
                favoritesRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        // If Data Have This Value Then Show It
                        if(dataSnapshot.hasChild("productImage")) {

                            // Load Images From URL
                            Glide.with(holder.favImage.getContext()).load(dataSnapshot.child("productImage").getValue().toString()).into(holder.favImage);
                        }
                        if(dataSnapshot.hasChild("productName")) {

                            // Show Name
                            holder.favName.setText(dataSnapshot.child("productName").getValue().toString());
                        }
                        if(dataSnapshot.hasChild("productStock")) {

                            // Show Stock Count
                            holder.favStock.setText(dataSnapshot.child("productStock").getValue().toString() + "pcs");
                        }
                        if(dataSnapshot.hasChild("productCategory")) {

                            // Show Category Info
                            holder.favDescription.setText(dataSnapshot.child("productDescription").getValue().toString());
                        }
                        if(dataSnapshot.hasChild("productPrice")) {

                            // Create IDR Currency Money
                            DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                            DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

                            // Money Format
                            formatRp.setCurrencySymbol("Rp. ");
                            formatRp.setMonetaryDecimalSeparator(',');
                            formatRp.setGroupingSeparator('.');

                            // Show Price
                            kursIndonesia.setDecimalFormatSymbols(formatRp);
                            holder.favPrice.setText(kursIndonesia.format(dataSnapshot.child("productPrice").getValue()));
                        }

                        // Set Listener Action When Item Is Clicked
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                // Open Detail Activity And Passing ID
                                Intent intent = new Intent(getContext(), DetailActivity.class);
                                intent.putExtra("productId", Integer.parseInt(dataSnapshot.child("productId").getValue().toString()));
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        // Failed to read value
                        Log.w("LOG", "Gagal Narik Data.", databaseError.toException());
                    }
                });
            }

            @NonNull
            @Override
            public FavoriteFragment.FavoritesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.favorite, viewGroup, false);

                FavoriteFragment.FavoritesViewHolder viewHolder = new FavoriteFragment.FavoritesViewHolder(view);
                return viewHolder;
            }
        };

        // Set Auto Refresh Data
        recyclerView.setAdapter(adapter);
        adapter.startListening();
    }

    public static class FavoritesViewHolder extends RecyclerView.ViewHolder {

        // Single UI Object
        TextView favName, favDescription, favStock, favPrice;
        ImageView favImage;

        public FavoritesViewHolder(@NonNull final View itemView) {
            super(itemView);

            // Single UI Item
            favName = itemView.findViewById(R.id.fav_name);
            favImage = itemView.findViewById(R.id.fav_image);
            favStock = itemView.findViewById(R.id.fav_stock);
            favDescription = itemView.findViewById(R.id.fav_description);
            favPrice = itemView.findViewById(R.id.fav_price);

            // For Testing Clicking Item But Now Overrided By `holder.itemView`
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Log.d("LOG", "Position: " + position);
                }
            });
        }
    }

}

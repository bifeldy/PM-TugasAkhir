package id.ac.umn.pm_tugasakhir_v2;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

public class ProductFragment extends Fragment {

    // For List Of Product View
    private RecyclerView recyclerView;
    private View v;

    // LoggedIn Google Account Info
    private GoogleSignInAccount account;

    // Firebase Realtime Database
    private DatabaseReference productsRef;

    // Firebase UI For RecyclerView
    private FirebaseRecyclerOptions options;
    private FirebaseRecyclerAdapter<Product, ProductsViewHolder> adapter;

    // UI Onject
    private TextView productSearchText;
    private Button productSearchButton;

    public ProductFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.product_layout, container, false);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this.getContext());

        // Realtime Database Product Path
        productsRef = FirebaseDatabase.getInstance().getReference("users/"+account.getId()+"/products");

        // Determine Size Of Column To View As Grid
        int mNoOfColumns = Utility.calculateNoOfColumns(getContext(), 175);

        // Find UI
        productSearchText = v.findViewById(R.id.product_search_text);
        productSearchButton = v.findViewById(R.id.product_search_button);

        // Setting Up RecyclerView
        recyclerView = v.findViewById(R.id.product_recycler);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), mNoOfColumns));

        // Search Button Is Clicked
        productSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Stop Listener From Previous Realtime Data Session And Start New
                adapter.stopListening();
                loadData();
                adapter.startListening();
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Opening Session For Realtime Data
        loadData();
        adapter.startListening();
    }

    public static class ProductsViewHolder extends RecyclerView.ViewHolder {

        // Single UI Object
        TextView productName, productCategory, productStock, productPrice;
        ImageView productImage;

        public ProductsViewHolder(@NonNull final View itemView) {
            super(itemView);

            // Single UI Item
            productName = itemView.findViewById(R.id.product_name);
            productImage = itemView.findViewById(R.id.product_image);
            productStock = itemView.findViewById(R.id.product_stock);
            productCategory = itemView.findViewById(R.id.product_category);
            productPrice = itemView.findViewById(R.id.product_price);

            // For Testing Clicking Item But Now Overrided By `holder.itemView`
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Log.d("Position", "Position: " + position);
                }
            });
        }
    }

    private void loadData() {

        // Search Key
        String searchQuery = productSearchText.getText().toString();

        if(searchQuery.equals("")) {

            // Without Search
            options = new FirebaseRecyclerOptions.Builder<Product>().setQuery(
                    productsRef.orderByChild("productName"),
                    Product.class
            ).build();
        }
        else {

            // Has Search Key `WHERE productName = SEARCH`
            options = new FirebaseRecyclerOptions.Builder<Product>().setQuery(
                    productsRef.orderByChild("productName").startAt(productSearchText.getText().toString()).endAt(productSearchText.getText().toString()),
                    Product.class
            ).build();
        }

        // RecyclerView Adapter Setting
        adapter = new FirebaseRecyclerAdapter<Product, ProductsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ProductsViewHolder holder, final int position, @NonNull final Product model) {

                // Get Folder Path Name
                String userId = getRef(position).getKey();

                // Get Data Inside Folder
                productsRef.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {

                        // If Data Have This Value Then Show It
                        if(dataSnapshot.hasChild("productImage")) {

                            // Load Images From URL
                            Glide.with(holder.productImage.getContext()).load(dataSnapshot.child("productImage").getValue().toString()).into(holder.productImage);
                        }
                        if(dataSnapshot.hasChild("productName")) {

                            // Show Name
                            holder.productName.setText(dataSnapshot.child("productName").getValue().toString());
                        }
                        if(dataSnapshot.hasChild("productStock")) {

                            // Show Stock Count
                            holder.productStock.setText(dataSnapshot.child("productStock").getValue().toString() + " pcs");
                        }
                        if(dataSnapshot.hasChild("productCategory")) {

                            // Show Category Info
                            holder.productCategory.setText(dataSnapshot.child("productCategory").getValue().toString());
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
                            holder.productPrice.setText(kursIndonesia.format(dataSnapshot.child("productPrice").getValue()));
                        }

                        // Set Listener Action When Item Is Clicked
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getContext(), DetailActivity.class);
                                // Passing ID
                                intent.putExtra("productId", Integer.parseInt(dataSnapshot.child("productId").getValue().toString()));
                                startActivity(intent);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        // Failed to read value
                        Log.w("LOG", "Gagal menarik data!", databaseError.toException());
                    }
                });
            }

            @NonNull
            @Override
            public ProductsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.product, viewGroup, false);

                ProductsViewHolder viewHolder = new ProductsViewHolder(view);
                return viewHolder;
            }
        };

        // Set Adapter
        recyclerView.setAdapter(adapter);
    }
}
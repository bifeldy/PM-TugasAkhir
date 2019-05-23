package id.ac.umn.pm_tugasakhir_v2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity {

    // LoggedIn Google Account Info
    private GoogleSignInAccount account;

    // Save Passed Id From Main Fragment
    private int productId;

    // UI Object
    private ImageView productImage;
    private TextView productName, productPrice, productStock, productCategory, productDescription;
    private FloatingActionButton productFavorite;

    // Dialog Object
    private ImageView buyImage;
    private TextView buyName, buyPrice;
    private Button buyIncrease, buyDecrease;
    private TextView buyStock;

    // Product Info
    String image;
    int stock, favorited;

    // Firebase Realtime Database
    DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this);

        // Get Id From Main Fragment
        Bundle bundle = getIntent().getExtras();
        productId = bundle.getInt("productId");

        // Change Activity Page Title
        getSupportActionBar().setTitle("Katalog #" + productId);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find UI
        productImage = findViewById(R.id.product_detail_image);
        productName = findViewById(R.id.product_detail_name);
        productPrice = findViewById(R.id.product_detail_price);
        productStock = findViewById(R.id.product_detail_stock);
        productCategory = findViewById(R.id.product_detail_category);
        productDescription = findViewById(R.id.product_detail_description);
        productFavorite = findViewById(R.id.fab);

        // Write a message to the database
        myRef = FirebaseDatabase.getInstance().getReference("users/" + account.getId() + "/products/" + productId);
        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // If Data Have This Value Then Show It
                if (dataSnapshot.hasChild("productImage")) {

                    // Load Images From URL
                    image = dataSnapshot.child("productImage").getValue().toString();
                    Glide.with(getApplicationContext()).load(image).into(productImage);
                }
                if (dataSnapshot.hasChild("productName")) {

                    // Show Name
                    productName.setText(dataSnapshot.child("productName").getValue().toString());
                }
                if (dataSnapshot.hasChild("productPrice")) {

                    // Create IDR Currency Money
                    DecimalFormat kursIndonesia = (DecimalFormat) DecimalFormat.getCurrencyInstance();
                    DecimalFormatSymbols formatRp = new DecimalFormatSymbols();

                    // Money Format
                    formatRp.setCurrencySymbol("Rp. ");
                    formatRp.setMonetaryDecimalSeparator(',');
                    formatRp.setGroupingSeparator('.');

                    // Show Price
                    kursIndonesia.setDecimalFormatSymbols(formatRp);
                    productPrice.setText(kursIndonesia.format(dataSnapshot.child("productPrice").getValue()));
                }
                if (dataSnapshot.hasChild("productStock")) {

                    // Show Stock Count
                    stock = Integer.parseInt(dataSnapshot.child("productStock").getValue().toString());
                    productStock.setText("Stok tersedia: " + stock + " pcs");
                }
                if (dataSnapshot.hasChild("productCategory")) {

                    // Show Category Info
                    productCategory.setText("Kategori: " + dataSnapshot.child("productCategory").getValue().toString());
                }
                if (dataSnapshot.hasChild("productDescription")) {

                    // Show Long Description
                    productDescription.setText(dataSnapshot.child("productDescription").getValue().toString());
                }
                if (dataSnapshot.hasChild("productFavorite")) {

                    // Check If The Product Is Favorited Or Not
                    favorited = Integer.parseInt(dataSnapshot.child("productFavorite").getValue().toString());
                    if (favorited == 1) {

                        // Set Image To Favorite
                        productFavorite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite));
                    } else {

                        // Set Image To Unfavorite
                        productFavorite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_border));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // Haha .. Connection Issue Maybe
                Toast.makeText(DetailActivity.this, "Gagal mengambil data karena koneksi bermasalah!", Toast.LENGTH_SHORT).show();
                Log.e("LOG", "Gagal mengambil data karena koneksi bermasalah!", databaseError.toException());
            }
        });


        // Favorite Floating Button Clicked
        productFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // For Favorite Data
                Map<String, Object> favData = new HashMap<>();

                if(favorited == 1){

                    // Save Favorite
                    favData.put("productFavorite", 0);
                    Toast.makeText(DetailActivity.this, "Berhasil menghapus dari Produk Disukai", Toast.LENGTH_SHORT).show();
                    Log.e("LOG", "Berhasil menghapus dari Produk Disukai");
                }
                else {
                    // Save Unfavorite
                    favData.put("productFavorite", 1);
                    Toast.makeText(DetailActivity.this, "Berhasil menambahkan ke Produk Disukai", Toast.LENGTH_SHORT).show();
                    Log.e("LOG", "Berhasil menambahkan ke Produk Disukai");
                }

                // Update Favs
                myRef.updateChildren(favData);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_buy) {

            // Get Data
            DialogForm();
            return true;
        }

        // Back To Parent Activity
        finish();

        return super.onOptionsItemSelected(item);
    }

    private void DialogForm() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(DetailActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_tambahkurang, null);

        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.drawable.ic_edit);
        dialog.setTitle("Ubah Stock");

        buyImage = dialogView.findViewById(R.id.buy_image);
        buyName = dialogView.findViewById(R.id.buy_name);
        buyPrice = dialogView.findViewById(R.id.buy_price);
        buyStock = dialogView.findViewById(R.id.buy_stock);
        buyIncrease = dialogView.findViewById(R.id.buy_tambah);
        buyDecrease = dialogView.findViewById(R.id.buy_kurang);

        Glide.with(this).load(image).apply(
                new RequestOptions().override(384, 384)
        ).into(buyImage);
        buyName.setText(productName.getText().toString());
        buyPrice.setText(productPrice.getText().toString());
        buyStock.setText(Integer.toString(stock));

        buyIncrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyStock.setText(Integer.toString(Integer.parseInt(buyStock.getText().toString())+1));
            }
        });

        buyDecrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buyStock.setText(Integer.toString(Integer.parseInt(buyStock.getText().toString())-1));
            }
        });

        dialog.setPositiveButton("UBAH", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // For Updated Stock
                Map<String, Object> stockData = new HashMap<>();
                stockData.put("productStock", Integer.parseInt(buyStock.getText().toString()));

                // Update Stock
                myRef.updateChildren(stockData);

                // Create A New Database Record For New History
                DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("users/" + account.getId() + "/history");

                History history;
                history = new History();
                history.setHistoryId(historyRef.push().getKey());
                history.setHistoryImage(image);
                history.setHistoryName(productName.getText().toString());
                history.setHistoryDeskripsi1("Mengupdate jumlah stock produk ..");
                history.setHistoryDeskripsi2("Stock: " + stock + " -> " + Integer.parseInt(buyStock.getText().toString()));

                // Upload Data
                historyRef.child(historyRef.push().getKey()).setValue(history);

                dialog.dismiss();
                Toast.makeText(DetailActivity.this, "Stok telah berhasil diperbaharui!", Toast.LENGTH_SHORT).show();
                Log.e("LOG", "Stok telah berhasil diperbaharui!");
            }
        });

        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}

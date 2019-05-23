package id.ac.umn.pm_tugasakhir_v2;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddActivity extends AppCompatActivity {

    // UI Object
    private ImageView addProductImage;
    private EditText addProductName, addProductCategory, addProductDescription, addProductStock, addProductPrice;
    private ProgressBar addProgressBar;

    // For Open Gallery Or Document Browser
    private static final int PICK_IMAGE_REQUEST = 1;

    // File Path On Phone Storage
    private Uri imageUri;

    // Firebase Storage
    private StorageReference storageRef;

    // Logged In Google Account
    private GoogleSignInAccount account;

    // Firebase Realtime Database
    private DatabaseReference myRef;

    // Products Info
    int productCount = 0;
    private boolean isSaving;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        account = GoogleSignIn.getLastSignedInAccount(this);

        // Firebase Storage Directory Path To Save Uploaded File
        storageRef = FirebaseStorage.getInstance().getReference("users/" + account.getId() + "/pictures");

        // Change Activity Page UI Toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Find UI
        addProductImage = findViewById(R.id.add_image);
        addProductName = findViewById(R.id.add_product_name);
        addProductCategory = findViewById(R.id.add_product_category);
        addProductDescription = findViewById(R.id.add_product_description);
        addProductStock = findViewById(R.id.add_product_stock);
        addProgressBar = findViewById(R.id.add_progress_bar);
        addProductPrice = findViewById(R.id.add_product_price);

        // Handle If The Image Clicked
        addProductImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // When Clicked It Will Open File Browser
                openFileChooser();
            }
        });

        // Write a message to the database
        myRef = FirebaseDatabase.getInstance().getReference("users/" + account.getId() + "/products");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Check If Data Is Not Null
                if (dataSnapshot.exists()) {

                    // Get Total Product Count We Have
                    productCount = (int) dataSnapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                // Error On Communication With Realtime Database
                Toast.makeText(AddActivity.this, "Whoops~ Koneksi Bermasalah ..", Toast.LENGTH_SHORT).show();
                Log.e("LOG", "Gagal Mengambil Database.", databaseError.toException());
            }
        });

        // Upload Status
        isSaving = false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // noinspection SimplifiableIfStatement
        if (id == R.id.action_save_product) {

            // Check If Is Already Uploading
            if(isSaving) {

                // Upload In Progress
                Toast.makeText(this, "Sedang Mengunggah .. Harap Tunggu.", Toast.LENGTH_SHORT).show();
                Log.i("LOG", "Currently Uploading ..");
            } else {

                // Product Name Is Empty
                if(addProductName.getText().toString().equals("")) {
                    Toast.makeText(this, "Harap Mengisi Nama Produk.", Toast.LENGTH_SHORT).show();
                    Log.i("LOG", "No Product Name Given ..");
                }
                else if(addProductStock.getText().toString().equals("")) {
                    Toast.makeText(this, "Harap Mengisi Jumlah Stock", Toast.LENGTH_SHORT).show();
                    Log.i("LOG", "No Product Stock Given ..");
                }
                else if(addProductPrice.getText().toString().equals("")) {
                    Toast.makeText(this, "Harap Mengisi Jumlah Harga", Toast.LENGTH_SHORT).show();
                    Log.i("LOG", "No Product Price Given ..");
                }
                else {
                    uploadFile();
                }
                return true;
            }
        }

        // Back To Parent Activity
        finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // File Browser Opened And Some File Is Choosen To Upload
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            // Get Storage Path & Load It As Preview
            imageUri = data.getData();
            Glide.with(this).load(imageUri).into(addProductImage);
        }
    }


    private void openFileChooser() {

        // Open File Browser To Pick Only Images File
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri) {

        // Checking File Type Extension
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {

        // Make Sure Storage Path Not Empty
        if (imageUri != null) {

            // Change Status To Uploading
            isSaving = true;

            // Set File Name To Be Uploaded As
            final StorageReference fileRef = storageRef.child((productCount + 1) + "." + getFileExtension(imageUri));

            // Yeah Uploading
            fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Handle Like MultiThread To Show ProgressBar
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    // Change ProgressBar Value
                                    addProgressBar.setProgress(0);
                                }
                            }, 2500);

                            // After Finish Upload Trying To Get The Download URL
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {

                                    // Create A New Database Record For New Product
                                    DatabaseReference newRef = FirebaseDatabase.getInstance().getReference("users/" + account.getId() + "/products/" + (productCount+1));

                                    // Create A Data Type Structure To Save Data
                                    Product product;
                                    product = new Product();
                                    product.setProductId(productCount+1);
                                    product.setProductName(addProductName.getText().toString());
                                    product.setProductImage(uri.toString());
                                    product.setProductCategory(addProductCategory.getText().toString());
                                    product.setProductDescription(addProductDescription.getText().toString());
                                    product.setProductPrice(Integer.parseInt(addProductPrice.getText().toString()));
                                    product.setProductStock(Integer.parseInt(addProductStock.getText().toString()));
                                    product.setProductFavorite(0);

                                    // Upload Data
                                    newRef.setValue(product);

                                    // Create A New Database Record For New History
                                    DatabaseReference historyRef = FirebaseDatabase.getInstance().getReference("users/" + account.getId() + "/history");

                                    History history;
                                    history = new History();
                                    history.setHistoryId(historyRef.push().getKey());
                                    history.setHistoryImage(uri.toString());
                                    history.setHistoryName(addProductName.getText().toString());
                                    history.setHistoryDeskripsi1("Menambahkan produk baru ..");
                                    history.setHistoryDeskripsi2("Stock: 0 -> " + Integer.parseInt(addProductStock.getText().toString()));

                                    // Upload Data
                                    historyRef.child(historyRef.push().getKey()).setValue(history);

                                }
                            });

                            // Upload Complete
                            Toast.makeText(AddActivity.this, "Berhasil mengunggah gambar ^_^.", Toast.LENGTH_LONG).show();
                            Log.i("LOG", "Gambar Berhasil Di Unggah.");

                            // Change Status To Uploading
                            isSaving = false;

                            // Go Back To Parent Activity
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {

                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // This Maybe Have Internet Connection
                            Toast.makeText(AddActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("LOG", "Gagal Mengunggah Gambar ... " + e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {

                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            // When Uploading Periodically Change ProgressBar Value Based On Upload Progress
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            addProgressBar.setProgress((int) progress);
                            Log.i("LOG", "Mengunggah Gambar ... " + progress + "% Completed");
                        }
                    });
        } else {

            // No Image Are Selected To Upload
            Toast.makeText(AddActivity.this, "Belum ada gambar .. UwUu~", Toast.LENGTH_SHORT).show();
            Log.w("LOG", "Belum ada gambar .. UwUu~");
        }
    }

}

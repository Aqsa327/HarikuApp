package com.example.harikuapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.harikuapp.model.Hariku;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;

import util.HarikuUser;

public class AddHarikuActivity extends AppCompatActivity {

    // widget
    private Button saveButton;
    private ProgressBar progressBar;
    private ImageView tombolAddFoto;
    private EditText judulEditText;
    private EditText deskripsiEditText;
    private TextView userSaatiniTextView;
    private ImageView imageView;

    // userId dan username
    private String userSaatiniID;
    private String userSaatiniNama;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    // koneksi Firestore
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;

    private CollectionReference collectionReference = db.collection("Hariku");
    private Uri imageUri;

    private static final int GALLERY_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_hariku);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.postProgresBar);
        judulEditText = findViewById(R.id.postJudul);
        deskripsiEditText = findViewById(R.id.postDeskripsi);
        userSaatiniTextView = findViewById(R.id.postUsernameText);
        imageView = findViewById(R.id.postImageView);
        saveButton = findViewById(R.id.tombolSimpahHari);
        tombolAddFoto = findViewById(R.id.postTombolKamera);

        progressBar.setVisibility(View.INVISIBLE);

        if (HarikuUser.getInstance() != null) {
            userSaatiniID = HarikuUser.getInstance().getUserID();
            userSaatiniNama = HarikuUser.getInstance().getUsername();
            userSaatiniTextView.setText(userSaatiniNama);
        }

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }
            }
        };

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                simpanHariku();
            }
        });

        tombolAddFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mendapatkan image dari gallery
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });
    }

    private void simpanHariku() {
        final String judul = judulEditText.getText().toString().trim();
        final String deskripsi = deskripsiEditText.getText().toString().trim();
        progressBar.setVisibility(View.VISIBLE);

        if (!TextUtils.isEmpty(judul) && !TextUtils.isEmpty(deskripsi) && imageView != null) {
            // path tersimpan dari gambar pada storage
            // hariku image/gambar_anda.png
            final StorageReference filepath = storageReference.child("hariku_images").child("gambarku" + Timestamp.now().getNanoseconds());

            // upload gambar
            filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();

                            // membuat objek hariku
                            Hariku hariku = new Hariku();
                            hariku.setJudul(judul);
                            hariku.setDeskrips(deskripsi);
                            hariku.setImageUrl(imageUrl);
                            hariku.setTimeAdded(new Timestamp(new Date()));
                            hariku.setPengguna(userSaatiniNama);
                            hariku.setPenggunaID(userSaatiniID);

                            // invoking collection reference
                            collectionReference.add(hariku).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressBar.setVisibility(View.VISIBLE);
                                    startActivity(new Intent(AddHarikuActivity.this, HarikuListActivity.class));
                                    finish();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getApplicationContext(), "Gagal: " + e.getMessage(), Toast.LENGTH_SHORT);
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressBar.setVisibility(View.VISIBLE);
                }
            });
        } else {
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requesCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requesCode, resultCode, data);

        if (requesCode == GALLERY_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                imageUri = data.getData(); // mendapatkan path aktual
                imageView.setImageURI(imageUri); // menampilkan gambar
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        user = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuth != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}

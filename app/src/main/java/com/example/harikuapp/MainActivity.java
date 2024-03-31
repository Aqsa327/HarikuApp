package com.example.harikuapp;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.example.harikuapp.model.Hariku;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import util.HarikuUser;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {

    //widget
    Button tombolLogin;
    Button tombolBuatAkun;
    private EditText emailET;
    private EditText passET;

    // firebase authentication
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    // firebase connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tombolLogin = findViewById(R.id.buttonSignIn);
        tombolBuatAkun = findViewById(R.id.buttonBuatAkun);
        emailET = findViewById(R.id.email);
        passET = findViewById(R.id.password);

        // Inisialisasi Auth reference
        firebaseAuth = FirebaseAuth.getInstance();

        tombolBuatAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, BuatAkunActivity.class);
                startActivity(i);
            }
        });

        tombolLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginEmailPasswordUser(
                        emailET.getText().toString().trim(),
                        passET.getText().toString().trim()
                );
            }
        });
    }

    private void LoginEmailPasswordUser(String email, String pwd) {
        // memeriksa untuk text kosong
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd)) {
            firebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    assert user != null;
                    final String userSaatiniId = user.getUid();
                    collectionReference.whereEqualTo("userId", userSaatiniId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {

                            }
                            assert value != null;
                            if (!value.isEmpty()) {
                                // mendapatkan semua queryDocSnapshot
                                for (QueryDocumentSnapshot snapshot : value) {
                                    HarikuUser harikuUser = HarikuUser.getInstance();
                                    harikuUser.setUsername(snapshot.getString("username"));
                                    harikuUser.setUserID(snapshot.getString("userId"));
                                    // menuju listActivity setelah berhasil login
                                    // startActivity(new Intent(MainActivity.this, AddHarikuActivity.class));
                                    startActivity(new Intent(MainActivity.this, HarikuListActivity.class));
                                }
                            }
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //jika gagal
                    Toast.makeText(MainActivity.this, "Ada sesuatu yang salah" + e, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(MainActivity.this, "Masukkan Email & Password Anda", Toast.LENGTH_SHORT).show();
        }
    }
}
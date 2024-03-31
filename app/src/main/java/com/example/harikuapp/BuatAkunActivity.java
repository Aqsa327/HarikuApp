package com.example.harikuapp;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.HarikuUser;

public class BuatAkunActivity extends AppCompatActivity {

    EditText buatPassword;
    EditText buatEmail;
    EditText buatUsername;
    Button tombolBuatAkun;

    // Firebase Auth
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser userSaatini;

    // koneksi Firebase
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_buat_akun);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        firebaseAuth = FirebaseAuth.getInstance();

        tombolBuatAkun = findViewById(R.id.buttonBuatAkunEmail);
        buatPassword = findViewById(R.id.password2);
        buatEmail = findViewById(R.id.email2);
        buatUsername = findViewById(R.id.username2);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                userSaatini = firebaseAuth.getCurrentUser();
                if (userSaatini != null) {
                    //User telah log in
                } else {
                    // Belum menjadi user
                }
            }
        };

        tombolBuatAkun.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(buatEmail.getText().toString())&&!TextUtils.isEmpty(buatPassword.getText().toString())) {
                    String email = buatEmail.getText().toString().trim();
                    String password = buatPassword.getText().toString().trim();
                    String username = buatUsername.getText().toString().trim();
                    CreateUserEmailAccount(email, password, username);
                } else {
                    Toast.makeText(BuatAkunActivity.this, "Kosong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void CreateUserEmailAccount(String email, String password, final String username) {
        if (!TextUtils.isEmpty(buatEmail.getText().toString()) && !TextUtils.isEmpty(buatPassword.getText().toString())) {
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        //Membawa user ke activity berikutnya : AddharikuActivity
                        FirebaseUser userSaatini = mAuth.getCurrentUser();
                        assert userSaatini != null;
                        final String userSaatiniId = userSaatini.getUid();
                        //String username = buatEmail.getText().toString();

                        //buat userMap sehingga kita dapat membuat user dalam user collection
                        Map<String, String> userObj = new HashMap<>();
                        userObj.put("userId", userSaatiniId);
                        userObj.put("username", username);

                        //Menambahkan pengguna ke Firestore
                        collectionReference.add(userObj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (Objects.requireNonNull(task.getResult()).exists()) {
                                            String nama = task.getResult().getString("username");
                                            //jika pengguna sukses diregistrasi, maka akan ditampilkan AddharikuActivity
                                            // mendapatkan global hariku user
                                            HarikuUser harikuUser = HarikuUser.getInstance();
                                            harikuUser.setUserID(userSaatiniId);
                                            harikuUser.setUsername(nama);

                                            Intent i = new Intent(BuatAkunActivity.this, AddHarikuActivity.class);
                                            i.putExtra("username", nama);
                                            i.putExtra("userId", userSaatiniId);
                                            startActivity(i);
                                        }else {

                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // pesan toast untuk informasi gagal registrasi
                                        Toast.makeText(BuatAkunActivity.this, "Ada sesuatu yang salah!",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                }
            });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        userSaatini = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
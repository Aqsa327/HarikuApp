package com.example.harikuapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import com.example.harikuapp.model.Hariku;
import com.example.harikuapp.ui.HarikuRecycleAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import util.HarikuUser;

public class HarikuListActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private StorageReference storageReference;
    private List<Hariku> harikuList;
    private RecyclerView recyclerView;
    private HarikuRecycleAdapter harikuRecycleAdapter;

    private CollectionReference collectionReference = db.collection("Hariku");
    private TextView noPostEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hariku_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();

        // widgets
        noPostEntry = findViewById(R.id.ListNoPost);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // post array list
        harikuList = new ArrayList<>();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    //tambah menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
        //return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Menggunakan if-else statement untuk menentukan aksi yang sesuai dengan ID
        int id = item.getItemId();
        if (id == R.id.action_add) {
            // Menuju ke AddHarikuActivity
            if (user != null && firebaseAuth != null) {
                startActivity(new Intent(HarikuListActivity.this, AddHarikuActivity.class));
            }
            return true;
        } else if (id == R.id.action_signout) {
            // Sign out
            if (user != null && firebaseAuth != null) {
                firebaseAuth.signOut();
                startActivity(new Intent(HarikuListActivity.this, MainActivity.class));
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // mendapatkan semua post
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        collectionReference.whereEqualTo("penggunaID", HarikuUser.getInstance().getUserID()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot harikus : queryDocumentSnapshots) {
                        Hariku hariku = harikus.toObject(Hariku.class);
                        harikuList.add(hariku);
                    }

                    // RecycleView

                    // Buat adapter untuk recyclerview
                    harikuRecycleAdapter = new HarikuRecycleAdapter(HarikuListActivity.this, harikuList);
                    recyclerView.setAdapter(harikuRecycleAdapter);
                    harikuRecycleAdapter.notifyDataSetChanged();
                } else {
                    noPostEntry.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // kesalahan apapun
                Toast.makeText(HarikuListActivity.this, "Ups! ada yang salah!", Toast.LENGTH_SHORT);
            }
        });
    }
}
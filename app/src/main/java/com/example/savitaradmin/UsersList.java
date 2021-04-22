package com.example.savitaradmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsersList extends AppCompatActivity {
    public static final String TAG = "UsersList";
    FirebaseAuth fAuth;
    FirebaseUser user;
    FirebaseFirestore db;
    String userId;
    String cond;
    UserListAdapter userListAdapter;
    private List<AuthorizedUser> usersList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_list);
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        cond = getIntent().getStringExtra("condominium");

        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();
        Toolbar myToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolBar);

        fillExampleList();

        FloatingActionButton addVisitor = findViewById(R.id.add_user);
        addVisitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getBaseContext(), CreateUser.class);
                i.putExtra("condominium" , cond);
                startActivity(i);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.search_menu,menu);
        MenuItem menuItem=menu.findItem(R.id.search_questions);
        SearchView searchView=(SearchView)menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                userListAdapter.getFilter().filter(s);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId== android.R.id.home) {
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillExampleList() {
        Log.d(TAG, "fillExampleList started");
        db.collection("authorizedUsers")
                .whereArrayContains("condominiums", cond)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG , "AuthorizedUser info " +  document.get("email"));
                                AuthorizedUser authUser = document.toObject(AuthorizedUser.class);
                                authUser.setId(document.getId());
                                usersList.add(authUser);
                            }
                            RecyclerView recyclerView = findViewById(R.id.recycler_view);
                            recyclerView.setHasFixedSize(true);
                            userListAdapter = new UserListAdapter(usersList);
                            recyclerView.setLayoutManager(new LinearLayoutManager(UsersList.this));
                            recyclerView.setAdapter(userListAdapter);
                            userListAdapter.setOnItemClickListener(new UserListAdapter.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    AuthorizedUser authUser = usersList.get(position);
                                    String id = authUser.getId();
                                    Log.d(TAG, "Clicked on visitor " + authUser.toString());
                                    Intent intent = new Intent(getBaseContext(), EditUser.class);
                                    intent.putExtra("condominium", cond);
                                    intent.putExtra("userId", id);
                                    intent.putExtra("authUser", authUser);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
    @Override
    protected void onStart() {
        super.onStart();
        //adapter.startListening();
    }


    @Override
    protected void onStop() {
        super.onStop();
        //adapter.stopListening();
    }
    @Override
    public void onBackPressed(){
        finish();
        Intent i = new Intent(getBaseContext(), AdminProfile.class);
        i.putExtra("condominium" , cond);
        startActivity(i);
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

}
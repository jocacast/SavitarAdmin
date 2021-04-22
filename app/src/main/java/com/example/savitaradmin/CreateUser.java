package com.example.savitaradmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class CreateUser extends AppCompatActivity implements View.OnClickListener  {
    public static final String TAG = "CreateUser";
    FirebaseAuth fAuth;
    String userId;
    FirebaseUser user;
    FirebaseFirestore db;
    Button saveBtn;
    String cond;
    EditText userEmailAddress;
    TextView condTextView;
    SwitchCompat isGuardSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        //Intent extra info
        cond = getIntent().getStringExtra("condominium");
        Log.d(TAG, "Cond " + cond);

        //Firebase Info
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        //Rest of items
        saveBtn = findViewById(R.id.saveUser);
        userEmailAddress = findViewById(R.id.userEmail);
        condTextView = findViewById(R.id.condName);
        isGuardSwitch = findViewById(R.id.isGuard);
        condTextView.setText(cond);

        Toolbar myToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolBar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        saveBtn.setOnClickListener(this);
    }
    @Override
    public void onClick(View v){
        int viewId = v.getId();
        if(viewId == R.id.saveUser){
            saveUser();
        }
    }

    private void saveUser() {
        String inputEmail  = userEmailAddress.getText().toString();
        Boolean switchState = isGuardSwitch.isChecked();
        List<String> condList = new ArrayList<>();
        condList.add(cond);
        //String name, String email, List<String> condominiums.
        AuthorizedUser authUser = new AuthorizedUser(inputEmail, condList, switchState);
        db.collection("authorizedUsers").whereEqualTo("email", inputEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.getResult().isEmpty()){
                    Log.d(TAG, "User does not exist, create new one");
                    //Create brand new AuthorizedUser
                    db.collection("authorizedUsers")
                            .add(authUser)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                    Toast.makeText(CreateUser.this, "User successfully created", Toast.LENGTH_SHORT).show();
                                    saveBtn.setEnabled(true);
                                    finishActivity();
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.w(TAG, "Error adding document", e);
                                Toast.makeText(CreateUser.this, "Error while saving visitor" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                saveBtn.setEnabled(true);
                            });
                }else{
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        //Update auth user condominiums info
                        Log.d(TAG, "User exists, update condominiums");
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        db.collection("authorizedUsers").document(document.getId()).update("condominiums", FieldValue.arrayUnion(cond));
                        finishActivity();
                    }
                }
            }
        });
    }

    private void finishActivity(){
        finish();
        Intent i = new Intent(getApplicationContext(), UsersList.class);
        i.putExtra("condominium", cond);
        startActivity(i);
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }
}
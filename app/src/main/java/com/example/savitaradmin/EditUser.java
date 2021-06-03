package com.example.savitaradmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.List;

public class EditUser extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = "EditUser";
    FirebaseAuth fAuth;
    String userId, documentId, cond, userName;
    FirebaseUser user;
    FirebaseFirestore db;
    Button saveBtn, deleteBtn;
    TextView userEmailTextView;
    EditText userAddress;
    AuthorizedUser authorizedUser;
    SwitchCompat isGuardSwitch, isEnabledSwitch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        //Intent info
        authorizedUser = (AuthorizedUser)getIntent().getSerializableExtra("authUser");
        documentId = getIntent().getStringExtra("userId");
        cond = getIntent().getStringExtra("condominium");
        //Firebase info
        fAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();
        //Rest of items
        userEmailTextView = findViewById(R.id.userEmail);
        saveBtn = findViewById(R.id.saveBtn);
        deleteBtn = findViewById(R.id.deleteBtn);
        isGuardSwitch = findViewById(R.id.isGuard);
        isEnabledSwitch = findViewById(R.id.isEnabled);
        userAddress = findViewById(R.id.userAddress);

        Toolbar myToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolBar);

        /*if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }*/
        setStaticInformation();
        saveBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
    }

    private void setStaticInformation() {
        Log.d(TAG, "AuthUser getEmail " + authorizedUser.getEmail());
        userEmailTextView.setText(authorizedUser.getEmail());
        isGuardSwitch.setChecked(authorizedUser.isGuard());
        isEnabledSwitch.setChecked(authorizedUser.isEnabled());
        userAddress.setText(authorizedUser.getAddress());
    }

    @Override
    public void onClick(View v){
        int viewId = v.getId();
        if(viewId == R.id.saveBtn){
            saveVisitorInfo();
        }else if(viewId == R.id.deleteBtn){
            deleteVisitorV2();
        }
    }

    private void saveVisitorInfo(){
        db.collection("authorizedUsers").document(documentId)
                .update("guard", isGuardSwitch.isChecked(),"enabled", isEnabledSwitch.isChecked(), "address", userAddress.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditUser.this, "User successfully updated", Toast.LENGTH_SHORT).show();
                        saveBtn.setEnabled(true);
                        finishEditActivity();
                    }
                });
    }

    private void deleteVisitorV2() {
        Log.d(TAG, "Delete Button selected");
        final TextView deleteVisitor = new TextView(EditUser.this);
        final AlertDialog.Builder deleteVisitorAlertDialog = new AlertDialog.Builder(EditUser.this);
        deleteVisitorAlertDialog.setTitle("Delete User " + authorizedUser.getEmail()+"?");
        deleteVisitorAlertDialog.setView(deleteVisitor);
        deleteVisitorAlertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Yes clicked");
                db.collection("authorizedUsers").document(documentId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                finishEditActivity();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditUser.this, "Delete Function Failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        deleteVisitorAlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "No clicked");
            }
        });
        deleteVisitorAlertDialog.create().show();
    }

    /*private void deleteVisitor(){
        final TextView deleteVisitor = new TextView(EditUser.this);
        final AlertDialog.Builder deleteVisitorAlertDialog = new AlertDialog.Builder(EditUser.this);
        deleteVisitorAlertDialog.setTitle("Delete User " + authorizedUser.getEmail()+"?");
        deleteVisitorAlertDialog.setView(deleteVisitor);
        /*
        * If user has more than one condominium >> Update user by removing from current condominium FieldValue.arrayRemove("east_coast"
        * else delete user
        *
        deleteVisitorAlertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "Yes clicked");
                DocumentReference docRef = db.collection("authorizedUsers").document(documentId);
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                List<String> conds = (List<String>)document.get("condominiums");
                                Log.d(TAG, "Conds size " + conds.size());
                                if (conds.size()>1){
                                    Log.d(TAG, "Update user by removing from current condominium");
                                    db.collection("authorizedUsers").document(documentId)
                                            .update("condominiums", FieldValue.arrayRemove(cond))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(EditUser.this, "User condominiums successfully updated", Toast.LENGTH_SHORT).show();
                                                    saveBtn.setEnabled(true);
                                                    finishEditActivity();
                                                }
                                            });
                                }else{
                                    Log.d(TAG, "Delete user");
                                    db.collection("authorizedUsers").document(documentId)
                                            .delete()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                                    finishEditActivity();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(EditUser.this, "Delete Function Failed.", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            } else {
                                Log.d(TAG, "No such document");
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });
            }
        });

        deleteVisitorAlertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.d(TAG, "No clicked");
            }
        });
        deleteVisitorAlertDialog.create().show();
    }*/

    private void finishEditActivity(){
        finish();
        Intent i = new Intent(getBaseContext(), UsersList.class);
        i.putExtra("condominium" , cond);
        startActivity(i);
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

}
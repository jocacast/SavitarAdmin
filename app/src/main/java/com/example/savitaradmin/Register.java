package com.example.savitaradmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Register extends AppCompatActivity  implements View.OnClickListener  {
    public static final String TAG = "Register";
    EditText mFullName,mEmail,mPassword,mPhone, mAddress;
    Button mRegisterBtn;
    TextView mLoginBtn;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    FirebaseFirestore fStore;
    String userID;
    List<String>adminCondominiums;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mFullName   = findViewById(R.id.fullName);
        mEmail      = findViewById(R.id.Email);
        mPassword   = findViewById(R.id.password);
        mRegisterBtn= findViewById(R.id.registerBtn);
        mLoginBtn   = findViewById(R.id.createText);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        progressBar = findViewById(R.id.progressBar);

        mRegisterBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View v){
        int viewId = v.getId();
        if(viewId == R.id.registerBtn){
            registerUser();
        }else if(viewId == R.id.loginBtn){
            login();
        }
    }

    private void registerUser(){
        final String email = mEmail.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        final String fullName = mFullName.getText().toString();

        if(TextUtils.isEmpty(email)){
            mEmail.setError("Email is Required.");
            return;
        }

        if(TextUtils.isEmpty(fullName)){
            mEmail.setError("Full name is Required.");
            return;
        }
        if(TextUtils.isEmpty(password)){
            mPassword.setError("Password is Required.");
            return;
        }

        if(password.length() < 6){
            mPassword.setError("Password Must be >= 6 Characters");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        fStore.collection("authorizedAdmins")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().isEmpty()){
                            Log.d(TAG, "Admin not authorized");
                            final TextView invalidAdmin = new TextView(Register.this);
                            final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(Register.this);
                            passwordResetDialog.setTitle("Invalid email address");
                            passwordResetDialog.setMessage("The provided email address is not yet registered as an admin. Contact IT").setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                        }
                                    });;
                            passwordResetDialog.setView(invalidAdmin);
                            passwordResetDialog.create().show();
                            return;
                        }else{
                            /*for (QueryDocumentSnapshot document : task.getResult()) {
                                adminCondominiums = (List<String>)document.get("condominiums");
                                Log.d(TAG, "Condominiums "+  adminCondominiums);
                            }*/

                            fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        // send verification link
                                        FirebaseUser fuser = fAuth.getCurrentUser();
                                        fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(Register.this, "Verification Email has been Sent.", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                                            }
                                        });

                                        Toast.makeText(Register.this, "Admin Created", Toast.LENGTH_SHORT).show();
                                        userID = fAuth.getCurrentUser().getUid();
                                        DocumentReference documentReference = fStore.collection("admins").document(userID);
                                        Map<String,Object> admin = new HashMap<>();
                                        admin.put("fName",fullName);
                                        admin.put("email",email);
                                        //admin.put("condominiums",  adminCondominiums);
                                        documentReference.set(admin).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.d(TAG, "onFailure: " + e.toString());
                                            }
                                        });
                                        Intent i = new Intent(Register.this, ConfirmEmail.class);
                                        i.putExtra("tittleMessage", "We are almost there!!!");
                                        i.putExtra("emailAddress" , fAuth.getCurrentUser().getEmail());
                                        startActivity(i);
                                    }else {
                                        Toast.makeText(Register.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        }
                    }
                });

        /*
        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // send verification link
                    FirebaseUser fuser = fAuth.getCurrentUser();
                    fuser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(Register.this, "Verification Email has been Sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: Email not sent " + e.getMessage());
                        }
                    });

                    Toast.makeText(Register.this, "Admin Created", Toast.LENGTH_SHORT).show();
                    userID = fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = fStore.collection("admin").document(userID);
                    Map<String,Object> admin = new HashMap<>();
                    admin.put("fName",fullName);
                    admin.put("email",email);
                    admin.put("condominiums", Arrays.asList(adminCondominiums));
                    documentReference.set(admin).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: user Profile is created for "+ userID);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.toString());
                        }
                    });
                    Intent i = new Intent(Register.this, ConfirmEmail.class);
                    i.putExtra("tittleMessage", "We are almost there!!!");
                    i.putExtra("emailAddress" , fAuth.getCurrentUser().getEmail());
                    startActivity(i);
                }else {
                    Toast.makeText(Register.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });*/

    }

    private List validateAuthAdminV2(String email) {
        final List<String>[] condominiums = new List[]{new ArrayList<>()};
        Log.d(TAG, "Inside validateAuthAdminV2");
        fStore.collection("authorizedAdmins")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                condominiums[0] = (List<String>) document.get("condominiums");
                                Log.d(TAG, "Condominiums "+  condominiums[0]);
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }

                });
        progressBar.setVisibility(View.GONE);
        Log.d(TAG, "validateAuthAdminV2 result " + condominiums[0].toString());
        return condominiums[0];
    }


    private boolean validateAuthAdmin(String email) {
        final boolean[] isValid = new boolean[1];
        Log.d(TAG, "Inside validateAuthAdmin");
        fStore.collection("authorizedAdmins")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.getResult().isEmpty()){
                            Log.d(TAG, "Admin not authorized");
                            isValid[0] = false;
                        }else{
                            Log.d(TAG, "Admin authorized");
                            isValid[0] = true;
                        }
                    }
                });
        progressBar.setVisibility(View.GONE);
        return isValid[0];
    }

    private void login(){
        startActivity(new Intent(getApplicationContext(),Login.class));
    }


}
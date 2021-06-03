package com.example.savitaradmin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class AdminProfile extends AppCompatActivity implements View.OnClickListener {
    public static final String TAG = "AdminProfile ";
    TextView fullName,email,condominium;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userId;
    FirebaseUser user;
    String cond;
    Button usersList, changePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        cond = getIntent().getStringExtra("condominium");

        //Firebase Info
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userId = fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        //Rest of the elements
        fullName = findViewById(R.id.userFullName);
        email    = findViewById(R.id.email);
        condominium = findViewById(R.id.adminCondominium);
        usersList = findViewById(R.id.usersList);
        changePassword = findViewById(R.id.changePass);


        Toolbar myToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolBar);

        usersList.setOnClickListener(this);
        changePassword.setOnClickListener(this);

        DocumentReference documentReference = fStore.collection("admins").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    String dsFName = documentSnapshot.getString("fName");
                    String dsEmail = documentSnapshot.getString("email");
                    condominium.setText(cond);
                    fullName.setText(dsFName);
                    email.setText(dsEmail);
                }else {
                    Log.d("tag", "onEvent: Document do not exists");
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(itemId == R.id.logout){
            logout();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v){
        int viewId = v.getId();
        if (viewId== R.id.usersList){
            Log.d(TAG, "Show Users List");
            Intent i = new Intent(getBaseContext(), UsersList.class);
            i.putExtra("condominium", cond);
            startActivity(i);
            //startActivity(intent);
        }else if (viewId == R.id.changePass){
            changePass();
        }
    }

    private void showVisitors() {
        //startActivity(new Intent(getApplicationContext(),VisitorsList.class));
    }

    public void editProfile(){
        Log.d(TAG, "Show edit profile activity");
    }

    public void changePass(){
        final EditText resetPassword = new EditText(AdminProfile.this);

        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(AdminProfile.this);
        passwordResetDialog.setTitle("Reset Password?");
        passwordResetDialog.setMessage("Enter New Password > 6 Characters long.");
        passwordResetDialog.setView(resetPassword);

        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // extract the email and send reset link
                String newPassword = resetPassword.getText().toString();
                user.updatePassword(newPassword).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(AdminProfile.this, "Password Reset Successfully.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AdminProfile.this, "Password Reset Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // close
            }
        });

        passwordResetDialog.create().show();
    }

    public void logout(){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(AdminProfile.this, MainActivity.class);
        startActivity(i);
        overridePendingTransition(R.anim.left_to_right,R.anim.right_to_left);
    }

}

package com.nabase1.atechconsultation;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nabase1.atechconsultation.databinding.ActivityMainBinding;
import com.nabase1.atechconsultation.databinding.ToolbarBinding;
import com.nabase1.atechconsultation.models.User;
import com.nabase1.atechconsultation.utils.FirebaseUtils;

import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;
    private ToolbarBinding mToolbarBinding;
    private Toolbar mToolbar;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        mToolbar = mToolbarBinding.mtoolbar;
       setSupportActionBar(mToolbar);

        FirebaseUtils.openFirebaseUtils(getString(R.string.db_node_user), this);
        mFirebaseDatabase = FirebaseUtils.firebaseDatabase;
        mDatabaseReference = FirebaseUtils.databaseReference;

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUtils.attachListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseUtils.detachListener();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
          getMenuInflater().inflate(R.menu.menu_items, menu);
          
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.item_chat:
                Toast.makeText(this, "Chat", Toast.LENGTH_SHORT).show();
                break;

            case R.id.item_settings:
                   Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                break;

            case R.id.item_logout:
                 Toast.makeText(this, "Log Out", Toast.LENGTH_SHORT).show();

                 break;
                
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == Constants.MY_CODE && resultCode == RESULT_OK){


            //send verification email
            sendVerificationEmail();

            mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            if(mFirebaseUser != null){
                User user = new User();
                user.setName(mFirebaseUser.getDisplayName());
                user.setPhone(mFirebaseUser.getPhoneNumber());
                user.setProfile_image(mFirebaseUser.getPhotoUrl().toString());
                user.setSecurity_level("1");
                user.setUser_id(mFirebaseUser.getUid());

                mDatabaseReference.child(mFirebaseUser.getUid()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(MainActivity.this, "Please Confirm Your Email!", Toast.LENGTH_SHORT).show();
                        FirebaseUtils.attachListener();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(MainActivity.this, "Something Went Wrong!", Toast.LENGTH_SHORT).show();
                        FirebaseUtils.attachListener();
                    }
                });
            }
        }
    }


    /**
     * sends an email verification link to the user
     */
    private void sendVerificationEmail() {
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (mFirebaseUser != null) {
            mFirebaseUser.sendEmailVerification()
                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if (task.isSuccessful()) {
                             Toast.makeText(MainActivity.this, "Sent Verification Email", Toast.LENGTH_SHORT).show();
                         }
                         else{
                             Toast.makeText(MainActivity.this, "Couldn't Send  Verification Email", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
        }

    }
}
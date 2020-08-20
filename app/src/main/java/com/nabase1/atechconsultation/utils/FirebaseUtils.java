package com.nabase1.atechconsultation.utils;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nabase1.atechconsultation.Constants;
import java.util.Arrays;
import java.util.List;


public class FirebaseUtils {

    public static FirebaseDatabase firebaseDatabase;
    public  static DatabaseReference databaseReference;
    private static FirebaseUtils firebaseUtils;
    public static FirebaseStorage firebaseStorage;
    public static StorageReference storageReference;
    private  static FirebaseAuth firebaseAuth;
    private static FirebaseAuth.AuthStateListener sAuthStateListener;
    private static Activity caller;

    private FirebaseUtils(){}

    public static void openFirebaseUtils(String ref, Activity callerActivity){
        if(firebaseUtils == null){
            firebaseUtils = new FirebaseUtils();
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();
            caller = callerActivity;

            sAuthStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if(firebaseAuth.getCurrentUser() == null){
                        firebaseUtils.signIn();
                    }
                }
            };

            connectStorage();
        }
        databaseReference = firebaseDatabase.getReference().child(ref);

    }

    public static void connectStorage(){
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
    }

    public void signIn(){
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.AnonymousBuilder().build());



// Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                Constants.MY_CODE);
    }

    public static void attachListener(){
        firebaseAuth.addAuthStateListener(sAuthStateListener);
    }

    public static void detachListener(){
        firebaseAuth.removeAuthStateListener(sAuthStateListener);
    }



}

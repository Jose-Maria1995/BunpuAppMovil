package com.example.bunpuoficial.providers;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class AuthProvider {

    private FirebaseAuth mAuth;

    public AuthProvider(){

        mAuth=FirebaseAuth.getInstance();
    }
    public void delete(){
        mAuth.getCurrentUser().delete();
    }

    public Task<AuthResult> register(String email , String password){
        return mAuth.createUserWithEmailAndPassword(email,password);
    }

    public Task<AuthResult> login(String email, String contraseña){
        return mAuth.signInWithEmailAndPassword(email,contraseña);
    }

    public Task<AuthResult> googleLogin(String idToken){
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken,null);
        return mAuth.signInWithCredential(credential);
    }

    public FirebaseUser getUserSesion() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser();
        }else{
            return null;
        }
    }

    public String getUid() {
        if (mAuth.getCurrentUser() != null) {
            return mAuth.getCurrentUser().getUid();
        }else{
            return null;
        }
    }
    public String getEmail() {
        if (mAuth.getCurrentUser()!=null){
            return mAuth.getCurrentUser().getEmail();
        }else{
            return null;
        }
    }

    public void logout()
    {
        if(mAuth!=null)
        {
            mAuth.signOut();
        }
    }
}

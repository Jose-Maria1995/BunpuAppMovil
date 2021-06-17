package com.example.bunpuoficial.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bunpuoficial.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.MenuItem;

import com.example.bunpuoficial.fragment.Chat_Fragment;
import com.example.bunpuoficial.fragment.FilterFragment;
import com.example.bunpuoficial.fragment.HomeFragment;
import com.example.bunpuoficial.fragment.ProfileFragment;
import com.example.bunpuoficial.providers.AuthProvider;
import com.example.bunpuoficial.providers.TokenProvider;
import com.example.bunpuoficial.providers.UsersProvider;
import com.example.bunpuoficial.utils.ViewedMessageHelper;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigation;

    TokenProvider mTokenProvider;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        mTokenProvider = new TokenProvider();
        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();
        openFragment(new HomeFragment());
        createToken();
    }

    @Override
    protected void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true,HomeActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,HomeActivity.this);
    }




    @Override
    public void onBackPressed() {
        BottomNavigationView mBottomNavigationView = findViewById(R.id.bottom_navigation);
        if (mBottomNavigationView.getSelectedItemId() == R.id.item_home) {
            super.onBackPressed();
            finish();
        }else {
            mBottomNavigationView.setSelectedItemId(R.id.item_home);
        }
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    if(item.getItemId()==R.id.item_home){
                        openFragment(new HomeFragment());
                    }else if(item.getItemId()==R.id.item_chat){
                        openFragment(new Chat_Fragment());
                    }else if(item.getItemId()==R.id.item_filters){
                        openFragment(new FilterFragment());
                    }else if(item.getItemId()==R.id.item_profile){
                        openFragment(new ProfileFragment());
                    }
                    return true;
                }
            };

    private void createToken(){
        mTokenProvider.create(mAuthProvider.getUid());
    }
}
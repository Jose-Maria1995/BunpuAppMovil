package com.example.bunpuoficial.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.bunpuoficial.R;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.View;

import com.example.bunpuoficial.fragment.ProductoFragment;
import com.example.bunpuoficial.utils.ViewedMessageHelper;

import de.hdodenhof.circleimageview.CircleImageView;

public class PostActivity extends AppCompatActivity {

    CircleImageView mCircleImageBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        mCircleImageBack=findViewById(R.id.circulo);
        openFragment(new ProductoFragment());

        mCircleImageBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.post_tipo, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    public void onStart() {
        super.onStart();
        ViewedMessageHelper.updateOnline(true,PostActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,PostActivity.this);
    }
}
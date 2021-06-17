package com.example.bunpuoficial.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.adapters.ProductAdapter;
import com.example.bunpuoficial.models.Product;
import com.example.bunpuoficial.providers.AuthProvider;
import com.example.bunpuoficial.providers.ProductProvider;
import com.example.bunpuoficial.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class FiltersActivity extends AppCompatActivity {
    
    String mExtraCategory,color;

    AuthProvider mAuthProvider;
    RecyclerView mRecyclerView;
    ProductProvider mProductProvider;
    ProductAdapter mProductAdapter;

    TextView mTextViewNumberfilter;

    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filters);
        mRecyclerView=findViewById(R.id.recycleViewFilter);
        mTextViewNumberfilter=findViewById(R.id.textViewNumberFilter);
        mExtraCategory=getIntent().getStringExtra("category");
        color=getIntent().getStringExtra("color");
        mToolbar=findViewById(R.id.toolbar);
        mToolbar.setBackgroundColor(Color.parseColor(color));

        if(mExtraCategory.equals("Panaderia"))
        {
            mToolbar.setTitleTextColor(Color.BLACK);
        }
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(mExtraCategory.toUpperCase());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView.setLayoutManager(new GridLayoutManager( FiltersActivity.this,1));

        mAuthProvider=new AuthProvider();
        mProductProvider=new ProductProvider();
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query= mProductProvider.getProductByCategoryAndTimestamp(mExtraCategory);
        FirestoreRecyclerOptions<Product> options=new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();
        mProductAdapter=new ProductAdapter(options,FiltersActivity.this, mTextViewNumberfilter);
        mRecyclerView.setAdapter(mProductAdapter);
        mProductAdapter.startListening();
        ViewedMessageHelper.updateOnline(true,FiltersActivity.this);
    }

    @Override
    public void onStop() {
        super.onStop();
        mProductAdapter.stopListening();
    }
    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,FiltersActivity.this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()== android.R.id.home)
        {
            finish();
        }
        return true;
    }
}
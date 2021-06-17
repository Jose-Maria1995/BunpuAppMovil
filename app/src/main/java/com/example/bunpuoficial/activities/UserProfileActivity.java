package com.example.bunpuoficial.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.adapters.MyProductAdapter;
import com.example.bunpuoficial.models.Product;
import com.example.bunpuoficial.providers.AuthProvider;
import com.example.bunpuoficial.providers.ProductProvider;
import com.example.bunpuoficial.providers.UsersProvider;
import com.example.bunpuoficial.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserProfileActivity extends AppCompatActivity {

    LinearLayout mLinearLayoutEditProfile;
    TextView mTextViewUsername;
    TextView mTextViewTelefono;
    TextView mTextViewEmail;
    TextView mTextViewProductNumber;
    TextView mTextViewProductExist;
    ImageView mImageViewCover;
    CircleImageView mCircleImageProfile;
    FloatingActionButton mFloatingActionButton;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    ProductProvider mProductProvider;

    String mExtraIdUser;

    RecyclerView mRecycleView;
    Toolbar mToolbar;
    ListenerRegistration mListenerRegistration;

    MyProductAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        mLinearLayoutEditProfile=findViewById(R.id.linearLaoutEditProfile);
        mFloatingActionButton = findViewById(R.id.fabchat);
        mTextViewEmail=findViewById(R.id.textViewEmail);
        mTextViewProductNumber=findViewById(R.id.textViewProductNumber);
        mTextViewTelefono=findViewById(R.id.textViewTelefono);
        mTextViewUsername=findViewById(R.id.textViewUsername);
        mTextViewProductExist=findViewById(R.id.textViewProductExist);
        mImageViewCover=findViewById(R.id.imageViewCover);
        mCircleImageProfile=findViewById(R.id.circleImageProfile);
        mToolbar=findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersProvider=new UsersProvider();
        mAuthProvider=new AuthProvider();
        mProductProvider=new ProductProvider();

        mExtraIdUser=getIntent().getStringExtra("idUser");

        mRecycleView=findViewById(R.id.recycleProfile);

        mRecycleView.setLayoutManager(new GridLayoutManager(UserProfileActivity.this,3));

        if(mAuthProvider.getUid().equals(mExtraIdUser)){
            mFloatingActionButton.setVisibility(View.GONE);
        }

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToChatActivity();
            }
        });

        getUser();
        getProductNumber();
        checkIfExistProduct();


    }

    private void goToChatActivity() {
        Intent intent = new Intent(UserProfileActivity.this,ChatActivity.class);
        intent.putExtra("idUser1",mAuthProvider.getUid());
        intent.putExtra("idUser2",mExtraIdUser);
        startActivity(intent);

    }

    @Override
    public void onStart() {
        super.onStart();

        Query query= mProductProvider.getProductByUserProfile(mExtraIdUser);
        FirestoreRecyclerOptions<Product> options=new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();
        mAdapter=new MyProductAdapter(options,UserProfileActivity.this);
        mRecycleView.setAdapter(mAdapter);
        mAdapter.startListening();
        ViewedMessageHelper.updateOnline(true,UserProfileActivity.this);
    }
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,UserProfileActivity.this);
    }


    @Override
    public void onStop() {
        super.onStop();
        mAdapter.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListenerRegistration!=null){
            mListenerRegistration.remove();
        }
    }

    private void checkIfExistProduct() {

        mListenerRegistration=mProductProvider.getProductByUser(mExtraIdUser).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error==null)
                {
                    if(value!=null) {
                        int numberProduct = value.size();
                        if (numberProduct > 0) {
                            mTextViewProductExist.setText("Publicaciones");
                            mTextViewProductExist.setTextColor(Color.RED);
                        } else {
                            mTextViewProductExist.setText("No hay publicaciones");
                            mTextViewProductExist.setTextColor(Color.GRAY);
                        }
                    }
                }
            }
        });

    }

    private void getProductNumber()
    {
        mProductProvider.getProductByUser(mExtraIdUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberProduct= queryDocumentSnapshots.size();
                mTextViewProductNumber.setText(String.valueOf(numberProduct));
            }
        });
    }

    private void getUser()
    {
        mUsersProvider.getUser(mExtraIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    if(documentSnapshot.contains("email"))
                    {
                        String email=documentSnapshot.getString("email");
                        mTextViewEmail.setText(email);
                    }
                    if(documentSnapshot.contains("telefono"))
                    {
                        String telefono=documentSnapshot.getString("telefono");
                        mTextViewTelefono.setText(telefono);
                    }
                    if(documentSnapshot.contains("username"))
                    {
                        String username=documentSnapshot.getString("username");
                        mTextViewUsername.setText(username);
                    }
                    if(documentSnapshot.contains("image_profile"))
                    {
                        String imageProfile=documentSnapshot.getString("image_profile");
                        if(imageProfile!=null)
                        {
                            if(!imageProfile.isEmpty())
                            {
                                Picasso.get().load(imageProfile).into(mCircleImageProfile);
                            }
                        }

                    }

                    if(documentSnapshot.contains("image_cover"))
                    {
                        String imageCover=documentSnapshot.getString("image_cover");
                        if(imageCover!=null)
                        {
                            if(!imageCover.isEmpty())
                            {
                                Picasso.get().load(imageCover).into(mImageViewCover);
                            }
                        }

                    }
                }
            }
        });
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
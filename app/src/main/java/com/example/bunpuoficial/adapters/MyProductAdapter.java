package com.example.bunpuoficial.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.activities.ProductDetailActivity;
import com.example.bunpuoficial.models.Like;
import com.example.bunpuoficial.models.Product;
import com.example.bunpuoficial.providers.AuthProvider;
import com.example.bunpuoficial.providers.LikesProvider;
import com.example.bunpuoficial.providers.ProductProvider;
import com.example.bunpuoficial.providers.UsersProvider;
import com.example.bunpuoficial.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyProductAdapter extends FirestoreRecyclerAdapter <Product, MyProductAdapter.ViewHolder>
{
    Context context;
    UsersProvider mUsersProvider;
    LikesProvider mLikesProvider;
    AuthProvider mAuthProvider;
    ProductProvider mProductProvider;

    public MyProductAdapter(FirestoreRecyclerOptions<Product> options, Context context)
    {
        super(options);
        this.context=context;
        mUsersProvider = new UsersProvider();
        mLikesProvider=new LikesProvider();
        mAuthProvider=new AuthProvider();
        mProductProvider=new ProductProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull Product product) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String productId= document.getId();
        String userId = document.getString("idUser");

        if(product.getImage1()!=null)
        {
            if(!product.getImage1().isEmpty())
            {
                Picasso.get().load(product.getImage1()).into(holder.imagenProducto);
            }
        }

        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ProductDetailActivity.class);
                intent.putExtra("id",productId);
                context.startActivity(intent);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_my_product, parent,false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        ImageView imagenProducto;

        View viewHolder;

        public ViewHolder (View view)
        {
            super(view);
            imagenProducto= view.findViewById(R.id.imageViewProduct1);

            viewHolder=view;
        }



    }
}

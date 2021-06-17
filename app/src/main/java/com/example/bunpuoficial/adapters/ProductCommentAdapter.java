package com.example.bunpuoficial.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.activities.ProductDetailActivity;
import com.example.bunpuoficial.models.Comment;
import com.example.bunpuoficial.models.Product;
import com.example.bunpuoficial.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductCommentAdapter extends FirestoreRecyclerAdapter <Comment, ProductCommentAdapter.ViewHolder>
{
    Context context;
    UsersProvider miUsersProvider;

    public ProductCommentAdapter(FirestoreRecyclerOptions<Comment> options, Context context)
    {
        super(options);
        this.context=context;
        miUsersProvider = new UsersProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Comment comment) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String comentId= document.getId();
        String idUser = document.getString("idUser");
        holder.textViewComentario.setText(comment.getComment());
        getUserInfo(idUser,holder);


    }

    private  void getUserInfo(String idUser,ViewHolder holder){
        miUsersProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        String username = documentSnapshot.getString("username");
                        holder.textViewUsername.setText(username);
                    }if(documentSnapshot.contains("image_profile")){
                        String image_profile = documentSnapshot.getString("image_profile");
                        if(image_profile!=null){
                            if(!image_profile.isEmpty()){
                                Picasso.get().load(image_profile).into(holder.circleImageComent);
                            }
                        }
                    }
                }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_product_comment, parent,false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView circleImageComent;
        TextView textViewComentario;
        TextView textViewUsername;
        View viewHolder;

        public ViewHolder (View view)
        {
            super(view);
            circleImageComent= view.findViewById(R.id.circleimage);
            textViewComentario= view.findViewById(R.id.textViewProductComments);
            textViewUsername= view.findViewById(R.id.textViewProductCommentUsername);
            viewHolder=view;
        }



    }
}

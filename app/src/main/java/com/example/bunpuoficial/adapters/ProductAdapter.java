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
import com.example.bunpuoficial.activities.OrderActivity;
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
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProductAdapter extends FirestoreRecyclerAdapter <Product, ProductAdapter.ViewHolder>
{
    Context context;
    UsersProvider mUsersProvider;
    LikesProvider mLikesProvider;
    AuthProvider mAuthProvider;
    ProductProvider mProductProvider;
    TextView mTextViewNumberFilter;
    ListenerRegistration mListenerRegistration;

    public ProductAdapter(FirestoreRecyclerOptions<Product> options,Context context)
    {
        super(options);
        this.context=context;
        mUsersProvider = new UsersProvider();
        mLikesProvider=new LikesProvider();
        mAuthProvider=new AuthProvider();
        mProductProvider=new ProductProvider();
    }

    public ProductAdapter(FirestoreRecyclerOptions<Product> options,Context context, TextView textView)
    {
        super(options);
        this.context=context;
        mUsersProvider = new UsersProvider();
        mLikesProvider=new LikesProvider();
        mAuthProvider=new AuthProvider();
        mProductProvider=new ProductProvider();
        mTextViewNumberFilter= textView;
    }

    @Override
    protected void onBindViewHolder(@NonNull final ViewHolder holder, int position, @NonNull Product product) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String productId= document.getId();


        if(mTextViewNumberFilter!=null)
        {
            int numberFilter=getSnapshots().size();
            mTextViewNumberFilter.setText(String.valueOf(numberFilter));
        }

        String userId = document.getString("idUser");

        holder.nombreEmpresa.setText(product.getNombreEmpresa());//para modificar las publicaciones
        holder.nombreProducto.setText(product.getProductName());//para modificar las publicaciones


        if(product.getImage1()!=null)
        {
            if(!product.getImage1().isEmpty())
            {
                Picasso.get().load(product.getImage1()).into(holder.imagenProducto);
            }
        }

        mUsersProvider.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.contains("image_profile"))
                {
                    String imagepProfile=documentSnapshot.getString("image_profile");
                    Picasso.get().load(imagepProfile).into(holder.imagenPerfil);
                }
            }
        });

        mProductProvider.getProductByid(productId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.contains("timestamp"))
                {
                    Long timestamp=documentSnapshot.getLong("timestamp");
                    String relativeTime= RelativeTime.getTimeAgo(timestamp, context);
                    holder.horas.setText(relativeTime);
                }
            }
        });


        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(context, ProductDetailActivity.class);
                intent.putExtra("id",productId);
                context.startActivity(intent);
            }
        });

        holder.estrella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Aqui", Toast.LENGTH_SHORT).show();
                Like like=new Like();
                like.setIdUser(mAuthProvider.getUid());
                like.setIdProduct(productId);
                like.setTimestamp(new Date().getTime());
                like(like, holder);
                Toast.makeText(context, "Aqui2", Toast.LENGTH_SHORT).show();
            }
        });




        getUserInfo(product.getIdUser(),holder);
        getNumberLikesByProduct(productId,holder);
        checkIfExistLike(productId,mAuthProvider.getUid(),holder);
    }

    private void getNumberLikesByProduct(String idProduct,final ViewHolder holder)
    {
        mListenerRegistration=mLikesProvider.getLikesByProduct(idProduct).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if(error==null)
                {
                    int numeroLikes= value.size();
                    holder.textViewLike.setText(numeroLikes +" Me interesa");
                }
                else
                {

                }
            }
        });

    }

    private void like(final Like like, final ViewHolder holder) {
        mLikesProvider.getLikeByProductAndUser(like.getIdProduct(),mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments=queryDocumentSnapshots.size();

                if(numberDocuments>0)
                {
                    String idLike=queryDocumentSnapshots.getDocuments().get(0).getId();
                    holder.estrella.setImageResource(R.drawable.ic_estrella);
                    mLikesProvider.delete(idLike);
                }
                else
                {
                    holder.estrella.setImageResource(R.drawable.ic_estrella_dorada);
                    mLikesProvider.create(like);
                }
            }
        });
    }

    private void checkIfExistLike(String idProduct, String idUser, final ViewHolder holder) {
        mLikesProvider.getLikeByProductAndUser(idProduct,idUser).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberDocuments=queryDocumentSnapshots.size();

                if(numberDocuments>0)
                {
                    holder.estrella.setImageResource(R.drawable.ic_estrella_dorada);
                }
                else
                {
                    holder.estrella.setImageResource(R.drawable.ic_estrella);
                }
            }
        });
    }

    private void getUserInfo(String idUser, ViewHolder holder) {

        mUsersProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
             if(documentSnapshot.exists())
             {
                 if(documentSnapshot.contains("username"))
                 {
                    String username=documentSnapshot.getString("username");
                    holder.nombreEmpresa.setText(username.toUpperCase());
                 }
             }
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_product, parent,false);
        return new ViewHolder(view);
    }

    public ListenerRegistration getmListenerRegistration(){
        return mListenerRegistration;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView imagenPerfil;
        ImageView imagenProducto;
        ImageView estrella;
        ImageView comentario;
        ImageView enlaceChat;
        TextView nombreEmpresa;
        TextView nombreProducto;
        TextView horas;
        TextView textViewLike;
        Button seguir;
        Button comprar;

        View viewHolder;

        public ViewHolder (View view)
        {
            super(view);
            imagenPerfil= view.findViewById(R.id.productImagenPerfil);
            imagenProducto= view.findViewById(R.id.productImagenProducto);
            estrella= view.findViewById(R.id.imageViewLike);
            nombreEmpresa= view.findViewById(R.id.productNombreEmpresa);
            nombreProducto= view.findViewById(R.id.productNombreProducto);
            horas= view.findViewById(R.id.productHoras);
            textViewLike=view.findViewById(R.id.textViewLikes);

            viewHolder=view;
        }
    }
}

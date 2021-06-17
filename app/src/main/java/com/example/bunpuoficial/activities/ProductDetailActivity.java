package com.example.bunpuoficial.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.adapters.ProductCommentAdapter;
import com.example.bunpuoficial.adapters.SliderAdapter;
import com.example.bunpuoficial.models.Comment;
import com.example.bunpuoficial.models.FCMBody;
import com.example.bunpuoficial.models.FCMResponse;
import com.example.bunpuoficial.models.SliderItem;
import com.example.bunpuoficial.providers.AuthProvider;
import com.example.bunpuoficial.providers.ComentProductProvider;
import com.example.bunpuoficial.providers.LikesProvider;
import com.example.bunpuoficial.providers.NotificationProvider;
import com.example.bunpuoficial.providers.ProductProvider;
import com.example.bunpuoficial.providers.TokenProvider;
import com.example.bunpuoficial.providers.UsersProvider;
import com.example.bunpuoficial.utils.RelativeTime;
import com.example.bunpuoficial.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailActivity extends AppCompatActivity {

    SliderView mSliderView;
    SliderAdapter mSliderAdapter;
    List<SliderItem> mSliderItems =new ArrayList<>();
    ProductProvider mProductProvider;
    UsersProvider mUsersProvider;
    ComentProductProvider miComentProductProvider;
    AuthProvider miAuthProvider;
    LikesProvider mLikesProvider;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

    ProductCommentAdapter miProductCommentAdapter;
    ListenerRegistration mListenerRegistration;

    String mExtraProductId;

    TextView mTextViewProductName;
    TextView mTextViewDescription;
    TextView mTextViewUsername;
    TextView mTextViewTelefono;
    TextView mTextViewPrice;
    TextView mTextViewNameCategory;
    TextView mTextViewRelativeTime;
    TextView mTextViewLikes;
    CircleImageView mCircleImageViewProfile;
    Button mButtonShowProfile,mButtonBuy;
    View linea;
    RecyclerView miReciclerView;

    Toolbar mToolbar;

    FloatingActionButton miFabcomemt;//boton de cometario
    FloatingActionButton mButtonDeleteProduct;//boton de cometario

    String mIdUser="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        mSliderView=findViewById(R.id.imageSlider);
        mTextViewProductName=findViewById(R.id.textViewProductName);
        mTextViewDescription=findViewById(R.id.textViewDescription);
        mTextViewUsername=findViewById(R.id.textViewUsername);
        mTextViewTelefono=findViewById(R.id.textViewTelefono);
        mTextViewPrice=findViewById(R.id.textViewPrice);
        mTextViewNameCategory=findViewById(R.id.textViewNameCategory);
        mTextViewRelativeTime=findViewById(R.id.textViewRelativeTime);
        mTextViewLikes=findViewById(R.id.textViewLikes);
        mCircleImageViewProfile=findViewById(R.id.circleImageProfile);
        mButtonShowProfile=findViewById(R.id.btnShowProfile);
        miFabcomemt = findViewById(R.id.fabComment);
        mButtonDeleteProduct = findViewById(R.id.deleteProduct);
        mButtonBuy=findViewById(R.id.boton_relalizar_pedido);
        miReciclerView = findViewById(R.id.recyclerViewProductComent);
        mToolbar = findViewById(R.id.toolbar);
        linea = findViewById(R.id.barra_ecima_boton_reaizar);


        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(ProductDetailActivity.this);
        miReciclerView.setLayoutManager(linearLayoutManager);

        miComentProductProvider = new ComentProductProvider();
        miAuthProvider= new AuthProvider();
        mProductProvider=new ProductProvider();
        mUsersProvider=new UsersProvider();
        mLikesProvider=new LikesProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();

        mExtraProductId=getIntent().getStringExtra("id");

        mUsersProvider.getTypeUser(miAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    if(documentSnapshot.contains("typeUser"))
                    {
                        String typeUser=documentSnapshot.getString("typeUser");
                        if(typeUser!=null)
                        {
                            if(typeUser.equalsIgnoreCase("Normal"))
                            {
                                mButtonBuy.setVisibility(View.VISIBLE);
                                mButtonBuy.setVisibility(View.VISIBLE);

                            }
                            else
                            {
                                mButtonBuy.setVisibility(View.GONE);
                                linea.setVisibility(View.GONE);
                            }
                        }
                    }
                }
            }
        });

        mButtonBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductDetailActivity.this, OrderActivity.class);
                if(mExtraProductId!=null&&(!mExtraProductId.isEmpty())) {
                    intent.putExtra("id", mExtraProductId);
                    startActivity(intent);
                }
            }
        });

        miFabcomemt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogComment();
            }
        });

        mButtonDeleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmDelete(mExtraProductId);
                //deleteProduct(mExtraProductId);
            }
        });

        mButtonShowProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToShowProfile();
            }
        });

        getProduct();
        getNumberLikes();
    }

    private void showConfirmDelete(String mExtraProductId) {
        new AlertDialog.Builder(ProductDetailActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Eliminar publicación")
                .setMessage("Estas seguro que deseas borrar este producto")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProduct(mExtraProductId);
                        finish();
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

    private void deleteProduct(String productId) {
        mProductProvider.delete(productId).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
             if(task.isSuccessful())
             {
                 Toast.makeText(ProductDetailActivity.this, "El producto se elimino correctamente", Toast.LENGTH_SHORT).show();
             }
             else
             {
                 Toast.makeText(ProductDetailActivity.this, "NO se pudo eliminar el producto", Toast.LENGTH_SHORT).show();
             }
            }
        });
    }

    private void getNumberLikes() {
        mListenerRegistration=mLikesProvider.getLikesByProduct(mExtraProductId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if(error==null)
                {
                    if(queryDocumentSnapshots!=null){
                        int numberLikes= queryDocumentSnapshots.size();

                        if(numberLikes==1)
                        {
                            mTextViewLikes.setText(numberLikes+ " Me gusta");
                        }
                        else
                        {
                            mTextViewLikes.setText(numberLikes+ " Me gustas");
                        }
                    }
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query= miComentProductProvider.getCommentsByProduct(mExtraProductId);
        FirestoreRecyclerOptions<Comment> options=new FirestoreRecyclerOptions.Builder<Comment>().setQuery(query, Comment.class).build();
        miProductCommentAdapter=new ProductCommentAdapter(options,ProductDetailActivity.this);
        miReciclerView.setAdapter(miProductCommentAdapter);
        miProductCommentAdapter.startListening();
        ViewedMessageHelper.updateOnline(true,ProductDetailActivity.this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,ProductDetailActivity.this);
    }


    @Override
    protected void onStop() {
        super.onStop();
        miProductCommentAdapter.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListenerRegistration!=null){
            mListenerRegistration.remove();
        }
    }

    private void showDialogComment() {

        AlertDialog.Builder alert = new AlertDialog.Builder(ProductDetailActivity.this);
        alert.setTitle("Comentario");
        alert.setMessage("Ingresa tu comentario");

        EditText editText = new EditText(ProductDetailActivity.this);
        editText.setHint("Texto");


        LinearLayout.LayoutParams params =new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT
                ,LinearLayout.LayoutParams.WRAP_CONTENT

        );
        params.setMargins(36,0,36,30);
        editText.setLayoutParams(params);
        RelativeLayout container = new RelativeLayout(ProductDetailActivity.this);
        RelativeLayout.LayoutParams relativeParam = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        container.setLayoutParams(relativeParam);
        container.addView(editText);
        alert.setView(container);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String valor = editText.getText().toString();
                if(!valor.isEmpty()){
                    createComment(valor);
                }else{
                    Toast.makeText(ProductDetailActivity.this,"Debes de ingresar a un comentario",Toast.LENGTH_LONG).show();
                }

            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        alert.show();
    }

    private void createComment(String valor) {
        Comment comment =new Comment();
        comment.setComment(valor);
        comment.setIdPost(mExtraProductId);
        comment.setIdUser(miAuthProvider.getUid());
        comment.setTimeStamp(new Date().getTime());
        miComentProductProvider.create(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    sendNotification(valor);
                    Toast.makeText(ProductDetailActivity.this,"El comentario se creo correctamente",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ProductDetailActivity.this,"El comentario no se creo correctamente",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void sendNotification(String comment){
        if(mIdUser==null){
            return;
        }
        mTokenProvider.getToken(mIdUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");
                        Map<String,String> data = new HashMap<>();
                        data.put("title","Nuevo comentario");
                        data.put("body",comment);
                        FCMBody body = new FCMBody(token,"high","4500s",data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if(response.body()!=null){
                                    if(response.body().getSuccess()==1){
                                        Toast.makeText(ProductDetailActivity.this, "La notificacion se envio correctamente", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(ProductDetailActivity.this, "La notificacion no se envio ", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(ProductDetailActivity.this, "La notificacion no se envio ", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    }
                }
                else{
                    Toast.makeText(ProductDetailActivity.this, "El token del ususario no existe ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void goToShowProfile() {

        if(!mIdUser.equals(""))
        {
            Intent intent=new Intent(ProductDetailActivity.this, UserProfileActivity.class);
            intent.putExtra("idUser",mIdUser);
            startActivity(intent);
        }
        else
        {
            Toast.makeText(this, "El id del usuario todavia no se carga", Toast.LENGTH_SHORT).show();
        }


    }

    private void intanceSlider()
    {
        mSliderAdapter=new SliderAdapter(ProductDetailActivity.this, mSliderItems);
        mSliderView.setSliderAdapter(mSliderAdapter);
        mSliderView.setIndicatorAnimation(IndicatorAnimationType.THIN_WORM);
        mSliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        mSliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_RIGHT);
        mSliderView.setIndicatorSelectedColor(Color.WHITE);
        mSliderView.setIndicatorUnselectedColor(Color.GRAY);
        mSliderView.setScrollTimeInSec(3);
        mSliderView.setAutoCycle(true);
        mSliderView.startAutoCycle();
    }

    private void getProduct()
    {
        mProductProvider.getProductByid(mExtraProductId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

             if(documentSnapshot.exists())
             {
                 if(documentSnapshot.contains("image1"))
                 {
                     String image1=documentSnapshot.getString("image1");
                     if(image1!=null)
                     {
                         SliderItem item=new SliderItem();
                         item.setImageUrl(image1);
                         mSliderItems.add(item);
                     }
                 }

                 if(documentSnapshot.contains("image2"))
                 {
                     String image2 = documentSnapshot.getString("image2");
                     if(image2!=null)
                     {
                         SliderItem item = new SliderItem();
                         item.setImageUrl(image2);
                         mSliderItems.add(item);
                     }
                 }

                 if(documentSnapshot.contains("image3"))
                 {
                     String image3=documentSnapshot.getString("image3");
                     if(image3!=null)
                     {
                         SliderItem item=new SliderItem();
                         item.setImageUrl(image3);
                         mSliderItems.add(item);
                     }
                 }

                 if(documentSnapshot.contains("image4"))
                 {
                     String image4=documentSnapshot.getString("image4");
                     if(image4!=null) {
                         SliderItem item = new SliderItem();
                         item.setImageUrl(image4);
                         mSliderItems.add(item);
                     }
                 }

                 if(documentSnapshot.contains("productName"))
                 {
                     String productName=documentSnapshot.getString("productName");
                     if(productName!=null)
                     {
                         mTextViewProductName.setText(productName.toUpperCase());
                     }
                 }

                 if(documentSnapshot.contains("description"))
                 {
                     String description=documentSnapshot.getString("description");
                     if(description!=null)
                     {
                         mTextViewDescription.setText(description);
                     }
                 }

                 if(documentSnapshot.contains("category"))
                 {
                     String category=documentSnapshot.getString("category");
                     if(category!=null)
                     {
                         mTextViewNameCategory.setText(category);
                     }
                 }

                 if(documentSnapshot.contains("price"))
                 {
                     String price=documentSnapshot.getString("price");
                     if(price!=null)
                     {
                         mTextViewPrice.setText(price);
                     }
                 }

                 if(documentSnapshot.contains("idUser"))
                 {
                     mIdUser=documentSnapshot.getString("idUser");
                     getUserInfo(mIdUser);
                     if(mIdUser.equals(miAuthProvider.getUid()))
                     {
                         mButtonDeleteProduct.setVisibility(View.VISIBLE);
                     }
                     else
                     {
                         mButtonDeleteProduct.setVisibility(View.GONE);
                     }
                     if(mIdUser!=null)
                     {
                         mTextViewUsername.setText(mIdUser);
                     }
                 }

                 if(documentSnapshot.contains("timestamp"))
                 {
                     Long timestamp=documentSnapshot.getLong("timestamp");
                     String relativeTime= RelativeTime.getTimeAgo(timestamp, ProductDetailActivity.this);
                     mTextViewRelativeTime.setText(relativeTime);
                 }

                 intanceSlider();
             }
            }
        });
    }

    private void getUserInfo(String idUser) {
        mUsersProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    if(documentSnapshot.contains("username"))
                    {
                        String username=documentSnapshot.getString("username");
                        mTextViewUsername.setText(username);
                    }

                    if(documentSnapshot.contains("telefono"))
                    {
                        String telefono=documentSnapshot.getString("telefono");
                        mTextViewTelefono.setText(telefono);
                    }

                    if(documentSnapshot.contains("image_profile"))
                    {
                        String imagepProfile=documentSnapshot.getString("image_profile");
                        Picasso.get().load(imagepProfile).into(mCircleImageViewProfile);
                    }


                }
            }
        });
    }
}
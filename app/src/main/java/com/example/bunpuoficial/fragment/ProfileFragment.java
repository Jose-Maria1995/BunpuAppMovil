package com.example.bunpuoficial.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.activities.EditProfileActivity;
import com.example.bunpuoficial.adapters.MyProductAdapter;
import com.example.bunpuoficial.adapters.OrderAdapter;
import com.example.bunpuoficial.adapters.ProductAdapter;
import com.example.bunpuoficial.models.Pedido;
import com.example.bunpuoficial.models.Product;
import com.example.bunpuoficial.providers.AuthProvider;
import com.example.bunpuoficial.providers.PedidoProvider;
import com.example.bunpuoficial.providers.ProductProvider;
import com.example.bunpuoficial.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    View mView;

    LinearLayout mLinearLayoutEditProfile;
    TextView informacion;
    TextView mTextViewUsername;
    TextView mTextViewTelefono;
    TextView mTextViewEmail;
    TextView mTextViewProductNumber;
    ImageView mImageViewCover;
    CircleImageView mCircleImageProfile;
    RadioGroup grupoAccionesPP;
    RadioButton radioPublicaciones;
    RadioButton radioPedidos;
    RadioButton radioPedidosCompletados;
    TextView tipoInformacion;

    RecyclerView mRecycleView;

    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    ProductProvider mProductProvider;
    PedidoProvider mPedidoProvider;
    ListenerRegistration mListenerRegistration;

    MyProductAdapter mAdapter;
    OrderAdapter mOrderAdapter;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView= inflater.inflate(R.layout.fragment_profile, container, false);
        mLinearLayoutEditProfile=mView.findViewById(R.id.linearLaoutEditProfile);

        informacion = mView.findViewById(R.id.informacionUsuario);
        mTextViewEmail=mView.findViewById(R.id.textViewEmail);
        mTextViewProductNumber=mView.findViewById(R.id.textViewProductNumber);
        mTextViewTelefono=mView.findViewById(R.id.textViewTelefono);
        mTextViewUsername=mView.findViewById(R.id.textViewUsername);
        mImageViewCover=mView.findViewById(R.id.imageViewCover);
        radioPublicaciones=mView.findViewById(R.id.radioPublicaciones);
        radioPedidos=mView.findViewById(R.id.radioPedidos);
        radioPedidosCompletados = mView.findViewById(R.id.radioPedidosAcabados);
        grupoAccionesPP=mView.findViewById(R.id.grupoAccionesPP);
        mCircleImageProfile=mView.findViewById(R.id.circleImageProfile);
        mRecycleView=mView.findViewById(R.id.recycleProfile);
        tipoInformacion=mView.findViewById(R.id.textViewProductExist);

        mUsersProvider=new UsersProvider();
        mAuthProvider=new AuthProvider();
        mProductProvider=new ProductProvider();
        mPedidoProvider=new PedidoProvider();

        mLinearLayoutEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditProfile();
            }
        });

        radioPublicaciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipoInformacion.setText("Productos");

                Query query= mProductProvider.getProductByUserProfile(mAuthProvider.getUid());
                FirestoreRecyclerOptions<Product> options=new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();
                mAdapter=new MyProductAdapter(options,getContext());
                mRecycleView.setLayoutManager(new GridLayoutManager(mView.getContext(),3));
                mRecycleView.setAdapter(mAdapter);
                mAdapter.startListening();
            }
        });

        radioPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllPedidoPedidoEmpresa();
                tipoInformacion.setText("Pedidos sin terminar");
                tipoInformacion.setTextColor(Color.BLACK);
            }
        });
        radioPedidosCompletados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tipoInformacion.setText("Pedidos acabados");
                tipoInformacion.setTextColor(Color.BLACK);
                getAllPedidoPedidoEmpresaAcabados();
            }
        });


        getUser();
        getProductNumber();
        checkIfExistProduct();

        return mView;
    }

    private void checkIfExistProduct() {

        mListenerRegistration=mProductProvider.getProductByUser(mAuthProvider.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error==null)
                {
                    if(value!=null){
                        int numberProduct= value.size();
                        if(numberProduct>0)
                        {
                            tipoInformacion.setText("Productos");
                            tipoInformacion.setTextColor(Color.BLACK);
                        }
                        else
                        {
                            tipoInformacion.setText("No hay productos");
                            tipoInformacion.setTextColor(Color.BLACK);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        mUsersProvider.getTypeUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getString("typeUser").equals("Empresa"))
                {
                    tipoInformacion.setText("Productos");
                    radioPublicaciones.setChecked(true);
                    Query query= mProductProvider.getProductByUserProfile(mAuthProvider.getUid());
                    FirestoreRecyclerOptions<Product> options=new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();
                    mAdapter=new MyProductAdapter(options,getContext());
                    mRecycleView.setLayoutManager(new GridLayoutManager(mView.getContext(),3));
                    mRecycleView.setAdapter(mAdapter);
                    mAdapter.startListening();
                }
                else if(documentSnapshot.getString("typeUser").equals("Normal"))
                {
                    getAllPedidoPedido();
                    informacion.setText("Pedidos");
                    radioPublicaciones.setVisibility(View.GONE);
                    radioPedidos.setText("Pedidos sin terminar");
                    radioPedidos.setChecked(true);
                    radioPedidosCompletados.setText(("Pedidos listos"));
                    getAllPedidoPedidoEmpresa();
                    tipoInformacion.setText("Pedidos");
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();

        mUsersProvider.getTypeUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.getString("typeUser").equals("Empresa"))
                {
                    mAdapter.stopListening();
                }
                else if(documentSnapshot.getString("typeUser").equals("Normal"))
                {
                    mOrderAdapter.stopListening();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mListenerRegistration!=null){
            mListenerRegistration.remove();
        }
    }

    private void getProductNumber()
    {
        mProductProvider.getProductByUser(mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numberProduct= queryDocumentSnapshots.size();
                mTextViewProductNumber.setText(String.valueOf(numberProduct));
            }
        });
    }

    private void goToEditProfile() {
        System.out.println("----------------------Aqui");
        Intent intent=new Intent(getContext(), EditProfileActivity.class);
        startActivity(intent);
    }

    private void getUser()
    {
        mUsersProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
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

    private void getAllPedidoPedido()
    {
        mPedidoProvider.getPedidoByUserProfile(mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int numerosPedidods= queryDocumentSnapshots.size();
                mTextViewProductNumber.setText(""+numerosPedidods);
            }
        });


    }

    private void getAllPedidoPedidoEmpresa()
    {
        mUsersProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String tipo = documentSnapshot.getString("typeUser");
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
                Query query;
                if(tipo.equals("Empresa")){
                    query = mPedidoProvider.getAllEmpresa(mAuthProvider.getUid());
                }else{
                    query = mPedidoProvider.getAllNormal(mAuthProvider.getUid());
                }
                FirestoreRecyclerOptions<Pedido> options=new FirestoreRecyclerOptions.Builder<Pedido>().setQuery(query, Pedido.class).build();
                mOrderAdapter=new OrderAdapter(options,getContext());
                mOrderAdapter.notifyDataSetChanged();
                mRecycleView.setLayoutManager(linearLayoutManager);
                mRecycleView.setAdapter(mOrderAdapter);
                mOrderAdapter.startListening();
            }
        });

    }

    private void getAllPedidoPedidoEmpresaAcabados()
    {

        mUsersProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String tipo = documentSnapshot.getString("typeUser");
                LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
                Query query;
                if(tipo.equals("Empresa")){
                   query = mPedidoProvider.getAllEmpresaPedidosEnd(mAuthProvider.getUid());
                }else{
                    query = mPedidoProvider.getAllNormalPedidosEnd(mAuthProvider.getUid());
                }
                FirestoreRecyclerOptions<Pedido> options=new FirestoreRecyclerOptions.Builder<Pedido>().setQuery(query, Pedido.class).build();
                mOrderAdapter=new OrderAdapter(options,getContext());
                mOrderAdapter.notifyDataSetChanged();
                mRecycleView.setLayoutManager(linearLayoutManager);
                mRecycleView.setAdapter(mOrderAdapter);
                mOrderAdapter.startListening();
            }
        });

    }
}
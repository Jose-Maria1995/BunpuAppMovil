package com.example.bunpuoficial.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.bunpuoficial.R;

import android.content.Intent;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.bunpuoficial.activities.MainActivity;
import com.example.bunpuoficial.activities.PostActivity;
import com.example.bunpuoficial.adapters.ProductAdapter;
import com.example.bunpuoficial.models.Product;
import com.example.bunpuoficial.models.User;
import com.example.bunpuoficial.providers.AuthProvider;
import com.example.bunpuoficial.providers.ProductProvider;
import com.example.bunpuoficial.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.mancj.materialsearchbar.MaterialSearchBar;

public class HomeFragment extends Fragment implements MaterialSearchBar.OnSearchActionListener{

    View view;
    FloatingActionButton miFab;

    MaterialSearchBar mSearchBar;

    AuthProvider mAuthProvider;
    UsersProvider mUserProvider;

    RecyclerView mRecyclerView;
    ProductProvider mProductProvider;
    ProductAdapter mProductAdapter;
    ProductAdapter mProductAdapterSearch;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);

        view=inflater.inflate(R.layout.fragment_home, container, false);
        miFab = view.findViewById(R.id.add_post);
        mRecyclerView=view.findViewById(R.id.recyclerViewHome);
        mSearchBar=view.findViewById(R.id.searchBar);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);

        setHasOptionsMenu(true);
        mAuthProvider=new AuthProvider();
        mProductProvider=new ProductProvider();
        mUserProvider=new UsersProvider();

        mSearchBar.setOnSearchActionListener(this);
        mSearchBar.inflateMenu(R.menu.main_menu);
        mSearchBar.getMenu().setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(item.getItemId()==R.id.itemLogout)
                {
                    logout();
                }

                return true;
            }
        });

        mUserProvider.getTypeUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    if(documentSnapshot.contains("typeUser"))
                    {
                        String typeUser=documentSnapshot.getString("typeUser");
                        if(typeUser!=null)
                        {
                            if(typeUser.equals("Normal"))
                            {
                                miFab.setVisibility(View.GONE);
                            }
                            else if(typeUser.equals("Empresa"))
                            {
                                miFab.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                }
            }
        });


        miFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPost();
            }
        });
        return view;
    }

    private void searchByTitle(String title)
    {
        super.onStart();
        Query query= mProductProvider.getProductByTitle(title);
        FirestoreRecyclerOptions<Product> options=new FirestoreRecyclerOptions
                .Builder<Product>()
                .setQuery(query, Product.class)
                .build();

        mProductAdapterSearch=new ProductAdapter(options,getContext());
        mProductAdapterSearch.notifyDataSetChanged();
        mRecyclerView.setAdapter(mProductAdapterSearch);
        mProductAdapterSearch.startListening();
    }

    private void getAllProduct()
    {
        Query query= mProductProvider.getAll();
        FirestoreRecyclerOptions<Product> options=new FirestoreRecyclerOptions.Builder<Product>().setQuery(query, Product.class).build();
        mProductAdapter=new ProductAdapter(options,getContext());
        mProductAdapter.notifyDataSetChanged();
        mRecyclerView.setAdapter(mProductAdapter);
        mProductAdapter.startListening();
    }

    @Override
    public void onStart() {
        super.onStart();
        getAllProduct();
    }

    @Override
    public void onStop() {
        super.onStop();
        mProductAdapter.stopListening();
        if(mProductAdapterSearch!=null)
        {
            mProductAdapterSearch.stopListening();  //Ojo que nos puede servir esto by orlando
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mProductAdapter.getmListenerRegistration()!=null){
            mProductAdapter.getmListenerRegistration().remove();
        }
    }

    private void goToPost() {
        Intent intent = new Intent(getContext(), PostActivity.class);
        startActivity(intent);
    }

    private void logout() {
        mAuthProvider.logout();
        Intent intent=new Intent(getContext(), MainActivity.class);
        /*Limpiamos todo el historial al momento de cerrar sesion*/
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {
        if(!enabled)
        {
            getAllProduct();
        }
    }

    @Override
    public void onSearchConfirmed(CharSequence text) {
        Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
        searchByTitle(text.toString().toLowerCase());
    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }
}
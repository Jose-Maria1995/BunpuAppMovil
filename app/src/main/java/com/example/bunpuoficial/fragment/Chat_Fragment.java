package com.example.bunpuoficial.fragment;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.adapters.ChatsAdapter;
import com.example.bunpuoficial.adapters.ProductAdapter;
import com.example.bunpuoficial.models.Chat;
import com.example.bunpuoficial.models.Product;
import com.example.bunpuoficial.providers.AuthProvider;
import com.example.bunpuoficial.providers.ChatProvider;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class Chat_Fragment extends Fragment {

    ChatsAdapter mChatsAdapter;
    RecyclerView mRecyclerView;
    View mView;

    ChatProvider mChatProvider;
    AuthProvider mAuthProvider;
    Toolbar mToolbar;
    public Chat_Fragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mView = inflater.inflate(R.layout.fragment_chat_, container, false);
        mRecyclerView = mView.findViewById(R.id.recyclerViewChat);
        mToolbar = mView.findViewById(R.id.toolbar);

        ((AppCompatActivity)getActivity()).setSupportActionBar(mToolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Chats");

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mChatProvider=new ChatProvider();
        mAuthProvider= new AuthProvider();

        return mView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mChatsAdapter.getListener()!=null){
            mChatsAdapter.getListener().remove();
        }
        if(mChatsAdapter.getListenerlastMessage()!=null){
            mChatsAdapter.getListenerlastMessage().remove();
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        Query query= mChatProvider.getAll(mAuthProvider.getUid());
        FirestoreRecyclerOptions<Chat> options=new FirestoreRecyclerOptions.Builder<Chat>().setQuery(query, Chat.class).build();
        mChatsAdapter=new ChatsAdapter(options,getContext());
        mRecyclerView.setAdapter(mChatsAdapter);
        mChatsAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        mChatsAdapter.stopListening();
    }
}
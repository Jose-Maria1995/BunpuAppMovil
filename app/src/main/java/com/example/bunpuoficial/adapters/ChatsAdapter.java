package com.example.bunpuoficial.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bunpuoficial.R;

import com.example.bunpuoficial.activities.ChatActivity;
import com.example.bunpuoficial.models.Chat;
import com.example.bunpuoficial.providers.AuthProvider;
import com.example.bunpuoficial.providers.ChatProvider;
import com.example.bunpuoficial.providers.MessagesProvider;
import com.example.bunpuoficial.providers.UsersProvider;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsAdapter extends FirestoreRecyclerAdapter <Chat, ChatsAdapter.ViewHolder>
{
    Context context;
    UsersProvider miUsersProvider;
    AuthProvider mAuthProvider;
    ChatProvider mChatProvider;
    MessagesProvider messagesProvider;
    ListenerRegistration mListenerRegistration;
    ListenerRegistration mListenerLastMessage;


    public ChatsAdapter(FirestoreRecyclerOptions<Chat> options, Context context)
    {
        super(options);
        this.context=context;
        miUsersProvider = new UsersProvider();
        mAuthProvider = new AuthProvider();
        mChatProvider = new ChatProvider();
        messagesProvider = new MessagesProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Chat chat) {

        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String chatId= document.getId();
        if(mAuthProvider.getUid().equals(chat.getIdUser1())){
            getUserInfo(chat.getIdUser2(),holder);
        }else{
            getUserInfo(chat.getIdUser1(),holder);
        }
        holder.viewHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToCharActivity(chatId,chat.getIdUser1(),chat.getIdUser2());
            }
        });
        getLastMessage(chatId,holder.textViewLastMessage);

        String idSender;
        if(mAuthProvider.getUid().equals(chat.getIdUser1())){
            idSender=chat.getIdUser2();
        }else{
            idSender=chat.getIdUser1();
        }
        getMessageNotRead(chatId,idSender,holder.textViewMessageNotRead,holder.frameLayoutMessageNotRead);

    }

    private void getMessageNotRead(String chatId, String idSender,TextView  textViewMessageNotRead ,FrameLayout frameLayoutMessageNotRead) {

        mListenerRegistration=messagesProvider.getMessageByChatAndSender(chatId,idSender).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable  QuerySnapshot value, @Nullable  FirebaseFirestoreException error) {
                if(value!=null) {
                    int size = value.size();
                    if (size > 0) {
                        frameLayoutMessageNotRead.setVisibility(View.VISIBLE);
                        textViewMessageNotRead.setText(String.valueOf(size));
                    } else {
                        frameLayoutMessageNotRead.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    public ListenerRegistration getListener(){
        return mListenerRegistration;
    }
    public ListenerRegistration getListenerlastMessage(){
        return mListenerLastMessage;
    }

    private void getLastMessage(String chatId, TextView textViewLastMessage) {
        mListenerLastMessage=messagesProvider.getLastMessage(chatId).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable  QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if(queryDocumentSnapshots!=null){
                    int size = queryDocumentSnapshots.size();
                    if(size>0){
                        String lastMessage = queryDocumentSnapshots.getDocuments().get(0).getString("message");
                        textViewLastMessage.setText(lastMessage);
                    }
                }
            }
        });
    }

    private void goToCharActivity(String chatId,String idUser1,String idUser2) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra("idChat",chatId);
        intent.putExtra("idUser1",idUser1);
        intent.putExtra("idUser2",idUser2);
        context.startActivity(intent);

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
                                Picasso.get().load(image_profile).into(holder.circleImageChat);
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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_chat, parent,false);

        return new ViewHolder(view);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView circleImageChat;
        TextView textViewUsername;
        TextView textViewLastMessage;
        TextView textViewMessageNotRead;
        FrameLayout frameLayoutMessageNotRead;
        View viewHolder;

        public ViewHolder (View view)
        {
            super(view);
            circleImageChat= view.findViewById(R.id.cicleImageChat);
            textViewLastMessage= view.findViewById(R.id.textViewUltimoMensaje);
            textViewMessageNotRead = view.findViewById(R.id.textViewMessageNotRead);
            textViewUsername= view.findViewById(R.id.textViewUserChat);
            frameLayoutMessageNotRead = view.findViewById(R.id.frameLayoutMessageNotRead);
            viewHolder=view;
        }



    }
}

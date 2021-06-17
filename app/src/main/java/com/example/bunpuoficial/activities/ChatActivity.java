package com.example.bunpuoficial.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.adapters.MessagesAdapter;
import com.example.bunpuoficial.adapters.MyProductAdapter;
import com.example.bunpuoficial.models.Chat;
import com.example.bunpuoficial.models.FCMBody;
import com.example.bunpuoficial.models.FCMResponse;
import com.example.bunpuoficial.models.Message;
import com.example.bunpuoficial.models.Product;
import com.example.bunpuoficial.providers.AuthProvider;
import com.example.bunpuoficial.providers.ChatProvider;
import com.example.bunpuoficial.providers.MessagesProvider;
import com.example.bunpuoficial.providers.NotificationProvider;
import com.example.bunpuoficial.providers.TokenProvider;
import com.example.bunpuoficial.providers.UsersProvider;
import com.example.bunpuoficial.utils.RelativeTime;
import com.example.bunpuoficial.utils.ViewedMessageHelper;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity {

    String mExtraIdUser1, mExtraIdUser2,mExtraIdChat;

    Long mIdNotificationChat;

    ChatProvider mChatProvider;
    MessagesProvider messagesProvider;
    AuthProvider mAuthProvider;
    UsersProvider mUsersProvider;
    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;

    View mAccionBarView;

    EditText editTextMessage;
    ImageView imageViewSendMessage;

    CircleImageView circleImageView;
    TextView textViewUsername;
    TextView textViewRelativeTime;
    ImageView imageViewBack;
    RecyclerView mRecyclerViewMessage;
    MessagesAdapter messagesAdapter;
    LinearLayoutManager linearLayoutManager;

    ListenerRegistration mListenerRegistration;

    String mMyUserName;
    String mUserNameChat;
    String mImageReceiver="";
    String mImageSender="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        mChatProvider=new ChatProvider();
        messagesProvider = new MessagesProvider();
        mAuthProvider = new AuthProvider();
        mUsersProvider = new UsersProvider();
        mNotificationProvider=new NotificationProvider();
        mTokenProvider=new TokenProvider();

        mExtraIdUser1 = getIntent().getStringExtra("idUser1");
        mExtraIdUser2 = getIntent().getStringExtra("idUser2");
        mExtraIdChat = getIntent().getStringExtra("idChat");

        editTextMessage= findViewById(R.id.editeTextMesagge);
        imageViewSendMessage = findViewById(R.id.sendMessage);
        mRecyclerViewMessage = findViewById(R.id.RecyclerViewMessage);

        linearLayoutManager=new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerViewMessage.setLayoutManager(linearLayoutManager);

        ShowCustomToolbar(R.layout.custom_chat_toolbar);
        getMyInfoUser();

        imageViewSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });

        ChechIfChatExist();

    }

    @Override
    public void onStart() {
        super.onStart();

        if(messagesAdapter!=null){
            messagesAdapter.startListening();
            ViewedMessageHelper.updateOnline(true,ChatActivity.this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        ViewedMessageHelper.updateOnline(false,ChatActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mListenerRegistration!=null){
            mListenerRegistration.remove();
        }
    }

    private void getMessageChat(){
        Query query= messagesProvider.getMessageByChat(mExtraIdChat);
        FirestoreRecyclerOptions<Message> options=new FirestoreRecyclerOptions.Builder<Message>().setQuery(query, Message.class).build();
        messagesAdapter=new MessagesAdapter(options,ChatActivity.this);
        mRecyclerViewMessage.setAdapter(messagesAdapter);
        messagesAdapter.startListening();
        messagesAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                updateViewed();
                int numeroMensajes= messagesAdapter.getItemCount();
                int ultimomensaje= linearLayoutManager.findLastCompletelyVisibleItemPosition();

                if(ultimomensaje==-1 ||(positionStart>=(numeroMensajes-1)&& ultimomensaje ==(positionStart-1))){
                    mRecyclerViewMessage.scrollToPosition(positionStart);
                }
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        messagesAdapter.stopListening();
    }

    private void sendMessage() {
        String textMessage = editTextMessage.getText().toString();
        if(!textMessage.isEmpty()){
            Message message = new Message();
            if(mAuthProvider.getUid().equals(mExtraIdUser1)){
                message.setIdSender(mExtraIdUser1);
                message.setIdReceiver(mExtraIdUser2);
            }else{
                message.setIdSender(mExtraIdUser2);
                message.setIdReceiver(mExtraIdUser1);
            }
            message.setTimestamp(new Date().getTime());
            message.setViewed(false);
            message.setIdChat(mExtraIdChat);
            message.setMessage(textMessage);

            messagesProvider.create(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        editTextMessage.setText("");
                        messagesAdapter.notifyDataSetChanged();
                        getToken(message);
                    }else{

                    }
                }
            });
        }
    }

    private void ShowCustomToolbar(int resourse) {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAccionBarView = inflater.inflate(resourse,null);
        actionBar.setCustomView(mAccionBarView);

        circleImageView = mAccionBarView.findViewById(R.id.circleImageProfile);
        textViewUsername =mAccionBarView.findViewById(R.id.textViewUsername);
        textViewRelativeTime =mAccionBarView.findViewById(R.id.textViewRelativeTime);
        imageViewBack =mAccionBarView.findViewById(R.id.imageViewBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getUserInfo();


    }

    private void getUserInfo() {
        String idUserInfo="";
        if(mAuthProvider.getUid().equals(mExtraIdUser1)){
            idUserInfo=mExtraIdUser2;
        }else{
            idUserInfo=mExtraIdUser1;
        }
        mListenerRegistration = mUsersProvider.getUserRealtime(idUserInfo).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable  DocumentSnapshot documentSnapshot, FirebaseFirestoreException error) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        mUserNameChat = documentSnapshot.getString("username");
                        textViewUsername.setText(mUserNameChat);
                    }if(documentSnapshot.contains("online")){
                        boolean online = documentSnapshot.getBoolean("online");
                        if(online){
                            textViewRelativeTime.setText("En linea");
                        }else if(documentSnapshot.contains("lastConnect")) {
                            long lastConnect = documentSnapshot.getLong("lastConnect");
                            String relativeTime = RelativeTime.getTimeAgo(lastConnect,ChatActivity.this);
                            textViewRelativeTime.setText(relativeTime);
                        }
                    }
                    if(documentSnapshot.contains("image_profile")){
                        mImageReceiver = documentSnapshot.getString("image_profile");
                        if(mImageReceiver!=null) {
                            if (!mImageReceiver.equals("")) {
                                Picasso.get().load(mImageReceiver).into(circleImageView);
                            }
                        }
                    }
                }
            }
        });
    }

    private void ChechIfChatExist(){
        mChatProvider.getChatByUser1AndUser2(mExtraIdUser1,mExtraIdUser2).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                int size = queryDocumentSnapshots.size();

                if(size == 0){
                    createChat();
                }else{
                    mExtraIdChat = queryDocumentSnapshots.getDocuments().get(0).getId();
                    mIdNotificationChat=queryDocumentSnapshots.getDocuments().get(0).getLong("idNotification");
                    getMessageChat();
                    updateViewed();
                }
            }
        });

    }

    private void updateViewed() {
        String idSender="";
        if(mAuthProvider.getUid().equals(mExtraIdUser1)){
            idSender = mExtraIdUser2;
        }else{
            idSender=mExtraIdUser1;
        }
        messagesProvider.getMessageByChatAndSender(mExtraIdChat,idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                    messagesProvider.updateViewed(document.getId(),true);
                }
            }
        });
    }

    private void createChat(){

        Chat chat = new Chat();
        chat.setIdUser1(mExtraIdUser1);
        chat.setIdUser2(mExtraIdUser2);
        chat.setWriting(false);
        chat.setTimestamp(new Date().getTime());
        chat.setId(mExtraIdUser1+mExtraIdUser2);
        Random random=new Random();
        int n=random.nextInt(1000000);
        chat.setIdNotification(n);
        mIdNotificationChat=Long.valueOf(n);

        ArrayList<String> ids = new ArrayList<>();
        ids.add(mExtraIdUser1);
        ids.add(mExtraIdUser2);
        chat.setIds(ids);
        mChatProvider.create(chat);
        mExtraIdChat=chat.getId();
        getMessageChat();

    }

    private void getToken(Message message){
        String idUser="";
        if(mAuthProvider.getUid().equals(mExtraIdUser1))
        {
            idUser=mExtraIdUser2;
        }
        else
        {
            idUser=mExtraIdUser1;
        }

        mTokenProvider.getToken(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");
                        getLastThreeMessages(message,token);
                    }
                }
                else{
                    Toast.makeText(ChatActivity.this, "El token del ususario no existe ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void getLastThreeMessages(Message message , String token) {
        messagesProvider.getLastThreeMessageByChatAndSender(mExtraIdChat,mAuthProvider.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                ArrayList<Message> messageArrayList=new ArrayList<>();

                for(DocumentSnapshot d :queryDocumentSnapshots.getDocuments()){
                    if(d.exists()){
                        Message message = d.toObject(Message.class);
                        messageArrayList.add(message);
                    }
                }
                if(messageArrayList.size()==0){
                    messageArrayList.add(message);
                }
                Collections.reverse(messageArrayList);

                Gson gson=new Gson(); //Te permite canvertir nuestro objeto mensage en un array de tipo Json
                String messages=gson.toJson(messageArrayList);

                sendNotification(token,messages,message);
            }
        });
    }

    private void sendNotification(String token,String messages,Message message){
        Map<String,String> data = new HashMap<>();
        data.put("title","Nuevo mensaje");
        data.put("body",message.getMessage());
        data.put("idNotification",String.valueOf(mIdNotificationChat));
        data.put("messages",messages);
        data.put("usernameSender",mMyUserName);
        data.put("usernameReceiver",mUserNameChat);
        data.put("idSender",message.getIdSender());
        data.put("idReceiver",message.getIdReceiver());
        data.put("idChat",message.getIdChat());

        if(mImageSender.equals(""))
        {
            mImageSender= "IMAGEN NO VALIDA";
        }
        if(mImageReceiver.equals(""))
        {
            mImageReceiver= "IMAGEN NO VALIDA";
        }

        data.put("imageSender",mImageSender);
        data.put("imageReceiver",mImageReceiver);

        String idSender="";
        if(mAuthProvider.getUid().equals(mExtraIdUser1)){
            idSender = mExtraIdUser2;
        }else{
            idSender=mExtraIdUser1;
        }

        messagesProvider.getLastMessageSender(mExtraIdChat,idSender).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                String lastMessage ="";
                int size = queryDocumentSnapshots.size();
                if(size>0){
                    lastMessage = queryDocumentSnapshots.getDocuments().get(0).getString("message");
                    data.put("lastMessage",lastMessage);
                }
                FCMBody body = new FCMBody(token,"high","4500s",data);
                mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                    @Override
                    public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                        if(response.body()!=null){
                            if(response.body().getSuccess()==1){
                                //Toast.makeText(ChatActivity.this, "La notificacion se envio correctamente", Toast.LENGTH_SHORT).show();
                            }else{
                                Toast.makeText(ChatActivity.this, "La notificacion no se envio ", Toast.LENGTH_SHORT).show();
                            }
                        }else{
                            Toast.makeText(ChatActivity.this, "La notificacion no se envio ", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<FCMResponse> call, Throwable t) {

                    }
                });
            }
        });

    }

    private void getMyInfoUser(){
        mUsersProvider.getUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("username")){
                        mMyUserName = documentSnapshot.getString("username");
                    }
                    if(documentSnapshot.contains("image_profile")){
                        mImageSender = documentSnapshot.getString("image_profile");
                    }
                }
            }
        });
    }
}
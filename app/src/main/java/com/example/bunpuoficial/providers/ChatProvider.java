package com.example.bunpuoficial.providers;

import com.example.bunpuoficial.models.Chat;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class ChatProvider {

    CollectionReference mCollection;

    public ChatProvider() {
        mCollection = FirebaseFirestore.getInstance().collection("Chats");
    }

    public void create(Chat chat){
        mCollection.document(chat.getIdUser1()+chat.getIdUser2()).set(chat);
    }

    public Query getAll(String idUser){
        return mCollection.whereArrayContains("ids",idUser);
    }



    public Query getChatByUser1AndUser2(String user1,String user2){
        ArrayList<String> ids = new ArrayList<>();
        ids.add(user1+user2);
        ids.add(user2+user1);
        return mCollection.whereIn("id",ids);
    }
}

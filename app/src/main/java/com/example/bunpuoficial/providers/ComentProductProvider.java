package com.example.bunpuoficial.providers;

import com.example.bunpuoficial.models.Comment;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ComentProductProvider {

    CollectionReference miCollection;

    public ComentProductProvider(){

        miCollection = FirebaseFirestore.getInstance().collection("Comments");

    }

    public Task<Void> create (Comment coment){
        return miCollection.document().set(coment);

    }
    public Query getCommentsByProduct(String idProduct)
    {
        return miCollection.whereEqualTo("idPost",idProduct);
    }
}

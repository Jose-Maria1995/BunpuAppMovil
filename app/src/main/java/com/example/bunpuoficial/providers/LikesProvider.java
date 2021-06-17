package com.example.bunpuoficial.providers;

import com.example.bunpuoficial.models.Like;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class LikesProvider {

    CollectionReference mCollection;

    public LikesProvider()
    {
        mCollection= FirebaseFirestore.getInstance().collection("Likes");
    }

    public Task<Void> create(Like like)
    {
        DocumentReference document=mCollection.document();
        String id=document.getId();
        like.setId(id);
        return document.set(like);
    }

    public Query getLikesByProduct(String idProduct)
    {
        return mCollection.whereEqualTo("idProduct",idProduct);
    }

    public Query getLikeByProductAndUser(String idProduct, String idUser)
    {
        return mCollection.whereEqualTo("idProduct",idProduct).whereEqualTo("idUser",idUser);
    }

    public Task<Void> delete(String id)
    {
        return mCollection.document(id).delete();
    }
}

package com.example.bunpuoficial.providers;

import com.example.bunpuoficial.models.Product;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ProductProvider {

    CollectionReference mCollection;

    public ProductProvider()
    {
        mCollection= FirebaseFirestore.getInstance().collection("Product");
    }

    //Funcion para guardar un producto en FireBase
    public Task<Void> save(Product product)
    {
        return mCollection.document().set(product);
    }

    //Funcion para obetener todos los productos ordenados por timestamp
    public Query getAll()
    {
        return mCollection.orderBy("timestamp", Query.Direction.DESCENDING);
    }

    //Funcion para obtener los o el producto que tiene un usuario tipo empresa
    public Query getProductByUser(String id)
    {
        return mCollection.whereEqualTo("idUser",id);
    }

    //Funcion para obtener los o el producto que tiene un usuario tipo empresa ordenado por timeStamp
    public Query getProductByUserProfile(String id)
    {
        return mCollection.orderBy("timestamp").whereEqualTo("idUser",id);
    }
    //Funcion para obtener los o el producto que tiene un usuario tipo empresa ordenado por timeStamp
    public Task<DocumentSnapshot> getProductByid(String id)
    {
        return mCollection.document(id).get();
    }
    //Funcion para borrar un producto
    public Task<Void> delete(String id)
    {
        return mCollection.document(id).delete();
    }

    //Funcion para obtener los o el producto segun su categoria ordenados por timestamp
    public Query getProductByCategoryAndTimestamp(String category)
    {
        return mCollection.whereEqualTo("category",category).orderBy("timestamp", Query.Direction.DESCENDING);
    }
    //Funcion para obetener los producto por titulo
    public Query getProductByTitle(String title)
    {
        return mCollection.orderBy("productName").startAt(title).endAt(title+'\uf8ff');
    }
}

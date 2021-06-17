package com.example.bunpuoficial.providers;

import com.example.bunpuoficial.models.Pedido;
import com.example.bunpuoficial.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PedidoProvider {

    CollectionReference mCollection;

    public PedidoProvider() {
        this.mCollection = FirebaseFirestore.getInstance().collection("Orders");
    }
    public Task<Void> save(Pedido pedido){
        return mCollection.document().set(pedido);
    }
    public Query getPedidoByUserNormalProfile(String id){
        return mCollection.orderBy("diaRealizado").whereEqualTo("idNormal",id);
    }
    public Query getPedidoByUserEmpresaProfile(String id){
        return mCollection.orderBy("diaRealizado").whereEqualTo("idEmpresa",id);
    }

    public Task<DocumentSnapshot> getPedidoByid(String id)
    {
        return mCollection.document(id).get();
    }

    public Query getPedidoByUserProfile(String id)
    {
        return mCollection.whereEqualTo("idNormal",id);
    }

    public Query getAll(String idNormal)
    {
        return mCollection.orderBy("diaRealizado", Query.Direction.DESCENDING).whereEqualTo("idNormal",idNormal);
    }

    public Query getAllEmpresa(String idEmpresa)
    {
        return mCollection.orderBy("diaRealizado", Query.Direction.DESCENDING).whereEqualTo("idEmpresa",idEmpresa).whereEqualTo("estado","no listo");
    }
    public Query getAllEmpresaPedidosEnd(String idEmpresa)
    {
        return mCollection.orderBy("diaRealizado", Query.Direction.DESCENDING).whereEqualTo("idEmpresa",idEmpresa).whereEqualTo("estado","listo");
    }
    public Query getAllNormal(String idUsuario)
    {
        return mCollection.orderBy("diaRealizado", Query.Direction.DESCENDING).whereEqualTo("idNormal",idUsuario).whereEqualTo("estado","no listo");
    }
    public Query getAllNormalPedidosEnd(String idUsuario)
    {
        return mCollection.orderBy("diaRealizado", Query.Direction.DESCENDING).whereEqualTo("idNormal",idUsuario).whereEqualTo("estado","listo");
    }

    public Task<Void> updatePedido(Pedido pedido, String idPedido){
        Map<String,Object> map = new HashMap<>();
        map.put("estado",pedido.getEstado());

        return mCollection.document(idPedido).update(map);
    }
}


package com.example.bunpuoficial.providers;

import com.example.bunpuoficial.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UsersProvider {

    private CollectionReference miCollection;

    public UsersProvider(){

        miCollection = FirebaseFirestore.getInstance().collection("Users");
    }

    public Task<DocumentSnapshot> getUser(String id){
        return miCollection.document(id).get();
    }
    public DocumentReference getUserRealtime(String id){
        return miCollection.document(id);
    }

    public Task<Void> create(User user){
        return  miCollection.document(user.getId()).set(user);
    }

    public void delete(String id){
        miCollection.document(id).delete();
    }

    public Task<Void> update(User user){
        Map<String,Object> map = new HashMap<>();
        map.put("username",user.getUsername());
        map.put("telefono",user.getTelefono());
        map.put("timestamp",new Date().getTime());
        map.put("image_profile",user.getImageProfile());
        map.put("image_cover",user.getImageCover());
        return miCollection.document(user.getId()).update(map);
    }

    public Task<Void> updateCompleteProfile(User user){
        Map<String,Object> map = new HashMap<>();
        map.put("username",user.getUsername());
        map.put("telefono",user.getTelefono());
        map.put("typeUser",user.getTypeUser());
        map.put("localizacion",user.getLocalizacion());
        map.put("timestamp",new Date().getTime());
        map.put("image_profile",user.getImageProfile());
        map.put("image_cover",user.getImageCover());
        return miCollection.document(user.getId()).update(map);
    }

    public Task<Void> updateOnline(String idUser,boolean status){
        Map<String,Object> map = new HashMap<>();
        map.put("online",status);
        map.put("lastConnect",new Date().getTime());
        return miCollection.document(idUser).update(map);
    }

    public Task<DocumentSnapshot> getTypeUser(String idUser)
    {
        return miCollection.document(idUser).get();
    }

}

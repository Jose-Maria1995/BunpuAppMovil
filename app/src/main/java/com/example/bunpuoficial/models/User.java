package com.example.bunpuoficial.models;

import java.util.Map;

public class User {

    private String id;
    private String email;
    private String username;
    private String telefono;
    private Long timestamp,lastConnection;
    private String imageProfile;
    private String imageCover;
    private String typeUser;
    private String localizacion;
    private boolean online;

    public User(){

    }

    public User(String id, String email, String username, String telefono, Long timestamp, Long lastConnection, String imageProfile, String imageCover, boolean online,String typeUser,String localizacion) {
        this.id = id;
        this.email = email;
        this.username = username;
        this.telefono = telefono;
        this.timestamp = timestamp;
        this.lastConnection = lastConnection;
        this.imageProfile = imageProfile;
        this.imageCover = imageCover;
        this.online = online;
        this.typeUser=typeUser;
        this.localizacion=localizacion;
    }

    public String getLocalizacion() {
        return localizacion;
    }

    public void setLocalizacion(String localizacion) {
        this.localizacion = localizacion;
    }

    public Long getLastConnection() {
        return lastConnection;
    }

    public void setLastConnection(Long lastConnection) {
        this.lastConnection = lastConnection;
    }

    public boolean isOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public String getImageProfile() {
        return imageProfile;
    }

    public void setImageProfile(String imageProfile) {
        this.imageProfile = imageProfile;
    }

    public String getImageCover() {
        return imageCover;
    }

    public void setImageCover(String imageCover) {
        this.imageCover = imageCover;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTypeUser() {
        return typeUser;
    }

    public void setTypeUser(String typeUser) {
        this.typeUser = typeUser;
    }
}

package com.example.bunpuoficial.models;

import java.util.ArrayList;

public class Chat {

    private String idUser1,idUser2,id;
    ArrayList<String> ids;
    private int idNotification;
    private boolean isWriting;
    private long timestamp;

    public Chat() {
    }

    public Chat(String idUser1, String idUser2, String id, ArrayList<String> ids, int idNotification, boolean isWriting, long timestamp) {
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.id = id;
        this.ids = ids;
        this.idNotification = idNotification;
        this.isWriting = isWriting;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ArrayList<String> getIds() {
        return ids;
    }

    public void setIds(ArrayList<String> ids) {
        this.ids = ids;
    }

    public String getIdUser1() {
        return idUser1;
    }

    public void setIdUser1(String idUser1) {
        this.idUser1 = idUser1;
    }

    public String getIdUser2() {
        return idUser2;
    }

    public void setIdUser2(String idUser2) {
        this.idUser2 = idUser2;
    }

    public boolean isWriting() {
        return isWriting;
    }

    public void setWriting(boolean writing) {
        isWriting = writing;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getIdNotification() {
        return idNotification;
    }

    public void setIdNotification(int idNotification) {
        this.idNotification = idNotification;
    }
}

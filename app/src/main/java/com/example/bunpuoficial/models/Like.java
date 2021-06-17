package com.example.bunpuoficial.models;

public class Like {

    private String id;
    private String idProduct;
    private String idUser;
    private Long timestamp;

    public Like()
    {

    }

    public Like(String id, String idProduct, String idUser, Long timestamp) {
        this.id = id;
        this.idProduct = idProduct;
        this.idUser = idUser;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}

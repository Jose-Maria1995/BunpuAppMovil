package com.example.bunpuoficial.models;

public class Pedido {

    String idNormal,idEmpresa,productName,nombreEmpresa,category,direccionEmpresa,tipoPago,imagenProduct,estado;
    double precioUnidad,factura;
    Long diaRealizado,diaEntrega;
    int cantidad,hora,minuto;



    public Pedido() {

    }

    public Pedido(String idNormal, String idEmpresa, String productName, String nombreEmpresa, String category, String direccionEmpresa, String tipoPago, double precioUnidad, double factura, Long diaRealizado, Long diaEntrega, int cantidad, int hora, int minuto,String imagenProduct,String estado) {
        this.idNormal = idNormal;
        this.idEmpresa = idEmpresa;
        this.productName = productName;
        this.nombreEmpresa = nombreEmpresa;
        this.category = category;
        this.direccionEmpresa = direccionEmpresa;
        this.tipoPago = tipoPago;
        this.precioUnidad = precioUnidad;
        this.factura = factura;
        this.diaRealizado = diaRealizado;
        this.diaEntrega = diaEntrega;
        this.cantidad = cantidad;
        this.hora = hora;
        this.minuto = minuto;
        this.imagenProduct=imagenProduct;
        this.estado=estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getImagenProduct() {
        return imagenProduct;
    }

    public void setImagenProduct(String imagenProduct) {
        this.imagenProduct = imagenProduct;
    }

    public String getIdNormal() {
        return idNormal;
    }

    public void setIdNormal(String idNormal) {
        this.idNormal = idNormal;
    }

    public String getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(String idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDireccionEmpresa() {
        return direccionEmpresa;
    }

    public void setDireccionEmpresa(String direccionEmpresa) {
        this.direccionEmpresa = direccionEmpresa;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public void setTipoPago(String tipoPago) {
        this.tipoPago = tipoPago;
    }

    public double getPrecioUnidad() {
        return precioUnidad;
    }

    public void setPrecioUnidad(double precioUnidad) {
        this.precioUnidad = precioUnidad;
    }

    public double getFactura() {
        return factura;
    }

    public void setFactura(double factura) {
        this.factura = factura;
    }

    public Long getDiaRealizado() {
        return diaRealizado;
    }

    public void setDiaRealizado(Long diaRealizado) {
        this.diaRealizado = diaRealizado;
    }

    public Long getDiaEntrega() {
        return diaEntrega;
    }

    public void setDiaEntrega(Long diaEntrega) {
        this.diaEntrega = diaEntrega;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public int getHora() {
        return hora;
    }

    public void setHora(int hora) {
        this.hora = hora;
    }

    public int getMinuto() {
        return minuto;
    }

    public void setMinuto(int minuto) {
        this.minuto = minuto;
    }
}

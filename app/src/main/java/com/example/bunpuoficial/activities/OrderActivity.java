package com.example.bunpuoficial.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.models.FCMBody;
import com.example.bunpuoficial.models.FCMResponse;
import com.example.bunpuoficial.models.Pedido;
import com.example.bunpuoficial.models.Token;
import com.example.bunpuoficial.providers.AuthProvider;
import com.example.bunpuoficial.providers.NotificationProvider;
import com.example.bunpuoficial.providers.PedidoProvider;
import com.example.bunpuoficial.providers.ProductProvider;
import com.example.bunpuoficial.providers.TokenProvider;
import com.example.bunpuoficial.providers.UsersProvider;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import dmax.dialog.SpotsDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.lang.Thread.sleep;

public class OrderActivity extends AppCompatActivity {

    String mExtraProductId,idEmpresa;
    TextView  nombreEmpresa,direccionEmpresa,nombreProducto,categoria,recogidaHora,recogidaDia,textFormTotal,textFormCantidad;
    Button abrirCalendario,abrirReloj,finalizarPedido;
    RadioButton tarjeta,efectivo;
    ImageView mas,menos;

    NotificationProvider mNotificationProvider;
    TokenProvider mTokenProvider;
    AuthProvider authProvider;
    UsersProvider mUsersProvider;
    PedidoProvider mPedidoProvider;
    ProductProvider mProductProvider;
    Double precio;
    AlertDialog miDialog;

    int dia;
    int mes;
    int anio;

    int hora;
    int minuto;

    String imagenProduct;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        mNotificationProvider = new NotificationProvider();
        mTokenProvider = new TokenProvider();
        mPedidoProvider = new PedidoProvider();
        authProvider = new AuthProvider();
        mProductProvider = new ProductProvider();
        mUsersProvider = new UsersProvider();
        mExtraProductId=getIntent().getStringExtra("id");
        miDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Espere un momento")
                .setCancelable(false).build();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        nombreEmpresa = findViewById(R.id.textFormNombreEmpresa);
        direccionEmpresa = findViewById(R.id.textDireccionEmpresa);
        nombreProducto = findViewById(R.id.textFormNombreProducto);
        categoria = findViewById(R.id.textFormCategoriaProducto);
        recogidaHora = findViewById(R.id.textHora);
        recogidaDia = findViewById(R.id.textRecogidaDia);

        abrirCalendario = findViewById(R.id.bottomAbrirCalendario);
        abrirReloj = findViewById(R.id.bottomAbrirReloj);
        finalizarPedido = findViewById(R.id.buttonFinalizarPedido);

        textFormTotal = findViewById(R.id.textFormTotal);
        textFormCantidad = findViewById(R.id.textFormCantidad);

        tarjeta = findViewById(R.id.radioTarjeta);
        efectivo = findViewById(R.id.radioEfectivo);

        mas = findViewById(R.id.imageViewMas);
        menos = findViewById(R.id.imageViewMenos);
        rellenarDatos();

        finalizarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    miDialog.show();
                    finalizarPedido();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        mas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cantidad = Integer.parseInt(textFormCantidad.getText().toString())+1;
                String totalCadena = textFormTotal.getText().toString().replace('€',' ');
                double totalFactura =  Double.parseDouble(totalCadena);
                totalFactura = precio*cantidad;
                textFormTotal.setText(""+totalFactura +"€");
                textFormCantidad.setText(""+cantidad);
            }
        });

        menos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int cantidad = Integer.parseInt(textFormCantidad.getText().toString());
                if((cantidad-1)!=0){
                    String totalCadena = textFormTotal.getText().toString().replace('€',' ');
                    double totalFactura =  Double.parseDouble(totalCadena);
                    totalFactura -= precio;
                    cantidad--;
                    textFormTotal.setText(""+totalFactura+" €");
                    textFormCantidad.setText(""+cantidad);
                }
            }
        });

        abrirCalendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c=Calendar.getInstance();
                dia=c.get(Calendar.DAY_OF_MONTH);
                mes=c.get(Calendar.MONTH);
                anio=c.get(Calendar.YEAR);

                DatePickerDialog datePickerDialog=new DatePickerDialog(OrderActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        Calendar calendar=Calendar.getInstance();
                        calendar.set(year,month,dayOfMonth);
                        //Devuelve el numero del dia (Lunes,Marte,...)
                        System.out.println("----------------------------------------"+calendar.get(Calendar.DAY_OF_WEEK));

                        if(calendar.get(Calendar.DAY_OF_WEEK)!=1)
                        {
                            recogidaDia.setText(String.format("%d/%d/%d",dayOfMonth,(month+1),year));
                        }
                        else
                        {
                            Toast.makeText(OrderActivity.this, "No trabajamos los domingos", Toast.LENGTH_SHORT).show();
                        }
                    }
                },dia,mes,anio);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.show();
            }
        });

        abrirReloj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c=Calendar.getInstance();
                hora=c.get(Calendar.HOUR_OF_DAY);
                minuto=c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog=new TimePickerDialog(OrderActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        if(hourOfDay>=9 && hourOfDay<=20)
                        {
                            recogidaHora.setText(hourOfDay+":"+minute);
                        }
                        else
                        {
                            Toast.makeText(OrderActivity.this, "Establecimiento cerrado", Toast.LENGTH_SHORT).show();
                        }

                    }
                },hora,minuto,true);

                timePickerDialog.show();
            }
        });
    }

    public void rellenarDatos(){

       mProductProvider.getProductByid(mExtraProductId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
           @Override
           public void onSuccess(DocumentSnapshot documentSnapshot) {
               if(documentSnapshot.exists()){
                   idEmpresa = documentSnapshot.getString("idUser");
                   getUserInfo(idEmpresa);
                   nombreProducto.setText(documentSnapshot.getString("productName"));
                   categoria.setText(documentSnapshot.getString("category"));
                   precio = Double.parseDouble(documentSnapshot.getString("price"));
                   imagenProduct = documentSnapshot.getString("image1");
                   double total =precio;
                   textFormTotal.setText(""+total+" €");
               }else{

               }
           }
       });

    }
    private void getUserInfo(String idUser) {
        mUsersProvider.getUser(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    if(documentSnapshot.contains("username"))
                    {
                        String username=documentSnapshot.getString("username");
                        nombreEmpresa.setText(username);
                    }

                    if(documentSnapshot.contains("localizacion"))
                    {
                        String localizacion=documentSnapshot.getString("localizacion");
                        direccionEmpresa.setText(localizacion);
                    }

                }
            }
        });
    }
    private void finalizarPedido() throws ParseException {
        String nombreEmpresa = this.nombreEmpresa.getText().toString();
        String direccionEmpresa = this.direccionEmpresa.getText().toString();
        String nombreProducto = this.nombreProducto.getText().toString();
        String categoria = this.categoria.getText().toString();
        String recogidaHora = this.recogidaHora.getText().toString();
        String recogidaDia = this.recogidaDia.getText().toString();
        String textFormTotal = this.textFormTotal.getText().toString().replace('€',' ');
        String textFormCantidad = this.textFormCantidad.getText().toString();
        String pago;

        int hora,minuto;

        SimpleDateFormat formatoDelTexto = new SimpleDateFormat("dd/MM/yyyy");
        if(recogidaHora.equals("Sin informacion")||recogidaDia.equals("Sin informacion")){
            Toast.makeText(OrderActivity.this, "Debes de completar los campos de recogida de dia y hora", Toast.LENGTH_SHORT).show();
            miDialog.dismiss();
        }else{
            if(!this.tarjeta.isChecked()&&!this.efectivo.isChecked()){
                Toast.makeText(OrderActivity.this, "Debes seleccionar un metodo de pago", Toast.LENGTH_SHORT).show();
                miDialog.dismiss();
            }else{
                if(this.tarjeta.isChecked()){
                    pago = "tarjeta";
                }else{
                    pago = "efectivo";
                }
                Pedido pedido = new Pedido();
                pedido.setIdNormal(authProvider.getUid());
                pedido.setIdEmpresa(idEmpresa);
                pedido.setProductName(nombreProducto);
                pedido.setNombreEmpresa(nombreEmpresa);
                pedido.setCategory(categoria);
                pedido.setDireccionEmpresa(direccionEmpresa);
                pedido.setDireccionEmpresa(direccionEmpresa);
                pedido.setTipoPago(pago);
                pedido.setPrecioUnidad(precio);
                Date date = formatoDelTexto.parse(recogidaDia);
                pedido.setDiaEntrega(date.getTime());
                pedido.setDiaRealizado(new Date().getTime());
                String [] partir = recogidaHora.split(":");
                hora = Integer.parseInt(partir[0]);
                minuto = Integer.parseInt((partir[1]));
                pedido.setHora(hora);
                pedido.setMinuto(minuto);
                pedido.setCantidad(Integer.parseInt(textFormCantidad));
                pedido.setFactura(Double.parseDouble(textFormTotal));
                pedido.setEstado("no listo");
                pedido.setImagenProduct(imagenProduct);
                guardarPedido(pedido);
                sendNotification("Producto: "+ nombreProducto +"\nCantidad: "+textFormCantidad +"\nPara el dia:"+recogidaDia);

                miDialog.dismiss();
            }
        }


    }

    private void guardarPedido(Pedido pedido){
        mPedidoProvider.save(pedido).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull  Task<Void> task) {
                if (task.isSuccessful()){
                    miDialog.dismiss();
                    Toast.makeText(OrderActivity.this, "Pedido realizado correctamente", Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(OrderActivity.this, "Pedido realizado incorrectamente", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void sendNotification(String comment){
        if(idEmpresa==null){
            return;
        }
        mTokenProvider.getToken(idEmpresa).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");
                        System.out.println("------------------------------------------------------------------------------"+token);
                        System.out.println("------------------------------------------------------------------------------"+idEmpresa);
                        System.out.println("------------------------------------------------------------------------------"+authProvider.getUid());
                        Map<String,String> data = new HashMap<>();
                        data.put("title","Nuevo pedido");
                        data.put("body",comment);
                        FCMBody body = new FCMBody(token,"high","4500s",data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if(response.body()!=null){
                                    if(response.body().getSuccess()==1){
                                        Toast.makeText(OrderActivity.this, "La notificacion se envio correctamente", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(OrderActivity.this, "La notificacion no se envio ", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(OrderActivity.this, "La notificacion no se envio ", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    }
                }
                else{
                    Toast.makeText(OrderActivity.this, "El token del ususario no existe ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
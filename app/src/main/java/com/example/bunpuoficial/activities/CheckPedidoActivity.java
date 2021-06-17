package com.example.bunpuoficial.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.models.FCMBody;
import com.example.bunpuoficial.models.FCMResponse;
import com.example.bunpuoficial.models.Pedido;
import com.example.bunpuoficial.providers.NotificationProvider;
import com.example.bunpuoficial.providers.PedidoProvider;
import com.example.bunpuoficial.providers.TokenProvider;
import com.example.bunpuoficial.providers.UsersProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckPedidoActivity extends AppCompatActivity {

    String mExtraPedidoId;
    PedidoProvider mPedidoProvider;
    TokenProvider mTokenProvider;
    UsersProvider usersProvider;
    NotificationProvider mNotificationProvider;
    ImageView tipoImagen;
    TextView nombreUsuario;
    TextView direccionUsuario;
    TextView nombreProducto;
    TextView categoria;
    TextView total;
    TextView cantidad;
    TextView diaRecogida;
    TextView horaRecogida;
    TextView tipoPago;
    TextView estado;
    Button terminarPedido;
    String idUser,nombreEmpresaNotificacion,nombreProductoNotificacion,totalFactura,estadoString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_pedido_main);

        tipoImagen=findViewById(R.id.tipoImagen);
        nombreUsuario =findViewById(R.id.textFormNombreUsuario);
        direccionUsuario =findViewById(R.id.textDireccionUsuario);
        nombreProducto=findViewById(R.id.textFormNombreProducto);
        categoria=findViewById(R.id.textFormCategoriaProducto);
        total=findViewById(R.id.textFormTotal);
        cantidad=findViewById(R.id.textFormCantidad);
        diaRecogida=findViewById(R.id.textRecogidaDia);
        horaRecogida=findViewById(R.id.textHora);
        tipoPago=findViewById(R.id.textTipopago);
        estado=findViewById(R.id.textEstado);
        terminarPedido=findViewById(R.id.terminarPedido);

        usersProvider = new UsersProvider();
        mNotificationProvider = new NotificationProvider();
        mTokenProvider=new TokenProvider();
        mExtraPedidoId= getIntent().getStringExtra("idPedido");

        mPedidoProvider=new PedidoProvider();

        terminarPedido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Pedido pedido=new Pedido();
                pedido.setEstado("listo");

                mPedidoProvider.updatePedido(pedido,mExtraPedidoId).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Toast.makeText(CheckPedidoActivity.this, "Pedido Terminado", Toast.LENGTH_SHORT).show();
                            sendNotification("Empresa: "+nombreEmpresaNotificacion+"\nProducto: "+nombreProductoNotificacion+"\nTotal factura "+totalFactura+" €");
                            finish();
                        }
                        else
                        {
                            Toast.makeText(CheckPedidoActivity.this, "No se pudo actualizar el pedido", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


        mPedidoProvider.getPedidoByid(mExtraPedidoId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    idUser= documentSnapshot.getString("idNormal");
                    nombreEmpresaNotificacion = documentSnapshot.getString("nombreEmpresa");
                    usersProvider.getUser(documentSnapshot.getString("idNormal")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            nombreUsuario.setText(documentSnapshot.getString("username"));
                            direccionUsuario.setText(documentSnapshot.getString("localizacion"));
                        }
                    });
                    nombreProductoNotificacion=documentSnapshot.getString("productName");
                    nombreProducto.setText(nombreEmpresaNotificacion);
                    categoria.setText(documentSnapshot.getString("category"));
                    totalFactura = ""+documentSnapshot.getLong("factura");
                    total.setText(totalFactura+" €");
                    cantidad.setText(""+documentSnapshot.getLong("cantidad"));

                    long diaEntrega=documentSnapshot.getLong("diaEntrega");
                    Date date=new Date(diaEntrega);
                    Calendar calendar=new GregorianCalendar();
                    calendar.setTime(date);
                    String string_diaEntrega= calendar.get(Calendar.DAY_OF_MONTH)+" - "+(calendar.get(Calendar.MONTH)+1)+" - "+calendar.get(Calendar.YEAR);

                    diaRecogida.setText(string_diaEntrega);

                    horaRecogida.setText(documentSnapshot.getLong("hora")+":"+documentSnapshot.getLong("minuto"));

                    String tPago=documentSnapshot.getString("tipoPago");

                    if(tPago.equals("efectivo"))
                    {
                        tipoImagen.setImageResource(R.drawable.efectivo);
                        tipoPago.setText("Efectivo");
                    }
                    else
                    {
                        tipoImagen.setImageResource(R.drawable.tarjeta_de_credito);
                        tipoPago.setText("Tarjeta");
                    }
                    estadoString = documentSnapshot.getString("estado");
                    estado.setText(documentSnapshot.getString("estado"));
                    if(estadoString.equals("listo")){
                        terminarPedido.setVisibility(View.GONE);
                    }
            }
        });
    }
    private void sendNotification(String comment){
        if(idUser==null){
            return;
        }
        mTokenProvider.getToken(idUser).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    if(documentSnapshot.contains("token")){
                        String token = documentSnapshot.getString("token");
                        Map<String,String> data = new HashMap<>();
                        data.put("title","Pedido listo");
                        data.put("idPedido",mExtraPedidoId);
                        data.put("body",comment);
                        FCMBody body = new FCMBody(token,"high","4500s",data);
                        mNotificationProvider.sendNotification(body).enqueue(new Callback<FCMResponse>() {
                            @Override
                            public void onResponse(Call<FCMResponse> call, Response<FCMResponse> response) {
                                if(response.body()!=null){
                                    if(response.body().getSuccess()==1){
                                        Toast.makeText(CheckPedidoActivity.this, "La notificacion se envio correctamente", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(CheckPedidoActivity.this, "La notificacion no se envio ", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    Toast.makeText(CheckPedidoActivity.this, "La notificacion no se envio ", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<FCMResponse> call, Throwable t) {

                            }
                        });
                    }
                }
                else{
                    Toast.makeText(CheckPedidoActivity.this, "El token del ususario no existe ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
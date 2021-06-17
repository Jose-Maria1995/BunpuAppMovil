package com.example.bunpuoficial.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.models.FCMBody;
import com.example.bunpuoficial.models.FCMResponse;
import com.example.bunpuoficial.providers.NotificationProvider;
import com.example.bunpuoficial.providers.PedidoProvider;
import com.example.bunpuoficial.providers.TokenProvider;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FacturaActivity extends AppCompatActivity {

    String mExtraPedidoId;
    PedidoProvider mPedidoProvider;
    TokenProvider mTokenProvider;
    NotificationProvider mNotificationProvider;

    String idUser;

    ImageView tipoImagen;
    TextView nombreEmpresa;
    TextView direccionEmpresa;
    TextView nombreProducto;
    TextView categoria;
    TextView total;
    TextView cantidad;
    TextView diaRecogida;
    TextView horaRecogida;
    TextView tipoPago;
    TextView estado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.factura_main);


        tipoImagen=findViewById(R.id.tipoImagen);
        nombreEmpresa=findViewById(R.id.textFormNombreEmpresa);
        direccionEmpresa=findViewById(R.id.textDireccionEmpresa);
        nombreProducto=findViewById(R.id.textFormNombreProducto);
        categoria=findViewById(R.id.textFormCategoriaProducto);
        total=findViewById(R.id.textFormTotal);
        cantidad=findViewById(R.id.textFormCantidad);
        diaRecogida=findViewById(R.id.textRecogidaDia);
        horaRecogida=findViewById(R.id.textHora);
        tipoPago=findViewById(R.id.textTipopago);
        estado=findViewById(R.id.textEstado);

        mExtraPedidoId= getIntent().getStringExtra("idPedido");

        mNotificationProvider = new NotificationProvider();
        mTokenProvider=new TokenProvider();
        mPedidoProvider=new PedidoProvider();

        mPedidoProvider.getPedidoByid(mExtraPedidoId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                    idUser=documentSnapshot.getString("idNormal");
                   nombreEmpresa.setText(documentSnapshot.getString("nombreEmpresa"));
                   direccionEmpresa.setText(documentSnapshot.getString("direccionEmpresa"));
                   nombreProducto.setText(documentSnapshot.getString("productName"));
                   categoria.setText(documentSnapshot.getString("category"));
                   total.setText(""+documentSnapshot.getLong("factura"));
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
                    estado.setText(documentSnapshot.getString("estado"));
            }
        });
    }
}
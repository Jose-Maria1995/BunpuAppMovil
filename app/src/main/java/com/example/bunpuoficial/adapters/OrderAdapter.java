package com.example.bunpuoficial.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.activities.ChatActivity;
import com.example.bunpuoficial.activities.CheckPedidoActivity;
import com.example.bunpuoficial.activities.FacturaActivity;
import com.example.bunpuoficial.activities.UserProfileActivity;
import com.example.bunpuoficial.models.Pedido;
import com.example.bunpuoficial.providers.AuthProvider;
import com.example.bunpuoficial.providers.LikesProvider;
import com.example.bunpuoficial.providers.PedidoProvider;
import com.example.bunpuoficial.providers.ProductProvider;
import com.example.bunpuoficial.providers.UsersProvider;
import com.example.bunpuoficial.utils.RelativeTime;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.type.DateTime;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

import de.hdodenhof.circleimageview.CircleImageView;

public class OrderAdapter extends FirestoreRecyclerAdapter<Pedido, OrderAdapter.ViewHolder>
{
    Context context;
    UsersProvider mUsersProvider;
    AuthProvider mAuthProvider;
    PedidoProvider mPedidoProvider;
    ProductProvider mProductProvider;
    ListenerRegistration mListenerRegistration;
    String idUsuario;
    String idEmpresa;

    public OrderAdapter(FirestoreRecyclerOptions<Pedido> options,Context context)
    {
        super(options);
        this.context=context;

        mUsersProvider = new UsersProvider();
        mAuthProvider=new AuthProvider();
        mPedidoProvider=new PedidoProvider();
        mProductProvider=new ProductProvider();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Pedido model) {
        DocumentSnapshot document = getSnapshots().getSnapshot(position);
        String pedidoId=document.getId();


        mPedidoProvider.getPedidoByid(pedidoId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                idUsuario = documentSnapshot.getString("idNormal");
                idEmpresa = documentSnapshot.getString("idEmpresa");
                final String idUsuarioChat =idUsuario;
                final String idEmpresaChat =idEmpresa;
                if(documentSnapshot.contains("diaEntrega"))
                {
                    long diaEntrega=documentSnapshot.getLong("diaEntrega");
                    Date date=new Date(diaEntrega);
                    Calendar calendar=new GregorianCalendar();
                    calendar.setTime(date);
                    String string_diaEntrega= calendar.get(Calendar.DAY_OF_MONTH)+" - "+(calendar.get(Calendar.MONTH)+1)+" - "+calendar.get(Calendar.YEAR);
                    holder.fechaEntrega.setText(string_diaEntrega);
                }

                if(documentSnapshot.contains("diaRealizado"))
                {
                    long diaRealizacion=documentSnapshot.getLong("diaRealizado");
                    Date date=new Date(diaRealizacion);
                    Calendar calendar=new GregorianCalendar();
                    calendar.setTime(date);
                    String string_diaRealizacion= calendar.get(Calendar.DAY_OF_MONTH)+" - "+(calendar.get(Calendar.MONTH)+1)+" - "+calendar.get(Calendar.YEAR);
                    holder.fechaRealizacion.setText(string_diaRealizacion);
                }

                if(documentSnapshot.contains("nombreEmpresa"))
                {
                    if(mAuthProvider.getUid().equals(documentSnapshot.getString("idNormal"))){
                        System.out.println("---------------------------Entra en nombre empresa");
                    String nombreEmpresa=documentSnapshot.getString("nombreEmpresa");
                    holder.nombreEmpresa.setText(nombreEmpresa);
                        mUsersProvider.getUser(documentSnapshot.getString("idEmpresa")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String imagenEmpresa=documentSnapshot.getString("image_profile");
                                Picasso.get().load(imagenEmpresa).into(holder.imagenEmpresa);
                            }
                        });
                    }
                    else if(mAuthProvider.getUid().equals(documentSnapshot.getString("idEmpresa"))) {
                        System.out.println("---------------------------Entra en nombre usuario");
                        mUsersProvider.getUser(documentSnapshot.getString("idNormal")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String imagenEmpresa=documentSnapshot.getString("image_profile");
                                Picasso.get().load(imagenEmpresa).into(holder.imagenEmpresa);
                            }
                        });
                        mUsersProvider.getUser(documentSnapshot.getString("idNormal")).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String nombreUsuarioNormal  = documentSnapshot.getString("username");
                                holder.nombreEmpresa.setText(nombreUsuarioNormal);
                            }
                        });
                    }

                }

                if(documentSnapshot.contains("productName"))
                {
                    String nombreProducto=documentSnapshot.getString("productName");
                    holder.nombreProducto.setText(nombreProducto);
                }

                if(documentSnapshot.contains("cantidad"))
                {
                    double cantidad=documentSnapshot.getDouble("cantidad");
                    holder.cantidad.setText(""+cantidad);
                }

                if(documentSnapshot.contains("factura"))
                {
                    double factura=documentSnapshot.getDouble("factura");
                    holder.precioTotal.setText(""+factura);
                }

                if(documentSnapshot.contains("hora") && documentSnapshot.contains("minuto"))
                {
                    Long hora=documentSnapshot.getLong("hora");
                    Long minuto=documentSnapshot.getLong("minuto");
                    holder.horaRealizacion.setText(hora+":"+minuto);
                }

                if(documentSnapshot.contains("imagenProduct"))
                {
                    String imageProduct=documentSnapshot.getString("imagenProduct");
                    Picasso.get().load(imageProduct).into(holder.imagenProducto);
                }
                holder.verEstado.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUsersProvider.getTypeUser(mAuthProvider.getUid()).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                if(documentSnapshot.getString("typeUser").equals("Empresa"))
                                {
                                    Intent intent=new Intent(context, CheckPedidoActivity.class);
                                    intent.putExtra("idPedido",pedidoId);
                                    context.startActivity(intent);
                                }
                                else
                                {
                                    Intent intent=new Intent(context, FacturaActivity.class);
                                    intent.putExtra("idPedido",pedidoId);
                                    context.startActivity(intent);
                                }
                            }
                        });
                    }
                });
                holder.abrirChat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(idEmpresa.equals(mAuthProvider.getUid())){
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("idUser1",mAuthProvider.getUid());
                            intent.putExtra("idUser2",idUsuarioChat);
                            context.startActivity(intent);
                        }else{
                            Intent intent = new Intent(context, ChatActivity.class);
                            intent.putExtra("idUser1",mAuthProvider.getUid());
                            intent.putExtra("idUser2",idEmpresaChat);
                            context.startActivity(intent);
                        }
                    }
                });
            }
        });

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview_pedido_normal,parent,false);
        return new ViewHolder(view);
    }
    public ListenerRegistration getmListenerRegistrationNomral()
    {
        return mListenerRegistration;
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {
        CircleImageView imagenEmpresa;
        ImageView imagenProducto;
        TextView nombreEmpresa;
        TextView nombreProducto;
        TextView fechaRealizacion;
        TextView fechaEntrega;
        TextView horaRealizacion;
        TextView cantidad;
        TextView precioTotal;
        Button verEstado;
       CircleImageView abrirChat;

        View viewHolder;

        public ViewHolder (View view)
        {
            super(view);
            imagenEmpresa=view.findViewById(R.id.imagenEmpresa);
            imagenProducto=view.findViewById(R.id.imagenProducto);
            nombreEmpresa=view.findViewById(R.id.nombreEmpresa);
            nombreProducto=view.findViewById(R.id.nombreProducto);
            fechaRealizacion=view.findViewById(R.id.fechaRealizacion);
            fechaEntrega=view.findViewById(R.id.fechaEntrega);
            horaRealizacion=view.findViewById(R.id.horaRealizacion);
            cantidad=view.findViewById(R.id.cantidad);
            precioTotal=view.findViewById(R.id.precioTotal);
            verEstado = view.findViewById(R.id.bottom_ver_estado);
            abrirChat = view.findViewById(R.id.imagenChat);


            viewHolder=view;
        }

    }
}

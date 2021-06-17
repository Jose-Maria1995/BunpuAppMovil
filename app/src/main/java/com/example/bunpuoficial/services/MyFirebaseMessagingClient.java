package com.example.bunpuoficial.services;

import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.RemoteInput;
import androidx.core.app.TaskStackBuilder;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.activities.FacturaActivity;
import com.example.bunpuoficial.channel.NotificacionHelper;
import com.example.bunpuoficial.models.Message;
import com.example.bunpuoficial.receivers.MessageReceiver;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Map;
import java.util.Random;

public class MyFirebaseMessagingClient extends FirebaseMessagingService {

    public static final String NOTIFICATION_REPLY="NotificationReply";

    @Override
    public void onNewToken(@NonNull  String s) {
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Map<String,String> data = remoteMessage.getData();
        String title = data.get("title");
        String body = data.get("body");
        if(title!=null){
            if(title.equals("Nuevo mensaje"))
            {
                showNotificationMessage(data);
            }
            else if(title.equals("Pedido listo"))
            {

                showNotificationPedido(data);
            }
            else
            {
                showNotification(title,body);
            }
        }
    }
     private void showNotification (String title,String body){
         NotificacionHelper notificacionHelper = new NotificacionHelper(getBaseContext());
         NotificationCompat.Builder builder = notificacionHelper.getNotifications(title,body);
         Random random=new Random();
         int n=random.nextInt(10000);
         notificacionHelper.getManager().notify(n,builder.build());
     }

    private void showNotificationMessage (Map<String,String> data){

        String imageSender=data.get("imageSender");
        String imageReceiver=data.get("imageReceiver");

        getImageSender(data, imageSender, imageReceiver);
    }

    private void showNotificationPedido (Map<String,String>data){
        String title,body,idPedido;
        title = data.get("title");
        body = data.get("body");
        idPedido = data.get("idPedido");
        Intent activityPedido=new Intent(this, FacturaActivity.class);
        activityPedido.putExtra("idPedido",idPedido);

        TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(this);
        taskStackBuilder.addParentStack(FacturaActivity.class);
        taskStackBuilder.addNextIntent(activityPedido);
        PendingIntent pendingIntent = taskStackBuilder.getPendingIntent(1,PendingIntent.FLAG_UPDATE_CURRENT);


        NotificacionHelper notificacionHelper = new NotificacionHelper(getBaseContext());
        NotificationCompat.Builder builder = notificacionHelper.getNotifications(title,body);
        builder.setContentIntent(pendingIntent);
        Random random=new Random();
        int n=random.nextInt(10000);
        notificacionHelper.getManager().notify(n,builder.build());
    }

    private void getImageSender(Map<String,String> data,String imageSender, String imageReceiver) {

        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.get().load(imageSender).into(new Target() {
                            @Override
                            public void onBitmapLoaded(Bitmap bitmapSender, Picasso.LoadedFrom from) {
                                getImageReceiver(data,imageReceiver, bitmapSender);
                            }

                            @Override
                            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                getImageReceiver(data, imageReceiver,null);
                            }

                            @Override
                            public void onPrepareLoad(Drawable placeHolderDrawable) {

                            }
                        });
                    }
                });
    }

    private void getImageReceiver(Map<String,String> data,String imageReceiver, Bitmap bitmapSender) {

        Picasso.get().load(imageReceiver).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmapReceiver, Picasso.LoadedFrom from) {
                notifyMessage(data, bitmapSender,bitmapReceiver);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                notifyMessage(data,bitmapSender,null);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });

    }

    private void notifyMessage(Map<String,String> data, Bitmap bitmapSender, Bitmap bitmapReceiver)
    {
        String usernameSender=data.get("usernameSender");
        String usernameReceiver=data.get("usernameReceiver");
        String lastMessage = data.get("lastMessage");
        String messagesJSON=data.get("messages");

        String imageSender=data.get("imageSender");
        String imageReceiver=data.get("imageReceiver");

        String idSender=data.get("idSender");
        String idReceiver=data.get("idReceiver");
        String idChat=data.get("idChat");
        int idNotification=Integer.parseInt(data.get("idNotification"));

        Intent intent=new Intent(this, MessageReceiver.class);
        intent.putExtra("idSender",idSender);
        intent.putExtra("idReceiver",idReceiver);
        intent.putExtra("idChat",idChat);
        intent.putExtra("idNotification",idNotification);
        intent.putExtra("usernameSender",usernameSender);
        intent.putExtra("usernameReceiver",usernameReceiver);
        intent.putExtra("imageSender",imageSender);
        intent.putExtra("imageReceiver",imageReceiver);
        PendingIntent pendingIntent=PendingIntent.getBroadcast(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        RemoteInput remoteInput=new RemoteInput.Builder(NOTIFICATION_REPLY).setLabel("Tu mensaje...").build();

        NotificationCompat.Action action=new NotificationCompat.Action.Builder(R.mipmap.ic_launcher, "Responder", pendingIntent).addRemoteInput(remoteInput).build();

        Gson gson=new Gson();
        Message[] messages=gson.fromJson(messagesJSON, Message[].class); //Transformacion de un String de mensajes a un arreglo de objetos


        NotificacionHelper notificacionHelper = new NotificacionHelper(getBaseContext());
        NotificationCompat.Builder builder = notificacionHelper.getNotificationMessage(messages,usernameSender,usernameReceiver,lastMessage,bitmapSender,bitmapReceiver,action);
        notificacionHelper.getManager().notify(idNotification,builder.build());
    }
}

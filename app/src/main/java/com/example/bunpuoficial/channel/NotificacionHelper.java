package com.example.bunpuoficial.channel;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.icu.text.CaseMap;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.Person;
import androidx.core.graphics.drawable.IconCompat;
import androidx.core.os.MessageCompat;

import com.example.bunpuoficial.R;
import com.example.bunpuoficial.models.Message;

import java.util.Date;

public class NotificacionHelper extends ContextWrapper {

    private static final String CHANNEL_ID ="com.example.bunpuoficial.channel";
    private static final String CHANNEL_NAME ="BunpuOficial";

    private NotificationManager manager;

     public NotificacionHelper(Context context){
         super(context);
         if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
             createChannels();
         }
     }

     @RequiresApi(api = Build.VERSION_CODES.O)
     private void createChannels(){
         NotificationChannel notificationChannel = new NotificationChannel(
           CHANNEL_ID,
           CHANNEL_NAME,
            NotificationManager.IMPORTANCE_HIGH
         );
         notificationChannel.enableLights(true);
         notificationChannel.enableVibration(true);
         notificationChannel.setLightColor(Color.BLUE);
         notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
         getManager().createNotificationChannel(notificationChannel);
     }

    public NotificationManager getManager() {
        if(manager==null){
            manager=(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }

    public NotificationCompat.Builder getNotifications(String title,String body){
         return new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID)
                 .setContentTitle(title)
                 .setContentText(body)
                 .setAutoCancel(true)
                 .setColor(Color.GREEN)
                 .setSmallIcon(R.drawable.bunpu_arriba)
                 .setStyle(new NotificationCompat.BigTextStyle().bigText(body).setBigContentTitle(title));


    }

    public NotificationCompat.Builder getNotificationMessage(Message[] messages, String usernameSender, String usernameReceiver, String lastMessage, Bitmap bitmapSender,Bitmap bitmapReceiver,NotificationCompat.Action action)
    {
        Person person1=null;
        if(bitmapReceiver==null)
        {
            person1=new Person.Builder()
                    .setName(usernameReceiver)
                    .setIcon(IconCompat.createWithResource(getApplicationContext(), R.drawable.imagen_perfil_predeterminada))
                    .build();
        }
        else
        {
            person1=new Person.Builder()
                    .setName(usernameReceiver)
                    .setIcon(IconCompat.createWithBitmap(bitmapReceiver))
                    .build();
        }

        Person person2=null;

        if(bitmapSender==null)
        {
            person2=new Person.Builder()
                    .setName(usernameSender)
                    .setIcon(IconCompat.createWithResource(getApplicationContext(),R.drawable.imagen_perfil_predeterminada))
                    .build();
        }
        else
        {
            person2=new Person.Builder()
                    .setName(usernameSender)
                    .setIcon(IconCompat.createWithBitmap(bitmapSender))
                    .build();
        }

        NotificationCompat.MessagingStyle messagingStyle=new NotificationCompat.MessagingStyle(person1);
        NotificationCompat.MessagingStyle.Message message1=new
                NotificationCompat.MessagingStyle.Message(lastMessage, new Date().getTime(),person1);

        messagingStyle.addMessage(message1);

        for (Message m: messages)
        {
            NotificationCompat.MessagingStyle.Message message2=new
                    NotificationCompat.MessagingStyle.Message(m.getMessage(), m.getTimestamp(),person2);

            messagingStyle.addMessage(message2);
        }

        return  new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID).setSmallIcon(R.mipmap.ic_launcher).setStyle(messagingStyle).addAction(action);
    }
}

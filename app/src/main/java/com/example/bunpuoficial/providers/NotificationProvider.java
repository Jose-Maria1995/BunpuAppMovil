package com.example.bunpuoficial.providers;

import com.example.bunpuoficial.models.FCMBody;
import com.example.bunpuoficial.models.FCMResponse;
import com.example.bunpuoficial.retrofit.IFCMApi;
import com.example.bunpuoficial.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Retrofit;

public class NotificationProvider {

    private final String url = "https://fcm.googleapis.com";

    public NotificationProvider() {
    }

    public Call<FCMResponse> sendNotification(FCMBody body){
        return RetrofitClient.getClient(url).create(IFCMApi.class).send(body);
    }
}

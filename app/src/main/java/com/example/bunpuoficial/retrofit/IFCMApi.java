package com.example.bunpuoficial.retrofit;

import com.example.bunpuoficial.models.FCMBody;
import com.example.bunpuoficial.models.FCMResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IFCMApi {

    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAHrPDMeY:APA91bEH7phBsZa6JKLJGf4R06v7c568ygb7e0oiwSF6XH3wAcbOVH_C9D077QropCrpCUF8Ry0sARv5c5E7lqo4-ufi9Jer_Gp50ncfXmBKo64ngU2tqzbLH4_MdXBbzv7xN9IWGQrd"})
    @POST("fcm/send")
    Call<FCMResponse> send(@Body FCMBody body);
}

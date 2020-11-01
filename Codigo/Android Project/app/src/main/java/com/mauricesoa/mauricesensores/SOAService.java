package com.mauricesoa.mauricesensores;


import com.google.gson.JsonObject;
import com.mauricesoa.mauricesensores.Requests.EventRequest;
import com.mauricesoa.mauricesensores.Requests.LogInRequest;
import com.mauricesoa.mauricesensores.Requests.SignUpRequest;
import com.mauricesoa.mauricesensores.Responses.LoginRefreshResponse;
import com.mauricesoa.mauricesensores.Responses.RegisterEventProdResponse;
import com.mauricesoa.mauricesensores.Responses.RegisterEventTestResponse;
import com.mauricesoa.mauricesensores.Responses.SignUpResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface SOAService {

    @Headers({"Content-Type: application/json"})

    @POST("api/register")
    Call<JsonObject> register(@Body SignUpRequest request);

    @POST("api/login")
    Call<JsonObject> login(@Body LogInRequest request);

    @PUT("api/refresh")
    Call<JsonObject> refresh(@Header("Authorization") String authorization);

    @POST("api/event")
    Call<JsonObject> eventTest(@Header("Authorization") String authorization, @Body EventRequest request);

    @POST("api/event")
    Call<JsonObject> eventProd(@Header("Authorization") String authorization, @Body EventRequest request);

}

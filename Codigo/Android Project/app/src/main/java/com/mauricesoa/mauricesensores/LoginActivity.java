package com.mauricesoa.mauricesensores;

import androidx.appcompat.app.AppCompatActivity;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mauricesoa.mauricesensores.Requests.LogInRequest;
import com.mauricesoa.mauricesensores.Responses.BadLoginRefreshResponse;
import com.mauricesoa.mauricesensores.Responses.LoginRefreshResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class LoginActivity extends AppCompatActivity {

    private EditText userText;
    private EditText passText;
    private Button loginButton;
    private TextView resultTextView;

    private static final int EMPTYFIELD = -1;
    private static final int SHORTPASSWORD = -2;
    private static final int OK = 0;
    private static final int PASSWORDLENGTH = 8;

    private BroadcastReceiver br;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /***************************Busco elementos del layout*************************************/
        userText = (EditText) findViewById(R.id.userText);
        passText = (EditText) findViewById(R.id.passText);
        loginButton = (Button) findViewById(R.id.loginButton);
        resultTextView = (TextView) findViewById(R.id.resultTextView);



        /******************Agrego funcion al boton de log in***************************************/
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                resultTextView.setText("");
                loginButton.setEnabled(false);

                /*************Obtengo los campos de la pantalla*********/
                String user = userText.getText().toString();
                String password = passText.getText().toString();


                /*************Valido los campos*************/
                int validation = validateFields(user,password);


                /***********Evaluo Resultado de la Validacion**********/
                if(validation == OK)
                {

                    LogInRequest request = new LogInRequest();
                    request.setEmail(user);
                    request.setPassword(password);

                    Retrofit retrofit = new Retrofit.Builder()
                            .addConverterFactory(GsonConverterFactory.create())
                            .baseUrl(getString(R.string.api_url))
                            .build();

                    SOAService soaService = retrofit.create(SOAService.class);

                    Call<JsonObject> call = soaService.login(request);

                    call.enqueue(new Callback<JsonObject>() {
                                     @Override
                                     public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                                         Gson gson = new Gson();

                                         if(response.isSuccessful()){

                                             Log.e("Respuesta Correcta", response.toString());
                                             Log.e("Respuesta Correcta", response.body().toString());

                                             LoginRefreshResponse responseClass = gson.fromJson(response.body().toString(),
                                                     LoginRefreshResponse.class);

                                             Intent intentLogin = new Intent(LoginActivity.this,SensorActivity.class);

                                             intentLogin.putExtra("env", "PROD");
                                             intentLogin.putExtra("token", responseClass.getToken());
                                             intentLogin.putExtra("toke_refresh", responseClass.getToken_refresh());


                                             startActivity(intentLogin);

                                         }else
                                         {
                                             Log.e("Respuesta Incorrecta", response.toString());


                                             BadLoginRefreshResponse responseClass = gson.fromJson(response.errorBody().charStream(),
                                                     BadLoginRefreshResponse.class);

                                             resultTextView.setText(responseClass.getMsg());

                                             loginButton.setEnabled(true);
                                         }


                                     }

                                     @Override
                                     public void onFailure(Call<JsonObject> call, Throwable t) {
                                         resultTextView.setText(R.string.error);
                                         loginButton.setEnabled(true);
                                     }
                                 }

                    );


                }else
                {
                    if(validation == EMPTYFIELD)
                    {
                        resultTextView.setText(R.string.campo_vacio);
                    }else if(validation == SHORTPASSWORD)
                    {
                        resultTextView.setText(R.string.contrasenia_corta);
                    }
                }

            }
        });



    }


    @Override
    public void onStart() {

        super.onStart();

        /******************Me registro al broadcast de conexion de internet************************/
        br = new MyBroadcastReceiver();

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
        this.registerReceiver(br, filter);

        loginButton.setEnabled(true);
    }


    @Override
    protected void onPause() {
        super.onPause();
        /******************Me desregistro del broadcast de conexion de internet********************/
        unregisterReceiver(br);
    }

    private int validateFields(String user, String password)
    {
        if(TextUtils.isEmpty(user) ||
                TextUtils.isEmpty(password) )
        {
            return EMPTYFIELD;
        }else
        {
            if(password.length() < PASSWORDLENGTH)
            {
                return SHORTPASSWORD;
            }
        }

        return OK;
    }



}

package com.mauricesoa.mauricesensores;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mauricesoa.mauricesensores.Requests.SignUpRequest;
import com.mauricesoa.mauricesensores.Responses.BadSignUpRegisterEventResponse;
import com.mauricesoa.mauricesensores.Responses.SignUpResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity extends AppCompatActivity {

    private EditText envEditText;
    private EditText nameEditText;
    private EditText lastnameEditText;
    private EditText  dniEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText  commissionEditText;
    private Button signupButton;
    private TextView resultTextView;

    private static final int EMPTYFIELD = -1;
    private static final int SHORTPASSWORD = -2;
    private static final int BADENVIROMENT = -3;
    private static final int OK = 0;

    private static final int PASSWORDLENGTH = 8;


    private BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        /***************************Busco elementos del layout*************************************/
        envEditText = (EditText) findViewById(R.id.envEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        lastnameEditText = (EditText) findViewById(R.id.lastnameEditText);
        dniEditText = (EditText) findViewById(R.id.dniEditText);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        commissionEditText = (EditText) findViewById(R.id.commissionEditText);
        signupButton = (Button) findViewById(R.id.signupButton);
        resultTextView = (TextView) findViewById(R.id.resultTextView);


        /******************Agrego funcion al boton de sign up**************************************/
        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                signupButton.setEnabled(true);
                resultTextView.setText("");

                /*************Obtengo los campos de la pantalla*********/
                String env = envEditText.getText().toString();
                String name = nameEditText.getText().toString();
                String lastname = lastnameEditText.getText().toString();
                Long dni;
                if(TextUtils.isEmpty(dniEditText.getText().toString()))
                {
                    dni = 0L;
                }else
                {
                    dni = Long.parseLong(dniEditText.getText().toString());
                }
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                Long commission;
                if(TextUtils.isEmpty(commissionEditText.getText().toString()))
                {
                    commission = 0L;
                }else
                {
                    commission = Long.parseLong(commissionEditText.getText().toString());
                }

                /*************Valido los campos*************/
                int validation = validateFields(env,name,lastname,dni,email,password,commission);


                /***********Evaluo Resultado de la Validacion**********/
                if(validation == OK)
                {

                    SignUpRequest request = new SignUpRequest();
                    request.setEnv(env);
                    request.setName(name);
                    request.setLastname(lastname);
                    request.setDni(dni);
                    request.setEmail(email);
                    request.setPassword(password);
                    request.setCommission(commission);

                    Retrofit retrofit = new Retrofit.Builder()
                                            .addConverterFactory(GsonConverterFactory.create())
                                            .baseUrl(getString(R.string.api_url))
                                            .build();

                    SOAService soaService = retrofit.create(SOAService.class);

                    Call<JsonObject> call = soaService.register(request);

                    call.enqueue(new Callback<JsonObject>() {
                                     @Override
                                     public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                                         Gson gson = new Gson();

                                         if(response.isSuccessful()){
                                             Log.e("Respuesta Correcta", response.toString());
                                             Log.e("Respuesta Correcta", response.body().toString());

                                             SignUpResponse responseClass = gson.fromJson(response.body().toString(),
                                                     SignUpResponse.class);

                                             Intent intentSignUp = new Intent(SignupActivity.this,SensorActivity.class);

                                             intentSignUp.putExtra("env", responseClass.getEnv());
                                             intentSignUp.putExtra("token", responseClass.getToken());
                                             intentSignUp.putExtra("toke_refresh", responseClass.getToken_refresh());

                                             startActivity(intentSignUp);

                                         }else
                                         {
                                             Log.e("Respuesta Incorrecta", response.toString());


                                             BadSignUpRegisterEventResponse responseClass = gson.fromJson(response.errorBody().charStream(),
                                                     BadSignUpRegisterEventResponse.class);

                                             resultTextView.setText(responseClass.getMsg());
                                             signupButton.setEnabled(true);
                                         }


                                     }

                                     @Override
                                     public void onFailure(Call<JsonObject> call, Throwable t) {
                                         resultTextView.setText(R.string.error);
                                         signupButton.setEnabled(true);
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
                    }else if(validation == BADENVIROMENT)
                    {
                        resultTextView.setText(R.string.ambiente_erroneo);
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
    }


    @Override
    protected void onPause() {
        super.onPause();
        /******************Me desregistro del broadcast de conexion de internet********************/
        unregisterReceiver(br);

    }

    private int validateFields(String env,String name,String lastname,Long dni,String email,String password,Long commission)
    {
        if(TextUtils.isEmpty(env) ||
            TextUtils.isEmpty(name) ||
            TextUtils.isEmpty(lastname) ||
            dni == 0L ||
            TextUtils.isEmpty(email)||
            TextUtils.isEmpty(password) ||
            commission == 0L )
        {
            return EMPTYFIELD;
        }else
        {
            if(password.length() < PASSWORDLENGTH)
            {
                return SHORTPASSWORD;
            }else if( !env.equals("TEST") && !env.equals("PROD") )
            {
                return BADENVIROMENT;
            }
        }

        return OK;
    }
}

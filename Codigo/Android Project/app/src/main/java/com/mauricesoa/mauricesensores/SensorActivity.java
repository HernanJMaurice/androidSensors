package com.mauricesoa.mauricesensores;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.mauricesoa.mauricesensores.Requests.EventRequest;
import com.mauricesoa.mauricesensores.Responses.BadLoginRefreshResponse;
import com.mauricesoa.mauricesensores.Responses.BadSignUpRegisterEventResponse;
import com.mauricesoa.mauricesensores.Responses.LoginRefreshResponse;
import com.mauricesoa.mauricesensores.Responses.RegisterEventProdResponse;
import com.mauricesoa.mauricesensores.Responses.RegisterEventTestResponse;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SensorActivity extends AppCompatActivity implements SensorEventListener {

    private TextView ejeXAceTextView;
    private TextView ejeYAceTextView;
    private TextView ejeZAceTextView;

    private TextView luminosidadTextView;

    private Button registerAcelerometerButton;
    private Button registrarDatosLuz;
    private Button listarEventos;
    private Button eliminarRegistroButton;
    private Button eliminarLumButton;
    private Button eliminarAcelButton;

    private TextView resultTextView;

    private SensorManager sensorManager;
    private Sensor acelerometro;
    private Sensor luminosidad;

    private String env;
    private String token;
    private String token_refresh;


    private ArrayList<EventRegistered> listaEventosRegistrados;

    private static final String FORMATODOSDECIMALES = "%.2f";
    private static final String UNIDADACELEROMETRO = " m/seg2";
    private static final String ENVPROD  = "PROD";
    private static final String ENVTEST  = "TEST";
    private static final String LUMINOSIDAD  = "Luminosidad";
    private static final String ACELEROMETRO  = "Acelerometro";



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor);

        ejeXAceTextView = (TextView) findViewById(R.id.ejeXAceTextView);
        ejeYAceTextView = (TextView) findViewById(R.id.ejeYAceTextView);
        ejeZAceTextView = (TextView) findViewById(R.id.ejeZAceTextView);

        resultTextView = (TextView) findViewById(R.id.resultTextView);

        registerAcelerometerButton = (Button) findViewById(R.id.registerAcelerometerButton);
        registrarDatosLuz = (Button) findViewById(R.id.registrarDatosLuz);
        listarEventos = (Button) findViewById(R.id.button);
        eliminarRegistroButton = (Button) findViewById(R.id.eliminarRegistroButton);
        eliminarLumButton = (Button) findViewById(R.id.eliminarLumButton);
        eliminarAcelButton = (Button) findViewById(R.id.eliminarAcelButton);

        luminosidadTextView = (TextView) findViewById(R.id.luminosidadTextView);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        acelerometro = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        luminosidad = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        Intent intentB = getIntent(); //se recupera el Intent que envió la Activity de Origen

        Bundle extras = intentB.getExtras(); //se guardan los parámetros en un objeto Bundle

        if(extras != null)
        {
            env = extras.getString ("env"); //se recupera el valor de la etiqueta1 y se
            token = extras.getString ("token");
            token_refresh = extras.getString ("token_refresh");
        }

        if(env.equals(ENVPROD) )
        {
            listaEventosRegistrados = cargarSharedPreferences();
        }else
        {
            listaEventosRegistrados = new ArrayList<EventRegistered>();
        }

        agregarFuncionalidadBotones();


    }

    @Override
    protected void onResume()
    {

        super.onResume();
        sensorManager.registerListener(this,acelerometro,SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,luminosidad,SensorManager.SENSOR_DELAY_NORMAL);

    }


    @Override
    protected void onPause()
    {

        super.onPause();
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT));

    }


    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        if(env.equals(ENVPROD))
        {
            guardarSharedPreferences();
        }


    }


    @Override
    public void onSensorChanged(SensorEvent event)
    {
        if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER )
        {
            ejeXAceTextView.setText(String.format(FORMATODOSDECIMALES,event.values[0]) + UNIDADACELEROMETRO );
            ejeYAceTextView.setText(String.format(FORMATODOSDECIMALES,event.values[1]) + UNIDADACELEROMETRO);
            ejeZAceTextView.setText(String.format(FORMATODOSDECIMALES,event.values[2]) + UNIDADACELEROMETRO);
        }else
        {
            luminosidadTextView.setText(String.format(FORMATODOSDECIMALES,event.values[0]));
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {

    }


    private void  guardarSharedPreferences()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(listaEventosRegistrados);
        editor.putString("json", json);
        editor.apply();
    }


    private ArrayList<EventRegistered> cargarSharedPreferences()
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString("json", null);
        if(json == null)
        {
            return new ArrayList<EventRegistered>();
        }else{
            Type type = new TypeToken<ArrayList<EventRegistered>>() {}.getType();
            return gson.fromJson(json, type);
        }
    }


    private void agregarFuncionalidadBotones()
    {
        /******************Agrego funcion al boton de registrar acelerometro***********************/
        registerAcelerometerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                DeactivateButtons();
                resultTextView.setText("");

                /*************Obtengo los campos de la pantalla*********/
                String ejeXAce = ejeXAceTextView.getText().toString();
                String ejeYAce = ejeYAceTextView.getText().toString();
                String ejeZAce = ejeZAceTextView.getText().toString();

                String type_event = ACELEROMETRO;

                String description = "Eje X: " + ejeXAce  +
                                            "; Eje Y: " + ejeYAce  +
                                            "; Eje Z: " + ejeZAce  ;

                registerEvent(type_event, description);

                addEventToList(type_event, description);

            }
        });



        /******************Agrego funcion al boton de registrar Luz********************************/
        registrarDatosLuz.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                DeactivateButtons();
                resultTextView.setText("");

                /*************Obtengo los campos de la pantalla*********/
                String luminosidad = luminosidadTextView.getText().toString();

                String type_event = LUMINOSIDAD;

                String description = "Luminosidad: " + luminosidad ;

                registerEvent(type_event, description);

                addEventToList(type_event, description);

            }
        });


        /******************Agrego funcion al boton de listar eventos*******************************/
        listarEventos.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intentEventList = new Intent(SensorActivity.this, EventListActivity.class);

                intentEventList.putExtra("listaEventos", listaEventosRegistrados);

                startActivity(intentEventList);

            }
        });


        eliminarRegistroButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

            listaEventosRegistrados.clear();
            resultTextView.setText(R.string.eliminacion_exitosa);

            }
        });

        eliminarLumButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                TaskData task = new TaskData();
                task.setList(listaEventosRegistrados);
                task.setType_event(LUMINOSIDAD);

                EliminarRegistros eliminarRegistros = new EliminarRegistros();

                eliminarRegistros.execute(task);

            }
        });


        eliminarAcelButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                TaskData task = new TaskData();
                task.setList(listaEventosRegistrados);
                task.setType_event(ACELEROMETRO);

                EliminarRegistros eliminarRegistros = new EliminarRegistros();

                eliminarRegistros.execute(task);

            }
        });


    }

    private void registerEvent(String type_event, String description) {
        String authorization = "Bearer " + token;

        EventRequest request = new EventRequest();
        request.setEnv(env);
        request.setType_events(type_event);
        request.setDescription(description);

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getString(R.string.api_url))
                .build();

        SOAService soaService = retrofit.create(SOAService.class);

        Call<JsonObject> call;

        if (env.equals(ENVPROD)) {
            call = soaService.eventProd(authorization, request);
        } else {
            call = soaService.eventTest(authorization, request);
        }

        call.enqueue(new Callback<JsonObject>() {

                         @Override
                         public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                             Gson gson = new Gson();

                             if (response.isSuccessful()) {

                                 Log.e("Respuesta Correcta", response.toString());
                                 Log.e("Respuesta Correcta", response.body().toString());


                                 if (env.equals(ENVPROD)) {
                                     /************ PROD ******************/
                                     RegisterEventProdResponse responseClass = gson.fromJson(response.body().toString(),
                                             RegisterEventProdResponse.class);

                                     String textoRespuesta = "Se guardo el evento: ";

                                     textoRespuesta += responseClass.getEvent().getType_events() + ". ";
                                     textoRespuesta += "Indicado: " + responseClass.getEvent().getDescription() + ". ";
                                     textoRespuesta += "Para el DNI: " + responseClass.getEvent().getDni();
                                     textoRespuesta += " con un ID: " + responseClass.getEvent().getId();

                                     resultTextView.setText(textoRespuesta);

                                 } else {
                                     /************ TEST ******************/
                                     RegisterEventTestResponse responseClass = gson.fromJson(response.body().toString(),
                                             RegisterEventTestResponse.class);

                                     String textoRespuesta = "Se guardo el evento: ";

                                     textoRespuesta += responseClass.getEvent().getType_events() + ". ";
                                     textoRespuesta += "Indicado: " + responseClass.getEvent().getDescription() + ". ";

                                     resultTextView.setText(textoRespuesta);
                                 }

                                 ActivateButtons();

                             }else{
                                 Log.e("Respuesta Incorrecta", response.toString());


                                 BadSignUpRegisterEventResponse responseClass = gson.fromJson(response.errorBody().charStream(),
                                         BadSignUpRegisterEventResponse.class);

                                 resultTextView.setText(responseClass.getMsg());

                                 refreshToken();

                                 ActivateButtons();

                             }


                         }

                         @Override
                         public void onFailure(Call<JsonObject> call, Throwable t) {
                             resultTextView.setText(R.string.error_conexion_local);

                             ActivateButtons();

                         }
                     }

        );
    }



    private void refreshToken(){

        Retrofit retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(getString(R.string.api_url))
                .build();

        SOAService soaService = retrofit.create(SOAService.class);

        String Authorization = "Bearer " + token_refresh;

        Call<JsonObject> call = soaService.refresh(Authorization);

        call.enqueue(new Callback<JsonObject>() {
                     @Override
                     public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                         Gson gson = new Gson();

                         if(response.isSuccessful()){

                             Log.e("Respuesta Correcta", response.toString());
                             Log.e("Respuesta Correcta", response.body().toString());

                             LoginRefreshResponse responseClass = gson.fromJson(response.body().toString(),
                                     LoginRefreshResponse.class);

                             token = responseClass.getToken();
                             token_refresh = responseClass.getToken_refresh();

                             resultTextView.setText(R.string.token_updated);



                         }else
                         {
                             Log.e("Respuesta Incorrecta", response.toString());


                             BadLoginRefreshResponse responseClass = gson.fromJson(response.errorBody().charStream(),
                                     BadLoginRefreshResponse.class);

                             resultTextView.setText(responseClass.getMsg());
                         }


                     }

                     @Override
                     public void onFailure(Call<JsonObject> call, Throwable t) {
                         resultTextView.setText(R.string.error);

                         ActivateButtons();
                     }
                 }

        );

    }

    private void addEventToList(String type_event,String description)
    {

        EventRegistered eventoRegistrado = new EventRegistered();

        eventoRegistrado.setType_event(type_event);
        eventoRegistrado.setDescription(description);

        listaEventosRegistrados.add(eventoRegistrado);

    }


    private void ActivateButtons()
    {
        registerAcelerometerButton.setEnabled(true);
        registrarDatosLuz.setEnabled(true);
        listarEventos.setEnabled(true);
        eliminarRegistroButton.setEnabled(true);
        eliminarLumButton.setEnabled(true);
        eliminarAcelButton.setEnabled(true);

    }


    private void DeactivateButtons()
    {
        registerAcelerometerButton.setEnabled(false);
        registrarDatosLuz.setEnabled(false);
        listarEventos.setEnabled(false);
        eliminarRegistroButton.setEnabled(false);
        eliminarLumButton.setEnabled(false);
        eliminarAcelButton.setEnabled(false);
    }


    private class EliminarRegistros extends AsyncTask<TaskData, Void, Boolean> {

        public EliminarRegistros(){
            //TODO código del constructor
        }

        @Override
        protected void onPreExecute() {
            //TODO código del onPreExecute (Hilo Principal)
            resultTextView.setText(R.string.eliminando);
            DeactivateButtons();
        }

        @Override
        protected Boolean doInBackground(TaskData... varList) {
            //TODO código del doInBackground (Hilo en Segundo Plano)

            ArrayList<EventRegistered> list = varList[0].getList();
            String type_event = varList[0].getType_event();

            for(Iterator<EventRegistered> iterator = list.iterator(); iterator.hasNext();)
            {

                EventRegistered evento = iterator.next();

                if(evento.getType_event().equals(type_event))
                {
                    iterator.remove();
                }

            }

            return true;
        }


        @Override
        protected void onPostExecute(Boolean finalizado) {
            //TODO código del onPostExecute (Hilo Principal)
            resultTextView.setText(R.string.eliminacion_exitosa);
            ActivateButtons();
        }

    }

}

package com.mauricesoa.mauricesensores;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class EventListActivity extends AppCompatActivity {

    private TextView NoListTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_list);

        ArrayList<EventRegistered> listaEventosRegistrados = (ArrayList<EventRegistered>) getIntent().getSerializableExtra("listaEventos");


        ListView listView= (ListView) findViewById(R.id.list_view);

        NoListTextView = (TextView)findViewById(R.id.NoListTextView);

        if(listaEventosRegistrados != null && !listaEventosRegistrados.isEmpty())
        {

            NoListTextView.setVisibility(View.INVISIBLE);

            LayoutInflater inflater = getLayoutInflater();

            ViewGroup header = (ViewGroup)inflater.inflate(R.layout.title_colmn_row,listView,false);
            listView.addHeaderView(header,null,false);

            ListEventRegisteredAdapter adapter = new ListEventRegisteredAdapter(this,listaEventosRegistrados);
            listView.setAdapter(adapter);
        }

    }
}

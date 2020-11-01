package com.mauricesoa.mauricesensores;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;



import java.util.ArrayList;

public class ListEventRegisteredAdapter extends BaseAdapter {

    public ArrayList<EventRegistered> list;
    Activity activity;
    public static final String TYPE_EVENT_COLUMN = "Tipo Evento";
    public static final String DESCRIPTION_COLUMN = "Descripcion";

    public ListEventRegisteredAdapter(Activity activity, ArrayList<EventRegistered> list)
    {
        super();
        this.activity = activity;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder{
        TextView txtFirst;
        TextView txtSecond;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater = activity.getLayoutInflater();

        if(convertView == null)
        {
            convertView = inflater.inflate(R.layout.colmn_row,null);
            holder = new ViewHolder();

            holder.txtFirst = (TextView) convertView.findViewById(R.id.TextFirst);
            holder.txtSecond = (TextView) convertView.findViewById(R.id.TextSecond);

            convertView.setTag(holder);
        }else{

            holder = (ViewHolder) convertView.getTag();

        }

        EventRegistered eventRegistered = list.get(position);
        holder.txtFirst.setText(eventRegistered.getType_event());
        holder.txtSecond.setText(eventRegistered.getDescription());

        return convertView;
    }
}

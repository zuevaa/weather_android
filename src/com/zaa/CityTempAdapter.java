/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zaa;

import android.content.Context;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.Hashtable;
import java.util.List;
import android.text.format.DateFormat;

/**
 *
 * @author ZuevAA
 */
public class CityTempAdapter extends ArrayAdapter<String> {
    List<String> gv_list;
    Context gv_context;
    String gv_city;
    public CityTempAdapter(String pCity, Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
        gv_list = objects;
        gv_context = context;
        gv_city = pCity;
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View lv_row = convertView;
            String lv_dt = gv_list.get(position), lv_image_resource;
            TextView lv_text_view;
            ImageView lv_image;
            Hashtable lv_city_temp_col = (Hashtable)Cache.gv_city_temp.get(gv_city);
            CityTemp lv_city_temp = (CityTemp)lv_city_temp_col.get(lv_dt);
            if (lv_row == null) {
                    LayoutInflater lv_inflater = LayoutInflater.from(gv_context);
                    lv_row = lv_inflater.inflate(R.layout.list_item_city_temp, parent, false);
            }
            lv_text_view = (TextView) lv_row.findViewById(R.id.tempDate);
            DateFormat lv_df = new DateFormat();            
            lv_text_view.setText(lv_df.format("dd.MM.yyyy", Long.parseLong(lv_dt)*1000L));
            lv_image = (ImageView) lv_row.findViewById(R.id.weatherIconDay);
            lv_image.setImageResource(gv_context.getResources().getIdentifier("w"+lv_city_temp.GetIcon()+"d", "drawable", gv_context.getPackageName()));
            lv_text_view = (TextView) lv_row.findViewById(R.id.tempDay);
            lv_text_view.setText(lv_city_temp.GetTempDay()+"°C");
            lv_image = (ImageView) lv_row.findViewById(R.id.weatherIconNight);
            lv_image.setImageResource(gv_context.getResources().getIdentifier("w"+lv_city_temp.GetIcon()+"n", "drawable", gv_context.getPackageName()));
            lv_text_view = (TextView) lv_row.findViewById(R.id.tempNight);
            lv_text_view.setText(lv_city_temp.GetTempNight()+"°C");
            return lv_row;
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zaa;
import android.widget.ArrayAdapter;
import java.util.List;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.LayoutInflater;
import android.text.format.Time;
/**
 *
 * @author ZuevAA
 */
public class CityAdapter extends ArrayAdapter<String> {
    List<String> gv_list;
    Context gv_context;
    public CityAdapter(Context context, int resource, int textViewResourceId, List<String> objects) {
        super(context, resource, textViewResourceId, objects);
        gv_list = objects;
        gv_context = context;
    }
    
    public String Get_Name(int pPos) {
        return gv_list.get(pPos);
    }
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View lv_row = convertView;
            String lv_name = gv_list.get(position), lv_image_resource;
            City lv_city = (City)Cache.gv_city.get(lv_name);
            if (lv_row == null) {
                    LayoutInflater lv_inflater = LayoutInflater.from(gv_context);
                    lv_row = lv_inflater.inflate(R.layout.list_item, parent, false);
            }
            Time lv_cur_time = new Time();
            lv_cur_time.setToNow();
            if (Integer.parseInt(lv_cur_time.format("%H")) > 7 && Integer.parseInt(lv_cur_time.format("%H")) < 22 ) {
                lv_image_resource = "w"+lv_city.GetIcon()+"d";
            }
            else {
                lv_image_resource = "w"+lv_city.GetIcon()+"n";
            }
            
            ImageView lv_image = (ImageView) lv_row.findViewById(R.id.weatherIcon);
            lv_image.setImageResource(gv_context.getResources().getIdentifier(lv_image_resource, "drawable", gv_context.getPackageName()));
            TextView lv_city_name = (TextView) lv_row.findViewById(R.id.cityName);
            lv_city_name.setText(lv_name);

            TextView lv_city_temp = (TextView) lv_row.findViewById(R.id.cityTemp);
            lv_city_temp.setText(lv_city.GetTemp()+"°C");

            return lv_row;
    }    
}

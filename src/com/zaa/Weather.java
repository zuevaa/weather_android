package com.zaa;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import java.lang.Thread;
import java.util.Timer;
import java.util.TimerTask;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.Collections;


public class Weather extends Activity
{   
    Context gv_context;
    ProgressDialog gv_progress_dialog;
    LoadHandler gv_handler;
    public static final int LOAD_OK = 1, NO_NET = 2;
    Timer gv_timer;
    View gv_dialog_view;
    CityAdapter gv_city_adapter;
    boolean gv_in_view, gv_is_table;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gv_handler = new LoadHandler(this);
        gv_context = this;
        DbHelper.Get_Instance(this);
        gv_is_table = getResources().getBoolean(R.bool.isTablet);
        if (Cache.gv_city == null) {
            StartLoad();
        }
        else {
            ShowMain();
            if (Cache.gv_cur_city_name != null) {
                ShowDetail(Cache.gv_cur_city_name);
            }
        }
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && gv_in_view) {
            Cache.gv_cur_city_name = null;
            ShowMain();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }      
    void StartLoad() {
        if (gv_timer != null) {
            gv_timer.cancel();
        }
        new Thread(new Runnable() {
            public void run() {
                WeatherLoad lv_weather_load = new WeatherLoad(gv_handler);
                lv_weather_load.Load();
            };
        }).start();
        gv_progress_dialog = ProgressDialog.show(this, "Идет загрузка", null, true, false);
        gv_progress_dialog.setContentView(R.layout.load_dialog);
    }
    
    void CheckLoad (int pResult) {
        long lv_sec_wait = 3600;
        if (pResult == LOAD_OK) {
            if (gv_progress_dialog != null) {
                gv_progress_dialog.dismiss();
                gv_progress_dialog = null;
            }
        }
        else if (pResult == NO_NET) {
            if (gv_progress_dialog != null) {
                gv_progress_dialog.dismiss();
                gv_progress_dialog = null;
                AlertDialog.Builder lv_builder = new AlertDialog.Builder(this);
                lv_builder.setMessage("Для обновления информации необходимо подключение к интернету. Будет отображена ранее загруженная информация.");
                lv_builder.setPositiveButton("Продолжить", null);
                AlertDialog lv_dialog = lv_builder.create();
                lv_dialog.show();
            }
            lv_sec_wait = 30;
        }                        
        if (gv_timer != null) {
            gv_timer.cancel();
        }
        gv_timer = new Timer();
        gv_timer.schedule(new TimerTask() {       
            public void run() {
                WeatherLoad lv_weather_load = new WeatherLoad(gv_handler);
                lv_weather_load.Load();
            }
        }, lv_sec_wait*1000);        
        ShowMain();        
    }
    void ShowMain() {
        gv_in_view = false;
        setContentView(R.layout.main);
        Button lv_button = (Button)findViewById(R.id.addCity);
        lv_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder lv_builder = new AlertDialog.Builder(v.getContext());
                LayoutInflater lv_inflater = LayoutInflater.from(gv_context);
                gv_dialog_view = lv_inflater.inflate(R.layout.add_city, null);
                lv_builder.setView(gv_dialog_view);
                lv_builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText lv_edit_text = (EditText)gv_dialog_view.findViewById(R.id.addCityName);
                        String lv_name = lv_edit_text.getText().toString();
                        if (lv_name == null || lv_name.equals("")) {
                            Toast.makeText(getApplicationContext(), "Необходимо указать город", Toast.LENGTH_LONG).show();
                        }
                        else {
                            if (Cache.gv_city.get(lv_name) != null) {
                                Toast.makeText(getApplicationContext(), "Город уже есть в списке", Toast.LENGTH_LONG).show();
                            }
                            else {
                                City lv_city = new City(lv_name);
                                Cache.gv_city.put(lv_name, lv_city);
                                lv_city.Save();
                                StartLoad();
                            }
                        }
                    }
                });
                AlertDialog lv_dialog = lv_builder.create();
                lv_dialog.show();
            }
        });        
        RefreshList();        
    }
    void RefreshList() {
        ListView lv_list_view = (ListView) findViewById(R.id.listCity);
        ArrayList<String> lv_list = new ArrayList<String>();
        Set lt_keys = Cache.gv_city.keySet ();
        Iterator lv_iterator = lt_keys.iterator ();
        while (lv_iterator.hasNext ()) {
            lv_list.add(lv_iterator.next().toString());
        }
        gv_city_adapter = new CityAdapter(this, R.layout.list_item_city, R.id.cityName, lv_list);
        lv_list_view.setAdapter(gv_city_adapter);
        lv_list_view.setOnItemClickListener(OnCityClickListener);
    }
    OnItemClickListener OnCityClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            String lv_name = gv_city_adapter.Get_Name(position);
            ShowDetail(lv_name);
        }
    };
    void ShowDetail(String pName) {
        Cache.gv_cur_city_name = pName;
        String lv_image_resource;
        if (gv_is_table) {
            LinearLayout lv_deatail_view = (LinearLayout) findViewById(R.id.detail_view);
            lv_deatail_view.removeAllViews();
            LayoutInflater lv_inflater = LayoutInflater.from(gv_context);
            lv_deatail_view.addView(lv_inflater.inflate(R.layout.city_view, null));                
        }
        else {
            gv_in_view = true;
            setContentView(R.layout.city_view);
        }            

        TextView lv_text_view;
        City lv_city = (City)Cache.gv_city.get(pName);

        Time lv_cur_time = new Time();
        lv_cur_time.setToNow();
        if (Integer.parseInt(lv_cur_time.format("%H")) > 7 && Integer.parseInt(lv_cur_time.format("%H")) < 22 ) {
            lv_image_resource = "w"+lv_city.GetIcon()+"d";
        }
        else {
            lv_image_resource = "w"+lv_city.GetIcon()+"n";
        }

        ImageView lv_image = (ImageView) findViewById(R.id.cityViewWeatherIcon);
        lv_image.setImageResource(gv_context.getResources().getIdentifier(lv_image_resource, "drawable", gv_context.getPackageName()));
        lv_text_view = (TextView)findViewById(R.id.cityViewName);
        lv_text_view.setText(pName);
        lv_text_view = (TextView)findViewById(R.id.cityViewTemp);
        lv_text_view.setText(lv_city.GetTemp()+"°C");
        lv_text_view = (TextView)findViewById(R.id.cityViewHumidity);
        lv_text_view.setText(lv_city.GetHumidity()+"%");
        lv_text_view = (TextView)findViewById(R.id.cityViewPressure);
        lv_text_view.setText(lv_city.GetPressure()+" Па");
        lv_text_view = (TextView)findViewById(R.id.cityViewWindSpeed);
        lv_text_view.setText(lv_city.GetWindSpeed()+" м/c");
        ListView lv_list_view = (ListView) findViewById(R.id.listCityTemp);
        ArrayList<String> lv_list = new ArrayList<String>();
        Hashtable lv_city_temp_col = (Hashtable)Cache.gv_city_temp.get(pName);
        Set lt_keys = lv_city_temp_col.keySet ();
        Iterator lv_iterator = lt_keys.iterator ();
        while (lv_iterator.hasNext ()) {
            lv_list.add(lv_iterator.next().toString());
        }
        Collections.sort(lv_list);
        CityTempAdapter lv_adapter = new CityTempAdapter(pName, gv_context, R.layout.list_item_city_temp, R.id.tempDate, lv_list);
        lv_list_view.setAdapter(lv_adapter);
        lv_image = (ImageView)findViewById(R.id.delCity);
        lv_image.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                City lv_city = new City(Cache.gv_cur_city_name);
                lv_city.DeleteCity();
                Cache.gv_cur_city_name = null;
                ShowMain();
            }
        });
    }
}

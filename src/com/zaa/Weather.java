package com.zaa;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.View;
import java.lang.Thread;
import java.util.Timer;
import java.util.TimerTask;
import android.view.LayoutInflater;
import android.widget.ListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnClickListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;


public class Weather extends Activity
{   
    Context gv_context;
    ProgressDialog gv_progress_dialog;
    LoadHandler gv_handler;
    public static final int LOAD_OK = 1, NO_NET = 2;
    Timer gv_timer;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gv_handler = new LoadHandler(this);
        gv_context = this;
        DbHelper.Get_Instance(this);
        setContentView(R.layout.main);
        Button lv_buton = (Button)findViewById(R.id.addCity);
        lv_buton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder lv_builder = new AlertDialog.Builder(v.getContext());
                LayoutInflater lv_inflater = LayoutInflater.from(gv_context);
                lv_builder.setView(lv_inflater.inflate(R.layout.add_city, null));
                lv_builder.setPositiveButton("Добавить", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EditText lv_edit_text = (EditText)findViewById(R.id.addCityName);
                        String lv_name = lv_edit_text.getText().toString();
                        if (Cache.gv_city.get(lv_name) != null) {
                            Toast.makeText(getApplicationContext(), "Город уже есть в списке", Toast.LENGTH_LONG).show();
                        }
                        else {
                            City lv_city = new City(lv_name);
                            Cache.gv_city.put(lv_name, lv_city);
                            StartLoad();
                        }
                    }
                });
                AlertDialog lv_dialog = lv_builder.create();
                lv_dialog.show();
            }
        });
        StartLoad();
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
        RefreshList();        
    }
    void RefreshList() {
        ListView lv_listView = (ListView) findViewById(R.id.listView);
        ArrayList<String> lv_list = new ArrayList<String>();
        Set lt_keys = Cache.gv_city.keySet ();
        Iterator lv_iterator = lt_keys.iterator ();
        while (lv_iterator.hasNext ()) {
            lv_list.add(lv_iterator.next().toString());
        }
        CityAdapter lv_adapter = new CityAdapter(this, R.layout.list_item, R.id.cityName, lv_list);
        lv_listView.setAdapter(lv_adapter);
    }
}

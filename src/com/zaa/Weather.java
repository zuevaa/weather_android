package com.zaa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import java.lang.Thread;
import java.util.Timer;
import java.util.TimerTask;
import android.os.Handler;

public class Weather extends Activity
{   
    Weather gv_this;
    ProgressDialog gv_progress_dialog;
    LoadHandler gv_handler;
    public static final int LOAD_OK = 1, NO_NET = 2;
    Timer gv_timer;
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        gv_handler = new LoadHandler(this);
        DbHelper.Get_Instance(this);
        Start_Load();
    }
    void Start_Load() {
        new Thread(new Runnable() {
            public void run() {
                WeatherLoad lv_weather_load = new WeatherLoad(gv_handler);
                lv_weather_load.Load();
            };
        }).start();
        gv_progress_dialog = ProgressDialog.show(this, "Идет загрузка", null, true, false);
        gv_progress_dialog.setContentView(R.layout.load_dialog);
    }
    
    void Check_Load (int pResult) {
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
        Refresh_List();        
    }
    void Refresh_List() {
        
    }
}

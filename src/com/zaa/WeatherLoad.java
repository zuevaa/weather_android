/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zaa;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.Service; 
import android.os.IBinder; 
import java.util.concurrent.TimeUnit;
import android.content.Intent;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import java.util.zip.GZIPInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URLEncoder;
import org.json.JSONArray;
import org.json.JSONObject;
import android.os.AsyncTask;

public class WeatherLoad {
    
    LoadHandler gv_handler;
    
    public WeatherLoad (LoadHandler pHandler) {
        gv_handler = pHandler;
    }
    
    public void Load () {
        HttpURLConnection lv_connection=null;
        try{
            City.InitCity();
            String lv_name, lv_dt, lv_encoding, lv_response;
            Set lt_keys = Cashe.gv_city.keySet ();        
            Iterator lv_iterator = lt_keys.iterator ();
            JSONArray lv_json_array, lv_json_weather;
            JSONObject lv_json, lv_json_response, lv_json_weather_row, lv_json_temp;
            City lv_city;
            CityTemp lv_city_temp;
            int lv_resp_code;
            InputStream lv_in_stream;
            Hashtable lv_city_temp_col;
            while (lv_iterator.hasNext ()) {
                lv_name = lv_iterator.next().toString();
                lv_city_temp_col = (Hashtable)Cashe.gv_city_temp.get(lv_name);
                lv_connection = (HttpURLConnection)new URL("http://api.openweathermap.org/data/2.5/weather?q="+URLEncoder.encode(lv_name, "utf-8")+"&units=metric").openConnection();
                lv_connection.setConnectTimeout(30000);
                lv_connection.setReadTimeout(30000);
                lv_connection.setDoOutput(true);
                lv_connection.setDoInput(true);
                lv_connection.setRequestMethod("GET");
                lv_connection.setRequestProperty("Accept-Encoding", "gzip");
                lv_resp_code = lv_connection.getResponseCode();
                lv_in_stream = new BufferedInputStream(lv_connection.getInputStream(), 8192);
                lv_encoding =lv_connection.getHeaderField("Content-Encoding");
                if (lv_encoding != null && lv_encoding.equalsIgnoreCase("gzip")) {
                    lv_in_stream = new GZIPInputStream(lv_in_stream);
                }
                lv_response = convertStreamToString(lv_in_stream);
                Cashe.gv_net = true;
                lv_json_response = new JSONObject(lv_response);
                if (lv_json_response.getString("cod").equals("200")) {
                    lv_city = (City)Cashe.gv_city.get(lv_name);
                    lv_json = lv_json_response.getJSONObject("main");
                    lv_city.SetTemp(lv_json.getString("temp"));
                    lv_city.SetPressure(lv_json.getString("pressure"));
                    lv_city.SetHumidity(lv_json.getString("humidity"));
                    lv_json = lv_json_response.getJSONObject("wind");
                    lv_city.SetPressure(lv_json.getString("speed"));
                    lv_json_weather = lv_json_response.getJSONArray("weather");
                    if (lv_json_weather != null) {
                        lv_json_weather_row = (JSONObject)lv_json_weather.get(0);
                        lv_city.SetIcon(lv_json_weather_row.getString("icon").substring(0, 2));
                    }
                    lv_city.Save();
                }
                lv_connection.disconnect();
                lv_connection = (HttpURLConnection)new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q="+URLEncoder.encode(lv_name, "utf-8")+"&units=metric&cnt=7").openConnection();
                lv_connection.setConnectTimeout(30000);
                lv_connection.setReadTimeout(30000);
                lv_connection.setDoOutput(true);
                lv_connection.setDoInput(true);
                lv_connection.setRequestMethod("GET");
                lv_connection.setRequestProperty("Accept-Encoding", "gzip");
                lv_resp_code = lv_connection.getResponseCode();
                lv_in_stream = new BufferedInputStream(lv_connection.getInputStream(), 8192);
                lv_encoding =lv_connection.getHeaderField("Content-Encoding");
                if (lv_encoding != null && lv_encoding.equalsIgnoreCase("gzip")) {
                    lv_in_stream = new GZIPInputStream(lv_in_stream);
                }
                lv_response = convertStreamToString(lv_in_stream);
                Cashe.gv_net = true;
                lv_json_response = new JSONObject(lv_response);
                if (lv_json_response.getString("cod").equals("200")) {
                    lv_json_array = lv_json_response.getJSONArray("list");
                    if (lv_json_array != null) {
                        for (int i=0; i<lv_json_array.length(); i++) {
                            lv_json = (JSONObject)lv_json_array.get(i);
                            lv_dt = lv_json.getString("dt");
                            lv_city_temp = (CityTemp)lv_city_temp_col.get(lv_dt);
                            if (lv_city_temp == null) {
                                lv_city_temp = new CityTemp(lv_name, lv_dt);
                                lv_city_temp_col.put(lv_dt, lv_city_temp);
                            }
                            lv_json_weather = lv_json.getJSONArray("weather");
                            if (lv_json_weather != null) {
                                lv_json_weather_row = (JSONObject)lv_json_weather.get(0);
                                lv_city_temp.SetIcon(lv_json_weather_row.getString("icon").substring(0, 2));
                            }
                            lv_json_temp = lv_json.getJSONObject("temp");
                            lv_city_temp.SetTempDay(lv_json_temp.getString("day"));
                            lv_city_temp.SetTempNight(lv_json_temp.getString("night"));
                            lv_city_temp.Save();
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            gv_handler.sendEmptyMessage(Weather.NO_NET);
        }
        finally {
            if (lv_connection != null) {
                lv_connection.disconnect();
            }
        }
        gv_handler.sendEmptyMessage(Weather.LOAD_OK);
    }

    public String convertStreamToString(InputStream is) throws IOException {
        InputStreamReader r = new InputStreamReader(is);
        StringWriter sw = new StringWriter();
        char[] buffer = new char[1024];
        try {
            for (int n; (n = r.read(buffer)) != -1;)
                sw.write(buffer, 0, n);
        }
        finally{
            try {
                is.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return sw.toString();
    }
}
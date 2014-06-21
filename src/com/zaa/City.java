/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zaa;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import java.util.Hashtable;
/**
 *
 * @author ZuevAA
 */
public class City {
    String gv_name, gv_icon, gv_temp, gv_humidity, gv_pressure, gv_wind_speed;
    DbHelper gv_db_helper;
    public City(String pName) {
        gv_name = pName;
        gv_db_helper = DbHelper.Get_Instance();
    }
    public String GetName() {
        return gv_name;
    }
    public String GetIcon() {
        return gv_icon;
    }
    public void SetIcon(String pIcon) {
        gv_icon = pIcon;
    }
    public String GetTemp() {
        return gv_temp;
    }
    public void SetTemp(String pTemp) {
        gv_temp = pTemp;
    }    
    public String GetHumidity() {
        return gv_humidity;
    }
    public void SetHumidity(String pHumidity) {
        gv_humidity = pHumidity;
    }
    public String GetPressure() {
        return gv_pressure;
    }
    public void SetPressure(String pPressure) {
        gv_pressure = pPressure;
    }
    public String GetWindSpeed() {
        return gv_wind_speed;
    }
    public void SetWindSpeed(String pWindSpeed) {
        gv_wind_speed = pWindSpeed;
    }
    public void Load() {
        SQLiteDatabase lv_db = gv_db_helper.getReadableDatabase();
        Cursor lv_city = lv_db.rawQuery("select * from city where name = ?", new String[]{gv_name});
        if (lv_city.getCount() != 0) {
            lv_city.moveToFirst();
            gv_icon = lv_city.getString(lv_city.getColumnIndexOrThrow("icon"));
            gv_temp = lv_city.getString(lv_city.getColumnIndexOrThrow("temp"));
            gv_humidity = lv_city.getString(lv_city.getColumnIndexOrThrow("humidity"));
            gv_pressure = lv_city.getString(lv_city.getColumnIndexOrThrow("pressure"));
            gv_wind_speed = lv_city.getString(lv_city.getColumnIndexOrThrow("wind_speed"));
        }
        lv_city.close();
    }
    public void Save() {
        SQLiteDatabase lv_db = gv_db_helper.getWritableDatabase();
        Cursor lv_city = lv_db.rawQuery("select * from city where name = ?", new String[]{gv_name});
        ContentValues lv_cv = new ContentValues();
        lv_cv.put("name", gv_name);
        lv_cv.put("icon", gv_icon);
        lv_cv.put("temp", gv_temp);
        lv_cv.put("humidity", gv_humidity);
        lv_cv.put("pressure", gv_pressure);
        lv_cv.put("wind_speed", gv_wind_speed);        
        if (lv_city.getCount() != 0) {
            lv_db.update("city", lv_cv, "name = ?", new String[]{gv_name});
        }
        else {
            lv_db.insert("city", null, lv_cv);
        }
        lv_city.close();
    }
    
    public void DeleteCity() {
        SQLiteDatabase lv_db = gv_db_helper.getWritableDatabase();
        lv_db.delete("city_temp", "name = ?", new String[]{gv_name});
        lv_db.delete("city", "name = ?", new String[]{gv_name});
        Cache.gv_city_temp.remove(gv_name);
        Cache.gv_city.remove(gv_name);
    }
    
    public static void InitCity() {
        SQLiteDatabase lv_db = DbHelper.Get_Instance().getReadableDatabase();
        String lv_name;
        City lv_city;
        Cache.gv_city = new Hashtable();
        Cache.gv_city_temp = new Hashtable();
        Cursor lv_city_cur = lv_db.rawQuery("select * from city", null);
        if (lv_city_cur.getCount() != 0) {
            lv_city_cur.moveToFirst();
            do {
                lv_name = lv_city_cur.getString(lv_city_cur.getColumnIndexOrThrow("name"));
                lv_city = new City(lv_name);
                lv_city.SetIcon(lv_city_cur.getString(lv_city_cur.getColumnIndexOrThrow("icon")));
                lv_city.SetTemp(lv_city_cur.getString(lv_city_cur.getColumnIndexOrThrow("temp")));
                lv_city.SetHumidity(lv_city_cur.getString(lv_city_cur.getColumnIndexOrThrow("humidity")));
                lv_city.SetPressure(lv_city_cur.getString(lv_city_cur.getColumnIndexOrThrow("pressure")));
                lv_city.SetWindSpeed(lv_city_cur.getString(lv_city_cur.getColumnIndexOrThrow("wind_speed")));
                Cache.gv_city.put(lv_name, lv_city);
                CityTemp.InitCityTemp(lv_name);
            }while(lv_city_cur.moveToNext());                
        }
        lv_city_cur.close();
    }
}

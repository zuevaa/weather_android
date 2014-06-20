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
public class CityTemp {
    String gv_name, gv_dt, gv_icon, gv_temp_day, gv_temp_nigth;
    DbHelper gv_db_helper;
    public CityTemp(String pName, String pDate) {
        gv_name = pName;
        gv_dt = pDate;
        gv_db_helper = DbHelper.Get_Instance();
    }
    public String GetName() {
        return gv_name;
    }
    public String GetDate() {
        return gv_dt;
    }
    public String GetIcon() {
        return gv_icon;
    }
    public void SetIcon(String pIcon) {
        gv_icon = pIcon;
    }
    public String GetTempDay() {
        return gv_temp_day;
    }
    public void SetTempDay(String pTemp) {
        gv_temp_day = pTemp;
    }
    public String GetTempNigth() {
        return gv_temp_nigth;
    }
    public void SetTempNigth(String pTemp) {
        gv_temp_nigth = pTemp;
    }
    public void Load() {
        SQLiteDatabase lv_db = gv_db_helper.getReadableDatabase();
        Cursor lv_city_temp = lv_db.rawQuery("select * from city_temp where name = ? and dt = ?", new String[]{gv_name, gv_dt});
        if (lv_city_temp.getCount() != 0) {
            lv_city_temp.moveToFirst();
            gv_icon = lv_city_temp.getString(lv_city_temp.getColumnIndexOrThrow("icon"));
            gv_temp_day = lv_city_temp.getString(lv_city_temp.getColumnIndexOrThrow("temp_day"));
            gv_temp_nigth = lv_city_temp.getString(lv_city_temp.getColumnIndexOrThrow("temp_nigth"));
        }
        lv_city_temp.close();
    }
    public void Save() {
        SQLiteDatabase lv_db = gv_db_helper.getWritableDatabase();
        Cursor lv_city_temp = lv_db.rawQuery("select * from city_temp where name = ? and dt = ?", new String[]{gv_name, gv_dt});
        ContentValues lv_cv = new ContentValues();
        lv_cv.put("name", gv_name);
        lv_cv.put("dt", gv_dt);
        lv_cv.put("icon", gv_icon);
        lv_cv.put("temp_day", gv_temp_day);
        lv_cv.put("temp_nigth", gv_temp_nigth);
        if (lv_city_temp.getCount() != 0) {
            lv_db.update("city_temp", lv_cv, "name = ? and dt = ?", new String[]{gv_name, gv_dt});
        }
        else {
            lv_db.insert("city_temp", null, lv_cv);
        }
        lv_city_temp.close();
    }
    public static void InitCityTemp(String pName) {
        SQLiteDatabase lv_db = DbHelper.Get_Instance().getReadableDatabase();
        CityTemp lv_city_temp;
        String lv_dt;
        Cashe.gv_city_temp = new Hashtable();
        Hashtable lv_city_temp_col = new Hashtable();
        Cursor lv_city_temp_cur = lv_db.rawQuery("select * from city_temp where name = ? order by dt", new String[]{pName});
        if (lv_city_temp_cur.getCount() != 0) {
            lv_city_temp_cur.moveToFirst();
            do {
                lv_dt = lv_city_temp_cur.getString(lv_city_temp_cur.getColumnIndexOrThrow("dt"));
                lv_city_temp = new CityTemp(pName, lv_dt);
                lv_city_temp.SetIcon(lv_city_temp_cur.getString(lv_city_temp_cur.getColumnIndexOrThrow("icon")));
                lv_city_temp.SetTempDay(lv_city_temp_cur.getString(lv_city_temp_cur.getColumnIndexOrThrow("temp_day")));
                lv_city_temp.SetTempNigth(lv_city_temp_cur.getString(lv_city_temp_cur.getColumnIndexOrThrow("temp_nigth")));
                lv_city_temp_col.put(lv_dt, lv_city_temp);
            }while(lv_city_temp_cur.moveToNext());                
        }
        lv_city_temp_cur.close();
        Cashe.gv_city_temp.put(pName, lv_city_temp_col);
    }    
}

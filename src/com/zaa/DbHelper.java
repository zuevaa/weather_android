/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zaa;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
/**
 *
 * @author ZuevAA
 */
public class DbHelper extends SQLiteOpenHelper{

  private static final int DB_VERSION = 1;
  private static final String DB_NAME = "weather_db";
  private static DbHelper gv_db_helper; 
  public SQLiteDatabase gv_db = getWritableDatabase();
  public static DbHelper Get_Instance(Context context) {
      if (gv_db_helper == null) {
          gv_db_helper = new DbHelper(context);
      }
      return gv_db_helper;
  }
  public static DbHelper Get_Instance() {
      return gv_db_helper;
  }  
  public DbHelper(Context context) {
    super(context, DB_NAME, null,DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE city (name TEXT, icon TEXT, temp TEXT, humidity TEXT, pressure TEXT, wind_speed TEXT");
        db.execSQL("CREATE TABLE city_temp (name TEXT, dt TEXT, icon TEXT, temp_day TEXT, temp_nigth TEXT");
        db.execSQL("CREATE INDEX city_name on city (name ASC);");
        db.execSQL("CREATE INDEX city_temp_name_dt on city_temp (name ASC, dt ASC);");
        db.execSQL("insert into city (name) values ('Москва');");
        db.execSQL("insert into city (name) values ('Санкт-Петербург');");
  }

  @Override
  public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
  }
   
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.zaa;
/**
 *
 * @author ZuevAA
 */
import android.os.Handler;
import android.os.Message;
public class LoadHandler extends Handler {
      Weather gv_weather;
      
      public LoadHandler(Weather pWeather) {
          gv_weather = pWeather;
      }
      @Override
      public void handleMessage(Message pMsg) {
          gv_weather.Check_Load(pMsg.what);
      };    
}

package com.zaa;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Weather extends Activity
{
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        startService(new Intent(this, WeatherService.class));
        setContentView(R.layout.main);
    }
}

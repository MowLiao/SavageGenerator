package com.example.savagegenerator;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.FileOutputStream;

/**
 * Code for the main screen.
 *
 * Created by Melissa Liau on 09/12/2015.
 * Last edited on: 16/12/2016.
 */

public class MainScreen extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        try
        {
            // Creates/appends to data file, in case it does not exist.
            FileOutputStream fos = openFileOutput("allcharacterdata", Context.MODE_APPEND);
            String emptyString = "";
            fos.write(emptyString.getBytes());
            fos.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Handles the press of the start button.
     * @param view
     */
    public void handleStartClick(View view)
    {
        Intent intent  = new Intent(this, SelectScreen.class);
        startActivity(intent);
    }
}

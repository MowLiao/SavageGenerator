package com.example.savagegenerator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Code for the character selection screen.
 *
 * Created by Melissa Liau on 09/12/2015.
 * Last edited on: 16/12/2016.
 */

public class SelectScreen extends AppCompatActivity
{
    private String[] iconList = {""};
    private String[] characterList = {""};
    private ArrayList<String> characterArrays;
    public static final String CHARACTER_INFO = "com.example.savagegenerator.CHARACTER_INFO";
    public static final String LOAD = "com.example.savagegenerator.LOAD";

    /**
     * Initialises the screen. Calls loadFiles();
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_screen);
        loaddata();
    }

    /**
     * Sets the list view with the custom adapter.
     */
    private void setListView()
    {
        ListView lv = (ListView) findViewById(R.id.listSelect);
        ListArrayAdapter listAdapter = new ListArrayAdapter(this, characterList, iconList);
        lv.setAdapter(listAdapter);
        lv.setOnItemClickListener(new listClickListener());
    }

    /**
     * Opens character creation screen with no character info loaded.
     * @param view
     */
    public void handleCreateClick(View view)
    {
        Intent intent  = new Intent(this, CharacterScreen.class);
        intent.putExtra("CHARACTER_INFO", "");
        intent.putExtra("LOAD", true);
        startActivity(intent);
    }

    /**
     * Reads file data and converts string into a arrays. Finds character names and icons from
     * arrays and creates new arrays of those to pass to list to display them.
     */
    private void loaddata()
    {
        String filecontents = "";
        try
        {   FileInputStream fis = openFileInput("allcharacterdata");
            int contentbyte = 0;
            StringBuffer fileBuffer = new StringBuffer("");
            while ((contentbyte = fis.read()) != -1)
            {
                fileBuffer.append((char) contentbyte);
            }

            filecontents = fileBuffer.toString();
            System.out.println("read: " + filecontents);
        }
        catch (Exception e)
        {   e.printStackTrace();        }

        if (!filecontents.equals(""))
        {
            List<String> names = new ArrayList<String>();
            List<String> icons = new ArrayList<String>();

            ArrayList<String> charactersAL = new ArrayList<String>(Arrays.asList(filecontents.split("\n")));
            this.characterArrays = charactersAL;
            for (String character : charactersAL)
            {
                ArrayList<String> sectionAL = new ArrayList<String>(Arrays.asList(character.split(":.")));
                names.add(sectionAL.get(0));
                icons.add(sectionAL.get(2));
            }
            characterList = names.toArray(new String[names.size()]);
            iconList = icons.toArray(new String[icons.size()]);
        }
        else
        {
            characterList = new String[] {""};
            iconList = new String[] {""};
        }
        setListView();
    }

    /**
     * Listens to the click on the list, and passes the name of the character selected to
     * handleListClick().
     */
    private class listClickListener implements AdapterView.OnItemClickListener
    {
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            ViewGroup child = (ViewGroup) parent.getChildAt(position);
            ViewGroup childchild = (ViewGroup) child.getChildAt(0);
            TextView childchildchild = (TextView) childchild.getChildAt(1);

            String name = childchildchild.getText().toString();
            handleListClick(name);
        }
    }

    /**
     * Finds corresponding string of character data from character name. Starts up character
     * creation screen passing intent with all character data.
     * @param name String name of character to load.
     */
    private void handleListClick(String name)
    {

        for (String character : characterArrays)
        {
            ArrayList<String> characterinfo = new ArrayList<String>(Arrays.asList(character.split(":.")));
            if (name.equals(characterinfo.get(0)))
            {   Toast.makeText(this, "Loading charater: " + name, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, CharacterScreen.class);
                intent.putExtra(CHARACTER_INFO, character);
                intent.putExtra(LOAD, true);
                startActivity(intent);
                // character string send in format:
                // name :. iconpath :. race :. agility :. smarts :. spirit :. strength :. vigor :. skill1, die | skill2, die :. edge 1 / edge 2 :. minHind1 / minHind2 :. majHind1 / majHind2
                //  0         1          2       3         4         5         6           7         8                          9                   10                      11
            }
        }
    }

    /**
     * Clears all characters from the data file by overwriting it with a blank string.
     * Refreshes the list afterwards.
     * @param view
     */
    public void clearallbuttonactivity(View view)
    {
        try
        {   System.out.println("Clearing savefile");
            FileOutputStream fos = openFileOutput("allcharacterdata", Context.MODE_PRIVATE);
            String emptyString = "";
            fos.write(emptyString.getBytes());
            fos.close();
            Toast.makeText(this, "Cleared all saved data!", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {   e.printStackTrace();
        }
        loaddata();
    }

    /**
     * Refreshes the list display.
     * @param view
     */
    public void refreshlistactivity(View view)
    {
        loaddata();
    }
}

package com.example.savagegenerator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Code for the character selection list adapter.
 *
 * Created by Melissa Liau on 09/12/2015.
 * Last edited on: 16/12/2016.
 */

public class ListArrayAdapter extends ArrayAdapter<String>
{
    private Context context = null;
    private String[] characterNames = null;
    private String[] iconPaths = null;

    /**
     *  Initialises the class.
     * @param thiscontext Context of the view.
     * @param charlist A string list of all saved characters.
     * @param iconlist A string list of all character icon paths.
     */
    public ListArrayAdapter(Context thiscontext, String[] charlist, String[] iconlist)
    {
        super(thiscontext, R.layout.activity_select_screen, charlist);
        context = thiscontext;
        characterNames = charlist;
        iconPaths = iconlist;
    }

    /**
     * Overrides the getView() method to implement the custom adapter into the parent.
     * @param position Integer position of the list.
     * @param view
     * @param parent ViewGroup parent of the ListView row.
     * @return
     */
    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_layout_select, parent, false);

        if (!characterNames[0].equals(""))
        {
            if (!iconPaths[0].equals(""))
            {
                Bitmap thumbnail = (BitmapFactory.decodeFile(iconPaths[position]));
                ImageView iv = (ImageView) rowView.findViewById(R.id.characterIcon);
                iv.setImageBitmap(thumbnail);
            }

            TextView textView = (TextView) rowView.findViewById(R.id.characterName);
            textView.setText(characterNames[position]);

            return rowView;
        }
        else
        {
            return new View(context);
        }
    }
}

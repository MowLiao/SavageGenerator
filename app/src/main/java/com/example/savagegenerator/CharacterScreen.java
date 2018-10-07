package com.example.savagegenerator;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Code for the character creation screen and its widget interactions.
 *
 * Created by Melissa Liau on 09/12/2015.
 * Last edited on: 16/12/2016.
 */

public class CharacterScreen extends AppCompatActivity
{
    private List<String> raceAdapterList = new ArrayList<String>();
    private List<String> skillAdapterList = new ArrayList<String>();
    private List<String> diceAdapterList = new ArrayList<String>();
    private List<String> edgeAdapterList  = new ArrayList<String>();
    private List<String> minHindAdapterList  = new ArrayList<String>();
    private List<String> majHindAdapterList  = new ArrayList<String>();

    private ArrayList<Object> skillTableArray = new ArrayList<Object>();
    private ArrayList<String> edgeTableArray = new ArrayList<String>();
    private ArrayList<String> minHindArray = new ArrayList<String>();
    private ArrayList<String> majHindArray = new ArrayList<String>();
    private String characterIconPath = "";

    private ArrayList<Integer> totalCPPT = new ArrayList<Integer>(4);
    private ArrayList<Integer> baseCPPT = new ArrayList<Integer>(4);
    private ArrayList<Integer> modCPPT = new ArrayList<Integer>(4);

    private String race = "";
    private String prevRace = "";

    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        System.out.println("Initialising screen...");
        setContentView(R.layout.activity_character_screen);
        Intent intent = getIntent();
        Resources r = getResources();
        initialiseCPPT();
        Collections.addAll(this.diceAdapterList, r.getStringArray(R.array.dice_array));
        Collections.addAll(this.raceAdapterList, r.getStringArray(R.array.race_array));
        Collections.addAll(this.skillAdapterList, r.getStringArray(R.array.skill_array));
        Collections.addAll(this.majHindAdapterList, r.getStringArray(R.array.majhindrance_array));
        Collections.addAll(this.minHindAdapterList, r.getStringArray(R.array.minhindrance_array));
        Collections.addAll(this.edgeAdapterList, r.getStringArray(R.array.edge_array));
        setAttributeSpinners();
        setSkillSpinner(true);
        setEdgeSpinner(true);
        setMinHindranceSpinner(true);
        setMajHindranceSpinner(true);
        setRaceSpinner(true);

        // if loading character
        Boolean load = intent.getBooleanExtra(SelectScreen.LOAD, false);
        if (load)
        {
            String charinfo = intent.getStringExtra(SelectScreen.CHARACTER_INFO);
            characterLoad(charinfo);
        }
    }

    /**
     * Loads the character from a string passed  through intent to this screen from the 
     * character select screen.
     *
     * @param charinfo String contains character info.
     */
    private void characterLoad(String charinfo)
    {
        ArrayList<String> characterinfoarr = new ArrayList<>(Arrays.asList(charinfo.split(":.")));

        // set all strings to be used later
        String namestr = characterinfoarr.get(0);
        String proffstr = characterinfoarr.get(1);
        String iconstr = characterinfoarr.get(2);
        String racestr = characterinfoarr.get(3);
        String agilitystr = characterinfoarr.get(4);
        String smartsstr = characterinfoarr.get(5);
        String spiritstr = characterinfoarr.get(6);
        String strengthstr = characterinfoarr.get(7);
        String vigorstr = characterinfoarr.get(8);
        String skillsstr = characterinfoarr.get(9);
        String edgesstr = characterinfoarr.get(10);
        String minHindstr = characterinfoarr.get(11);
        String majHindstr = characterinfoarr.get(12);

        // set name
        EditText et = (EditText) findViewById(R.id.characterName);
        if (!namestr.equals("_"))
        {   et.setText(namestr);
        }

        // set profession
        et = (EditText) findViewById(R.id.editProfession);
        if (!proffstr.equals("_"))
        {   et.setText(proffstr);
        }

        // set icon
        if (!iconstr.equals("_"))
        {   setCharacterIcon(iconstr);
        }

        // set race
        Spinner tmpSpn = (Spinner) findViewById(R.id.spinnerRace);
        tmpSpn.setSelection(Integer.parseInt(racestr));

        // set ASSSV spinners
        tmpSpn = (Spinner) findViewById(R.id.spinnerAgility);
        tmpSpn.setSelection(Integer.parseInt(agilitystr));

        tmpSpn = (Spinner) findViewById(R.id.spinnerSmarts);
        tmpSpn.setSelection(Integer.parseInt(smartsstr));

        tmpSpn = (Spinner) findViewById(R.id.spinnerSpirit);
        tmpSpn.setSelection(Integer.parseInt(spiritstr));

        tmpSpn = (Spinner) findViewById(R.id.spinnerStrength);
        tmpSpn.setSelection(Integer.parseInt(strengthstr));

        tmpSpn = (Spinner) findViewById(R.id.spinnerVigor);
        tmpSpn.setSelection(Integer.parseInt(vigorstr));

        // setting skills
        ArrayList<String> allSkills = new ArrayList<>(Arrays.asList(skillsstr.split("''")));
        ArrayList<String> tmpArray = new ArrayList<String>();
        for (int i = 0; i < allSkills.size() ; i++)
        {   tmpArray.add(i, allSkills.get(i));
        }
        for (int i = 0; i < tmpArray.size(); i++)
        {
            if (!tmpArray.get(i).equals(""))
            {   ArrayList<String> skillArray = new ArrayList<>(Arrays.asList(tmpArray.get(i).split(",")));
                addSkill(skillArray.get(0), Integer.parseInt(skillArray.get(1)), true);
                this.skillTableArray.add(i, skillArray);
            }
        }

        // set edges
        tmpArray = new ArrayList<>(Arrays.asList(edgesstr.split("''")));
        for (int i = 0; i < tmpArray .size(); i++)
        {   if (!tmpArray.get(i).equals(""))
        {   addEdge(tmpArray.get(i), true);
            this.edgeTableArray.add(tmpArray.get(i));
        }
        }

        // set minhind
        tmpArray = new ArrayList<>(Arrays.asList(minHindstr.split("''")));
        for (int i = 0; i < tmpArray .size(); i++)
        {   if (!tmpArray.get(i).equals(""))
        {   addMinHindrance(tmpArray.get(i), true);
            this.minHindArray.add(tmpArray.get(i));
        }
        }

        // set majhind
        tmpArray = new ArrayList<>(Arrays.asList(majHindstr.split("''")));
        for (int i = 0; i < tmpArray .size(); i++)
        {   if (!tmpArray.get(i).equals(""))
        {   addMajHindrance(tmpArray.get(i), true);
            this.majHindArray.add(tmpArray.get(i));
        }
        }
    }

    //-------------------------------------------------
    // CODE FOR SETTING THE SPINNERS
    //-------------------------------------------------

    /**
     * Sets the race spinner and adds the listener.
     * @param initialise True if spinner is being initialised on screen start up/
     *                   recreation.
     */
    private void setRaceSpinner(boolean initialise)
    {
        Spinner spinnerRace = (Spinner) findViewById(R.id.spinnerRace);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.default_spinner_item, this.raceAdapterList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRace.setAdapter(adapter);

        if (initialise)
        {spinnerRace.setOnItemSelectedListener(new raceSpinnerActivity());
        }
    }

    /**
     * Sets the attribute dice spinners.
     */
    private void setAttributeSpinners()
    {
        Spinner spinnerAgility = (Spinner) findViewById(R.id.spinnerAgility);
        Spinner spinnerSmarts = (Spinner) findViewById(R.id.spinnerSmarts);
        Spinner spinnerSpirit = (Spinner) findViewById(R.id.spinnerSpirit);
        Spinner spinnerStrength = (Spinner) findViewById(R.id.spinnerStrength);
        Spinner spinnerVigor = (Spinner) findViewById(R.id.spinnerVigor);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.default_spinner_item, this.diceAdapterList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        List<Spinner> spinnerList = new ArrayList<Spinner>();
        spinnerList.add(spinnerAgility);
        spinnerList.add(spinnerSmarts);
        spinnerList.add(spinnerSpirit);
        spinnerList.add(spinnerStrength);
        spinnerList.add(spinnerVigor);

        for (Spinner spinner : spinnerList)
        {   spinner.setAdapter(adapter);
            spinner.setSelection(0);
        }
    }

    /**
     * Sets the skill spinner and adds a listener.
     * @param initialise True if spinner is being initialised on screen start up/
     *                   recreation.
     */
    private void setSkillSpinner(boolean initialise)
    {
        Spinner spinnerSkill = (Spinner) findViewById(R.id.spinnerSkillsAdd);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.default_spinner_item_right, this.skillAdapterList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSkill.setAdapter(adapter);

        if (initialise)
        {
            spinnerSkill.setOnItemSelectedListener(new skillSpinnerActivity());
        }
    }

    /**
     * Sets the edge spinner and adds a listener.
     * @param initialise True if spinner is being initialised on screen start up/
     *                   recreation.
     */
    private void setEdgeSpinner(boolean initialise)
    {
        Spinner spinnerEdge = (Spinner) findViewById(R.id.spinnerEdgesAdd);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.default_spinner_item_right, this.edgeAdapterList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerEdge.setAdapter(adapter);

        if (initialise)
        {   spinnerEdge.setOnItemSelectedListener(new edgeSpinnerActivity());
        }
    }

    /**
     * Sets the minor hindrance spinner and adds a listener.
     * @param initialise True if spinner is being initialised on screen start up/
     *                   recreation.
     */
    private void setMinHindranceSpinner(boolean initialise)
    {
        Spinner spinnerMinHindrances = (Spinner) findViewById(R.id.spinnerMinHindAdd);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.default_spinner_item_right, this.minHindAdapterList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMinHindrances.setAdapter(adapter);

        if (initialise)
        {   spinnerMinHindrances.setOnItemSelectedListener(new minHindSpinnerActivity());
        }
    }

    /**
     * Sets the major hindrance spinner and adds a listener.
     * @param initialise True if spinner is being initialised on screen start up/
     *                   recreation.
     */
    private void setMajHindranceSpinner(boolean initialise)
    {
        Spinner spinnerMajHindrances = (Spinner) findViewById(R.id.spinnerMajHindAdd);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.default_spinner_item_right, this.majHindAdapterList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMajHindrances.setAdapter(adapter);

        if (initialise)
        {   spinnerMajHindrances.setOnItemSelectedListener(new majHindSpinnerActivity());
        }
    }

    //-------------------------------------------------------
    // CODE FOR MODIFYING OTHER WIDGETS ON SPINNER SELECTION
    //-------------------------------------------------------

    /**
     * Called when a skill is to be added. Adds the skill to the skill table with specified
     * die, modifier, and button to allow removal.
     * @param skill String of skill name to be added.
     * @param die Spinner position of skill die.
     * @param removable True if remove button to be created.
     */
    private void addSkill(String skill, Integer die, boolean removable)
    {
        TableLayout layout = (TableLayout) findViewById(R.id.skillsLayout);

        TextView skillText = new TextView(this);
        skillText.setId(skillText.generateViewId());
        skillText.setText(skill);
        skillText.setTextColor(this.getResources().getColor(R.color.colorDefaultText));

        Spinner dieSpinner = new Spinner(this);
        dieSpinner.setId(dieSpinner.generateViewId());
        ArrayAdapter<CharSequence> dieadapter = ArrayAdapter.createFromResource(this, R.array.dice_array, R.layout.default_spinner_item);
        dieadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dieSpinner.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        dieSpinner.setAdapter(dieadapter);
        dieSpinner.setSelection(die);
        dieSpinner.setOnItemSelectedListener(new skillDieSpinnerActivity());

        TextView modifierText = new TextView(this);
        modifierText.setId(modifierText.generateViewId());
        modifierText.setText("0");
        modifierText.setTextColor(this.getResources().getColor(R.color.colorDefaultText));
        modifierText.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        ImageView removeButton = new ImageView(this);

        if (removable)
        {
            removeButton.setId(removeButton.generateViewId());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(30, 30);
            lp.gravity = Gravity.CENTER;
            removeButton.setLayoutParams(lp);
            removeButton.setImageResource(R.drawable.cancel);
            removeButton.setOnTouchListener(new removeButtonActivity());
        }

        TableRow tr = new TableRow(this);
        tr.setId(tr.generateViewId());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr.addView(skillText);
        tr.addView(dieSpinner);
        tr.addView(modifierText);
        tr.addView(removeButton);

        layout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        skillAdapterList.remove(skill);
        setSkillSpinner(false);

    }

    /**
     * Called when an edge is to be added. Adds edge to the edge table with a button to allow
     * removal if specified.
     * @param edge Edge to be added.
     * @param removable True if remove button to be created.
     */
    private void addEdge(String edge, boolean removable)
    {
        TableLayout layout = (TableLayout) findViewById(R.id.edgesLayout);

        TextView skillText = new TextView(this);
        skillText.setId(skillText.generateViewId());
        skillText.setText(edge);
        skillText.setTextColor(this.getResources().getColor(R.color.colorDefaultText));

        ImageView removeButton = new ImageView(this);

        if (removable)
        {
            removeButton.setId(removeButton.generateViewId());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(30, 30);
            lp.gravity = Gravity.CENTER;
            removeButton.setLayoutParams(lp);
            removeButton.setImageResource(R.drawable.cancel);
            removeButton.setOnTouchListener(new removeButtonActivity());
        }

        TableRow tr = new TableRow(this);
        tr.setId(tr.generateViewId());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr.addView(skillText);
        tr.addView(removeButton);

        layout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        edgeAdapterList.remove(edge);
        setEdgeSpinner(false);
    }

    /**
     * Called when a minor hindrance is to be added. Adds to the minor hindrance table with a 
     * button to allow removal if specified.
     * @param minHindrance Minor hindrance to be added.
     * @param removable True if remove button to be created.
     */
    private void addMinHindrance(String minHindrance, boolean removable)
    {
        TableLayout layout = (TableLayout) findViewById(R.id.minHindLayout);

        TextView skillText = new TextView(this);
        skillText.setId(skillText.generateViewId());
        skillText.setText(minHindrance);
        skillText.setTextColor(this.getResources().getColor(R.color.colorDefaultText));

        ImageView removeButton = new ImageView(this);

        if (removable)
        {
            removeButton.setId(removeButton.generateViewId());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(30, 30);
            lp.gravity = Gravity.CENTER;
            removeButton.setLayoutParams(lp);
            removeButton.setImageResource(R.drawable.cancel);
            removeButton.setOnTouchListener(new removeButtonActivity());
        }

        TableRow tr = new TableRow(this);
        tr.setId(tr.generateViewId());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr.addView(skillText);
        tr.addView(removeButton);

        layout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        minHindAdapterList.remove(minHindrance);
        setMinHindranceSpinner(false);

    }

    /**
     * Called when a major hindrance is to be added. Adds to the major hindrance table with a 
     * button to allow removal if specified.
     * @param majHindrance Major hindrance to be added.
     * @param removable True if remove button to be created.
     */
    private void addMajHindrance(String majHindrance, boolean removable)
    {
        TableLayout layout = (TableLayout) findViewById(R.id.majHindLayout);

        TextView skillText = new TextView(this);
        skillText.setId(skillText.generateViewId());
        skillText.setText(majHindrance);
        skillText.setTextColor(this.getResources().getColor(R.color.colorDefaultText));

        ImageView removeButton = new ImageView(this);

        if (removable)
        {
            removeButton.setId(removeButton.generateViewId());
            TableRow.LayoutParams lp = new TableRow.LayoutParams(30, 30);
            lp.gravity = Gravity.CENTER;
            removeButton.setLayoutParams(lp);
            removeButton.setImageResource(R.drawable.cancel);
            removeButton.setOnTouchListener(new removeButtonActivity());
        }

        TableRow tr = new TableRow(this);
        tr.setId(tr.generateViewId());
        tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        tr.addView(skillText);
        tr.addView(removeButton);

        layout.addView(tr, new TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

        majHindAdapterList.remove(majHindrance);
        setMajHindranceSpinner(false);
    }

    /**
     * Removes a specified row from a table. This is done by specifying the text of the 1st
     * child of the row. Tables are specified by a certain code name.
     * @param table String code of table to be removed.
     * @param row String text of the 1st child of the row to be removed.
     */
    private void removeFromTable(String table, String row)
    {
        TableLayout tl = null;
        Integer rows = 0;
        TableRow tr = null;
        Spinner spinner = null;
        if (table.equals("skill"))
        {   tl = (TableLayout) findViewById(R.id.skillsLayout);
            skillAdapterList.add(row);
            Collections.sort(skillAdapterList);
            setSkillSpinner(false);
        }
        if (table.equals("edge"))
        {   tl = (TableLayout) findViewById(R.id.edgesLayout);
            edgeAdapterList.add(row);
            Collections.sort(edgeAdapterList);
            setEdgeSpinner(false);
        }
        if (table.equals("minhind"))
        {   tl = (TableLayout) findViewById(R.id.minHindLayout);
            minHindAdapterList.add(row);
            Collections.sort(minHindAdapterList);
            setMinHindranceSpinner(false);
        }
        if (table.equals("majhind"))
        {   tl = (TableLayout) findViewById(R.id.majHindLayout);
            majHindAdapterList.add(row);
            Collections.sort(majHindAdapterList);
            setMajHindranceSpinner(false);
        }

        rows = tl.getChildCount();
        for (int rowNumber=0; rowNumber < rows; rowNumber++)
        {
            tr = (TableRow) tl.getChildAt(rowNumber);
            TextView tv = (TextView) tr.getChildAt(0);
            String rowLabel = (String) tv.getText();
            if (rowLabel.equals(row))
            {
                tl.removeView(tr);
                break;
            }
        }
    }

    /**
     * Sets the modifier of a certain skill. Called by setting races.
     * @param skill String of skill text to have the modifier changed of.
     * @param modifier Integer to be added to the current modifer.
     */
    private void modSkill(String skill, Integer modifier)
    {
        TableLayout tl = (TableLayout) findViewById(R.id.skillsLayout);
        TableRow tr = null;
        TextView tv = null;
        Integer modNum = null;
        Integer rows = tl.getChildCount();

        for (int rowNumber=0; rowNumber < rows; rowNumber++)
        {
            tr = (TableRow) tl.getChildAt(rowNumber);
            tv = (TextView) tr.getChildAt(0);
            if (tv.getText().equals(skill))
            {
                tv = (TextView) tr.getChildAt(2);
                String tvText = (String) tv.getText();
                try
                {
                    modNum = Integer.parseInt(tvText);
                    tv.setText(String.valueOf(modNum + modifier));
                } catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Called when the race is changed via spinner. Changes certain race-specific attributes.
     * @param reverseChange True if race changes are to be reversed.
     */
    private void raceChange(boolean reverseChange)
    {
        Spinner tmpspinner = null;

        if (race.equals("Android") && !reverseChange)
        {   modCharisma(-2);
            addMajHindrance("Pacifist", false);
        }
        if (prevRace.equals("Android") && reverseChange)
        {   modCharisma(2);
            removeFromTable("majhind", "Pacifist");
        }

        if (race.equals("Atlantean") && !reverseChange)
        {   modToughness(1);
            addSkill("Swimming", 2, false);
            tmpspinner = (Spinner) findViewById(R.id.spinnerSmarts);
            tmpspinner.setSelection(2);
        }
        if (prevRace.equals("Atlantean") && reverseChange)
        {   modToughness(-1);
            removeFromTable("skill", "Swimming");
            tmpspinner = (Spinner) findViewById(R.id.spinnerSmarts);
            tmpspinner.setSelection(0);
        }

        if (race.equals("Avion") && !reverseChange)
        {   modToughness(-1);
        }
        if (prevRace.equals("Avion") && reverseChange)
        {   modToughness(1);
        }

        if (race.equals("Dwarf") && !reverseChange)
        {   modPace(-1);
            tmpspinner = (Spinner) findViewById(R.id.spinnerVigor);
            tmpspinner.setSelection(2);
        }
        if (prevRace.equals("Dwarf") && reverseChange)
        {   modPace(1);
            tmpspinner = (Spinner) findViewById(R.id.spinnerVigor);
            tmpspinner.setSelection(0);
        }

        if (race.equals("Elf") && !reverseChange)
        {   addMinHindrance("All Thumbs", false);
            tmpspinner = (Spinner) findViewById(R.id.spinnerAgility);
            tmpspinner.setSelection(2);
        }
        if (prevRace.equals("Elf") && reverseChange)
        {   removeFromTable("minhind", "All Thumbs");
        }

        if (race.equals("Half-Elf") && !reverseChange)
        {   addMinHindrance("Outsider", false);
        }
        if (prevRace.equals("Half-Elf") && reverseChange)
        {   removeFromTable("minhind", "Outsider");
        }

        if (race.equals("Half-Folk") && !reverseChange)
        {   modToughness(-1);
            tmpspinner = (Spinner) findViewById(R.id.spinnerSpirit);
            tmpspinner.setSelection(2);
        }
        if (prevRace.equals("Half-Folk") && reverseChange)
        {   modToughness(1);
            tmpspinner = (Spinner) findViewById(R.id.spinnerSpirit);
            tmpspinner.setSelection(0);
        }

        if (race.equals("Half-Orc") && !reverseChange)
        {   modCharisma(-2);
            tmpspinner = (Spinner) findViewById(R.id.spinnerStrength);
            tmpspinner.setSelection(2);
        }
        if (prevRace.equals("Half-Orc") && reverseChange)
        {   modCharisma(2);
            tmpspinner = (Spinner) findViewById(R.id.spinnerStrength);
            tmpspinner.setSelection(0);
        }

        if (race.equals("Human") && !reverseChange)
        {
        }
        if (prevRace.equals("Human") && reverseChange)
        {
        }

        if (race.equals("Rakashan") && !reverseChange)
        {   modCharisma(-4);
            tmpspinner = (Spinner) findViewById(R.id.spinnerAgility);
            tmpspinner.setSelection(2);
            addSkill("Climbing", 0, false);
            modSkill("Climbing", 2);
        }
        if (prevRace.equals("Rakashan") && reverseChange)
        {   modCharisma(4);
            removeFromTable("skill", "Climbing");
        }

        if (race.equals("Saurian") && !reverseChange)
        {   modCharisma(-2);
            addSkill("Notice", 0, false);
            modSkill("Notice", 2);
        }
        if (prevRace.equals("Saurian") && reverseChange)
        {   modCharisma(2);
            removeFromTable("skill", "Notice");
        }
    }

    //-------------------------------------------------------
    // CODE FOR LISTENERS
    //-------------------------------------------------------

    /**
     * Listener class for race spinner. Calls raceChange().
     */
    private class raceSpinnerActivity implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            prevRace = race;
            race = (String) parent.getItemAtPosition(position);
            raceChange(true);
            raceChange(false);
        }

        public void onNothingSelected(AdapterView<?> parent)
        {
        }
    }

    /**
     * Listener for skill spinner. Calls addSkill() and adds to skillTableArray.
     */
    private class skillSpinnerActivity implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            String skill = (String) parent.getItemAtPosition(position);
            if (!skill.equals("--Add Skill"))
            {   addSkill(skill, 0, true);
                ArrayList<String> skillArray = new ArrayList<>(2);
                skillArray.add(skill);
                skillArray.add("0");
                skillTableArray.add(skillArray);
            }
        }

        public void onNothingSelected(AdapterView<?> parent)        {        }
    }

    /**
     * Listener for skill dice spinners. Adjusts skillTableArray to include type of die
     * when changed.
     */
    private class skillDieSpinnerActivity implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            TableRow tr = (TableRow) parent.getParent();
            TextView tv = (TextView) tr.getChildAt(0);
            String tvString =  tv.getText().toString();

            ArrayList<String> skillArray = new ArrayList<String>();
            for (int i = 0; i < skillTableArray.size(); i++)
            {
                skillArray = (ArrayList<String>) skillTableArray.get(i);
                if (skillArray.get(0).equals(tvString))
                {
                    skillArray.set(1, Integer.toString(position));
                    skillTableArray.set(i, skillArray);
                    break;
                }
            }
        }

        public void onNothingSelected(AdapterView<?> parent)        {        }
    }

    /**
     * Listener for edge spinner. Calls addEdge() and adds edge to edgeTableArray.
     */
    private class edgeSpinnerActivity implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            String edge = (String) parent.getItemAtPosition(position);
            if (!edge.equals("--Add Edge"))
            {   addEdge(edge, true);
                edgeTableArray.add(edge);
            }
            parent.setSelection(0);
        }

        public void onNothingSelected(AdapterView<?> parent)        {        }
    }

    /**
     * Listener for minor hindrance spinner. Calls addMinHindrance(). Adds to minHindArray.
     */
    private class minHindSpinnerActivity implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            String minHind = (String) parent.getItemAtPosition(position);
            if (!minHind.equals("--Add Minor Hindrance"))
            {   addMinHindrance(minHind, true);
                minHindArray.add(minHind);
            }
            parent.setSelection(0);
        }

        public void onNothingSelected(AdapterView<?> parent)        {        }
    }

    /**
     * Listener for major hindrance spinner. Calls addMajHindrance(). Adds to majHindArray.
     */
    private class majHindSpinnerActivity implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
        {
            String majHind = (String) parent.getItemAtPosition(position);
            if (!majHind.equals("--Add Major Hindrance"))
            {   addMajHindrance(majHind, true);
                majHindArray.add(majHind);
            }
            parent.setSelection(0);
        }

        public void onNothingSelected(AdapterView<?> parent)        {        }
    }

    /**
     * Listener for remover buttons in tables. Reacts on touch release and calls
     * removeButton().
     */
    private class removeButtonActivity implements View.OnTouchListener
    {
        public boolean onTouch(View view, MotionEvent me)
        {
            switch (me.getAction())
            {   case MotionEvent.ACTION_UP:
                {   removeButton(view);
                }
            }
            return true;
        }
    }

    //-------------------------------------------------
    // CODE FOR BUTTON ACTIVITIES
    //-------------------------------------------------

    /**
     * Sets the character icon picture as file at the path specified.
     * @param picturePath String path of the character icon.
     */
    private void setCharacterIcon(String picturePath)
    {
        Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
        ImageButton ib = (ImageButton) findViewById(R.id.characterImageButton);
        ib.setImageBitmap(thumbnail);
        this.characterIconPath = picturePath;
    }

    /**
     * Activity for when the icon is pressed. Starts picker activity for character icon.
     * @param view
     */
    public void iconClickActivity(View view)
    {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 1);
    }

    /**
     * Overrides onActivityResult. Gets the icon filepath and sets it to the global variable
     * characterIconPath. Calls setCharacterIcon.
     * @param requestCode Auto-generated integer code for request.
     * @param resultCode Auto-generated integer result code.
     * @param intent
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != intent)
        {   try
        {
            Uri selectedImage = intent.getData();
            String[] filePath = {MediaStore.Images.Media.DATA};
            Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
            c.moveToFirst();
            int columnIndex = c.getColumnIndex(filePath[0]);
            String picturePath = c.getString(columnIndex);
            c.close();
            this.characterIconPath = picturePath;
            setCharacterIcon(picturePath);
        }
        catch (Exception e)
        {   e.printStackTrace();
        }
        }
    }

    /**
     * Actions when the remove buttons within tables are pressed:
     *      - removes row from table
     *      - adds item back to spinner adapter
     *      - re-sets the spinner (calls appropriate spinner setter)
     * @param view
     */
    private void removeButton(View view)
    {
        TableRow tr = (TableRow) view.getParent();
        TextView tv = (TextView) tr.getChildAt(0);
        TableLayout tl = (TableLayout) tr.getParent();
        tl.removeView(tr);

        String rowLabel = tv.getText().toString();
        boolean found = false;
        for (int i=0; i < skillTableArray.size(); i++)
        {
            ArrayList<String> skill = (ArrayList<String>) skillTableArray.get(i);
            if (skill.contains(rowLabel))
            {   skillTableArray.remove(skill);
                skillAdapterList.add(rowLabel);
                Collections.sort(skillAdapterList);
                setSkillSpinner(false);
                found = true;
            }
        }

        if (edgeTableArray.contains(rowLabel) && !found)
        {   edgeTableArray.remove(rowLabel);
            edgeAdapterList.add(rowLabel);
            Collections.sort(edgeAdapterList);
            setEdgeSpinner(false);
            found = true;
        }

        if (minHindArray.contains(rowLabel) && !found)
        {   minHindArray.remove(rowLabel);
            minHindAdapterList.add(rowLabel);
            Collections.sort(minHindAdapterList);
            setMinHindranceSpinner(false);
            found = true;
        }

        if (majHindArray.contains(rowLabel) && !found)
        {   majHindArray.remove(rowLabel);
            majHindAdapterList.add(rowLabel);
            Collections.sort(majHindAdapterList);
            setMajHindranceSpinner(false);
        }
    }

    /**
     * Transforms a string array to a string with the specified separator.
     * @param array String array to be turned into a string.
     * @param separator String separator to be used between each array item in final string.
     * @return Passed array as a string with specified separators.
     */
    private String arraytostring(List<String> array, String separator)
    {
        String returnStr = "";
        String tmp = "";
        for (int i=0; i < array.size(); i++)
        {   tmp = array.get(i);
            returnStr += tmp;
            if (i < array.size() - 1)
            {   returnStr += separator;
            }
        }
        return returnStr;
    }

    /**
     * Gathers all information about the character required to set character again. Appends
     * information as a single string to file of filename: allcharacterdata
     * Saved in format:
     * name :. iconpath :. race :. agility :. smarts :. spirit :. strength :. vigor :.
     * skill1, die '' skill2, die :. edge 1 / edge 2 :. minHind1 / minHind2 :. majHind1 / majHind2
     * @param view
     */
    public void saveButtonClick(View view)
    {
        String saveString = "";
        EditText tmpet = null;
        String tmpStr = "";
        ArrayList<String> tmpArray = null;
        Spinner tmpSpn = null;

        // get name
        tmpet = (EditText) findViewById(R.id.characterName);
        String chaname = tmpet.getText().toString();
        if (!chaname.equals(""))
        {   saveString += chaname + ":.";
        }
        else
        {   saveString += "_:.";
        }

        // get profession
        tmpet = (EditText) findViewById(R.id.editProfession);
        tmpStr = tmpet.getText().toString();
        if (!tmpStr.equals(""))
        {   saveString += tmpStr + ":.";
        }
        else
        {   saveString += "_:.";
        }

        // get icon path
        if (!this.characterIconPath.equals(""))
        {   saveString += this.characterIconPath + ":.";
        }
        else
        {   saveString += "_:.";
        }

        // get race
        tmpSpn = (Spinner) findViewById(R.id.spinnerRace);
        saveString += tmpSpn.getSelectedItemPosition() + ":.";

        // get ASSSV attributes
        tmpSpn = (Spinner) findViewById(R.id.spinnerAgility);
        saveString += tmpSpn.getSelectedItemPosition() + ":.";
        tmpSpn = (Spinner) findViewById(R.id.spinnerSmarts);
        saveString += tmpSpn.getSelectedItemPosition() + ":.";
        tmpSpn = (Spinner) findViewById(R.id.spinnerSpirit);
        saveString += tmpSpn.getSelectedItemPosition() + ":.";
        tmpSpn = (Spinner) findViewById(R.id.spinnerStrength);
        saveString += tmpSpn.getSelectedItemPosition() + ":.";
        tmpSpn = (Spinner) findViewById(R.id.spinnerVigor);
        saveString += tmpSpn.getSelectedItemPosition() + ":.";

        // get skills
        tmpStr = "";
        for (int i=0; i < skillTableArray.size(); i++)
        {   tmpArray = (ArrayList<String>) skillTableArray.get(i);
            tmpStr += (tmpArray.get(0) + "," + tmpArray.get(1));
            if (i < skillTableArray.size() - 1)
            {   tmpStr += "''";
            }
        }

        // get edges, minhind and majhind
        saveString += tmpStr + "'':.";
        tmpStr = arraytostring(edgeTableArray, "/");
        saveString += tmpStr + "'':.";
        tmpStr = arraytostring(minHindArray, "/");
        saveString += tmpStr + "'':.";
        tmpStr = arraytostring(majHindArray, "/");
        saveString += tmpStr + "''\n";

        // append to file
        String FILENAME = "allcharacterdata";
        try
        {   FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
            fos.write(saveString.getBytes());
            fos.close();
            System.out.println("Wrote to file:");
            Toast.makeText(this, "Saved " + chaname + " to file.", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {   e.printStackTrace();
        }
        System.out.println(saveString);
    }

    //-----------------------------------------------------
    // CODE FOR SAVING/LOADING STATE
    // Saves and loads:     skill table
    //                      edge table
    //                      minHind table
    //                      majHind table
    //                      icon
    //                      baseCPPT
    //                      race
    //-----------------------------------------------------

    /**
     * Overriden method. Saves all necessary data for when the screen activity is interrupted,
     * e.g. screen rotation.
     * @param saveState Bundle where data is passed to.
     */
    @Override
    protected void onSaveInstanceState (Bundle saveState)
    {
        try
        {
            String skillKey = "";
            String skillText = "";
            ArrayList<String> skillArray = new ArrayList<String>();
            Integer openParenthesis = 0;
            ArrayList<String> allSkills = new ArrayList<String>();

            for (int i = 0; i < this.skillTableArray.size(); i++)
            {
                skillArray = (ArrayList<String>) this.skillTableArray.get(i);
                skillText = (String) skillArray.get(0);
                openParenthesis = skillText.indexOf("(");
                skillKey = skillText.substring(0, 4) + skillText.substring(openParenthesis + 1, openParenthesis + 4);
                saveState.putStringArrayList(skillKey, skillArray);
                allSkills.add(skillKey);
            }
            saveState.putStringArrayList("allSkills", allSkills);
            saveState.putStringArrayList("edgeTable", this.edgeTableArray);
            saveState.putStringArrayList("minHindTable", this.minHindArray);
            saveState.putStringArrayList("majHindTable", this.majHindArray);
            saveState.putString("iconPath", this.characterIconPath);
            saveState.putIntegerArrayList("baseCPPT", this.baseCPPT);
            saveState.putIntegerArrayList("modCPPT", this.modCPPT);
            saveState.putString("race", this.race);
            saveState.putString("prevRace", this.prevRace);
        }

        catch (Exception e)
        {   e.printStackTrace();
        }
        super.onSaveInstanceState(saveState);
    }

    /**
     * Overriden method. Retrieves all necessary data for when the screen activity is interrupted,
     * e.g. screen rotation.
     * @param savedState Bundle where data is retrieved from.
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedState)
    {
        try
        {
            super.onRestoreInstanceState(savedState);
            this.race = savedState.getString("race");
            this.prevRace = savedState.getString("prevRace");
            raceChange(true);
            raceChange(false);
            initialiseCPPT();
            this.modCPPT = savedState.getIntegerArrayList("modCPPT");

            // Get list of skill keys saved
            ArrayList<String> allSkills = savedState.getStringArrayList("allSkills");
            // create temp array
            ArrayList<Object> tmpArray = new ArrayList<Object>();
            // for each key, add to temp array key corresponding value
            for (int i = 0; i < allSkills.size(); i++)
            {   tmpArray.add(i, savedState.getStringArrayList(allSkills.get(i)));
            }
            ArrayList<String> skillArray = new ArrayList<String>(2);
            for (int i = 0; i < tmpArray.size(); i++)
            {   skillArray = (ArrayList<String>) tmpArray.get(i);
                addSkill(skillArray.get(0), Integer.parseInt(skillArray.get(1)), true);
                this.skillTableArray.add(i, skillArray);
            }

            this.edgeTableArray = savedState.getStringArrayList("edgeTable");
            for (int i = 0; i < this.edgeTableArray.size(); i++)
            {   addEdge(this.edgeTableArray.get(i), true);
            }

            this.minHindArray = savedState.getStringArrayList("minHindTable");
            for (int i = 0; i < this.minHindArray.size(); i++)
            {   addMinHindrance(this.minHindArray.get(i), true);
            }

            this.majHindArray = savedState.getStringArrayList("majHindTable");
            for (int i = 0; i < this.majHindArray.size(); i++)
            {   addMajHindrance(this.majHindArray.get(i), true);
            }

            this.characterIconPath = savedState.getString("iconPath");
            if (!this.characterIconPath.equals(""))
            {
                setCharacterIcon(this.characterIconPath);
            }

        }
        catch (Exception e)
        {   e.printStackTrace();        }
    }

    //------------------------------------------------------
    // CODE FOR MODIFYING CHARISMA, PACE, PARRY, TOUGHNESS
    //                       0        1     2        3
    //------------------------------------------------------

    /**
     * Sets the base charisma, pace, parry and toughness values. Calls setCPPT().
     */
    private void initialiseCPPT()
    {
        this.totalCPPT = new ArrayList<Integer>(4);
        this.baseCPPT = new ArrayList<Integer>(4);
        this.modCPPT = new ArrayList<Integer>(4);

        this.baseCPPT.add(0);     // Setting CHR
        this.baseCPPT.add(6);     // Setting PACE
        this.baseCPPT.add(2);     // Setting PARRY
        this.baseCPPT.add(2);     // Setting TOUGHNESS

        for (int i = 0; i < 4; i++)
        {   this.modCPPT.add(0);
            this.totalCPPT.add(0);
        }
        setCPPT();
    }

    /**
     * Sets the textviews displaying the CPPT information, including base, modifiers and total.
     */
    private void setCPPT()
    {
        for (int i = 0; i < 4; i++)
        {
            this.totalCPPT.set(i, this.baseCPPT.get(i) + this.modCPPT.get(i));
        }

        TextView charisma = (TextView) findViewById(R.id.iconCharismaNum);
        TextView pace = (TextView) findViewById(R.id.iconPaceNum);
        TextView parry = (TextView) findViewById(R.id.iconParryNum);
        TextView toughness = (TextView) findViewById(R.id.iconToughnessNum);

        charisma.setText(this.totalCPPT.get(0).toString());
        pace.setText(this.totalCPPT.get(1).toString());
        parry.setText(this.totalCPPT.get(2).toString());
        toughness.setText(this.totalCPPT.get(3).toString());

        charisma = (TextView) findViewById(R.id.pointsCharisma);
        pace = (TextView) findViewById(R.id.pointsPace);
        parry = (TextView) findViewById(R.id.pointsParry);
        toughness = (TextView) findViewById(R.id.pointsToughness);

        charisma.setText(this.baseCPPT.get(0).toString());
        pace.setText(this.baseCPPT.get(1).toString());
        parry.setText(this.baseCPPT.get(2).toString());
        toughness.setText(this.baseCPPT.get(3).toString());

        charisma = (TextView) findViewById(R.id.modifierCharisma);
        pace = (TextView) findViewById(R.id.modifierPace);
        parry = (TextView) findViewById(R.id.modifierParry);
        toughness = (TextView) findViewById(R.id.modifierToughness);

        charisma.setText(this.modCPPT.get(0).toString());
        pace.setText(this.modCPPT.get(1).toString());
        parry.setText(this.modCPPT.get(2).toString());
        toughness.setText(this.modCPPT.get(3).toString());
    }

    /**
     * Changes the modifier for charisma
     * @param modifier Integer amount to change modifer by.
     */
    private void modCharisma(Integer modifier)
    {
        Integer current = this.modCPPT.get(0);
        this.modCPPT.set(0, current + modifier);
        setCPPT();
    }

    /**
     * Changes the modifier for page
     * @param modifier Integer amount to change modifer by.
     */
    private void modPace(Integer modifier)
    {
        Integer current = this.modCPPT.get(1);
        this.modCPPT.set(1, current + modifier);
        setCPPT();
    }

    /**
     * Changes the modifier for parry
     * @param modifier Integer amount to change modifer by.
     */
    private void modParry(Integer modifier)
    {
        Integer current = this.modCPPT.get(2);
        this.modCPPT.set(2, current + modifier);
        setCPPT();
    }

    /**
     * Changes the modifier for toughness
     * @param modifier Integer amount to change modifer by.
     */
    private void modToughness(Integer modifier)
    {
        Integer current = this.modCPPT.get(3);
        this.modCPPT.set(3, current + modifier);
        setCPPT();
    }


}

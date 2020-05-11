package com.example.projectapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ThirdActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private SharedPreferences sharedPreferences;
    private Spinner spinner;
    private Map<String, ?> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        //Intent intent = getIntent();
        sharedPreferences = getSharedPreferences("app_esiea", Context.MODE_PRIVATE);
        addItemsOnSpinner();

    }


    public void addItemsOnSpinner() {

        map = sharedPreferences.getAll();

        spinner = (Spinner) findViewById(R.id.spinner);
        List<String> list = new ArrayList<String>();
        list.add("Favorite list :");
        for(Map.Entry mEntry : map.entrySet()){
            if(Pattern.matches("fav .*", mEntry.getKey()+"")){
                list.add(mEntry.getValue()+"");
            }
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if(!Pattern.matches("Favorite .*",parent.getItemAtPosition(position)+"")){
            int idAnime = sharedPreferences.getInt(parent.getItemAtPosition(position)+" id", 0);

            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            intent.putExtra("ID",idAnime);
            startActivity(intent);
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

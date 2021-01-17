package com.example.projectbeer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AddGeneralBeerData extends AppCompatActivity {
    Button next_button;
    Context c;

    EditText beerName_input;
    EditText type_input;
    EditText brewery_input;
    EditText alcohol_input;

    boolean b_allFieldsAreCompleted = true;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_general_beer_data);
        getSupportActionBar().hide();

        c = this;
        next_button = (Button) findViewById(R.id.next_button);

        beerName_input = (EditText) findViewById(R.id.beerName_input);
        type_input = (EditText) findViewById(R.id.beerType_input);
        brewery_input = (EditText) findViewById(R.id.brewery_input);
        alcohol_input = (EditText) findViewById(R.id.degrees_input);

        Intent addPersonalData_activity = new Intent(this, AddPersonalBeerData.class);
        Log.i("edittext", "create");

        beerName_input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus && beerName_input.getText().toString() != ""){
                    //Préremplir les autres champs si ce nom de bière existe déjà dans la bdd
                }
            }
        });

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                b_allFieldsAreCompleted = true;

                String name = beerName_input.getText().toString();
                String type = type_input.getText().toString();
                String brewery = brewery_input.getText().toString();

                float percent = 0f;

                if((alcohol_input.getText().toString().matches("")))
                    b_allFieldsAreCompleted = false;
                else percent = Float.valueOf(alcohol_input.getText().toString());

               // Log.i("biere", name + " " + type + " " + brewery + " " + String.valueOf(percent));

                if(DataIsValid(name, type, brewery, 10f)) {
                    Beer beer = new Beer(name, type, brewery, percent);

                    addPersonalData_activity.putExtra("Beer", beer);
                    addPersonalData_activity.putExtra("idUser", getIntent().getIntExtra("idUser", -1));
                    startActivity(addPersonalData_activity);
                    startActivity(addPersonalData_activity);
                }
                else
                {
                    DisplayErrorMessage("Name or degrees of alcohol fields are not completed");
                }
            }
        });
    }

    void DisplayErrorMessage(String a_msg){
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(c);
        builder.setMessage(a_msg);
        builder.setCancelable(true);
        builder.create().show();
    }

    boolean DataIsValid(String a_name, String a_type, String a_brewery, float a_percent){

        if(a_name.matches("") || a_percent > 100) b_allFieldsAreCompleted = false;

        //On vérifie si tous les champs sont complétés

        return b_allFieldsAreCompleted;
    }
}

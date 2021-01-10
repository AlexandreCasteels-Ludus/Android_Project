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

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_general_beer_data);
        getSupportActionBar().hide();

        c = this;
        next_button = (Button) findViewById(R.id.next_button);
        beerName_input = (EditText) findViewById(R.id.beerName_input);

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
                if(AllFieldsAreCompleted()) startActivity(addPersonalData_activity);
                else                        DisplayErrorMessage("Some fields are not completed...");
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

    boolean AllFieldsAreCompleted(){
        boolean b_allFieldsAreCompleted = true;

        //On vérifie si tous les champs sont complétés

        return b_allFieldsAreCompleted;
    }
}

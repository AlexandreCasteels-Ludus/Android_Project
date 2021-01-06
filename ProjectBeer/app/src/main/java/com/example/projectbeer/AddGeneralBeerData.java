package com.example.projectbeer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AddGeneralBeerData extends AppCompatActivity {
    Button next_button;
    Context c;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_general_beer_data);

        c = this;
        next_button = findViewById(R.id.next_button);
        Intent addPersonalDatas_activity = new Intent(this, AddPersonalBeerData.class);

        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(AllFieldsAreCompleted()) startActivity(addPersonalDatas_activity);
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

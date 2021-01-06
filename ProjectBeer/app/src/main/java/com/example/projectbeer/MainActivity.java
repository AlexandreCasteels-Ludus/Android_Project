package com.example.projectbeer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton addButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addButton = findViewById(R.id.addButton);
        Intent addBeerActivity = new Intent(this, AddGeneralBeerData.class);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { startActivity(addBeerActivity); }
        });
    }
}
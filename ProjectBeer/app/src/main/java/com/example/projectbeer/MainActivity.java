package com.example.projectbeer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton addButton;
    RecyclerView recyclerView;

    //Variables affichées dans le Recycler View
    String beerName[] = {"66fdsqfdsqfdsqfd54q6f5d4q6f54d6qfhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh6", "2", "3", "4", "5","66fdsqfdsqfdsqfd54q6f5d4q6f54d6qfhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh6", "2", "3", "4", "5"};
    int images[] = {R.drawable.img_20180309_091856, R.drawable.img_20180428_123844, R.drawable.img_20180428_123852, R.drawable.img_20180507_172843, R.drawable.img_20180507_175301,R.drawable.img_20180309_091856, R.drawable.img_20180428_123844, R.drawable.img_20180428_123852, R.drawable.img_20180507_172843, R.drawable.img_20180507_175301};
    float rating[] = {3.5f,2,3,4,5,3.5f,2,3,4,5}, degrees[] = {1.1f,2,3.5f,4.9f,5.2f,1,2.2f,3.5f,4,5.2f};
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        addButton = findViewById(R.id.addButton);
        recyclerView = findViewById(R.id.recyclerView);

        Adapter adapter = new Adapter(this, images, beerName, degrees, rating);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

/*      recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);*/


        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addBeerActivity = new Intent(getApplicationContext(), AddGeneralBeerData.class);
                startActivity(addBeerActivity);
            }
        });
    }
}
package com.example.projectbeer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Date;

public class BeerDetails extends FragmentActivity implements OnMapReadyCallback {

    GoogleMap map;
    int idCatalog;

    TextView beerName, beerType, beerBrewery, beerDegrees;
    ImageView photo;
    TextView comment, containerDate;
    RatingBar rating;

    String[] spinnerChoices = {"Barrel", "Bottle", "Can"};

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.beer_details);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);

        idCatalog = getIntent().getIntExtra("idCatalog", -1);

        if(idCatalog == -1){
            //erreur

        }
        else{
            beerName = findViewById(R.id.name);
            beerType = findViewById(R.id.type);
            beerDegrees = findViewById(R.id.degrees);
            beerBrewery = findViewById(R.id.brewery);
            photo = findViewById(R.id.image);
            containerDate = findViewById(R.id.container_date);
            comment = findViewById(R.id.commentary);
            rating = findViewById(R.id.rating);

            GetDetails getDetails = (GetDetails) new GetDetails().execute(String.valueOf(idCatalog));

            while(! getDetails.taskIsDone){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(getDetails.result != null){
                            /*
                            PROPERTIES:
                            0   localisation
                            1   comment
                            2   score
                            3   photo
                            4   date
                            5   name
                            6   type
                            7   brewery
                            8   percentAlcohol
                            9   container
                            10  volume
                             */

                //set localisation     ((String) getDetails.result.getProperty(0));
                comment.setText((String) getDetails.result.getProperty(1));
                rating.setRating(Float.valueOf((String) getDetails.result.getProperty(2)));
                //photo.setImageBitmap((Bitmap) getDetails.result.getProperty(3));
                containerDate.setText(
                        "Drink in a " + (String) getDetails.result.getProperty(10) + "cl " +
                        spinnerChoices[Math.max(0, Math.min(Integer.valueOf((String)getDetails.result.getProperty(9)), 2)) ] + " on the " +
                        (String) getDetails.result.getProperty(4)
                );
                beerName.setText((String) getDetails.result.getProperty(5));
                beerType.setText((String) getDetails.result.getProperty(6));
                beerBrewery.setText((String) getDetails.result.getProperty(7));
                beerDegrees.setText((String) getDetails.result.getProperty(8) + "°");
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        LatLng latLng = new LatLng(43.2936228,5.3728778);
        map.addMarker(new MarkerOptions().position(latLng).title(""));
        map.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        map.setMinZoomPreference(16);

    }
}


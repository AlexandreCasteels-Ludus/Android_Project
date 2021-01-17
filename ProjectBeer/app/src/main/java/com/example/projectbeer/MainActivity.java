package com.example.projectbeer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    FloatingActionButton addButton;
    RecyclerView recyclerView;


    Vector<String[]> Data;

    int idUser;
    //Variables affichées dans le Recycler View
    String beerName[];// = {"66fdsqfdsqfdsqfd54q6f5d4q6f54d6qfhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh6", "2", "3", "4", "5","66fdsqfdsqfdsqfd54q6f5d4q6f54d6qfhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh6", "2", "3", "4", "5"};
    int images[];// = {R.drawable.img_20180309_091856, R.drawable.img_20180428_123844, R.drawable.img_20180428_123852, R.drawable.img_20180507_172843, R.drawable.img_20180507_175301,R.drawable.img_20180309_091856, R.drawable.img_20180428_123844, R.drawable.img_20180428_123852, R.drawable.img_20180507_172843, R.drawable.img_20180507_175301};
    float rating[];// = {3.5f,2,3,4,5,3.5f,2,3,4,5};
    float degrees[];// = {1.1f,2,3.5f,4.9f,5.2f,1,2.2f,3.5f,4,5.2f};

    int idCatalogs[];
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        addButton = findViewById(R.id.addButton);
        recyclerView = findViewById(R.id.recyclerView);

        idUser = getIntent().getIntExtra("idUser", 2);

        if(idUser != -1){

            GetAllCatalogOfUser getAllCatalogOfUser = (GetAllCatalogOfUser) new GetAllCatalogOfUser().execute(String.valueOf(idUser));

            while(! getAllCatalogOfUser.taskIsEnded) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            beerName = new String[getAllCatalogOfUser.result.size()];
            images = new int[getAllCatalogOfUser.result.size()];
            rating = new float[getAllCatalogOfUser.result.size()];
            degrees = new float[getAllCatalogOfUser.result.size()];
            idCatalogs = new int[getAllCatalogOfUser.result.size()];



            for (int i = 0; i < getAllCatalogOfUser.result.size(); i++) {
                            /*
                            PROPERTIES:
                            0   _idCatalog
                            1   name
                            2   photo
                            3   score
                               percentAlcohol
                             */

                idCatalogs[i] = Integer.parseInt((String) getAllCatalogOfUser.result.get(i).getProperty(0));
                beerName[i] = (String) getAllCatalogOfUser.result.get(i).getProperty(1);
                //images[i] = Integer.parseInt((String) getAllCatalogOfUser.result.get(i).getProperty(2));
                rating[i] = Float.parseFloat((String) getAllCatalogOfUser.result.get(i).getProperty(3));
                degrees[i] = Float.parseFloat((String) getAllCatalogOfUser.result.get(i).getProperty(4));
            }


            Adapter adapter = new Adapter(this, null, beerName, degrees, rating, idCatalogs);
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
                    addBeerActivity.putExtra("idCatalog", -1); // specify a new Catalog line
                    startActivity(addBeerActivity);
                }
            });
        }
        else{

        }
    }

    class GetAllCatalogOfUser extends AsyncTask<String, Void, String> {

        String NAMESPACE = "http://beerapp.atwebpages.com/";
        String NAME_WEBSERVICE = "webService/";
        String SOAP_ACTION = NAMESPACE + NAME_WEBSERVICE;
        String URL= NAMESPACE + NAME_WEBSERVICE + "service.php?wsdl";

        String METHOD_NAME = "GetAllCatalogOfUser";
        String PARAMS_1_idUser = "idUser";

        Vector<SoapObject> result;
        boolean taskIsEnded = false;


        @Override
        protected String doInBackground(String... params) {

            SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo propertyInfo1 = new PropertyInfo();

            propertyInfo1.setName(PARAMS_1_idUser);
            propertyInfo1.setValue(params[0]);
            propertyInfo1.setType(Integer.class);
            soapObject.addProperty(propertyInfo1);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(soapObject);

            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);

            try {
                httpTransportSE.call(SOAP_ACTION + METHOD_NAME, envelope);
                result = (Vector<SoapObject>) envelope.getResponse();

            } catch (Exception e) {
                Log.i("debug", e.getMessage());
            }

            taskIsEnded = true;
            return "end";
        }
    }
}
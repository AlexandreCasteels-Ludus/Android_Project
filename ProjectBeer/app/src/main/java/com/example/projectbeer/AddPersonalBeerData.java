package com.example.projectbeer;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPersonalBeerData extends AppCompatActivity {

    public static final int  CAMERA_PERM_CODE     = 101;
    public static final int  CAMERA_REQUEST_CODE  = 102;
    public static final int  GALLERY_REQUEST_CODE = 105;

    String[] spinnerChoices = {"Barrel", "Bottle", "Can"};
    Button localisation_button, recordDataButton, editImage;

    String currentPhotoPath;

    Beer beer;
    float volume;

    //Entrées utilisateur
    ImageView imageView;
    DatePicker datePicker;
    EditText commentary;
    RatingBar ratingBar;
    Spinner spinner;
    EditText container_volume;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_personal_beer_data);
        getSupportActionBar().hide();

        Log.i("alldata", "ok - 1");


        datePicker = (DatePicker) findViewById(R.id.date_input);
        datePicker.setMaxDate(new Date().getTime());
        commentary = (EditText) findViewById(R.id.commentary_input);
        ratingBar = (RatingBar) findViewById(R.id.rating_input);
        container_volume = (EditText) findViewById(R.id.containerVolume_input);

        //Création du menu déroulant (spinner)
        spinner = findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddPersonalBeerData.this, R.layout.spinner_dropdown_layout, spinnerChoices);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //Bouton enregistrement data
        recordDataButton = (Button) findViewById(R.id.recordDataButton);

        recordDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                volume = Float.valueOf(container_volume.getText().toString());
                if(DataIsValid(volume)) RecordData();
            }
        });

        //Bouton d'ajout d'image
        imageView = (ImageView) findViewById(R.id.image_apbd) ;
        editImage = (Button) findViewById(R.id.editImg_button);

        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { DisplayChoices(); }
        });


        //Bouton d'édition de la localisation
        localisation_button = findViewById(R.id.editLocation);

        localisation_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent locationPicherActivity = new Intent(getApplicationContext(), Map.class);
                startActivity(locationPicherActivity);
            }
        });

        //Récupération des infos de l'activity précédente
        beer = (Beer) getIntent().getSerializableExtra("Beer");
    }

    boolean DataIsValid(float volume){
        boolean dataIsValid = true;

        if(volume <= 0) dataIsValid = false;

        if(dataIsValid){
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
        } else{
            Toast.makeText(getApplication(),"The volume must be greater than 0", Toast.LENGTH_SHORT).show();
        }

        return dataIsValid;
    }

    void RecordData(){
        //Si la bière existe déjà dans la bdd générale alors on modifie ses informations, sinon on les ajoute aux bdd générale et personnelle
        boolean existsInGeneralDB = false;

      //  Log.i("alldata", String.valueOf(ratingBar.getRating()));

        int container = (int) spinner.getSelectedItemId();
        LatLng consumptionPlace = new LatLng(0,0);
        String comment = commentary.getText().toString();
        float rating = ratingBar.getRating();
        Date date = new Date(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
        Image image;

        Beer newBeerData = new Beer(container, volume, consumptionPlace, comment, rating, null , date);

        beer.container = newBeerData.container;
        beer.volume = newBeerData.volume;
        beer.consumption_place = newBeerData.consumption_place;
        beer.comment = newBeerData.comment;
        beer.rating = newBeerData.rating;
        beer.image = newBeerData.image;
        beer.date = newBeerData.date;

        Log.i("alldata", beer.name + " " + beer.type + " " + beer.brewery + " " + String.valueOf(beer.percent) + " " + beer.comment+ " " + String.valueOf(beer.consumption_place) + " " + beer.container + " " + String.valueOf(beer.rating) + " " + String.valueOf(beer.date));

        new AddBeer().execute(beer.name, beer.type,beer.brewery, String.valueOf(beer.percent), String.valueOf(beer.container), String.valueOf(beer.volume),
                String.valueOf(beer.consumption_place), beer.comment, String.valueOf(beer.rating), beer.image, String.valueOf(beer.date), "2");

        if(existsInGeneralDB){

        } else{

        }

    }

    String NAMESPACE = "http://beerapp.atwebpages.com/";
    String NAME_WEBSERVICE = "webService/";
    String SOAP_ACTION = NAMESPACE + NAME_WEBSERVICE;
    String URL= NAMESPACE + NAME_WEBSERVICE + "service.php?wsdl";

    boolean b_beerWellCreated;
    boolean taskIsDone;

    class AddBeer extends AsyncTask<String, Void, String> {

        String METHOD_NAME = "AddBeer";

        //beer
        String PARAMS_1_name = "name";
        String PARAMS_2_type = "type";
        String PARAMS_3_brewery = "brewery";
        String PARAMS_4_percent = "percentAlcohol";
        String PARAMS_5_container = "container";        //!=type
        String PARAMS_6_volume = "volume";              //!=type

        //catalog
        String PARAMS_7_localisation = "localisation";  //!=type
        String PARAMS_8_comment = "comment";
        String PARAMS_9_score = "score";                //!=type
        String PARAMS_10_photo = "photo";               //!=type
        String PARAMS_11_date = "date";

        String PARAMS_12_user = "fk_user";
        String PARAMS_13_beer = "fk_beer";


        boolean result = false;

        @Override
        protected void onPostExecute(String s) {
            b_beerWellCreated = result;
        }

        @Override
        protected String doInBackground(String... params) {

            SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);

            //Nom bière
            PropertyInfo propertyInfo1 = new PropertyInfo();
            propertyInfo1.setName(PARAMS_1_name);
            propertyInfo1.setValue(params[0]);
            propertyInfo1.setType(String.class);
            soapObject.addProperty(propertyInfo1);

            //Type bière
            PropertyInfo propertyInfo2 = new PropertyInfo();
            propertyInfo2.setName(PARAMS_2_type);
            propertyInfo2.setValue(params[1]);
            propertyInfo2.setType(String.class);
            soapObject.addProperty(propertyInfo2);

            //Nom brasserie
            PropertyInfo propertyInfo3 = new PropertyInfo();
            propertyInfo3.setName(PARAMS_3_brewery);
            propertyInfo3.setValue(params[2]);
            propertyInfo3.setType(String.class);
            soapObject.addProperty(propertyInfo3);

            //Pourcentage d'alcool
            PropertyInfo propertyInfo4 = new PropertyInfo();
            propertyInfo4.setName(PARAMS_4_percent);
            propertyInfo4.setValue(params[3]);
            propertyInfo4.setType(float.class);
            soapObject.addProperty(propertyInfo4);

            //Type de contenant
            PropertyInfo propertyInfo5 = new PropertyInfo();
            propertyInfo5.setName(PARAMS_5_container);
            propertyInfo5.setValue(params[4]);
            propertyInfo5.setType(int.class);
            soapObject.addProperty(propertyInfo5);

            //Volume du contenant
            PropertyInfo propertyInfo6 = new PropertyInfo();
            propertyInfo6.setName(PARAMS_6_volume);
            propertyInfo6.setValue(params[5]);
            propertyInfo6.setType(float.class);
            soapObject.addProperty(propertyInfo6);

/*            String PARAMS_7_localisation = "localisation";
            String PARAMS_8_comment = "comment";
            String PARAMS_9_score = "score";
            String PARAMS_10_photo = "photo";
            String PARAMS_11_date = "date";

            String PARAMS_12_user = "fk_user";
            String PARAMS_13_beer = "fk_beer";*/

            //Localisation
            PropertyInfo propertyInfo7 = new PropertyInfo();
            propertyInfo7.setName(PARAMS_7_localisation);
            propertyInfo7.setValue(params[6]);
            propertyInfo7.setType(LatLng.class);
            soapObject.addProperty(propertyInfo7);

            //Commentaire
            PropertyInfo propertyInfo8 = new PropertyInfo();
            propertyInfo8.setName(PARAMS_8_comment);
            propertyInfo8.setValue(params[7]);
            propertyInfo8.setType(String.class);
            soapObject.addProperty(propertyInfo8);

            //Note
            PropertyInfo propertyInfo9 = new PropertyInfo();
            propertyInfo9.setName(PARAMS_9_score);
            propertyInfo9.setValue(params[8]);
            propertyInfo9.setType(float.class);
            soapObject.addProperty(propertyInfo9);

            //Photo
            PropertyInfo propertyInfo10 = new PropertyInfo();
            propertyInfo10.setName(PARAMS_10_photo);
            propertyInfo10.setValue(params[9]);
            propertyInfo10.setType(String.class);
            soapObject.addProperty(propertyInfo10);

            //Date
            PropertyInfo propertyInfo11 = new PropertyInfo();
            propertyInfo11.setName(PARAMS_11_date);
            propertyInfo11.setValue(params[10]);
            propertyInfo11.setType(Date.class);
            soapObject.addProperty(propertyInfo11);

            //#Utilisateur lié
            PropertyInfo propertyInfo12 = new PropertyInfo();
            propertyInfo12.setName(PARAMS_12_user);
            propertyInfo12.setValue(params[11]);
            propertyInfo12.setType(String.class);
            soapObject.addProperty(propertyInfo12);

/*            //#Bière liée
            PropertyInfo propertyInfo13 = new PropertyInfo();
            propertyInfo13.setName(PARAMS_13_beer);
            propertyInfo13.setValue(*//*params[12]*//*2);
            propertyInfo13.setType(int.class);
            soapObject.addProperty(propertyInfo13);*/

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(soapObject);

            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);

            try {
                httpTransportSE.call(SOAP_ACTION + METHOD_NAME, envelope);
                //result = (boolean) envelope.getResponse();
                Log.i("wxcv", String.valueOf(envelope.getResponse()));

            } catch (Exception e) {
                Log.i("wxcv", e.getMessage().toString());
            }

            taskIsDone = true;
            return "end";
        }
    }



    //Ci-dessous fonctions pour l'édition de l'image
    void DisplayChoices(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Take a picture or choose image from gallery?");

        builder.setPositiveButton("Take a picture", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int which) { AskCameraPermission(); }
        });

        builder.setNegativeButton("Choose image from gallery", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE);
            }
        });

        builder.setCancelable(true);
        builder.show();
    }

    //Sources pour les fonctions ci-dessous : https://developer.android.com/training/camera/photobasics
    //                                        https://www.youtube.com/watch?v=s1aOlr3vbbk PART 1
    //                                        https://www.youtube.com/watch?v=KaDwSvOpU5E PART 2
    //                                        https://www.youtube.com/watch?v=q5pqnT1n-4s PART 3

    //Demande la permission à l'utilisateur d'utiliser sa caméra
    void AskCameraPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA }, CAMERA_PERM_CODE);
        else
            dispatchTakePictureIntent();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Image prise par la camera
        if (requestCode == CAMERA_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                File f = new File(currentPhotoPath);
                imageView.setImageURI(Uri.fromFile(f));

                Log.i("storage directory", String.valueOf(Uri.fromFile(f)));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                mediaScanIntent.setData(contentUri);
                this.sendBroadcast(mediaScanIntent);
            }
        }

        //Choix de l'image dans la gallery
        if (requestCode == GALLERY_REQUEST_CODE) {
            if(resultCode == Activity.RESULT_OK){
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG" + timeStamp + "." + getFileExt(contentUri);
                Log.d("tag", "image file name: " + imageFileName);
                imageView.setImageURI(contentUri);
            }
        }    }

    private String getFileExt(Uri contentUri) {
        ContentResolver c= getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if(requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) dispatchTakePictureIntent();
            else Toast.makeText(this, "Camera permission is required to use camera", Toast.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }
}

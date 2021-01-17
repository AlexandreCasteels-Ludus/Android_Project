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

    int idUser;
    int idCatalog;

    boolean dataIsValid = true;

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
        idCatalog = getIntent().getIntExtra("idCatalog", -1);
        idUser = getIntent().getIntExtra("idUser", -1);

        Log.i("userid", String.valueOf(idUser));

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
                dataIsValid = true;

                if (container_volume.getText().toString().matches("")) volume = 0;
                else volume = Float.parseFloat(container_volume.getText().toString());

                RecordData();

                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                mainActivity.putExtra("idUser", idUser);
                startActivity(mainActivity);
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


        //Vérification si on veut ajouter une nouvelle biere ou modifier ses informations dans le catalogue
        if(idCatalog == -1){
            //Récupération des infos de l'activity précédente
            beer = (Beer) getIntent().getSerializableExtra("Beer");
        }
        else{
            Log.i("modif", "modif");
            //Récupération des infos de la DB

            beer = new Beer();

            GetDetails getDetails = (GetDetails) new GetDetails().execute(String.valueOf(idCatalog));

            while (! getDetails.taskIsDone){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            /*
                            PROPERTIES result:
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


            //imageView.setImageBitmap((Bitmap) getDetails.result.getProperty(3));
            String[] dateConcatenated = ((String) getDetails.result.getProperty(4)).split("-");
            datePicker.updateDate(Integer.parseInt(dateConcatenated[0]), Integer.parseInt(dateConcatenated[1]), Integer.parseInt(dateConcatenated[2]));
            commentary.setText((String)getDetails.result.getProperty(1));
            ratingBar.setRating(Float.parseFloat((String)getDetails.result.getProperty(2)));
            spinner.setSelection(Integer.parseInt((String) getDetails.result.getProperty(9)));
            spinner.setEnabled(false);
            container_volume.setText((String)getDetails.result.getProperty(10));
            container_volume.setEnabled(false);
        }
    }

    void RecordData(){
        //Si la bière existe déjà dans la bdd générale alors on modifie ses informations, sinon on les ajoute aux bdd générale et personnelle
        boolean existsInGeneralDB = false;

        //  Log.i("alldata", String.valueOf(ratingBar.getRating()));

        int container = (int) spinner.getSelectedItemId();
        LatLng consumptionPlace = new LatLng(0,0);
        String comment = commentary.getText().toString();
        float rating = ratingBar.getRating();
        java.sql.Date date = new java.sql.Date(datePicker.getYear() - 1900, datePicker.getMonth(), datePicker.getDayOfMonth());
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

        if(idCatalog == -1){
            Log.i("mode123456", "creation");
            AddBeer addbeer = (AddBeer) new AddBeer().execute(
                    beer.name,
                    beer.type,
                    beer.brewery,
                    String.valueOf(beer.percent),
                    String.valueOf(beer.container),
                    String.valueOf(beer.volume),
                    String.valueOf(beer.consumption_place),
                    beer.comment,
                    String.valueOf(beer.rating),
                    beer.image,
                    String.valueOf(beer.date),
                    String.valueOf(idUser)
            );
            Log.i("mode123456", "après requete");
            Log.i("mode123456", beer.name + " " + beer.rating);

            while(! addbeer.taskIsDone){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else{
            Log.i("mode123456", "modification");
            ModifyCatalog modifyCatalog = (ModifyCatalog) new ModifyCatalog().execute(
                    String.valueOf(beer.consumption_place),
                    beer.comment,
                    String.valueOf(beer.rating),
                    beer.image,
                    String.valueOf(beer.date),
                    String.valueOf(idCatalog)
            );

            while(! modifyCatalog.taskIsDone){
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Log.i("modif", container_volume.getText().toString());
            Log.i("modif", String.valueOf(spinner.getSelectedItemId()));
            Log.i("modif", "modif");
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

package com.example.projectbeer;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AddPersonalBeerData extends AppCompatActivity {

    public static final int  CAMERA_PERM_CODE     = 101;
    public static final int  CAMERA_REQUEST_CODE  = 102;
    public static final int  GALLERY_REQUEST_CODE = 105;
    Spinner spinner;
    String[] spinnerChoices = {"Bottle", "Barrel", "Can"};
    Button localisation_button, recordDataButton, editImage;
    ImageView imageView;

    String currentPhotoPath;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_personal_beer_data);
        getSupportActionBar().hide();

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
                if(DataIsValid()) RecordData(); }
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
    }

    boolean DataIsValid(){
        boolean dataIsValid = false;

        if(dataIsValid){
            Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(mainActivity);
        } else{
            Toast.makeText(getApplication(),"Information invalid", Toast.LENGTH_SHORT).show();

        }

        return dataIsValid;
    }

    void RecordData(){
        //Si la bière existe déjà dans la bdd personnelle alors on modifie ses informations, sinon on les ajoute aux bdd générale et personnelle
        boolean existsInPersonalDB = false;

        if(existsInPersonalDB){

        } else{

        }
    }

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

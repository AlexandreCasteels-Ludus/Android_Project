package com.example.projectbeer;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AccountCreation extends AppCompatActivity {

    Button creationButton;
    TextView connectionLink;
    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_creation);
        getSupportActionBar().hide();

        connectionLink = (TextView) findViewById(R.id.accountConnection_link);
        creationButton = (Button) findViewById(R.id.create_button);
        c = this;

        creationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(AccountAlreadyExists())      DisplayErrorMessage("This account already exists");
                else if(!AccountDataIsValid())  DisplayErrorMessage("Datas are invalid");
                else                            AddAccountToDataBase("", "", "");
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

    boolean AccountDataIsValid(){
        boolean b_dataIsValid = false;

        //Vérification si les données entrées par l'utilisateur sont conformes à ce qui est attendu (si le mail est cohérent par exemple)

        return b_dataIsValid;
    }

    boolean AccountAlreadyExists(){
        boolean b_accountAlreadyExists = false;

        //Vérification dans la BDD si un compte avec le même username ou le même mail existe

        return b_accountAlreadyExists;
    }

    void AddAccountToDataBase(String a_username, String a_password, String a_mail){
        //Ajout du compte dans la BDD
    }

}

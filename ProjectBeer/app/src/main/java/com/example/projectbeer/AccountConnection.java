package com.example.projectbeer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class AccountConnection extends AppCompatActivity {
    Button connection_btn;
    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_connection);
        connection_btn = (Button) findViewById(R.id.connection_button);
        c = this;

        Intent catalogueIntent = new Intent(this, MainActivity.class);

        connection_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(AccountExists("datas")) {
                    startActivity(catalogueIntent);
                }
                else {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(c);
                    builder.setMessage("Username or passwords are incorrect");
                    builder.setCancelable(true);
                    builder.create().show();
                }
            }
        });
    }

    boolean AccountExists(String accountDatas){
        boolean b_accountExists = true;

        //Ici, Vérification de l'existence du compte et de la validité des infos

        return b_accountExists;
    }
}

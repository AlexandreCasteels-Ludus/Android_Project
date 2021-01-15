package com.example.projectbeer;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class AccountConnection extends AppCompatActivity {
    Button connection_btn;
    EditText login_input, password_input;
    Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_connection);
        connection_btn = (Button) findViewById(R.id.connection_button);
        login_input = (EditText) findViewById(R.id.login_input);
        password_input = (EditText) findViewById(R.id.password_input);

        c = this;
        getSupportActionBar().hide();

        Intent catalogueIntent = new Intent(this, MainActivity.class);

        connection_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(AccountExists()) {
                    startActivity(catalogueIntent);
                }
                else {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(c);
                    builder.setMessage("Username or password are incorrect");
                    builder.setCancelable(true);
                    builder.create().show();
                }
            }
        });
    }

    boolean AccountExists(){
        SignIn signIn = (SignIn) new SignIn().execute(login_input.getText().toString(), password_input.getText().toString());

        signIn.taskIsEnded = false;
        while(! signIn.taskIsEnded) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return true;//signIn.b_accountAlreadyExists;
    }
}

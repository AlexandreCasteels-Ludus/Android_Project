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
                    GetUserId getUserId = (GetUserId) new GetUserId().execute(login_input.getText().toString(), password_input.getText().toString());

                    getUserId.taskIsEnded = false;
                    while(! getUserId.taskIsEnded) {
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    catalogueIntent.putExtra("idUser", getUserId.result);
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
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return true; //signIn.result;
    }

    class GetUserId extends AsyncTask<String, Void, String> {

        String NAMESPACE = "http://beerapp.atwebpages.com/";
        String NAME_WEBSERVICE = "webService/";
        String SOAP_ACTION = NAMESPACE + NAME_WEBSERVICE;
        String URL= NAMESPACE + NAME_WEBSERVICE + "service.php?wsdl";

        String METHOD_NAME = "GetUserId";
        String PARAMS_1_login = "login";
        String PARAMS_2_password = "password";;

        int result = -1;
        boolean taskIsEnded = false;


        @Override
        protected String doInBackground(String... params) {

            SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);

            PropertyInfo propertyInfo1 = new PropertyInfo();

            propertyInfo1.setName(PARAMS_1_login);
            propertyInfo1.setValue(params[0]);
            propertyInfo1.setType(String.class);
            soapObject.addProperty(propertyInfo1);

            PropertyInfo propertyInfo2 = new PropertyInfo();
            propertyInfo2.setName(PARAMS_2_password);
            propertyInfo2.setValue(params[1]);
            propertyInfo2.setType(String.class);
            soapObject.addProperty(propertyInfo2);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(soapObject);

            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);

            try {
                httpTransportSE.call(SOAP_ACTION + METHOD_NAME, envelope);
                result = (int) envelope.getResponse();
            } catch (Exception e) {
                Log.i("debug", e.getMessage());
            }

            taskIsEnded = true;
            return "end";
        }
    }
}

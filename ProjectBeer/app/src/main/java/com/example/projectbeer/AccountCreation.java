package com.example.projectbeer;

import android.content.Context;
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



public class AccountCreation extends AppCompatActivity {

    final int MIN_CHAR_INPUT = 4;

    Button creationButton;
    Button connectionPageButton;
    Context c;

    EditText login_input, password_input, email_input;

    boolean b_accountWellCreated = false;
    boolean taskIsEnded = false;

    String NAMESPACE = "http://beerapp.atwebpages.com/";
    String NAME_WEBSERVICE = "webService/";
    String SOAP_ACTION = NAMESPACE + NAME_WEBSERVICE;
    String URL= NAMESPACE + NAME_WEBSERVICE + "service.php?wsdl";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_creation);
        getSupportActionBar().hide();

        connectionPageButton = (Button) findViewById(R.id.accountConnection_link);
        creationButton = (Button) findViewById(R.id.create_button);
        login_input = (EditText) findViewById(R.id.login_input);
        password_input = (EditText) findViewById(R.id.password_input);
        email_input = (EditText) findViewById(R.id.email_input);
        c = this;

        creationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!AccountDataIsValid())
                    DisplayErrorMessage("Datas are invalid");
                else if(AddAccountToDataBase(
                        login_input.getText().toString(),
                        password_input.getText().toString(),
                        email_input.getText().toString()))
                    DisplayErrorMessage("Account created");
                else
                    DisplayErrorMessage("This account already exists");
            }
        });

        connectionPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent connectionActivity = new Intent(c, AccountConnection.class);
                startActivity(connectionActivity);
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
         return true;
        //Vérification si les données entrées par l'utilisateur sont conformes à ce qui est attendu (si le mail est cohérent par exemple)
        /*
        return login_input.getText().toString().length() > MIN_CHAR_INPUT &&
                 password_input.getText().toString().length() > MIN_CHAR_INPUT &&
                 email_input.getText().toString().length() > MIN_CHAR_INPUT &&
                 email_input.getText().toString().contains("@") &&
                 email_input.getText().toString().contains(".");*/
    }

    boolean AccountAlreadyExists(){

        //Vérification dans la BDD si un compte avec le même username ou le même mail existe

        SignIn signIn = (SignIn) new SignIn().execute(login_input.getText().toString(), password_input.getText().toString());

        signIn.taskIsEnded = false;
        while(! signIn.taskIsEnded) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return signIn.b_accountAlreadyExists;
    }

    boolean AddAccountToDataBase(String a_username, String a_password, String a_mail){
        //Ajout du compte dans la BDD

        Registration registration = (Registration) new Registration().execute(a_username, a_password, a_mail);

        taskIsEnded = false;
        while(! taskIsEnded) {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return registration.result;
    }


    class Registration extends AsyncTask<String, Void, String> {

        String METHOD_NAME = "Registration";
        String PARAMS_1_login = "login";
        String PARAMS_2_password = "password";
        String PARAMS_3_email = "mail";

        boolean result = false;


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

            PropertyInfo propertyInfo3 = new PropertyInfo();
            propertyInfo3.setName(PARAMS_3_email);
            propertyInfo3.setValue(params[2]);
            propertyInfo3.setType(String.class);
            soapObject.addProperty(propertyInfo3);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(soapObject);

            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);

            try {
                httpTransportSE.call(SOAP_ACTION + METHOD_NAME, envelope);
                result = (boolean) envelope.getResponse();
            } catch (Exception e) {
                Log.i("debug", e.getMessage());
            }

            taskIsEnded = true;
            return "end";
        }
    }
}

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
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;


public class AccountCreation extends AppCompatActivity {

    Button creationButton;
    Button connectionPageButton;
    Context c;

    EditText login_input, password_input, email_input;

    boolean b_accountAlreadyExists = false;

    String NAMESPACE = "http://192.168.1.5:9999/";
    String NAME_WEBSERVICE = "beerAppServiceWeb/";
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
                if(AccountAlreadyExists())      DisplayErrorMessage("This account already exists");
                else if(!AccountDataIsValid())  DisplayErrorMessage("Datas are invalid");
                else
                    AddAccountToDataBase(
                        login_input.getText().toString(),
                        password_input.getText().toString(),
                        email_input.getText().toString()
                    );
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
        boolean b_dataIsValid = false;

        //Vérification si les données entrées par l'utilisateur sont conformes à ce qui est attendu (si le mail est cohérent par exemple)

        return b_dataIsValid;
    }

    boolean AccountAlreadyExists(){

        new SignIn().execute(login_input.getText().toString(), password_input.getText().toString());
        //Vérification dans la BDD si un compte avec le même username ou le même mail existe

        return b_accountAlreadyExists;
    }

    void AddAccountToDataBase(String a_username, String a_password, String a_mail){
        //Ajout du compte dans la BDD
    }


    class SignIn extends AsyncTask<String, Void, String> {

        String METHOD_NAME = "SignIn";
        String PARAMS_1_login = "login";
        String PARAMS_2_password = "password";

        boolean result = false;
        @Override
        protected void onPostExecute(String s) {
            b_accountAlreadyExists = result;
        }

        @Override
        protected String doInBackground(String... params) {

            SoapObject soapObject = new SoapObject(NAMESPACE, "SignIn");

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
            propertyInfo3.setName("getId");
            propertyInfo3.setValue(false);
            propertyInfo3.setType(Boolean.class);
            soapObject.addProperty(propertyInfo3);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
            envelope.setOutputSoapObject(soapObject);

            HttpTransportSE httpTransportSE = new HttpTransportSE(URL);

            try {
                httpTransportSE.call(SOAP_ACTION + METHOD_NAME, envelope);
                SoapPrimitive soapPrimitive = (SoapPrimitive) envelope.getResponse();
                result = (boolean)soapPrimitive.getValue();
            }
            catch(Exception e) {
                Log.i("debug", e.getMessage());
            }

            // return result;
            return "end";
        }
    }

}

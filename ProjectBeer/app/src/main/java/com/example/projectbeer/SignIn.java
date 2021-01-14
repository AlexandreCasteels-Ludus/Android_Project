package com.example.projectbeer;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class SignIn extends AsyncTask<String, Void, String> {

    String NAMESPACE = "http://beerapp.atwebpages.com/";
    String NAME_WEBSERVICE = "webService/";
    String SOAP_ACTION = NAMESPACE + NAME_WEBSERVICE;
    String URL= NAMESPACE + NAME_WEBSERVICE + "service.php?wsdl";

    String METHOD_NAME = "SignIn";
    String PARAMS_1_login = "login";
    String PARAMS_2_password = "password";

    public boolean b_accountAlreadyExists = false;
    public boolean result = false;
    public boolean taskIsEnded = false;

    @Override
    protected void onPostExecute(String s) {
        b_accountAlreadyExists = result;
    }

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
            result = (boolean) envelope.getResponse();
        } catch (Exception e) {
            Log.i("debug", e.getMessage());
        }

        taskIsEnded = true;
        return "end";
    }
}
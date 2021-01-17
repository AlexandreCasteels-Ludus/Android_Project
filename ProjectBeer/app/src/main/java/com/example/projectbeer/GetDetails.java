package com.example.projectbeer;

import android.os.AsyncTask;
import android.util.Log;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GetDetails extends AsyncTask<String, Void, String> {

    String NAMESPACE = "http://beerapp.atwebpages.com/";
    String NAME_WEBSERVICE = "webService/";
    String SOAP_ACTION = NAMESPACE + NAME_WEBSERVICE;
    String URL = NAMESPACE + NAME_WEBSERVICE + "service.php?wsdl";


    String METHOD_NAME = "GetDetails";

    //beer
    String PARAMS_1_idCatalog = "idCatalog";
    SoapObject result;
    boolean taskIsDone = false;

    @Override
    protected String doInBackground(String... params) {

        SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName(PARAMS_1_idCatalog);
        propertyInfo1.setValue(params[0]);
        propertyInfo1.setType(String.class);
        soapObject.addProperty(propertyInfo1);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(soapObject);

        HttpTransportSE httpTransportSE = new HttpTransportSE(URL);

        try {
            httpTransportSE.call(SOAP_ACTION + METHOD_NAME, envelope);
            result = (SoapObject) envelope.getResponse();

        } catch (Exception e) {
            Log.i("wxcv", e.getMessage().toString());
        }

        taskIsDone = true;
        return "end";
    }
}

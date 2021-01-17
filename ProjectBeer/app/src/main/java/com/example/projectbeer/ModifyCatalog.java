package com.example.projectbeer;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Date;

public class ModifyCatalog extends AsyncTask<String, Void, String> {

    String NAMESPACE = "http://beerapp.atwebpages.com/";
    String NAME_WEBSERVICE = "webService/";
    String SOAP_ACTION = NAMESPACE + NAME_WEBSERVICE;
    String URL= NAMESPACE + NAME_WEBSERVICE + "service.php?wsdl";

    String METHOD_NAME = "ModifyCatalog";

    boolean taskIsDone;

    String PARAMS_1_localisation = "localisation";
    String PARAMS_2_comment = "comment";
    String PARAMS_3_score = "score";
    String PARAMS_4_photo = "photo";
    String PARAMS_5_date = "date";
    String PARAMS_6_idCatalog = "idCatalog";

    boolean result = false;

    @Override
    protected String doInBackground(String... params) {

        SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);

        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName(PARAMS_1_localisation);
        propertyInfo1.setValue(params[0]);
        propertyInfo1.setType(String.class);
        soapObject.addProperty(propertyInfo1);

        PropertyInfo propertyInfo2 = new PropertyInfo();
        propertyInfo2.setName(PARAMS_2_comment);
        propertyInfo2.setValue(params[1]);
        propertyInfo2.setType(String.class);
        soapObject.addProperty(propertyInfo2);

        PropertyInfo propertyInfo3 = new PropertyInfo();
        propertyInfo3.setName(PARAMS_3_score);
        propertyInfo3.setValue(params[2]);
        propertyInfo3.setType(String.class);
        soapObject.addProperty(propertyInfo3);

        PropertyInfo propertyInfo4 = new PropertyInfo();
        propertyInfo4.setName(PARAMS_4_photo);
        propertyInfo4.setValue(params[3]);
        propertyInfo4.setType(float.class);
        soapObject.addProperty(propertyInfo4);

        PropertyInfo propertyInfo5 = new PropertyInfo();
        propertyInfo5.setName(PARAMS_5_date);
        propertyInfo5.setValue(params[4]);
        propertyInfo5.setType(int.class);
        soapObject.addProperty(propertyInfo5);

        PropertyInfo propertyInfo6 = new PropertyInfo();
        propertyInfo6.setName(PARAMS_6_idCatalog);
        propertyInfo6.setValue(params[5]);
        propertyInfo6.setType(float.class);
        soapObject.addProperty(propertyInfo6);

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.setOutputSoapObject(soapObject);

        HttpTransportSE httpTransportSE = new HttpTransportSE(URL);

        try {
            httpTransportSE.call(SOAP_ACTION + METHOD_NAME, envelope);
            result = (boolean) envelope.getResponse();
            Log.i("wxcv", String.valueOf(envelope.getResponse()));

        } catch (Exception e) {
            Log.i("wxcv", e.getMessage().toString());
        }

        taskIsDone = true;
        return "end";
    }
}

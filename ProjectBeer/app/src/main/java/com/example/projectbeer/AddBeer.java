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

class AddBeer extends AsyncTask<String, Void, String> {


    String NAMESPACE = "http://beerapp.atwebpages.com/";
    String NAME_WEBSERVICE = "webService/";
    String SOAP_ACTION = NAMESPACE + NAME_WEBSERVICE;
    String URL= NAMESPACE + NAME_WEBSERVICE + "service.php?wsdl";

    String METHOD_NAME = "AddBeer";

    boolean taskIsDone;
    //beer
    String PARAMS_1_name = "name";
    String PARAMS_2_type = "type";
    String PARAMS_3_brewery = "brewery";
    String PARAMS_4_percent = "percentAlcohol";
    String PARAMS_5_container = "container";        //!=type
    String PARAMS_6_volume = "volume";              //!=type

    //catalog
    String PARAMS_7_localisation = "localisation";  //!=type
    String PARAMS_8_comment = "comment";
    String PARAMS_9_score = "score";                //!=type
    String PARAMS_10_photo = "photo";               //!=type
    String PARAMS_11_date = "date";

    String PARAMS_12_user = "fk_user";


    boolean result = false;

    @Override
    protected String doInBackground(String... params) {

        SoapObject soapObject = new SoapObject(NAMESPACE, METHOD_NAME);

        //Nom bière
        PropertyInfo propertyInfo1 = new PropertyInfo();
        propertyInfo1.setName(PARAMS_1_name);
        propertyInfo1.setValue(params[0]);
        propertyInfo1.setType(String.class);
        soapObject.addProperty(propertyInfo1);

        //Type bière
        PropertyInfo propertyInfo2 = new PropertyInfo();
        propertyInfo2.setName(PARAMS_2_type);
        propertyInfo2.setValue(params[1]);
        propertyInfo2.setType(String.class);
        soapObject.addProperty(propertyInfo2);

        //Nom brasserie
        PropertyInfo propertyInfo3 = new PropertyInfo();
        propertyInfo3.setName(PARAMS_3_brewery);
        propertyInfo3.setValue(params[2]);
        propertyInfo3.setType(String.class);
        soapObject.addProperty(propertyInfo3);

        //Pourcentage d'alcool
        PropertyInfo propertyInfo4 = new PropertyInfo();
        propertyInfo4.setName(PARAMS_4_percent);
        propertyInfo4.setValue(params[3]);
        propertyInfo4.setType(float.class);
        soapObject.addProperty(propertyInfo4);

        //Type de contenant
        PropertyInfo propertyInfo5 = new PropertyInfo();
        propertyInfo5.setName(PARAMS_5_container);
        propertyInfo5.setValue(params[4]);
        propertyInfo5.setType(int.class);
        soapObject.addProperty(propertyInfo5);

        //Volume du contenant
        PropertyInfo propertyInfo6 = new PropertyInfo();
        propertyInfo6.setName(PARAMS_6_volume);
        propertyInfo6.setValue(params[5]);
        propertyInfo6.setType(float.class);
        soapObject.addProperty(propertyInfo6);

        //Localisation
        PropertyInfo propertyInfo7 = new PropertyInfo();
        propertyInfo7.setName(PARAMS_7_localisation);
        propertyInfo7.setValue(params[6]);
        propertyInfo7.setType(LatLng.class);
        soapObject.addProperty(propertyInfo7);

        //Commentaire
        PropertyInfo propertyInfo8 = new PropertyInfo();
        propertyInfo8.setName(PARAMS_8_comment);
        propertyInfo8.setValue(params[7]);
        propertyInfo8.setType(String.class);
        soapObject.addProperty(propertyInfo8);

        //Note
        PropertyInfo propertyInfo9 = new PropertyInfo();
        propertyInfo9.setName(PARAMS_9_score);
        propertyInfo9.setValue(params[8]);
        propertyInfo9.setType(float.class);
        soapObject.addProperty(propertyInfo9);

        //Photo
        PropertyInfo propertyInfo10 = new PropertyInfo();
        propertyInfo10.setName(PARAMS_10_photo);
        propertyInfo10.setValue(params[9]);
        propertyInfo10.setType(String.class);
        soapObject.addProperty(propertyInfo10);

        //Date
        PropertyInfo propertyInfo11 = new PropertyInfo();
        propertyInfo11.setName(PARAMS_11_date);
        propertyInfo11.setValue(params[10]);
        propertyInfo11.setType(Date.class);
        soapObject.addProperty(propertyInfo11);

        //#Utilisateur lié
        PropertyInfo propertyInfo12 = new PropertyInfo();
        propertyInfo12.setName(PARAMS_12_user);
        propertyInfo12.setValue(params[11]);
        propertyInfo12.setType(String.class);
        soapObject.addProperty(propertyInfo12);

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
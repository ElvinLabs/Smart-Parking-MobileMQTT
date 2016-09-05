package com.fypma.elvinlabs.parkme;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.content.Context;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationManager;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class MapsActivity extends ActionBarActivity implements MqttCallback{

    private GoogleMap mMap;
    private Socket mSocket;
    private MqttClient client;
    private Toast toast;
    private boolean isSataliteMode = false;
    private String filtering = "both";
    private MqttMobileClient mqttMobileClient;
    private String m_deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle extras = getIntent().getExtras();
        try{
            isSataliteMode = extras.getBoolean("isSalatite");
            filtering = extras.getString("filter");
        }catch(NullPointerException e){
            System.out.println(e);
        }

        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            buildAlertMessageNoGps();
        }

//        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
//        m_deviceId = telephonyManager.getDeviceId();

//        mqttMobileClient = new MqttMobileClient();
//        mqttMobileClient.createMobileClient(client);

        createMobileClient();

        Context context = getApplicationContext();
        CharSequence text = "CONNECTING TO THE SERVER ...";
        int duration = Toast.LENGTH_LONG;
        toast = Toast.makeText(context, text, duration);
        toast.show();

        setUpMapIfNeeded();
    }

    public void onButtonClicked(View v){
        setUpMapIfNeeded();
    }


    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog,  final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void onNewMessage (MqttMessage message) {

//            final JSONArray obj = message;
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mMap.clear();
//                    for(int i=0; i<obj.length(); i++){
//                        try{
//
////                            System.out.println(" >>>>>>>>>>>> socket.io connceted --- "+ obj.getJSONObject(i).toString());
//
//                            if(filtering.equals("indoor") && obj.getJSONObject(i).getString("prkType").equals("Indoor")){
//
//                                createMarker(obj.getJSONObject(i).getDouble("lat"), obj.getJSONObject(i).getDouble("lng"), obj.getJSONObject(i).getString("name"),
//                                        "Available parking slots - " + obj.getJSONObject(i).getInt("availableSlots") + "/"+obj.getJSONObject(i).getInt("numOfSlots"),obj.getJSONObject(i).getString("prkType"),obj.getJSONObject(i).getInt("availableSlots"));
//
//                            }else if(filtering.equals("outdoor") && obj.getJSONObject(i).getString("prkType").equals("Outdoor")){
//
//                                createMarker(obj.getJSONObject(i).getDouble("lat"), obj.getJSONObject(i).getDouble("lng"), obj.getJSONObject(i).getString("name"),
//                                        "Available parking slots - " + obj.getJSONObject(i).getInt("availableSlots") + "/"+obj.getJSONObject(i).getInt("numOfSlots"),obj.getJSONObject(i).getString("prkType"),obj.getJSONObject(i).getInt("availableSlots"));
//
//                            }else if(filtering.equals("both")){
//
//                                createMarker(obj.getJSONObject(i).getDouble("lat"), obj.getJSONObject(i).getDouble("lng"), obj.getJSONObject(i).getString("name"),
//                                        "Available parking slots - " + obj.getJSONObject(i).getInt("availableSlots") + "/"+obj.getJSONObject(i).getInt("numOfSlots"),obj.getJSONObject(i).getString("prkType"),obj.getJSONObject(i).getInt("availableSlots"));
//
//                            }
//
//                        } catch (JSONException e) {
//                            return;
//                        }
//                    }
//                }
//            });

    };

    public void createMarker(double latitude, double longitude, String title, String snippet, String prkType, int avaliable) {

        if(avaliable >= 5){

            if(prkType.equalsIgnoreCase("Indoor")){
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .anchor(0.5f, 0.5f)
                        .title(title)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.in5p)));
            }else{
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .anchor(0.5f, 0.5f)
                        .title(title)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.out5m)));
            }

        }else if(avaliable<5 && avaliable>=2){

            if(prkType.equalsIgnoreCase("Indoor")){
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .anchor(0.5f, 0.5f)
                        .title(title)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.in2p)));
            }else{
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .anchor(0.5f, 0.5f)
                        .title(title)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.out2p)));
            }

        }else{

            if(prkType.equalsIgnoreCase("Indoor")){
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .anchor(0.5f, 0.5f)
                        .title(title)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.in2m)));
            }else{
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(latitude, longitude))
                        .anchor(0.5f, 0.5f)
                        .title(title)
                        .snippet(snippet)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.out2m)));
            }

        }



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent i = new Intent(MapsActivity.this, SettingsActivity.class);
            i.putExtra("isSalatite", isSataliteMode);
            i.putExtra("filter", filtering);
            startActivity(i);
            finish();
            return true;
        }else if(id == R.id.action_help) {
            Intent i = new Intent(MapsActivity.this, HelpActivity.class);
            startActivity(i);
            finish();
            return true;
        }else if(id == R.id.action_about) {
            Intent i = new Intent(MapsActivity.this, AboutActivity.class);
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setUpMapIfNeeded() {
        if (mMap == null) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        mMap.moveCamera(CameraUpdateFactory.zoomTo(14));

        if (isSataliteMode == true){
            mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        }

        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location arg0) {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(arg0.getLatitude(), arg0.getLongitude())));
            }
        });
    }

    // MQTT methods --------------------------------------------------

    public void createMobileClient(){
        try {
            client = new MqttClient("tcp://52.24.61.14:1883","mobileclient" , null);
            client.connect();
            client.setCallback(this);
            client.subscribe("toMobile");
            System.out.println(client.isConnected()+"-------------------------------------------------------------");
        } catch (MqttException e) {
            System.out.println("error prints --------------------------------------------------------------------------");
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
//        createMarker(80.3552,7.1521,"a","b","Indoor",10);
//        createMarker(80.3552,7.1521,"title","parkig","Indoor",10);
        runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mMap.clear();
                    createMarker(7.25417,80.59667,"title","parkig","Indoor",10);
                }
            });
        System.out.println(message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}
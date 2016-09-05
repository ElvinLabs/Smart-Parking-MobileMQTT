package com.fypma.elvinlabs.parkme;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;

/**
 * Created by dileepa on 9/4/16.
 */
public class MqttMobileClient implements MqttCallback  {


    MqttClient client;

    public MqttMobileClient(){}

    public void createMobileClient(MqttClient mqttClient){
        try {
            client = new MqttClient("tcp://52.24.61.14:1883","mobileclient" , null);
            client.connect();
            client.setCallback(this);
            client.subscribe("toMobile");
//            System.out.println(client.isConnected()+"-------------------------------------------------------------");
        } catch (MqttException e) {
//            System.out.println("error prints --------------------------------------------------------------------------");
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
        System.out.println(message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}

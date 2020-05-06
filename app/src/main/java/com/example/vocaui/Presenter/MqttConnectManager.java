package com.example.vocaui.Presenter;

import android.util.Log;

import com.example.vocaui.BackgroudProccess.MqttBroadcast;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

public class MqttConnectManager {
    static MqttConnectManager instance;
    private ArrayList<Callback> onEventMqttList;



    private ArrayList<Callback> onEventNoClearList;
    public void setOnEventNoClear(Callback onEventNoClear) {
        if(onEventNoClearList == null)
            onEventNoClearList = new ArrayList<>();
        this.onEventNoClearList.add(onEventNoClear);
    }
    private MqttConnectManager() {
        if(onEventNoClearList == null)
            onEventNoClearList = new ArrayList<>();
        if(onEventMqttList == null)
            onEventMqttList = new ArrayList<>();
        MqttBroadcast.setOnConnectStatusChange(new MqttBroadcast.OnConnectStatusChange() {
            @Override
            public void onDisconnect() {
                for (Callback onEventMqtt:
                onEventMqttList) {
                    if(onEventMqtt!=null)
                        onEventMqtt.onDisconnect();
                }
                for (Callback onEventMqtt:
                        onEventNoClearList) {
                    if(onEventMqtt!=null)
                        onEventMqtt.onDisconnect();
                }

            }
            @Override
            public void onConnect() {
                for (Callback onEventMqtt:
                        onEventMqttList) {
                    if (onEventMqtt != null)
                        onEventMqtt.onConnect();
                }
                for (Callback onEventMqtt:
                        onEventNoClearList) {
                    if (onEventMqtt != null)
                        onEventMqtt.onConnect();
                }
            }
            @Override
            public void messageArrived(String topic, MqttMessage message) {
                for (Callback onEventMqtt:
                        onEventNoClearList) {
                    if (onEventMqtt != null)
                        onEventMqtt.onMessageArrived(topic, message);
                }
                for (Callback onEventMqtt:
                        onEventMqttList) {
                    if (onEventMqtt != null)
                        onEventMqtt.onMessageArrived(topic, message);
                }

            }
        });
    }

    public static MqttConnectManager getInstance() {
        if(instance==null)
            instance = new MqttConnectManager();
        return instance;
    }

    public void setOnEventMqtt(Callback onEventMqtt) {
        if(onEventMqttList == null)
            onEventMqttList = new ArrayList<>();
        onEventMqttList.add(onEventMqtt);
        String tmp=" ";
        for (Callback e:
                onEventMqttList) {
            tmp+= e.toString();
        }
        Log.d("setOnEventMqtt", String.valueOf(onEventMqttList.size()));

    }
    public void removeOnEventMqtt(Callback onEventMqtt) {
        if(onEventMqttList == null)
            return;
        for (Callback e:
                onEventMqttList) {
            if (e.toString().equals(onEventMqtt.toString()))
                onEventMqttList.remove(e);
        }


    }
    public void clearOnEventMqtt() {
        if(onEventMqttList == null)
            return;
        onEventMqttList.clear();


    }
    public static void sendData(String topic, String content) {

        MqttBroadcast.publish(topic,content);
    }

    public String getName(){
        return MqttBroadcast.get_userName();
    }
    public interface Callback{
        void onDisconnect();
        void onConnect();
        void onMessageArrived(String topic, MqttMessage message);

    }

}

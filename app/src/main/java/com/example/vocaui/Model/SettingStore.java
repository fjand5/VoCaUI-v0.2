package com.example.vocaui.Model;

import com.example.vocaui.BackgroudProccess.MqttBroadcast;

import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

public class SettingStore {
    private static SettingStore instance;

    volatile ArrayList<OnNewSettingListenner> onNewSettingListennerArrayList;

    private SettingStore() {
        MqttBroadcast.setOnMessageArrivedListenner(new MqttBroadcast.OnMessageArrivedListenner() {
            @Override
            public void onMessageArrived(String topic, MqttMessage message) {
                if(onNewSettingListennerArrayList == null)
                    return;



                for (OnNewSettingListenner msg:
                     onNewSettingListennerArrayList) {

                    String key = topic.replace(
                            MqttBroadcast.get_topicRx(),
                            ""
                    ).replace(
                            "/",
                            ""
                    );
                    msg.onNewSetting(key,new String(message.getPayload()));
                }
            }
        });
    }

    public static SettingStore getInstance(){
        if (instance == null)
            instance = new SettingStore();
        return instance;
    }

    public void setOnNewSettingListenner(OnNewSettingListenner onNewSettingListenner) {
        if(onNewSettingListennerArrayList == null)
            onNewSettingListennerArrayList = new ArrayList<>();

        this.onNewSettingListennerArrayList.add(onNewSettingListenner);
    }

    public void commitSetting(String key, String newSetting){

        MqttBroadcast.publish(MqttBroadcast.get_topicRx()+"/"+key,newSetting,true);
    }
    public interface OnNewSettingListenner{
        void onNewSetting(String key, String inComeNewSetting);
    };
}

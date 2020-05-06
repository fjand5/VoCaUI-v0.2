package com.example.vocaui.BackgroudProccess;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.vocaui.Presenter.MqttSetting;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Dictionary;


public class MqttBroadcast extends BroadcastReceiverExt {
    static OnConnectStatusChange onConnectStatusChange;



    static OnMessageArrivedListenner _onMessageArrivedListenner;
    static public boolean connectNow = false;

    static Context mContext;

   MqttConnectOptions options;
   String clientId;
    public static MqttAndroidClient client;
    private IMqttActionListener mqttListenner;
    MqttCallback callbackMQTT;

    static String _add;
    static int _port;
    static String _user;
    static String _pass;
    static String _topicRx;
    public static String get_topicRx() {
        String ret;
        ret = _topicRx.replace("#","")
        .replace("/","");
        return ret;
    }
    public static String get_userName() {
        return _user;
    }

    public static void setOnConnectStatusChange(OnConnectStatusChange callback){
        onConnectStatusChange=callback;

    }
    public static void setOnMessageArrivedListenner(OnMessageArrivedListenner onMessageArrivedListenner) {
        _onMessageArrivedListenner = onMessageArrivedListenner;
    }
    public static boolean getStatus(){
        if(client == null)
            return false;
        return client.isConnected();
    }

    static String getActionName(){
        return MqttBroadcast.class.getName();
    }
    MqttBroadcast() {
        super(MqttBroadcast.class.getName());
    }
    public static void getInfo(){
        Dictionary<String,Object> tmp = MqttSetting.getInstance().getInfo(mContext);
        _add=tmp.get("address").toString();
        try {
            _port=Integer.valueOf(tmp.get("port").toString());
        } catch (NumberFormatException nfe) {
            _port = 0;
        }

        _user= tmp.get("username").toString();
        _pass= tmp.get("password").toString();
        _topicRx=tmp.get("topic").toString();

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        initMqttConnection();
    }
    public static void reSubcribe(){
        try {

            client.subscribe(_topicRx, 2, mContext, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    if(onConnectStatusChange != null)
                        onConnectStatusChange.onConnect();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    if(onConnectStatusChange != null)
                        onConnectStatusChange.onDisconnect();
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public  void initMqttConnection(){
        getInfo();
        options = new MqttConnectOptions();
        clientId = MqttClient.generateClientId();

        client= new MqttAndroidClient(mContext, "tcp://"+_add+":"+_port,
                clientId);

        options.setUserName(_user);
        options.setPassword(_pass.toCharArray());


        mqttListenner = new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                connectNow=false;
                try {
                    IMqttActionListener iMqttActionListener = new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            Log.d("mqtt","onSuccess onSuccess: ");

                            if(onConnectStatusChange != null)
                                onConnectStatusChange.onConnect();
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            Log.d("mqtt","onFailure111  ");
                            if(onConnectStatusChange != null)
                                onConnectStatusChange.onDisconnect();

                        }
                    };
                    client.subscribe(_topicRx +"/tx", 2, mContext, iMqttActionListener);
                    client.subscribe(_topicRx +"/dashboard", 2, mContext, iMqttActionListener);
                    Log.d("mqtt","subscribing: " + _topicRx);


                } catch (MqttException e) {
                    e.printStackTrace();
                }
                if(onConnectStatusChange != null)
                    onConnectStatusChange.onConnect();

            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                if(onConnectStatusChange != null)
                    onConnectStatusChange.onDisconnect();
                reCallMe(mContext);
                Log.d("mqtt","onFailure  ");

            }
        };
        callbackMQTT = new MqttCallback() {

            @Override
            public void connectionLost(Throwable cause) {
                Log.d("mqtt","connectionLost  ");
                if(onConnectStatusChange != null)
                    onConnectStatusChange.onDisconnect();
                if(connectNow)
                    reCallMe(mContext,0);
                else
                    reCallMe(mContext);
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) {
                String content = new String(message.getPayload());
                Log.d("mqtt", "topic: " + topic +" - "+ "content: " + content);
                if(_onMessageArrivedListenner!=null)
                {

                    _onMessageArrivedListenner.onMessageArrived(topic,  message);
                }


                if(onConnectStatusChange!=null){

                    onConnectStatusChange.messageArrived( topic,  message);
                }




            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        };

        client.setCallback(callbackMQTT);
        try {
            client.connect(options,mContext,mqttListenner);
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    static public void publish(String topic, String content){
        publish(topic ,content,false);
    }
    static public void publish(String topic, String content, boolean retain){
        if(client == null){

            return;
        }

        try {
            client.publish(topic ,content.getBytes(),2,retain);

        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
    public interface OnConnectStatusChange{
        void onDisconnect();
        void onConnect();
        void messageArrived(String topic, MqttMessage message);
    }
    public interface OnMessageArrivedListenner{
      void onMessageArrived(String topic, MqttMessage message);
    };
}

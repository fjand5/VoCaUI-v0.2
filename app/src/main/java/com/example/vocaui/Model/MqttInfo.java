package com.example.vocaui.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MqttInfo{

    static MqttInfo instance;
    private MqttInfo(){}
    public static MqttInfo getInstance(){
        if(instance == null){
            instance = new MqttInfo();
        }
        return instance;
    };


    public String getAddress(Context context) {

        if(instance == null)
            return "";
        else
            return getSharedPreferences(context).getString("address","");
    }


    public int getPort(Context context) {
        if(instance == null)
            return 0;
        else
            return getSharedPreferences(context).getInt("port",0);
    }


    public String getUsername(Context context) {
        if(instance == null)
            return "";
        else
            return getSharedPreferences(context).getString("username","");
    }


    public String getPassword(Context context) {
        if(instance == null)
            return "";
        else
            return getSharedPreferences(context).getString("password","");
    }


    public String getTopic(Context context) {
        if(instance == null)
            return "";
        return getSharedPreferences(context).getString("topic","");
    }

    public void setInfo(Context context, String addr, int port, String name, String pass, String topic) {

            SharedPreferences.Editor editor = getSharedPreferences(context).edit();
            editor.putString("address",addr).commit();
            editor.putInt("port",port).commit();
            editor.putString("username",name).commit();
            editor.putString("password",pass).commit();
            editor.putString("topic",topic).commit();
        editor.apply();

    }
    private SharedPreferences getSharedPreferences(Context context){
        SharedPreferences  ret;
        ret = context.getSharedPreferences(
                com.example.vocaui.Model.MqttInfo.class.toString()
                ,context.MODE_PRIVATE);

        return ret;
    }

}

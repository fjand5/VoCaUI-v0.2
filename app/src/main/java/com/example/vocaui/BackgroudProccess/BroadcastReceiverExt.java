package com.example.vocaui.BackgroudProccess;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;

abstract class BroadcastReceiverExt extends BroadcastReceiver {
    final static int WAIT_FOR_RECONNECT = 3000;
    String _actionName;

    BroadcastReceiverExt(String  actionName){
        _actionName=actionName;
    }
    void reCallMe(final Context context, int timeDelay){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent fintent = new Intent();
                fintent.setAction(_actionName);
                fintent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
                context.sendBroadcast(fintent);
            }
        },timeDelay);
    }
    void reCallMe(final Context context){
        reCallMe(context,WAIT_FOR_RECONNECT);
    }


    public void startMe(Context context){
        Intent mIntent = new Intent();
        mIntent.setAction(_actionName);
        mIntent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
        context.sendBroadcast(mIntent);


    }

}


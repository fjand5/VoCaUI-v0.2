package com.example.vocaui.View.Utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.vocaui.Model.MqttInfo;
import com.example.vocaui.Presenter.MqttConnectManager;
import com.example.vocaui.Presenter.MqttSetting;

import com.example.vocaui.R;
import com.example.vocaui.View.MenuPage_Frag;

import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.nodes.Element;

import java.util.ArrayList;
import java.util.List;


public class RenderElement {
    private List<MenuPage_Frag> menuPage_frags;
    private static final RenderElement ourInstance = new RenderElement();
    int curCol =0;
    public static RenderElement getInstance() {
        return ourInstance;
    }

    public List<MenuPage_Frag> getMenuPage_frags() {
        return menuPage_frags;
    }
    public void clearAll(){
        curCol =0;
        if(menuPage_frags==null)
            menuPage_frags = new ArrayList<>();
        else
            menuPage_frags.clear();
        MqttConnectManager.getInstance().clearOnEventMqtt();
    }
    private RenderElement() {
        menuPage_frags = new ArrayList<>();
    }
    public GridLayout render_container(Context context, String name){
        GridLayout childElementMenuLayout = new GridLayout(context);
        childElementMenuLayout.setPadding(15,15,15,15);
        MenuPage_Frag menuPage_frag = new MenuPage_Frag();

        render_newLine(childElementMenuLayout);

        menuPage_frag.setRootView(childElementMenuLayout);

        menuPage_frag.setName(name);
        menuPage_frags.add(menuPage_frag);
        return childElementMenuLayout;
    }
    public void render_button(final Element element, final GridLayout parent){
        Log.d("render","render_button");
        Button button = new Button(parent.getContext());

//        MaterialButton button = new MaterialButton(parent.getContext());
        button.setText(element.html());
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id",element.id());
                    jsonObject.put("value",element.val());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MqttConnectManager.sendData(MqttSetting.getInstance().getInfo(parent.getContext()).get("topic").toString()+"/rx",jsonObject.toString());
            }
        });
        parent.addView(button,new GridLayout.LayoutParams(
                GridLayout.spec(parent.getRowCount()-1),
                GridLayout.spec(curCol++)));




    }
    public void render_label(final Element element, final GridLayout parent){
        Log.d("render","render_label" + element);
        final TextView textView = new TextView(parent.getContext());
        textView.setText(element.html());
        parent.addView(textView,new GridLayout.LayoutParams(
                GridLayout.spec(parent.getRowCount()-1),
                GridLayout.spec(curCol++)));


    }
    public void render_textView(final Element element, final GridLayout parent){
        Log.d("render","render_textView");

        final TextView textView = new TextView(parent.getContext());
        MqttConnectManager.getInstance().setOnEventMqtt(new MqttConnectManager.Callback() {
            @Override
            public void onDisconnect() {

            }

            @Override
            public void onConnect() {

            }

            @Override
            public void onMessageArrived(String topic, MqttMessage message) {
                String content = new String(message.getPayload());

                if(topic.equals(MqttInfo.getInstance().getTopic(parent.getContext())+"/tx")){
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        Log.d("json",element.html()+": "+jsonObject.getString(element.id()));
                            textView.setText(jsonObject.getString(element.id()));

                    } catch (JSONException e) {

                        textView.setText(element.id()+": "+"N/A");
                        e.printStackTrace();
                    }

                }

            }
        });
            parent.addView(textView,new GridLayout.LayoutParams(
                    GridLayout.spec(parent.getRowCount()-1),
                    GridLayout.spec(curCol++)));


    }

    public void render_range(final Element element, final GridLayout parent){
        Log.d("render","render_range: " + element.toString());
        int max = 100;
        int min = 0;

        min = Integer.valueOf(element.attributes().get("min"));
        max = Integer.valueOf(element.attributes().get("max"));
        final IndicatorSeekBar indicatorSeekBar = IndicatorSeekBar.with(parent.getContext())
                .max(max)
                .min(min)
                .thumbSize(25)
                .tickTextsArray(new String[]{"1","2","3"})
                .indicatorTextSize(30)
                .showThumbText(true)
        .build();
        indicatorSeekBar.setEnabled(true);
        parent.addView(indicatorSeekBar,new GridLayout.LayoutParams(
                GridLayout.spec(parent.getRowCount()-1),
                GridLayout.spec(curCol++)));
        indicatorSeekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                Log.d("seekBar","onSeeking");
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {
                Log.d("seekBar","onStartTrackingTouch");
            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                Log.d("seekBar","onStopTrackingTouch");
                String tmp = String.valueOf(seekBar.getProgress());
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id",element.id());
                    jsonObject.put("value",tmp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MqttConnectManager.sendData(MqttSetting.getInstance().getInfo(parent.getContext()).get("topic").toString()+"/rx",jsonObject.toString());
                indicatorSeekBar.setEnabled(false);
            }
        });
        MqttConnectManager.getInstance().setOnEventMqtt(new MqttConnectManager.Callback() {
            @Override
            public void onDisconnect() {

            }

            @Override
            public void onConnect() {

            }

            @Override
            public void onMessageArrived(String topic, MqttMessage message) {
                String content = new String(message.getPayload());

                if(topic.equals(MqttInfo.getInstance().getTopic(parent.getContext())+"/tx")){
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        int pos = Integer.valueOf(jsonObject.getString(element.id()));
                        indicatorSeekBar.setProgress(pos);
                        indicatorSeekBar.setEnabled(true);
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                }

            }
        });
    }
    public void render_timepicker(final Element element, final GridLayout parent){
        final TextView timeViewTxt = new TextView(parent.getContext());
        timeViewTxt.setText("...");
        timeViewTxt.setPadding(10,10,10,10);
        timeViewTxt.setTextSize(25);
        timeViewTxt.setBackground(parent.getContext().getResources().getDrawable(R.drawable.timepicker_background));
        View mView = parent.getChildAt(parent.getChildCount()-1);
        View hView = parent.getChildAt(parent.getChildCount()-2);
        parent.removeView(mView);
        parent.removeView(hView);

        timeViewTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerDialog mTimePicker = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id",element.id());
                            jsonObject.put("value",String.valueOf(selectedHour*60 + selectedMinute));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MqttConnectManager.sendData(MqttSetting.getInstance().getInfo(parent.getContext()).get("topic").toString()+"/rx",jsonObject.toString());

                    }
                }, 0, 0, true);//mention true for 24 hour's time format,false for 12 hour's time format
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });


        parent.addView(timeViewTxt,new GridLayout.LayoutParams(
                GridLayout.spec(parent.getRowCount()-1),
                GridLayout.spec(curCol-2)));
        curCol++;
       MqttConnectManager.getInstance().setOnEventMqtt(new MqttConnectManager.Callback() {
            @Override
            public void onDisconnect() {

            }

            @Override
            public void onConnect() {

            }

            @Override
            public void onMessageArrived(String topic, MqttMessage message) {
                String content = new String(message.getPayload());

                if(topic.equals(MqttInfo.getInstance().getTopic(parent.getContext())+"/tx")){
                    try {
                        JSONObject jsonObject = new JSONObject(content);
                        int value = Integer.valueOf(jsonObject.getString(element.id()));
                        int h = value/60;
                        int m = value%60;

                        timeViewTxt.setText(""+h+" Giờ "+m+" Phút");

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                }

            }
        });
    }
    public void render_newLine(final GridLayout parent){
        Log.d("render","render_newLine");
        TextView aLine = new TextView(parent.getContext());
        aLine.setText("");
        parent.addView(aLine,new GridLayout.LayoutParams(
                GridLayout.spec(parent.getRowCount(),1),
                GridLayout.spec(0)));
        curCol=0;
    }

    public void render_finish(AppCompatActivity activity){
        Log.d("htl","render_finish:" + activity);
        boolean fisrtTimeFlag = true;
//        if(activity.isDestroyed())
//            return;
        for (MenuPage_Frag elm:menuPage_frags
             ) {

            activity.getSupportFragmentManager().beginTransaction()
                    .add(R.id.mainLayout,elm,elm.getName()).commit();
            if(fisrtTimeFlag){
                activity.getSupportFragmentManager().beginTransaction()
                .show(elm).commit();
                activity.getSupportActionBar().setTitle(elm.getName());
            }
            else
                activity.getSupportFragmentManager().beginTransaction()
                        .hide(elm).commit();
            fisrtTimeFlag=false;
        }

        requestState(activity);


    }

    public void showMenuByName(AppCompatActivity activity, String name){
        for (MenuPage_Frag elm:menuPage_frags
        ) {
            if(elm.getName().equals(name))
                activity.getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter,R.anim.exit)
                .show(elm).commit();
            else
                activity.getSupportFragmentManager().beginTransaction()
                        .hide(elm).commit();
        }
    }
    public void requestUI(Context context){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id","ui");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttConnectManager.sendData(MqttSetting.getInstance().getInfo(context).get("topic").toString()+"/rx",jsonObject.toString());

    }
    public void requestState(Context context){
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id","all");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        MqttConnectManager.sendData(MqttSetting.getInstance().getInfo(context).get("topic").toString()+"/rx",jsonObject.toString());

    }
}

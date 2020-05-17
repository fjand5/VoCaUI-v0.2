package com.example.vocaui.View.Utils;

import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.beardedhen.androidbootstrap.BootstrapButton;
import com.beardedhen.androidbootstrap.BootstrapEditText;
import com.beardedhen.androidbootstrap.BootstrapLabel;
import com.beardedhen.androidbootstrap.BootstrapProgressBar;
import com.beardedhen.androidbootstrap.BootstrapText;
import com.beardedhen.androidbootstrap.api.defaults.DefaultBootstrapBrand;
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
    private boolean gotGUI = false;
    private static List<MenuPage_Frag> menuPage_frags;
    private static final RenderElement ourInstance = new RenderElement();
    int curCol =0;
    public static RenderElement getInstance() {
        return ourInstance;
    }
    public static MenuPage_Frag getFragByHtmlId(String id){
        if (menuPage_frags == null)
            return null;
        for (MenuPage_Frag frag:
                menuPage_frags) {
            if(frag.getHtmlId().equals(id))
                return  frag;
        }
        return null;
    }
    public List<MenuPage_Frag> getMenuPage_frags() {
        return menuPage_frags;
    }

    public void beginRender(){
        curCol =0;
        if(menuPage_frags==null)
            menuPage_frags = new ArrayList<>();
        menuPage_frags.clear();
        MqttConnectManager.getInstance().clearOnEventMqtt();


    }
    private RenderElement() {
        menuPage_frags = new ArrayList<>();
    }
    public GridLayout render_container(Context context, String name,String id){
        GridLayout childElementMenuLayout = new GridLayout(context);
        childElementMenuLayout.setPadding(15,15,15,15);
        MenuPage_Frag menuPage_frag = new MenuPage_Frag();
        render_newLine(childElementMenuLayout);

        menuPage_frag.setRootView(childElementMenuLayout);

        menuPage_frag.setName(name);
        menuPage_frag.setHtmlId(id);
        menuPage_frag.setContainer(childElementMenuLayout);
        menuPage_frags.add(menuPage_frag);
        return childElementMenuLayout;
    }
    public void render_button(final Element element, final GridLayout parent){
        Log.d("render","render_button");
        final BootstrapButton button = new BootstrapButton(parent.getContext());
        final String htmlId = element.id();
        final String name = element.child(0).html();

        button.setText(name);
        button.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id",htmlId);
                    jsonObject.put("value",name);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MqttConnectManager.sendData(MqttSetting.getInstance().getInfo(parent.getContext()).get("topic").toString()+"/rx",jsonObject.toString());
                view.setEnabled(false);
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
                        if(jsonObject.has(htmlId)){
                            button.setEnabled(true);

                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                }

            }
        });
        parent.addView(button,new GridLayout.LayoutParams(
                GridLayout.spec(parent.getRowCount()-1),
                GridLayout.spec(curCol++)));
        render_newLine(parent);



    }

    public void render_textView(final Element element, final GridLayout parent){
        Log.d("render","render_textView");
        final String htmlId = element.id();
        BootstrapLabel labelTxt = new BootstrapLabel(parent.getContext());
        final BootstrapLabel contentTxt = new BootstrapLabel(parent.getContext());

        labelTxt.setText(element.child(0).html());
        labelTxt.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
        contentTxt.setText("Đang kết nối");
        contentTxt.setBootstrapBrand(DefaultBootstrapBrand.INFO);
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
                        contentTxt.setText(jsonObject.getString(htmlId));

                    } catch (JSONException e) {

                        contentTxt.setText("N/A");
                        e.printStackTrace();
                    }

                }

            }
        });
        parent.addView(labelTxt,new GridLayout.LayoutParams(
                GridLayout.spec(parent.getRowCount()-1),
                GridLayout.spec(curCol++)));
            parent.addView(contentTxt,new GridLayout.LayoutParams(
                    GridLayout.spec(parent.getRowCount()-1),
                    GridLayout.spec(curCol++)));
        render_newLine(parent);


    }
    public void render_inputText(final Element element, final GridLayout parent){
        final String htmlId = element.id();
        final BootstrapLabel labelTxt = new BootstrapLabel(parent.getContext());
        final BootstrapEditText inputText = new BootstrapEditText(parent.getContext());
        final BootstrapButton submitButton = new BootstrapButton(parent.getContext());
        submitButton.setText("OK");
        labelTxt.setText(element.child(0).html());
        labelTxt.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
        inputText.setText("Đang kết nối");
        inputText.setBootstrapBrand(DefaultBootstrapBrand.INFO);
        submitButton.setBootstrapBrand(DefaultBootstrapBrand.SUCCESS);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("id",htmlId);
                    jsonObject.put("value",inputText.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MqttConnectManager.sendData(MqttSetting.getInstance().getInfo(parent.getContext()).get("topic").toString()+"/rx",jsonObject.toString());
                labelTxt.setEnabled(false);
                inputText.setEnabled(false);
                submitButton.setEnabled(false);
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
                        if(jsonObject.has(htmlId)){
                            labelTxt.setEnabled(true);
                            inputText.setEnabled(true);
                            submitButton.setEnabled(true);
                            inputText.setText(jsonObject.getString(htmlId));
                        }

                    } catch (JSONException e) {

                        inputText.setText("N/A");
                        e.printStackTrace();
                    }

                }

            }
        });
        parent.addView(labelTxt,new GridLayout.LayoutParams(
                GridLayout.spec(parent.getRowCount()-1),
                GridLayout.spec(curCol++)));
        parent.addView(inputText,new GridLayout.LayoutParams(
                GridLayout.spec(parent.getRowCount()-1),
                GridLayout.spec(curCol++)));
        parent.addView(submitButton,new GridLayout.LayoutParams(
                GridLayout.spec(parent.getRowCount()-1),
                GridLayout.spec(curCol++)));
        render_newLine(parent);


    }

    public void render_range(final Element element, final GridLayout parent){
        Element inputTag = element.child(2); // vị trí của tag Range
        final String label = element.child(0).html();
        final BootstrapLabel labelTxt = new BootstrapLabel(parent.getContext());
        labelTxt.setText(label);
        labelTxt.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
        final String htmlId = element.id();
        int max = 100;
        int min = 0;

        min = Integer.valueOf(inputTag.attributes().get("min"));
        max = Integer.valueOf(inputTag.attributes().get("max"));
        final IndicatorSeekBar indicatorSeekBar = IndicatorSeekBar.with(parent.getContext())
                .max(max)
                .min(min)
                .thumbSize(25)
                .tickTextsArray(new String[]{"1","2","3"})
                .indicatorTextSize(30)
                .showThumbText(true)
        .build();
        indicatorSeekBar.setEnabled(true);
        parent.addView(labelTxt,new GridLayout.LayoutParams(
                GridLayout.spec(parent.getRowCount()-1),
                GridLayout.spec(curCol++)));
        parent.addView(indicatorSeekBar,new GridLayout.LayoutParams(
                GridLayout.spec(parent.getRowCount()-1),
                GridLayout.spec(curCol++)));
        render_newLine(parent);
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
                    jsonObject.put("id",htmlId);
                    jsonObject.put("value",tmp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MqttConnectManager.sendData(MqttSetting.getInstance().getInfo(parent.getContext()).get("topic").toString()+"/rx",jsonObject.toString());
                indicatorSeekBar.setEnabled(false);
                labelTxt.setEnabled(false);
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
                        int pos = Integer.valueOf(jsonObject.getString(htmlId));
                        indicatorSeekBar.setProgress(pos);
                        indicatorSeekBar.setEnabled(true);
                        labelTxt.setEnabled(true);
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                }

            }
        });
    }
    public void render_timepicker(final Element element, final GridLayout parent){
        String label = element.child(0).html();
        final String htmlId = element.id();
        final BootstrapLabel labelTxt = new BootstrapLabel(parent.getContext());
        labelTxt.setBootstrapBrand(DefaultBootstrapBrand.PRIMARY);
        final BootstrapLabel timeViewTxt = new BootstrapLabel(parent.getContext());
        timeViewTxt.setBootstrapBrand(DefaultBootstrapBrand.INFO);
        labelTxt.setText(label);
        timeViewTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeViewTxt.setEnabled(false);
                labelTxt.setEnabled(false);
                TimePickerDialog mTimePicker = new TimePickerDialog(view.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("id",htmlId);
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
        parent.addView(labelTxt,new GridLayout.LayoutParams(
                GridLayout.spec(parent.getRowCount()-1),
                GridLayout.spec(curCol++)));
        parent.addView(timeViewTxt,new GridLayout.LayoutParams(
                GridLayout.spec(parent.getRowCount()-1),
                GridLayout.spec(curCol++)));
        render_newLine(parent);

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
                        if(jsonObject.has(htmlId)){
                            int value = Integer.valueOf(jsonObject.getString(htmlId));
                            int h = value/60;
                            int m = value%60;

                            timeViewTxt.setText(""+h+" Giờ "+m+" Phút");
                            timeViewTxt.setEnabled(true);
                            labelTxt.setEnabled(true);
                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                }

            }
        });
    }
    public void render_newLine(final GridLayout parent){
        TextView aLine = new TextView(parent.getContext());
        aLine.setText("");
        parent.addView(aLine,new GridLayout.LayoutParams(
                GridLayout.spec(parent.getRowCount(),1),
                GridLayout.spec(0)));
        curCol=0;
    }

    public void render_finish(AppCompatActivity activity){
        boolean fisrtTimeFlag = true;
        if(activity.isDestroyed())
            return;
        FragmentManager fm = activity.getSupportFragmentManager();
        for (Fragment frm: fm.getFragments()
             ) {
            fm.beginTransaction().remove(frm).commit();
        }
        for (MenuPage_Frag elm:menuPage_frags
             ) {

            fm.beginTransaction()
                    .add(R.id.mainLayout,elm,elm.getName()).commit();
            if(fisrtTimeFlag){
                fm.beginTransaction()
                .show(elm).commit();
                activity.getSupportActionBar().setTitle(elm.getName());
            }
            else
                fm.beginTransaction()
                        .hide(elm).commit();
            fisrtTimeFlag=false;
        }

        requestState(activity);
        Log.d("htl","render_finish222:" + "render_finish");
        gotGUI=true;

    }

    public void showMenuByName(AppCompatActivity activity, String name){
        for (MenuPage_Frag elm:menuPage_frags
        ) {
            if(elm.getName().equals(name)){
                activity.getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.enter,R.anim.exit)
                        .show(elm).commit();
                        JSONObject jsonObject = new JSONObject();
                        //gửi lệnh  Chạy event khi chọn menu
                try {
                    jsonObject.put("id",elm.getHtmlId());
                    jsonObject.put("value","clkd");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                MqttConnectManager.sendData(MqttSetting.getInstance().getInfo(activity).get("topic").toString()+"/rx",jsonObject.toString());

            }
            else
                activity.getSupportFragmentManager().beginTransaction()
                        .hide(elm).commit();
        }
    }
    public void requestUI(final Context context){
        gotGUI=false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!gotGUI){
                    Log.d("htl","gotGUI:" + "sssss");

                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("id","ui");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MqttConnectManager.sendData(MqttSetting.getInstance().getInfo(context).get("topic").toString()+"/rx",jsonObject.toString());
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

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

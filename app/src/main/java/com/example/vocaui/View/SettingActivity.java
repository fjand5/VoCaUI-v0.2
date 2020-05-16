package com.example.vocaui.View;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vocaui.BackgroudProccess.MainService;
import com.example.vocaui.Presenter.MqttSetting;
import com.example.vocaui.R;

import java.util.Dictionary;

import static android.widget.Toast.LENGTH_LONG;


public class SettingActivity extends Activity implements TextWatcher, View.OnClickListener {
    EditText addrTxt,portTxt,nameTxt,passTxt,topicTxt;
    TextView checkTxt;
    Button saveBtn,exitBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        initView();
        addEvent();
        updateData();
        checkInfo();

    }

    private void updateData() {
        Dictionary<String, Object> tmp= MqttSetting.getInstance().getInfo(this);
        addrTxt.setText(tmp.get("address").toString());
        portTxt.setText(tmp.get("port").toString());
        nameTxt.setText(tmp.get("username").toString());
        passTxt.setText(tmp.get("password").toString());
        topicTxt.setText(tmp.get("topic").toString());
    }


    private void addEvent() {
        addrTxt.addTextChangedListener(this);
        portTxt.addTextChangedListener(this);
        nameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInfo();

            }

            @Override
            public void afterTextChanged(Editable s) {
                topicTxt.setText(s+"/#");

            }
        });
        passTxt.addTextChangedListener(this);
        saveBtn.setOnClickListener(this);
        exitBtn.setOnClickListener(this);

    }

    private void initView() {
        checkTxt = findViewById(R.id.checkTxt);

        addrTxt = findViewById(R.id.addrTxt);
        portTxt = findViewById(R.id.portTxt);
        nameTxt = findViewById(R.id.nameTxt);
        passTxt = findViewById(R.id.passTxt);
        topicTxt = findViewById(R.id.topicTxt);

        saveBtn = findViewById(R.id.saveBtn);
        exitBtn = findViewById(R.id.exitBtn);

    }

    void checkInfo(){
        if(addrTxt.getText().length()>0
        && portTxt.getText().length()>0
        && nameTxt.getText().length()>0
        && passTxt.getText().length()>0
        ){
            saveBtn.setEnabled(true);
            checkTxt.setText(
                    ""
            );
        }
        else{
            saveBtn.setEnabled(false);
            checkTxt.setText("Nhập thông sai !!!");
            checkTxt.setTextColor(Color.RED);
        }
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        checkInfo();
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.saveBtn:
                saveInfo();
                Toast.makeText(this,"Đã lưu, vui lòng mở lại ứng dụng", LENGTH_LONG).show();
                break;
            case R.id.exitBtn:
               finish();
                break;
        }

    }

    private void saveInfo() {
        MqttSetting.getInstance().setInfo(
                this,
                addrTxt.getText().toString(),
        Integer.parseInt(portTxt.getText().toString()),
        nameTxt.getText().toString(),
        passTxt.getText().toString(),
        topicTxt.getText().toString()

                );
    }


}

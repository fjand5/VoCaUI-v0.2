package com.example.vocaui.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridLayout;
import android.widget.LinearLayout;


import com.example.vocaui.BackgroudProccess.MainService;
import com.example.vocaui.Presenter.MqttConnectManager;
import com.example.vocaui.Presenter.MqttSetting;
import com.example.vocaui.R;
import com.example.vocaui.View.Utils.RenderElement;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;



public class MainActivity extends AppCompatActivity {

    MqttConnectManager.Callback callback;
    Menu mMenu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        mMenu =menu;
        mMenu.add("Cài Đặt");
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getTitle().equals("Cài Đặt")){
            Intent i = new Intent(this,SettingActivity.class);
            startActivity(i);
            return true;
        }
        for (MenuPage_Frag elm:
                RenderElement.getInstance().getMenuPage_frags()) {
            if(elm.getName().equals(item.getTitle())){
                RenderElement.getInstance().showMenuByName(this,elm.getName());
                getSupportActionBar().setTitle(elm.getName());
            }

        }
        return true;
    }


//    @Override
//    protected void onResume() {
////        super.onResume();
//
////        mActivity = this;
////        if(!countinueFlag
////        && MainService.isMyServiceRunning(this,MainService.class))
////            MainService.getInstance().stopMe();
//
////        countinueFlag = false;
//
//    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Log.d("htl","onCreate:" + this);
        setContentView(R.layout.activity_main);
        RenderElement.getInstance().requestUI(this);
        addEvent(this);
        MainService.beginService(this);

    }
    private void addEvent(final Context context) {
        callback = new MqttConnectManager.Callback() {
            @Override
            public void onDisconnect()
            {
            }

            @Override
            public void onConnect() {
            }

            @Override
            public void onMessageArrived(String topic, MqttMessage message) {

                String content = new String(message.getPayload());

                if(topic.equals(MqttSetting.getInstance().getInfo(context).get("topic").toString() + "/dashboard"))
                    renderView(content);
            }

        };
        MqttConnectManager.getInstance().setOnEventNoClear(callback);
    }

    void renderView(String htmlDoc){

        LinearLayout mainLayout = findViewById(R.id.mainLayout);
        RenderElement.getInstance().clearAll();
        if(mainLayout.getChildCount() > 0)
            mainLayout.removeAllViews();

        Document doc = Jsoup.parse(htmlDoc, "UTF-8");
        String title =doc.getElementsByTag("title").first().html();
        setTitle(title);
//        for (Element menu : menuList){
//            if(     menu.id().equals("menu")
//                    || menu.id().equals("mask")
//                    || menu.id().equals("menu-button")
//                    || menu.id().equals("setting")
//                    || menu.id().equals("wifi")
//            )
//                continue;
//            Elements childElementMenu = menu.children();
//            GridLayout container = RenderElement.getInstance().render_container(this,menu.id());
//
//
//            for (final Element child:
//            childElementMenu) {
//                if(child.attributes().get("class").equals("timePicker")){
//                    RenderElement.getInstance().render_timepicker(child,container);
//                }
//                else if(child.tagName().equals("button")){
//                   RenderElement.getInstance().render_button(child,container);
//                }
//                else if(child.tagName().equals("input")
//                && child.hasAttr("readonly")){
//                                RenderElement.getInstance().render_textView(child,container);
//                }
//                else if(child.tagName().equals("input")
//                        && child.attributes().get("type").equals("range")){
//                    RenderElement.getInstance().render_range(child,container);
//                }
//                else if(child.tagName().equals("br")){
//                    RenderElement.getInstance().render_newLine(container);
//                }else if(child.tagName().equals("label")){
//                    RenderElement.getInstance().render_label(child,container);
//                }
//
//
//            }
//
//
//        }



        mMenu.clear();
        mMenu.add("Cài Đặt");
        for (MenuPage_Frag elm:
                RenderElement.getInstance().getMenuPage_frags()) {
            mMenu.add(elm.getName());
        }
        RenderElement.getInstance().render_finish(this);
    }

}


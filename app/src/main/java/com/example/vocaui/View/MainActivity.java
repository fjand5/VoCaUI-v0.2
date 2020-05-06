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

        // Tạo các fragment dựa trên danh sách menu
        Elements elements = doc.getElementsByClass("lmnu").select("ul");
        mMenu.clear();
        mMenu.add("Cài Đặt");
        for (Element e: elements.select("li")
             ) {
            String mnuName = e.select("a").html();
            String tmp =e.attr("onclick");
            String id = tmp
                    .substring(tmp.indexOf('(')+1,tmp.indexOf(')'))
                    .split(",")[0]
                    .replace("\'","");
            if(id.equals("wifi")
                    ||id.equals("setting"))
                continue;
            RenderElement.getInstance().render_container(this,mnuName,id);
            mMenu.add(mnuName);

        }
        // Đưa các phần tử vào fragment tương ứng
        for (Element elm:
             doc.select("*")) {
            String className = elm.attr("class");
            if(!elm.hasAttr("class"))
                continue;

            String htmlid = elm.parent().id();
            if(htmlid.equals("wifi")
                    ||htmlid.equals("setting"))
                continue;
            if(className.equals("rng")){

                RenderElement.getInstance().render_range(elm,RenderElement.getFragByHtmlId(htmlid).getContainer());
            }
            else if(className.equals("txtvie")){
                RenderElement.getInstance().render_textView(elm,RenderElement.getFragByHtmlId(htmlid).getContainer());

            }else if(className.equals("inptxt")){
                RenderElement.getInstance().render_inputText(elm,RenderElement.getFragByHtmlId(htmlid).getContainer());

            }else if(className.equals("sglbtn")){
                RenderElement.getInstance().render_button(elm,RenderElement.getFragByHtmlId(htmlid).getContainer());

            }else if(className.equals("tmpk")){
                RenderElement.getInstance().render_timepicker(elm,RenderElement.getFragByHtmlId(htmlid).getContainer());

            }


        }


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




        RenderElement.getInstance().render_finish(this);
    }

}


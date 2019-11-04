package com.example.ootd;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.ootd.dto.FashionSetDTO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class PopupActivity extends Activity implements Runnable{

    Button save_btn;
    EditText edit_text;

    Intent intent;
    String set_name;
    FashionSetDTO set;
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popup);

        save_btn = (Button)findViewById(R.id.save_btn);
        edit_text = (EditText) findViewById(R.id.edit_text);

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 옷장 class 받기
                intent = getIntent();
                try {
                    set = (FashionSetDTO) intent.getSerializableExtra("set");
                    Log.d("PopupActivity","이전 인텐트에서 보낸 set 있음");
                } catch(Exception e){
                    Log.d("PopupActivity","가져온게없음");
                    set = null;
                }

                set_name = String.valueOf(edit_text.getText()); // 세트이름 적은것 가져와서
                set.setSet_name(set_name);

                Thread th = new Thread(PopupActivity.this);
                th.start();

                //액티비티(팝업) 닫기
                Intent intent;
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // closetActivity 원래열었던곳으로 돌아감

                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void run() {

        StringBuffer sb = new StringBuffer();

        String outer, upper, lower, cap, bag, shoes, accessory1, accessory2, accessory3;
        if(set.getOuter()==null) outer = "null"; else outer = set.getOuter();
        if(set.getUpper()==null) upper = "null"; else upper = set.getUpper();
        if(set.getLower()==null) lower = "null"; else lower = set.getLower();
        if(set.getCap()==null) cap = "null"; else cap = set.getCap();
        if(set.getBag()==null) bag = "null"; else bag = set.getBag();
        if(set.getShoes()==null) shoes = "null"; else shoes = set.getShoes();
        if(set.getAccessory1()==null) accessory1 = "null"; else accessory1 = set.getAccessory1();
        if(set.getAccessory2()==null) accessory2 = "null"; else accessory2 = set.getAccessory2();
        if(set.getAccessory3()==null) accessory3 = "null"; else accessory3 = set.getAccessory3();
        try {
            MyUrl myUrl = new MyUrl();
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            String ip = pref.getString("ip_addr", "");   // http://192.168.55.193:8080
            String id = pref.getString("id", "");   // test

            String[][] values = {{"id", id}, {"set_name", set_name}, {"outer", outer}, {"upper", upper}
                                    , {"lower", lower}, {"cap", cap}, {"bag", bag}, {"shoes", shoes}
                                    ,{"accessory1", accessory1}, {"accessory2", accessory2}, {"accessory3",accessory3}};

            JSONObject jsonObj = myUrl.getJsonResult(ip,"/saveSet",values);

            JSONArray jArray = (JSONArray) jsonObj.get("result");

            // 0번째 JSONObject를 받아옴
            JSONObject row = jArray.getJSONObject(0);
            Log.d("받아온값1 : ", row.getString("result"));

            if(row.getString("result").equals("ok")) {
                Log.d("PopupActivity", "패션세트 잘 추가함");
                result = "ok";
            }
            else {
                Log.d("PopupActivity", "패션세트 추가못함");
                result = "no";
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("myLog_error", "에러발생했습니다...");
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }
/*
    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }

 */
}

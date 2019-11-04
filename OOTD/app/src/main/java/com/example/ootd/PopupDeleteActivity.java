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
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class PopupDeleteActivity extends Activity implements Runnable{

    Button delete_btn;
    EditText edit_text;

    Intent intent;
    FashionSetDTO set;
    String category;
    String folder;
    String dress_num;
    String filepath;
    String result;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popupdelete);

        // 옷장 class 받기
        intent = getIntent();
        try {
            set = (FashionSetDTO) intent.getSerializableExtra("set");
            category = intent.getStringExtra("category");
            folder = intent.getStringExtra("folder");
            dress_num = intent.getStringExtra("dress_num");
            filepath = intent.getStringExtra("filepath");
            Log.d("PopupActivity","이전 인텐트에서 보낸 set 있음");
        } catch(Exception e){
            Log.d("PopupActivity","가져온게없음");
            set = null;
        }

        delete_btn = (Button)findViewById(R.id.delete_btn);

        delete_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Thread th = new Thread(PopupDeleteActivity.this);
                th.start();

                //액티비티(팝업) 닫기
                Intent intent;
                intent = new Intent(getApplicationContext(), ItemsetActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("set", (Serializable) set);
                intent.putExtras(bundle);

                intent.putExtra("category", category);
                intent.putExtra("folder", folder);

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // closetActivity 원래열었던곳으로 돌아감
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    public void run() {
        // id와 dress_num에 따라 db에서 삭제
        StringBuffer sb = new StringBuffer();

        try {
            MyUrl myUrl = new MyUrl();
            Log.d("서버보내기1_PopupDeleteActivity","서버보내기");

            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            String ip = pref.getString("ip_addr", "");   // http://192.168.55.193:8080
            String id = pref.getString("id", "");   // test
            String[][] values = {{"id", id}, {"dress_number", dress_num}};

            JSONObject jsonObj = myUrl.getJsonResult(ip,"/deleteClothDB", values);
            JSONArray jArray = (JSONArray) jsonObj.get("delete_result");

            // 0번째 JSONObject를 받아옴
            JSONObject row = jArray.getJSONObject(0);
            Log.d("받아온값1 : ", row.getString("result"));

            Log.d("서버보내기 끝","서버보내기 끝");
            if(row.getString("result").equals("ok")) {
                result = "ok";

                File original = new File(filepath);
                original.delete();
                Log.d("PopupDeleteActivity", "옷 삭제됨.");
            }
            else {
                Log.d("PopupDeleteActivity", "옷 삭제 안됨.");
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

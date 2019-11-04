package com.example.ootd;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class JoinActivity extends AppCompatActivity implements Runnable {
    Button join_btn;
    EditText id_edit;                // id 에디트
    EditText pw_edit;                // pw 에디트
    EditText nick_edit;                // nickname 에디트

    String result; // join 결과 ("ok" or "no")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        join_btn = (Button)findViewById(R.id.join_btn);    // 회원가입 버튼을 찾고

        id_edit = (EditText)findViewById(R.id.id_edit);    // id 에디트를 찾음.
        pw_edit = (EditText)findViewById(R.id.pw_edit);    // pw 에디트를 찾음
        nick_edit = (EditText)findViewById(R.id.nick_edit);    // nickname 에디트

        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("join","회원가입 하는중");

                Thread th = new Thread(JoinActivity.this);
                th.start();

            }
        });
    }

    @Override
    public void run() {
        // 내가 적은 id, pw
        String id = id_edit.getText().toString();
        String pw = pw_edit.getText().toString();
        String nickname = nick_edit.getText().toString();

        try {
            MyUrl myUrl = new MyUrl();
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            String ip = pref.getString("ip_addr", "");   // http://192.168.55.193:8080
            String[][] values = {{"id", id}, {"password", pw}, {"nickname", nickname}};

            // json data 받기
            JSONObject jsonObj;
            jsonObj = myUrl.getJsonResult(ip,"/insertToMembers",values);
            JSONArray jArray = (JSONArray) jsonObj.get("join_result");

            // 몇개가져왔는지
            JSONObject row = jArray.getJSONObject(0);

            //int label_num[] = {1, 2, 3, 5, 4, 5};
            result = row.getString("result");

            if(result.equals("ok")) {
                Log.d("회원가입 성공", "회원가입 성공");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(JoinActivity.this, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent intent = new Intent(JoinActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
            else {
                Log.d("회원가입 실패", "회원가입 실패");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(JoinActivity.this, "회원가입에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MainActivity", "에러발생했습니다...");
        }
    }
}

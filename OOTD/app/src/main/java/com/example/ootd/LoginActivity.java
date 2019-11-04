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
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity implements Runnable {

    Button join_btn;
    Button login_btn;
    EditText id_edit;                // id 에디트
    EditText pw_edit;                // pw 에디트

    String result; // login 결과 ("ok" or "no")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("ip_addr", "http://192.168.55.193:8080");
        editor.commit();

        join_btn = (Button) findViewById(R.id.join_btn);    // 회원가입 버튼을 찾고
        login_btn = (Button) findViewById(R.id.login_btn);  // 로그인 버튼을 찾고

        id_edit = (EditText) findViewById(R.id.id_edit);    // id 에디트를 찾음.
        pw_edit = (EditText) findViewById(R.id.pw_edit);    // pw 에디트를 찾음

        // 로그인 버튼
        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("login", "로그인 하는중");

                Thread th = new Thread(LoginActivity.this);
                th.start();


            }
        });

        // 회원가입 버튼
        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("join_btn", "회원가입");
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void run() {
        // 내가 적은 id, pw
        String id = id_edit.getText().toString();
        String pw = pw_edit.getText().toString();

        try {
            MyUrl myUrl = new MyUrl();

            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);

            String ip = pref.getString("ip_addr", "");   // http://192.168.55.193:8080
            Log.d("LoginActivity", ip);

            // 서버에 보낼 파라미터 설정 (writer.write("id=" + id); writer.write("&password="+pw);)
            String[][] values = {{"id", id}, {"password", pw}};

            // json data 받기
            JSONObject jsonObj;
            jsonObj = myUrl.getJsonResult(ip, "/loginCheck", values);
            JSONArray jArray = (JSONArray) jsonObj.get("login_result");

            // 몇개가져왔는지
            JSONObject row = jArray.getJSONObject(0);

            //int label_num[] = {1, 2, 3, 5, 4, 5};
            result = row.getString("result");

            if (result.equals("ok")) {
                Log.d("로그인 성공", "로그인 성공");
                id = id_edit.getText().toString();

                SharedPreferences.Editor editor = pref.edit();
                editor.putString("id", id);
                editor.commit();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "로그인에 성공했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });

                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                //finish();
            } else {
                Log.d("로그인 실패", "로그인 실패");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("LoginActivity", "에러발생했습니다...");
        }
    }
}

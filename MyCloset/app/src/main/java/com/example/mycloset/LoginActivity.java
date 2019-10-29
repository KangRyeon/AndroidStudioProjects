package com.example.mycloset;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.mycloset.JoinActivity;
import com.example.mycloset.MainActivity;
import com.example.mycloset.R;

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

public class LoginActivity extends AppCompatActivity implements Runnable {

    Button join_btn;
    Button login_btn;
    EditText id_edit;                // id 에디트
    EditText pw_edit;                // pw 에디트

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        join_btn = (Button)findViewById(R.id.join_btn);    // 회원가입 버튼을 찾고
        login_btn = (Button)findViewById(R.id.login_btn);  // 로그인 버튼을 찾고

        id_edit = (EditText)findViewById(R.id.id_edit);    // id 에디트를 찾음.
        pw_edit = (EditText)findViewById(R.id.pw_edit);    // pw 에디트를 찾음

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("login","로그인 하는중");

                Thread th = new Thread(LoginActivity.this);
                th.start();
            }
        });

        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w("join_btn","회원가입");
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
                //finish();
            }
        });
    }

    @Override
    public void run() {
        // 내가 적은 id, pw
        String id = id_edit.getText().toString();
        String pw = pw_edit.getText().toString();

        try {
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("ip_addr","http://192.168.55.193:8080");
            editor.commit();

            String ip = pref.getString("ip_addr", "");   // http://192.168.55.193:8080
            Log.d("PopupChartActivity", ip);

            URL connectUrl = new URL(ip+"/loginCheck");       // 스프링프로젝트의 home.jsp 주소
            DataOutputStream dos;
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();       // URL 연결한 객체 생성
            Log.d("PopupChartActivity","URL객체 생성");
            if (conn != null) {
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                // 원래 DataOutputStream 사용했으나 utf-8전송위해 아래껄로 바꿈
                OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                PrintWriter writer = new PrintWriter(outStream);

                Log.d("서버보내기 시작","서버보내기 시작");

                //writer.write("id=asdf");
                Log.d("PopupChartActivity", id);

                writer.write("id="+id);
                writer.write("&password="+pw);
                writer.flush();
                writer.close();

                Log.d("서버보내기 끝","서버보내기 끝");

                // json data 받기
                JSONObject jsonObj;
                jsonObj = getJSONDataFromServer(conn);
                JSONArray jArray = (JSONArray) jsonObj.get("login_result");

                // 몇개가져왔는지
                JSONObject row = jArray.getJSONObject(0);

                //int label_num[] = {1, 2, 3, 5, 4, 5};
                String result = row.getString("result");
                if(result.equals("ok")) {
                    Log.d("로그인 성공", "로그인 성공");
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
                }
                else {
                    Log.d("로그인 실패", "로그인 실패");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "로그인에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MainActivity", "에러발생했습니다...");
        }
    }
    // 서버에서 값 받아 JSONObjtect로 변환
    public JSONObject getJSONDataFromServer(HttpURLConnection conn) throws IOException, JSONException
    {
        StringBuffer sb = new StringBuffer();

        Log.d("파일", "파일 다 전송함 딥러닝결과 받아오기 시작");
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            Log.d("연결", "연결이 제대로 됨");
            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                sb.append(line + "\n");
            }
            Log.d("받은것", "오잉"+sb.toString());
            br.close();
        }
        conn.disconnect();

        // 받아온 source를 JSONObject로 변환한다.
        JSONObject jsonObj = new JSONObject(sb.toString());

        return jsonObj;
    }
}

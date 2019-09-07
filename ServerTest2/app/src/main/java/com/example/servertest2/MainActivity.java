package com.example.servertest2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.example.servertest2.dto.MemberDTO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Runnable {

    private Button load_btn;
    ListView list;
    List<MemberDTO> items;

    // 핸들러
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            String[] str =new String[items.size()];
            for(int i=0; i<str.length; i++){
                MemberDTO dto =items.get(i);
                str[i]=(i+1)+". "+ dto.getName()+" [ " + dto.getEmail()+" ] " ;
            }

            // 안드로이드가 미리 만들어놓은 simple_list_item_1 레이아웃으로 어댑터 생성(텍스트뷰 하나로 구성)
            ArrayAdapter<String> adapter=new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, str);
            list.setAdapter(adapter);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 내가 만든 버튼, list 받아옴
        load_btn = (Button) findViewById(R.id.load_btn);
        list = (ListView) findViewById (R.id.list);

        items = new ArrayList<>();

        // 백그라운드 스레드
        load_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread th = new Thread(MainActivity.this);
                th.start();
            }
        });
    }

    @Override
    public void run() {

        try {
            StringBuffer sb = new StringBuffer();
            URL url = new URL("http://192.168.55.193:8080/json.do");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // 저 경로의 source를 받아온다.
            if (conn != null) {
                conn.setConnectTimeout(5000);
                conn.setUseCaches(false);

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                    while (true) {
                        String line = br.readLine();
                        if (line == null)
                            break;
                        sb.append(line + "\n");
                    }
                    Log.d("myLog", sb.toString());
                    br.close();
                }
                conn.disconnect();
            }

            // 받아온 source를 JSONObject로 변환한다.
            JSONObject jsonObj = new JSONObject(sb.toString());
            JSONArray jArray = (JSONArray) jsonObj.get("members");

            // 0번째 JSONObject를 받아옴
            JSONObject row = jArray.getJSONObject(0);
            MemberDTO dto = new MemberDTO();
            dto.setName(row.getString("name"));
            dto.setEmail(row.getString("email"));
            items.add(dto);

            Log.d("받아온값1 : ", row.getString("name"));
            Log.d("받아온값2 : ", row.getString("email"));

            // 1번째 JSONObject를 받아옴
            JSONObject row2 = jArray.getJSONObject(1);
            MemberDTO dto2 = new MemberDTO();
            dto2.setName(row2.getString("name"));
            dto2.setEmail(row2.getString("email"));
            items.add(dto2);

            Log.d("받아온값3 : ", row2.getString("name"));
            Log.d("받아온값4 : ", row2.getString("email"));

        }catch (Exception e){
            e.printStackTrace();
            Log.e("eroor", e.getMessage());
        }
        //핸들러에게 메시지 요청
        handler.sendEmptyMessage(0);
    }
}

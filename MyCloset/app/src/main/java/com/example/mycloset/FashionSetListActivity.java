package com.example.mycloset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycloset.dto.FashionSetDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class FashionSetListActivity extends AppCompatActivity implements Runnable{

    LinearLayout list;

    JSONObject jsonObj;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fashionsetlist);

        String[] outer = {"cardigan", "jacket", "padding", "coat", "jumper", "hood zipup"};
        String[] upper = {"hood_T", "long_T", "pola", "shirt", "short_T", "sleeveless", "vest"};
        String[] lower = {"long_pants", "short_pants", "Leggings", "mini_skirt", "long_skirt"};
        String[] onepeace = {"long_arm_mini_onepeace", "long_arm_long_onepeace", "short_arm_mini_onepeace", "short_arm_long_onepeace"};

        list = (LinearLayout)findViewById(R.id.list);

        // 서버에서 db결과 받아옴
        Thread th = new Thread(FashionSetListActivity.this);
        th.start();

        //String[] sets = {"set1", "캐주얼1"};
    }

    @Override
    public void run() {
        try {
            URL connectUrl = new URL("http://192.168.55.193:8080/loadFashionSetList");       // 스프링프로젝트의 home.jsp 주소
            DataOutputStream dos;
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();       // URL 연결한 객체 생성
            Log.d("FashionSetListActivity","URL객체 생성");
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
                writer.write("id=test");
                writer.flush();
                writer.close();

                Log.d("서버보내기 끝","서버보내기 끝");

                // json data 받기
                jsonObj = getJSONDataFromServer(conn);
                JSONArray jArray = (JSONArray) jsonObj.get("fashionset_result");
                Log.d("row 개수",""+jArray.length());

                // fashion set가 몇개인지
                JSONObject row = jArray.getJSONObject(0);
                String result = row.getString("result");


                // result 값=내 패션 세트 개수 : 0 or * -> 0이 아니면 jsondata를 fashionset 객체로 만들어
                // 다음 뷰로 보냄.
                if(result != "0") {
                    for(int i=0; i<Integer.parseInt(result); i++) {
                        JSONObject row_set = jArray.getJSONObject(i);
                        String set_name = row_set.getString("set_name");

                        Log.d("세트이름", set_name);
                        Log.d("받은것 result",row_set.getString("result"));
                        Log.d("받은것 id",row_set.getString("id"));
                        Log.d("받은것 set_name",row_set.getString("set_name"));
                        Log.d("받은것 outer",row_set.getString("outer"));
                        Log.d("받은것 upper",row_set.getString("upper"));
                        Log.d("받은것 lower",row_set.getString("lower"));
                        Log.d("받은것 cap",row_set.getString("cap"));
                        Log.d("받은것 bag",row_set.getString("bag"));
                        Log.d("받은것 shoes",row_set.getString("shoes"));
                        Log.d("받은것 accessory1",row_set.getString("accessory1"));
                        Log.d("받은것 accessory2",row_set.getString("accessory2"));
                        Log.d("받은것 accessory3",row_set.getString("accessory3"));


                        final FashionSetDTO set;

                        set = new FashionSetDTO(row_set.getString("id"),row_set.getString("set_name")
                                                ,row_set.getString("outer"),row_set.getString("upper")
                                                ,row_set.getString("lower"),row_set.getString("cap")
                                                ,row_set.getString("bag"),row_set.getString("shoes")
                                                ,row_set.getString("accessory1"),row_set.getString("accessory2"),row_set.getString("accessory3"));

                        btn = new Button(this);
                        btn.setId(i);
                        btn.setText(row_set.getString("set_name"));

                        btn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(FashionSetListActivity.this, FashionSetActivity.class);
                                // 다음 뷰에 set 클래스 그대로 넘김
                                Bundle bundle = new Bundle();
                                bundle.putSerializable("set", (Serializable) set);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
                            }
                        });
                        //list.addView(btn);
                        Thread.sleep(50);
                        handler.sendEmptyMessage(0);
                        Thread.sleep(50);
                    }
                }
                else {    //result 값이 0이면
                    Log.d("저장된 세트가 없음", "저장된 세트가 없음");
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("FashionSetListActivity", "에러발생했습니다...");
        }


    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            list.addView(btn);
        }
    };


    // 서버에서 값 받아 JSONObjtect로 변환
    public JSONObject getJSONDataFromServer(HttpURLConnection conn) throws IOException, JSONException {
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

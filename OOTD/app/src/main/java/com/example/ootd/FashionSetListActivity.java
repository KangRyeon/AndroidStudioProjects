package com.example.ootd;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.ootd.dto.FashionSetDTO;

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

    GridLayout list;

    JSONObject jsonObj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fashionsetlist);

        String[] outer = {"cardigan", "jacket", "padding", "coat", "jumper", "hood_zipup"};
        String[] upper = {"hood_T", "long_T", "pola", "shirt", "short_T", "sleeveless", "vest"};
        String[] lower = {"long_pants", "short_pants", "mini_skirt", "long_skirt"};
        String[] etc = {"accessory", "bag", "shoes", "cap"};

        list = (GridLayout)findViewById(R.id.list);

        // 서버에서 db결과 받아옴
        Thread th = new Thread(FashionSetListActivity.this);
        th.start();
    }

    @Override
    public void run() {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.d("list 지움", "list 지움");
                    list.removeAllViewsInLayout();
                }
            });

            MyUrl myUrl = new MyUrl();
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            String ip = pref.getString("ip_addr", "");   // http://192.168.55.193:8080
            String id = pref.getString("id", "");   // test
            String[][] values = {{"id", id}};

            // json data 받기
            jsonObj = myUrl.getJsonResult(ip,"/loadFashionSetList",values);
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
/*
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
*/

                    final FashionSetDTO set;

                    set = new FashionSetDTO(row_set.getString("id"),row_set.getString("set_name")
                            ,row_set.getString("outer"),row_set.getString("upper")
                            ,row_set.getString("lower"),row_set.getString("cap")
                            ,row_set.getString("bag"),row_set.getString("shoes")
                            ,row_set.getString("accessory1"),row_set.getString("accessory2"),row_set.getString("accessory3"));

                    final Button btn = new Button(this);
                    btn.setId(i);
                    btn.setText(row_set.getString("set_name"));
                    GridLayout.LayoutParams p = new GridLayout.LayoutParams();
                    p.width=350;
                    p.height=350;
                    btn.setLayoutParams(p);

                    btn.setBackground(ContextCompat.getDrawable(this, R.drawable.myset));

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(FashionSetListActivity.this, FashionSetActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putSerializable("set", (Serializable) set);
                            intent.putExtras(bundle);

                            startActivity(intent);
                        }
                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            list.addView(btn);
                        }
                    });
                }
            }
            else {    //result 값이 0이면
                Log.d("저장된 세트가 없음", "저장된 세트가 없음");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("FashionSetListActivity", "에러발생했습니다...");
        }
    }
}

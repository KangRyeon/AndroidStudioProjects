package com.example.mycloset;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mycloset.dto.FashionSetDTO;

import org.w3c.dom.Text;

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

    String set_name;
    FashionSetDTO set;
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
                Intent intent = getIntent();
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
                finish();
            }
        });

    }

    @Override
    public void run() {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

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
            Log.d("서버보내기1","서버보내기");
            URL connectUrl = new URL("http://192.168.55.193:8080/saveSet");       // 스프링프로젝트의 home.jsp 주소
            DataOutputStream dos;
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();       // URL 연결한 객체 생성

            if (conn != null) {
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                // 원래 DataOutputStream 사용했으나 utf-8전송위해 아래껄로 바꿈
                OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                PrintWriter writer = new PrintWriter(outStream);

                writer.write("id=test");
                writer.write("&set_name="+set_name);
                writer.write("&outer="+outer);
                writer.write("&upper="+upper);
                writer.write("&lower="+lower);
                writer.write("&cap="+cap);
                writer.write("&bag="+bag);
                writer.write("&shoes="+shoes);
                writer.write("&accessory1="+accessory1);
                writer.write("&accessory2="+accessory2);
                writer.write("&accessory3="+accessory3);
                writer.flush();
                /*
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes("id=test");
                String decoded_result = new String("세트1".getBytes("utf-8"), "utf-8");
                Log.d("보내는값",decoded_result);
                dos.writeBytes("&set_name="+decoded_result);
                dos.writeBytes("&outer=null&upper=null&lower=null");
                Log.d("보내는값","id=test&set_name=세트1&outer=null&upper=null&lower=null");


                dos.flush(); // finish upload...
                dos.close();
*/
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

                Log.d("서버보내기 끝","서버보내기 끝");


            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("myLog_error", "에러발생했습니다...");
        }
    }
/*
    //확인 버튼 클릭
    public void mOnClose(View v){
        //데이터 전달하기
        Intent intent = new Intent();
        intent.putExtra("result", "Close Popup");
        setResult(RESULT_OK, intent);

        //액티비티(팝업) 닫기
        finish();
        Toast.makeText(this,"세트를 저장했습니다.", Toast.LENGTH_LONG).show();
    }
*/
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        //바깥레이어 클릭시 안닫히게
        if(event.getAction()==MotionEvent.ACTION_OUTSIDE){
            return false;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        //안드로이드 백버튼 막기
        return;
    }
}

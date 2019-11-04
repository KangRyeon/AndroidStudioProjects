package com.example.ootd;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ootd.dto.ClothesDTO;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class SaveResultActivity extends AppCompatActivity implements Runnable {
    // 캐시폴더의 test.jpg이미지를 가져와 보여주고, 저장하기 버튼을 누르면 서버로 이미지를 보내고 딥러닝 결과 가져옴

    ClothesDTO clothes;
    ImageView img_view;
    EditText category1_edit;
    EditText category2_edit;
    EditText color_edit;
    EditText pattern_edit;
    EditText length_edit;
    Button save_btn;

    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saveresult);

        img_view = (ImageView) findViewById(R.id.img_view);
        category1_edit = (EditText)findViewById(R.id.category1_edit);
        category2_edit = (EditText)findViewById(R.id.category2_edit);
        color_edit = (EditText)findViewById(R.id.color_edit);
        pattern_edit = (EditText)findViewById(R.id.pattern_edit);
        length_edit = (EditText)findViewById(R.id.length_edit);
        save_btn = (Button)findViewById(R.id.save_btn);

        // 이전 뷰에 있던 세트 정보 가져옴
        Intent intent = getIntent();
        try {
            clothes = (ClothesDTO) intent.getSerializableExtra("clothes");
        } catch(Exception e){
            Log.d("오류", "못가져옴");
            Log.d("오류",e.toString());
        }

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 저장하기누르면 현재 써져있는 값을 서버로 보냄
                Thread th = new Thread(SaveResultActivity.this);
                th.start();
                // 서버에서는 그 내용대로 db 업데이트

                // 패딩입힌 이미지를 서버에서 가져옴

                // 그 이미지를 bitmap으로 받아서 저장

                Intent intent = new Intent(SaveResultActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Log.d("dress_num",clothes.getDress_number()+", "+clothes.getColor()+", "+clothes.getCategory());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        String files = getApplicationContext().getCacheDir()+"/"+"test.jpg"; // files/upper/shirt
        bitmap = BitmapFactory.decodeFile(files, options);

        text_handler.sendEmptyMessage(0);
    }

    // handler = 백그라운드 thread에서 전달된 메시지 처리(UI변경 등을 여기서 해줌.)
    Handler text_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            img_view.setImageBitmap(bitmap);
            category1_edit.setText(clothes.getCategory());                      //백그라운드로 갤러리에서 골라온 사진을 이미지버튼에 뿌림. //이미지뷰도 가능
            category2_edit.setText(clothes.getLow_category());
            color_edit.setText(clothes.getColor());
            pattern_edit.setText(clothes.getPattern());
            length_edit.setText(clothes.getLength());
        }
    };


    @Override
        public void run() {
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";

            StringBuffer sb = new StringBuffer();

            // 적혀져있는것 가져옴
            String id, dress_number, category, low_category, color, pattern, lengthC;
            id = clothes.getId();
            dress_number = clothes.getDress_number();
            category = category1_edit.getText().toString();
            low_category = category2_edit.getText().toString();
            color = color_edit.getText().toString();
            pattern = pattern_edit.getText().toString();
            lengthC = length_edit.getText().toString();

        try {
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            String ip = pref.getString("ip_addr", "");   // http://192.168.55.193:8080
            Log.d("PopupActivity", ip);

            Log.d("서버보내기1","서버보내기");
            URL connectUrl = new URL(ip+"/updateDBdownloadImage");       // 스프링프로젝트의 home.jsp 주소

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

                writer.write("id="+id);
                writer.write("&dress_number="+dress_number);
                writer.write("&category="+category);
                writer.write("&low_category="+low_category);
                writer.write("&color="+color);
                writer.write("&pattern="+pattern);
                writer.write("&length="+lengthC);
                writer.flush();

                conn.connect();
                InputStream is = conn.getInputStream();
                bitmap = BitmapFactory.decodeStream(is);

                Log.d("비트맵으로 받아옴","비트맵으로 받아옴");
                Log.d("가로세로", bitmap.getWidth()+", "+bitmap.getHeight());
                is.close();
                conn.disconnect();


                text_handler.sendEmptyMessage(0);

                //////////////////////////////////////////////////////////////////////////여기바꺼야댐 이미지 이대로저장해야딤
                // 받아온 결과에 따라 이미지 저장하기 (files 밑에 hood_T 같은 폴더 생성, hood_T_201909231148.jpg로 저장)

                // 캐시에 저장되어있던것 삭제
                File testFile = new File(getApplicationContext().getCacheDir().toString()+"/test.jpg");     // cache/test.jpg
                testFile.delete();

                // bitmap에 있는것을 category/low_category/low_category_받아온 number 로 저장
                String folder_name = getApplicationContext().getFilesDir().toString()+"/"+category+"/"+low_category;    // "/data/data/com.example.cameraandgallery/files/"+row.getString("result"); // /files/upper/hood_T/ 폴더생성
                String saveFileName = folder_name + "/" + dress_number + ".jpg";     // /files/category/low_category/hood_T_받아온이름.jpg(/files/upper/hood_T/201909231148.jpg)

                // data/data/패키지이름 밑에 "files" 라는 폴더 먼저 생성하고 "hood_T.jpg" 생성
                // "files"라는 폴더 생성
                File newFolder = new File(folder_name);
                try {
                    newFolder.mkdirs();
                    Log.d("폴더생성", "폴더생성 성공");
                } catch (Exception e) {
                    Log.d("폴더생성", "폴더생성이 이미 되어있거나 실패");
                }

                // "hood_T.jpg"라는 파일을 생성
                File file = new File(saveFileName);
                //File file = File.createTempFile(row.getString("result")+"_",".jpg",new File(folder_name));  // 이름 랜덤으로(hood_T_1234325345.jpg)
                try {
                    //Log.d("파일다시저장 : ", saveFileName);
                    Log.d("파일다시저장 : ", file.getName());
                    FileOutputStream out = new FileOutputStream(file.getPath());
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Log.d("SaveResultActivity","서버에서 가져온 패딩입힌 이미지를 저장했어요");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("myLog_error", "에러발생했습니다...");
        }
    }
}

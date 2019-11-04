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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ootd.dto.FashionSetDTO;
import com.example.ootd.dto.ClothesDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class ModifyItemActivity extends AppCompatActivity implements Runnable {
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

    String filepath;
    String id;
    String dress_number;

    String click="load";

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
            filepath = intent.getStringExtra("filepath");
            id = intent.getStringExtra("id");
            dress_number = intent.getStringExtra("dress_num");
            Log.d("이전뷰에서 받아온것", filepath+", "+id+", "+dress_number);
        } catch(Exception e){
            Log.d("오류", "못가져옴");
            Log.d("오류",e.toString());
        }

        // ShowItemsActivity에 넘길값들
        FashionSetDTO set = null;
        String category = null;
        String folder=null;
        try {
            set = (FashionSetDTO) intent.getSerializableExtra("set");
            Log.d("ShowItemsActivity","이전 인텐트에서 보낸 set 있음");
        } catch(Exception e){
            Log.d("ShowItemsActivity","이전 인텐트에서 보낸 set 없음");
        }

        try {
            category = intent.getExtras().getString("category");
            folder = intent.getExtras().getString("folder");
            Log.d("이전 intent에서 받은값", folder);
        } catch(Exception e){
            Log.d("이전 intent에서 받은값", "업서..");
        }


        // 세트정보에서 id, dress_num에 따라 clothesDTO 생성
        click="load";
        Thread th = new Thread(ModifyItemActivity.this);
        th.start();
        //clothes = new ClothesDTO();

        final FashionSetDTO finalSet = set;
        final String finalCategory = category;
        final String finalFolder = folder;
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 저장하기누르면 현재 써져있는 값을 서버로 보냄
                click="save";
                Thread th = new Thread(ModifyItemActivity.this);
                th.start();

                // 서버에서는 그 내용대로 db 업데이트하고 이 뷰를 끝냄
                Intent intent = new Intent(ModifyItemActivity.this, ItemsetActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("set", (Serializable) finalSet);
                intent.putExtras(bundle);

                intent.putExtra("category", finalCategory);
                intent.putExtra("folder", finalFolder);

                startActivity(intent);
                finish();
            }
        });


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        String files = filepath; // files/upper/shirt
        bitmap = BitmapFactory.decodeFile(files, options);

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
        MyUrl myUrl = new MyUrl();
        if(click.equals("load")) {
            try {
                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                String ip = pref.getString("ip_addr", "");   // http://192.168.55.193:8080
                String id = pref.getString("id", "");   // test
                String[][] values = {{"id",id}, {"dress_number",dress_number}};

                JSONObject jsonObj = myUrl.getJsonResult(ip,"/loadCloth", values);
                JSONArray jArray = (JSONArray) jsonObj.get("cloth_result");

                JSONObject row = jArray.getJSONObject(0);
                // 받아온 clothes 결과에 따라 세팅해줌.
                clothes = new ClothesDTO(id, row.getString("dress_number"), row.getString("category1")
                        , row.getString("category2"), row.getString("color")
                        , row.getString("pattern"), row.getString("length"));

                // 세팅한 것에 대해 보여줌
                text_handler.sendEmptyMessage(0);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("ModifyItemActivity", "에러발생했습니다...");
            }
        }
        else if(click.equals("save")) {
            try {
                // 적혀져있는것 가져옴
                String id, dress_number, category, low_category, color, pattern, lengthC;
                id = clothes.getId();
                dress_number = clothes.getDress_number();
                category = category1_edit.getText().toString();
                low_category = category2_edit.getText().toString();
                color = color_edit.getText().toString();
                pattern = pattern_edit.getText().toString();
                lengthC = length_edit.getText().toString();

                SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
                String ip = pref.getString("ip_addr", "");   // http://192.168.55.193:8080
                String[][] values = {{"id", id}
                        , {"dress_number", dress_number}
                        , {"category", category}
                        , {"low_category", low_category}
                        , {"color", color}
                        , {"pattern", pattern}
                        , {"length", lengthC}};

                JSONObject jsonObj = myUrl.getJsonResult(ip, "/updateClothDB", values);
                JSONArray jArray = (JSONArray) jsonObj.get("cloth_result");

                // 몇개가져왔는지
                JSONObject row = jArray.getJSONObject(0);

                //int label_num[] = {1, 2, 3, 5, 4, 5};
                String result = row.getString("result");
                if (result.equals("ok")) {
                    Log.d("DB 업데이트 성공", "DB 업데이트 성공");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ModifyItemActivity.this, "DB 업데이트에 성공했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.d("DB 업데이트 실패", "DB 업데이트 실패");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(ModifyItemActivity.this, "DB 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }


                text_handler.sendEmptyMessage(0);

                // 받아온 결과에 따라 이미지 다시 저장하기 (원래 있던 파일을 새로운 경로로 저장하고, 원래있던것 삭제)

                // bitmap에 있는것을 category/low_category/low_category_받아온 number 로 저장
                String folder_name = getApplicationContext().getFilesDir().toString() + "/" + category + "/" + low_category;    // "/data/data/com.example.cameraandgallery/files/"+row.getString("result"); // /files/upper/hood_T/ 폴더생성
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

                try {
                    FileInputStream fis = new FileInputStream(getApplicationContext().getFilesDir().toString() + "/" + clothes.getCategory() + "/" + clothes.getLow_category() + "/" + clothes.getDress_number() + ".jpg");
                    FileOutputStream fos = new FileOutputStream(saveFileName);

                    int data = 0;
                    while ((data = fis.read()) != -1) {
                        fos.write(data);
                    }
                    fis.close();
                    fos.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }

                File original = new File(getApplicationContext().getFilesDir().toString() + "/" + clothes.getCategory() + "/" + clothes.getLow_category() + "/" + clothes.getDress_number() + ".jpg");
                original.delete();
                Log.d("ModifyItemActivity", "바꾼 내용대로 다시 저장했어요");
                Log.d("ModifyItemActivity", "원래경로 : " + getApplicationContext().getFilesDir().toString() + "/" + clothes.getCategory() + "/" + clothes.getLow_category() + "/" + clothes.getDress_number() + ".jpg");
                Log.d("ModifyItemActivity", "새로운경로 : " + getApplicationContext().getFilesDir().toString() + "/" + category + "/" + low_category + "/" + dress_number + ".jpg");
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("ModifyItemActivity", "에러발생했습니다...");
            }
        }
    }
}

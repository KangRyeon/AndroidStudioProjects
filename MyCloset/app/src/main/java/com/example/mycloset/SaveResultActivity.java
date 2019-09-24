package com.example.mycloset;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycloset.dto.ClothesDTO;
import com.example.mycloset.dto.FashionSetDTO;

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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

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

        Log.d("dress_num",clothes.getDress_number()+", "+clothes.getColor()+", "+clothes.getCategory());

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        String files = getApplicationContext().getFilesDir()+"/"+clothes.getCategory()+"/"+clothes.getLow_category()+"/"+clothes.getDress_number()+".jpg"; // files/upper/shirt
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

    }
}

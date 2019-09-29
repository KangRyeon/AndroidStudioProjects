package com.example.mycloset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mycloset.dto.FashionSetDTO;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
public class ClosetActivity extends AppCompatActivity implements View.OnClickListener, Runnable {

    ImageButton outer_btn;
    ImageButton bag_btn;
    ImageButton shoes_btn;
    ImageButton cap_btn;
    ImageButton upper_btn;
    ImageButton lower_btn;

    Button setsave_btn;

    Bitmap bitmap;
    FashionSetDTO set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet);

        outer_btn = (ImageButton)findViewById(R.id.outer_btn);
        outer_btn.setOnClickListener(this);

        bag_btn = (ImageButton)findViewById(R.id.bag_btn);
        bag_btn.setOnClickListener(this);

        shoes_btn = (ImageButton)findViewById(R.id.shoes_btn);
        shoes_btn.setOnClickListener(this);

        cap_btn = (ImageButton)findViewById(R.id.cap_btn);
        cap_btn.setOnClickListener(this);

        upper_btn = (ImageButton)findViewById(R.id.upper_btn);
        upper_btn.setOnClickListener(this);

        lower_btn = (ImageButton)findViewById(R.id.lower_btn);
        lower_btn.setOnClickListener(this);

        setsave_btn = (Button)findViewById(R.id.setsave_btn);
        setsave_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PopupActivity.class);
                if(set != null) {
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("set", (Serializable) set);
                    intent.putExtras(bundle);
                }

                startActivityForResult(intent, 0);

                /*
                // 서버로 세트 클래스 보내기
                Thread th = new Thread(ClosetActivity.this);
                th.start();

                 */
            }
        });

        // 옷장 class 받기
        Intent intent = getIntent();
        //set = null;

        try {
            set = (FashionSetDTO) intent.getSerializableExtra("set");
            Log.d("ClosetActivity","이전 인텐트에서 보낸 set 있음");
        } catch(Exception e){
            Log.d("ClosetAcitivity","가져온게없음");
            set = null;
        }

        if(set != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = false;

            if(set.getAccessory1() != null){
                //악세사리에 넣어야함
            }
            if(set.getAccessory2() != null){
                //악세사리에 넣어야함
            }
            if(set.getAccessory3() != null){
                //악세사리에 넣어야함
            }
            if(set.getOuter() != null){
                String files = set.getOuter();
                Log.d("가져온파일",files);
                bitmap = BitmapFactory.decodeFile(files, options);

                Bitmap resize;
                int maxsize = 200;
                resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);

                outer_btn.setImageBitmap(resize);
            }
            if(set.getBag() != null){
                String files = set.getBag();
                Log.d("가져온파일",files);
                bitmap = BitmapFactory.decodeFile(files, options);

                Bitmap resize;
                int maxsize = 200;
                resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);

                bag_btn.setImageBitmap(resize);
            }
            if(set.getShoes() != null){
                String files = set.getShoes();
                Log.d("가져온파일",files);
                bitmap = BitmapFactory.decodeFile(files, options);

                Bitmap resize;
                int maxsize = 200;
                resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);

                shoes_btn.setImageBitmap(resize);
            }
            if(set.getCap() != null){
                String files = set.getCap();
                Log.d("가져온파일",files);
                bitmap = BitmapFactory.decodeFile(files, options);

                Bitmap resize;
                int maxsize = 200;
                resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);

                cap_btn.setImageBitmap(resize);
            }
            if(set.getUpper() != null){
                String files = set.getUpper();
                Log.d("가져온파일",files);
                bitmap = BitmapFactory.decodeFile(files, options);

                Bitmap resize;
                int maxsize = 200;
                resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);

                upper_btn.setImageBitmap(resize);
            }
            if(set.getLower() != null){
                String files = set.getLower();
                Log.d("가져온파일",files);
                bitmap = BitmapFactory.decodeFile(files, options);

                Bitmap resize;
                int maxsize = 200;
                resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);

                lower_btn.setImageBitmap(resize);
            }
        }
    }

    @Override
    public void onClick(View v) {
        String[] outer = {"cardigan", "jacket", "padding", "coat", "jumper", "hood zipup"};
        String[] upper = {"hood_T", "long_T", "pola", "shirt", "short_T", "sleeveless", "vest"};
        String[] lower = {"long_pants", "short_pants", "Leggings", "mini_skirt", "long_skirt"};
        String[] onepeace = {"long_arm_mini_onepeace", "long_arm_long_onepeace", "short_arm_mini_onepeace", "short_arm_long_onepeace"};

        Intent intent;
        intent = new Intent(getApplicationContext(), ItemsetActivity.class);

        if(set == null) {
            set = new FashionSetDTO();
            Log.d("현재 사용할 세트 생성", "현재 사용할 세트 생성");
        }
        Bundle bundle = new Bundle();
        bundle.putSerializable("set", (Serializable)set);
        intent.putExtras(bundle);

        // etc로 변경
        switch(v.getId()){
            case R.id.outer_btn:     // ip 받아오는 버튼
                intent.putExtra("category","outer");
                break;
            case R.id.bag_btn:
                intent.putExtra("category","etc");
                break;
            case R.id.shoes_btn:
                intent.putExtra("category","etc");
                break;
            case R.id.cap_btn:
                intent.putExtra("category","etc");
                break;
            case R.id.upper_btn:
                intent.putExtra("category","upper");
                //intent.putExtra("items2", onepeace);
                break;
            case R.id.lower_btn:
                intent.putExtra("category","lower");
                break;
        }
        /*
        switch(v.getId()){
            case R.id.outer_btn:     // ip 받아오는 버튼
                intent.putExtra("items",outer);
                break;
            case R.id.bag_btn:
                intent.putExtra("item","bag");
                break;
            case R.id.shoes_btn:
                intent.putExtra("item","shoes");
                break;
            case R.id.cap_btn:
                intent.putExtra("item","cap");
                break;
            case R.id.upper_btn:
                intent.putExtra("items",upper);
                intent.putExtra("items2", onepeace);
                break;
            case R.id.lower_btn:
                intent.putExtra("items",lower);
                break;
        }
*/
        startActivity(intent);
    }


    @Override
    public void run() {
        Log.d("버튼 눌림", "버튼 눌림");


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(this, "세트가 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }
}

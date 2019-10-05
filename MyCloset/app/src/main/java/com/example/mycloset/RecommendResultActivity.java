package com.example.mycloset;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycloset.dto.ClothesDTO;

import java.io.File;

public class RecommendResultActivity extends AppCompatActivity {

    ImageView img_view;
    Bitmap bitmap;
    Button recommend_by_kinds_btn;
    Button recommend_by_color_btn;
    Button recommend_by_pattern_btn;
    LinearLayout upper_list_layout;
    LinearLayout lower_list_layout;
    ClothesDTO clothes_upper;
    ClothesDTO clothes_lower;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendresult);

        // 이전 뷰에 있던 세트 정보 가져옴
        Intent intent = getIntent();
        try {
            clothes_upper = (ClothesDTO) intent.getSerializableExtra("clothes_upper");
            Log.d("RecommendResultActivity","받아온 값 : "+clothes_upper.getLow_category());
            clothes_lower = (ClothesDTO) intent.getSerializableExtra("clothes_lower");
            Log.d("RecommendResultActivity","받아온 값 : "+clothes_lower.getLow_category());
        } catch(Exception e){
            Log.d("오류", "못가져옴");
            Log.d("오류",e.toString());
        }

        recommend_by_kinds_btn = (Button)findViewById(R.id.recommend_by_kinds_btn);
        recommend_by_color_btn = (Button)findViewById(R.id.recommend_by_color_btn);
        recommend_by_pattern_btn = (Button)findViewById(R.id.recommend_by_pattern_btn);
        upper_list_layout = (LinearLayout)findViewById(R.id.upper_list_layout);
        lower_list_layout = (LinearLayout)findViewById(R.id.lower_list_layout);



        // 옷종류 버튼 클릭시 - 분석한 옷종류에 따라 이미지 이름들 가져옴.
        recommend_by_kinds_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load_clothes_by_kinds();
            }
        });

        // 색 버튼 클릭시 - 분석한 옷 색에 따라 이미지 이름들 가져옴.
        // 상의중에 black인것들 이름 가져와 보여주기
        // 하의중에 baige인것들 이름 가져와 보여주기
        recommend_by_color_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        // 무늬 버튼 클릭시 - db에서 받아온 옷종류에 따라 이미지 이름들 가져옴.
        recommend_by_pattern_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        img_view = (ImageView)findViewById(R.id.img_view);

        loading_handler.sendEmptyMessage(0);
    }

    Handler loading_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = false;
            String files = getApplicationContext().getCacheDir()+"/test.jpg";
            bitmap = BitmapFactory.decodeFile(files, options);
            img_view.setImageBitmap(bitmap);
        }
    };


    // 옷종류(hood_T, long_pants)에 따라서 옷 보여주기
    public void load_clothes_by_kinds(){
        // 상의 hood_T 밑에 무슨 파일들이 있는지 가져옴
        String category = clothes_upper.getCategory();
        String folder = clothes_upper.getLow_category();
        File file = new File(getApplicationContext().getFilesDir()+"/"+category+"/"+folder);
        Log.d("상위폴더",getApplicationContext().getFilesDir()+"/"+category+"/"+folder);

        File list[] = file.listFiles();

        // 파일 하나당 버튼하나만들어 넣어줌.
        for (int i=0; i<list.length; i++) {
            Log.d("파일목록", list[i].getName());
            ImageButton btn = new ImageButton(getApplicationContext());
            btn.setId(i);
            //btn.setText(list[i].getName());
            btn.setMaxWidth(200);
            btn.setMaxHeight(200);
            btn.setMinimumWidth(200);
            btn.setMinimumHeight(200);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = false;
            String files = getApplicationContext().getFilesDir() + "/" + category + "/" + folder + "/" + list[i].getName();
            Log.d("가져온파일", files);
            bitmap = BitmapFactory.decodeFile(files, options);

            Bitmap resize;
            int maxsize = 200;
            resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);
            btn.setImageBitmap(resize);
            upper_list_layout.addView(btn);

            final String onclick_filename = list[i].getName();
            final String onclick_foldername = folder;
            final String finalCategory = category;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("가져오기", "files에서 파일 가져오기: " + onclick_filename);
                    String files = getApplicationContext().getFilesDir() + "/" + finalCategory + "/" + onclick_foldername + "/" + onclick_filename;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(files, options);

                    Bitmap resize;
                    int maxsize = 200;
                    resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);
//                            img_view.setImageBitmap(bitmap);
                    //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
                }
            });
        }

        // 상의 hood_T 밑에 무슨 파일들이 있는지 가져옴
        category = clothes_lower.getCategory();
        folder = clothes_lower.getLow_category();
        file = new File(getApplicationContext().getFilesDir()+"/"+category+"/"+folder);
        Log.d("상위폴더",getApplicationContext().getFilesDir()+"/"+category+"/"+folder);

        list = file.listFiles();

        // 파일 하나당 버튼하나만들어 넣어줌.
        for (int i=0; i<list.length; i++) {
            Log.d("파일목록", list[i].getName());
            ImageButton btn = new ImageButton(getApplicationContext());
            btn.setId(i);
            //btn.setText(list[i].getName());
            btn.setMaxWidth(200);
            btn.setMaxHeight(200);
            btn.setMinimumWidth(200);
            btn.setMinimumHeight(200);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = false;
            String files = getApplicationContext().getFilesDir() + "/" + category + "/" + folder + "/" + list[i].getName();
            Log.d("가져온파일", files);
            bitmap = BitmapFactory.decodeFile(files, options);

            Bitmap resize;
            int maxsize = 200;
            resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);
            btn.setImageBitmap(resize);
            lower_list_layout.addView(btn);

            final String onclick_filename = list[i].getName();
            final String onclick_foldername = folder;
            final String finalCategory1 = category;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("가져오기", "files에서 파일 가져오기: " + onclick_filename);
                    String files = getApplicationContext().getFilesDir() + "/" + finalCategory1 + "/" + onclick_foldername + "/" + onclick_filename;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(files, options);

                    Bitmap resize;
                    int maxsize = 200;
                    resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);
//                            img_view.setImageBitmap(bitmap);
                    //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
                }
            });
        }

    }
}

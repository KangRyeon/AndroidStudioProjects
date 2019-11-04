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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ootd.dto.ClothesDTO;
import com.example.ootd.dto.FashionSetDTO;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;

public class RecommendResultActivity extends AppCompatActivity implements Runnable {
    Button choose_btn;
    FashionSetDTO set;

    ImageView img_view;
    Bitmap bitmap;
    Button recommend_by_kinds_btn;
    Button recommend_by_color_btn;
    Button recommend_by_pattern_btn;
    LinearLayout upper_list_layout;
    LinearLayout lower_list_layout;
    ClothesDTO clothes_upper;
    ClothesDTO clothes_lower;

    ImageView choose_upper_imageview;
    ImageView choose_lower_imageview;
    Bitmap choose_upper_bitmap;
    Bitmap choose_lower_bitmap;
    ClothesDTO clothes_choose_upper;
    ClothesDTO clothes_choose_lower;

    ImageButton add_btn;
    int count;

    String url_request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommendresult);

        count = 0;

        // 이전뷰에서 입력한 사진에 대한 상의, 하의 결과
        Intent intent = getIntent();
        try {
            clothes_upper = (ClothesDTO) intent.getSerializableExtra("clothes_upper");
            Log.d("RecommendResultActivity", "받아온 값 : " + clothes_upper.getLow_category());
            clothes_lower = (ClothesDTO) intent.getSerializableExtra("clothes_lower");
            Log.d("RecommendResultActivity", "받아온 값 : " + clothes_lower.getLow_category());
        } catch (Exception e) {
            Log.d("오류", "못가져옴");
            Log.d("오류", e.toString());
        }

        choose_btn = (Button) findViewById(R.id.choose_btn);
        set = new FashionSetDTO();

        recommend_by_kinds_btn = (Button) findViewById(R.id.recommend_by_kinds_btn);
        recommend_by_color_btn = (Button) findViewById(R.id.recommend_by_color_btn);
        recommend_by_pattern_btn = (Button) findViewById(R.id.recommend_by_pattern_btn);
        upper_list_layout = (LinearLayout) findViewById(R.id.upper_list_layout);
        lower_list_layout = (LinearLayout) findViewById(R.id.lower_list_layout);
        choose_upper_imageview = (ImageView) findViewById(R.id.choose_upper_imageview);
        choose_lower_imageview = (ImageView) findViewById(R.id.choose_lower_imageview);

        // 옷선택 버튼을 누르면
        choose_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                intent = new Intent(getApplicationContext(), ClosetActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // closetActivity 원래열었던곳으로 돌아감

                Bundle bundle = new Bundle();
                bundle.putSerializable("set", (Serializable)set);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });

        // 옷종류 버튼 클릭시 - 분석한 옷종류에 따라 이미지 이름들 가져옴.
        // 상의폴더중에 hood_T 가져와서 보여줌
        // 하의폴더중에 long_pants 가져와서 보여줌
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
                url_request = "/selectClothesbyCategoryNColor";
                Thread th = new Thread(RecommendResultActivity.this);
                th.start();
            }
        });

        // 무늬 버튼 클릭시 - db에서 받아온 옷종류에 따라 이미지 이름들 가져옴.
        recommend_by_pattern_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url_request = "/selectClothesbyCategoryNPattern";
                Thread th = new Thread(RecommendResultActivity.this);
                th.start();
            }
        });

        img_view = (ImageView) findViewById(R.id.img_view);

        loading_handler.sendEmptyMessage(0);
    }

    Handler loading_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = false;
            String files = getApplicationContext().getCacheDir() + "/test.jpg";
            bitmap = BitmapFactory.decodeFile(files, options);
            img_view.setImageBitmap(bitmap);
        }
    };

    // 이미지 버튼 200x200으로 생성
    public ImageButton makeImageButton(int i) {
        ImageButton btn = new ImageButton(getApplicationContext());
        count++;
        btn.setId(count);
        //btn.setText(list[i].getName());
        btn.setMaxWidth(200);
        btn.setMaxHeight(200);
        btn.setMinimumWidth(200);
        btn.setMinimumHeight(200);

        return btn;
    }

    // 내부메모리 파일 경로에 따라서 비트맵 가져옴
    public Bitmap loadBitmapFromfiles(String files) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        Log.d("가져온파일", files);
        bitmap = BitmapFactory.decodeFile(files, options);
        return bitmap;
    }

    Handler upper_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            upper_list_layout.addView(add_btn);
        }
    };

    Handler lower_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            lower_list_layout.addView(add_btn);
        }
    };

    // files 밑의 category, folder, filename 받아와 버튼으로 만들고, layout에 넣어주기
    // 0, upper, hood_T, test.jpg 넘어올 것
    public void addImageButtonToLayout(int i, String category, String folder, String filename){
        Log.d("파일목록", filename);

        // 이미지버튼 생성함(200x200으로)
        final ImageButton btn = makeImageButton(i);

        // 내부메모리 경로에서 bitmap 가져옴.
        String files = getApplicationContext().getFilesDir() + "/" + category + "/" + folder + "/" + filename;
        bitmap = loadBitmapFromfiles(files);

        // 이미지 200x200으로 만들어서 버튼에 세팅하고, layout에 넣음.
        Bitmap resize;
        int maxsize = 200;
        resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);
        btn.setImageBitmap(resize);
        add_btn = btn;
        if(category.equals("upper")) {
            //upper_list_layout.addView(add_btn);
            //upper_handler.sendEmptyMessage(0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    upper_list_layout.addView(btn);
                }
            });
        }
        else {
            //lower_list_layout.addView(add_btn);
            //lower_handler.sendEmptyMessage(0);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    lower_list_layout.addView(btn);
                }
            });
        }

        // 각 버튼을 누를때마다 그 버튼의 내부메모리경로따라 bitmap가져옴.
        final String onclick_filename = filename;
        final String onclick_foldername = folder;
        final String finalCategory = category;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("가져오기", "files에서 파일 가져오기: " + onclick_filename);
                final String files = getApplicationContext().getFilesDir() + "/" + finalCategory + "/" + onclick_foldername + "/" + onclick_filename;

                // bitmap에 파일경로에 따라서 넣어줌

                if(finalCategory.equals("upper")) {
                    choose_upper_bitmap = loadBitmapFromfiles(files);
                    Bitmap resize;
                    int maxsize = 200;
                    resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("upper 채워넣음", "upper 채워넣음");
                            choose_upper_imageview.setImageBitmap(choose_upper_bitmap);
                            set.setUpper(files);
                        }
                    });
                }
                else {
                    choose_lower_bitmap = loadBitmapFromfiles(files);
                    Bitmap resize;
                    int maxsize = 200;
                    resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("lower 채워넣음", "lower 채워넣음");
                            choose_lower_imageview.setImageBitmap(choose_lower_bitmap);
                            set.setLower(files);
                        }
                    });
                }


                //                            img_view.setImageBitmap(bitmap);
                //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
            }
        });
    }
    // 옷종류(hood_T, long_pants)에 따라서 옷 보여주기
    public void load_clothes_by_kinds() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("upper, lower 지움", "upper, lower 지움");
                upper_list_layout.removeAllViewsInLayout();
                lower_list_layout.removeAllViewsInLayout();
            }
        });

        // 상의 hood_T 밑에 무슨 파일들이 있는지 가져옴
        String category = clothes_upper.getCategory();
        String folder = clothes_upper.getLow_category();
        File file = new File(getApplicationContext().getFilesDir() + "/" + category + "/" + folder);
        Log.d("상위폴더", getApplicationContext().getFilesDir() + "/" + category + "/" + folder);

        File list[] = file.listFiles();

        // 파일 하나당 버튼하나만들어 넣어줌.
        for (int i = 0; i < list.length; i++) {
            addImageButtonToLayout(i, category, folder, list[i].getName());
        }

        // 하의 폴더 밑에 무슨 파일들이 있는지 가져옴
        category = clothes_lower.getCategory();
        folder = clothes_lower.getLow_category();
        file = new File(getApplicationContext().getFilesDir() + "/" + category + "/" + folder);
        Log.d("상위폴더", getApplicationContext().getFilesDir() + "/" + category + "/" + folder);

        list = file.listFiles();

        // 파일 하나당 버튼하나만들어 넣어줌.
        for (int i = 0; i < list.length; i++) {
            addImageButtonToLayout(i, category, folder, list[i].getName());
        }

    }



    // 옷종류(hood_T, long_pants)에 따라서 옷 보여주기
    public void load_clothes_by_color() {
        // clothes upper에 있는 색깔을 db에서 검색해 나온 것들 json으로 받고
        // json을 clothes로 변환, 각 해당하는 옷 보여줌.
        // clothes lower에 있는 색깔을 db에서 검색해 나온 것들 보여줌.
        //

    }

    @Override
    public void run() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("upper, lower 지움", "upper, lower 지움");
                upper_list_layout.removeAllViewsInLayout();
                lower_list_layout.removeAllViewsInLayout();
            }
        });

        StringBuffer sb = new StringBuffer();
        try {
            MyUrl myUrl = new MyUrl();

            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            String ip = pref.getString("ip_addr", "");   // http://192.168.55.193:8080
            String id = pref.getString("id", "");   // test

            JSONObject jsonObj = null;

            // color을 request로 보냄
            if(url_request.equals("/selectClothesbyCategoryNColor")) {
                String[][] values = {{"id", id}
                        , {"upper_category", clothes_upper.getCategory()}
                        , {"upper_color", clothes_upper.getColor()}
                        , {"lower_category", clothes_lower.getCategory()}
                        , {"lower_color", clothes_lower.getColor()}};
                jsonObj = myUrl.getJsonResult(ip,url_request, values);
            }

            // pattern을 request로 보냄
            else if(url_request.equals("/selectClothesbyCategoryNPattern")) {
                String[][] values = {{"id", id}
                        , {"upper_category", clothes_upper.getCategory()}
                        , {"upper_pattern", clothes_upper.getPattern()}
                        , {"lower_category", clothes_lower.getCategory()}
                        , {"lower_pattern", clothes_lower.getPattern()}};
                jsonObj = myUrl.getJsonResult(ip,url_request, values);
            }

            // 받아온 source를 JSONObject로 변환한다.

            JSONArray jArray_upper = (JSONArray) jsonObj.get("upper_result");
            JSONArray jArray_lower = (JSONArray) jsonObj.get("lower_result");

            ClothesDTO[] upper_clothes = new ClothesDTO[jArray_upper.length()];
            ClothesDTO[] lower_clothes = new ClothesDTO[jArray_lower.length()];

            // upper 대한 JSONObject 받아옴
            for(int i=0; i<jArray_upper.length(); i++) {
                JSONObject row = jArray_upper.getJSONObject(i);
                String r_dress_num = row.getString("dress_num");
                String r_category1 = row.getString("category1");
                String r_category2 = row.getString("category2");
                String r_color = row.getString("color");
                String r_pattern = row.getString("pattern");
                String r_length = row.getString("length");

                upper_clothes[i] = new ClothesDTO(id, r_dress_num, r_category1, r_category2, r_color, r_pattern, r_length);
            }
            Log.d("색따른 upper 다 받아옴", ""+jArray_upper.length()+"개");

            // lower 대한 JSONObject 받아옴
            for(int i=0; i<jArray_lower.length(); i++) {
                JSONObject row = jArray_lower.getJSONObject(i);
                String r_dress_num = row.getString("dress_num");
                String r_category1 = row.getString("category1");
                String r_category2 = row.getString("category2");
                String r_color = row.getString("color");
                String r_pattern = row.getString("pattern");
                String r_length = row.getString("length");

                lower_clothes[i] = new ClothesDTO(id, r_dress_num, r_category1, r_category2, r_color, r_pattern, r_length);
            }
            Log.d("색따른 lower 다 받아옴", ""+jArray_lower.length()+"개");

            // upper 대해 이미지버튼 생성
            for(int i=0; i<upper_clothes.length; i++) {
                addImageButtonToLayout(i, "upper", upper_clothes[i].getLow_category(), upper_clothes[i].getDress_number()+".jpg");
            }

            // lower 대해 이미지버튼 생성
            for(int i=0; i<lower_clothes.length; i++) {
                addImageButtonToLayout(i, "lower", lower_clothes[i].getLow_category(), lower_clothes[i].getDress_number()+".jpg");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("myLog_error", "에러발생했습니다...");
        }
    }
}

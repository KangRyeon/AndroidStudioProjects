package com.example.mycloset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycloset.dto.FashionSetDTO;

import java.io.File;
import java.io.Serializable;

public class ShowItemsActivity  extends AppCompatActivity {

    LinearLayout list_layout;
    ImageView img_view;
    Button choose_btn;

    Bitmap bitmap;
    Intent intent;

    SharedPreferences sf;
    SharedPreferences.Editor editor;
    String category;
    String item;
    String files;

    FashionSetDTO set;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showitems);

        list_layout = (LinearLayout) findViewById(R.id.list_layout);
        img_view = (ImageView) findViewById(R.id.img_view);
        choose_btn = (Button)findViewById(R.id.choose_btn);


        // 이전 뷰에 있던 세트 정보 가져옴
        Intent intent = getIntent();
        try {
            set = (FashionSetDTO) intent.getSerializableExtra("set");
            Log.d("ShowItemsActivity","이전 인텐트에서 보낸 set 있음");
        } catch(Exception e){
            Log.d("ShowItemsActivity","이전 인텐트에서 보낸 set 없음");
        }


        String folder="";

        intent = getIntent();


        try {
            category = intent.getExtras().getString("category");
            folder = intent.getExtras().getString("folder");
            Log.d("이전 intent에서 받은값", folder);
        } catch(Exception e){
            Log.d("이전 intent에서 받은값", "업서..");
        }


        File file = new File(getApplicationContext().getFilesDir()+"/"+category+"/"+folder);
        Log.d("상위폴더",getApplicationContext().getFilesDir()+"/"+category+"/"+folder);

        File list[] = file.listFiles();

        for (int i=0; i<list.length; i++){
            Log.d("파일목록",list[i].getName());
            ImageButton btn = new ImageButton(this);
            btn.setId(i);
            //btn.setText(list[i].getName());
            btn.setMaxWidth(200);
            btn.setMaxHeight(200);
            btn.setMinimumWidth(200);
            btn.setMinimumHeight(200);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = false;
            files = getApplicationContext().getFilesDir()+"/"+category+"/"+folder+"/"+list[i].getName();
            Log.d("가져온파일",files);
            bitmap = BitmapFactory.decodeFile(files, options);

            Bitmap resize;
            int maxsize = 200;
            resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);
            btn.setImageBitmap(resize);
            list_layout.addView(btn);

            final String onclick_filename = list[i].getName();
            final String onclick_foldername = folder;
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("가져오기", "files에서 파일 가져오기: "+onclick_filename);
                    files = getApplicationContext().getFilesDir()+"/"+category+"/"+onclick_foldername+"/"+onclick_filename;
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inSampleSize = 1;
                    options.inJustDecodeBounds = false;
                    bitmap = BitmapFactory.decodeFile(files, options);

                    Bitmap resize;
                    int maxsize = 200;
                    resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);
                    img_view.setImageBitmap(bitmap);
                    //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
                }
            });

            // bag, cap, shoes는 folder에 "bag"이라는 글자가 있음.
            // category = "etc"로 설정되어있음.
            final String finalFolder = folder;
            choose_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("가져오기", "files에서 파일 가져오기");
                    Intent intent = new Intent(ShowItemsActivity.this, ClosetActivity.class);
                    if(finalFolder.equals("bag")) {
                        set.setBag(files);
                        Log.d("set의 bag에 들어있는것", set.getBag());
                    }
                    if(finalFolder.equals("cap")) {
                        set.setCap(files);
                        Log.d("set의 cap에 들어있는것", set.getCap());
                    }
                    if(finalFolder.equals("shoes")) {
                        set.setShoes(files);
                        Log.d("set의 shoes에 들어있는것", set.getShoes());
                    }
                    if(category.equals("outer")) {
                        set.setOuter(files);
                        Log.d("set의 outer에 들어있는것", set.getOuter());
                    }
                    if(category.equals("upper")) {
                        set.setUpper(files);
                        Log.d("set의 upper에 들어있는것", set.getUpper());
                    }
                    if(category.equals("lower")) {
                        set.setLower(files);
                        Log.d("set의 lower에 들어있는것", set.getLower());
                    }

                    Log.d("옷장으로 넘기는 파일",category+", "+files);
                    try{
                        Log.d("set의 upper에 들어있는것", set.getUpper());
                    }catch(Exception e){}
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("set", (Serializable) set);
                    intent.putExtras(bundle);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // closetActivity 원래열었던곳으로 돌아감
                    startActivity(intent);
                    finish();      // finish() 를 하지 않으면 메인액티비티가 꺼지지 않음
                    //img_view.setImageBitmap(bitmap);
                }
            });
        }
    }
}

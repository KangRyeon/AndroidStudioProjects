package com.example.mycloset;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycloset.dto.FashionSetDTO;

public class FashionSetActivity extends AppCompatActivity implements View.OnClickListener{

    FashionSetDTO set;
    Bitmap bitmap;

    ImageButton outer_btn;
    ImageButton bag_btn;
    ImageButton shoes_btn;
    ImageButton cap_btn;
    ImageButton upper_btn;
    ImageButton lower_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fashionset);

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

        // 옷장 class 받기
        Intent intent = getIntent();
        try {
            set = (FashionSetDTO) intent.getSerializableExtra("set");
            Log.d("FashionSetActivity","이전 인텐트에서 보낸 set 있음");
            Log.d("FashionSetActivity에서 받은 set이름", set.getSet_name());
        } catch(Exception e){
            Log.d("FashionSetActivity","가져온게없음");
            set = null;
        }

        if(set != null) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = false;

            if(set.getAccessory1().equals("null") != true){
                //악세사리에 넣어야함
            }
            if(set.getAccessory2().equals("null") != true){
                //악세사리에 넣어야함
            }
            if(set.getAccessory3().equals("null") != true){
                //악세사리에 넣어야함
            }
            if(set.getOuter().equals("null") != true){

                String files = set.getOuter();
                Log.d("가져온파일",files);
                bitmap = BitmapFactory.decodeFile(files, options);

                Bitmap resize;
                int maxsize = 200;
                resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);

                outer_btn.setImageBitmap(resize);
            }
            if(set.getBag().equals("null") != true){
                String files = set.getBag();
                Log.d("가져온파일",files);
                bitmap = BitmapFactory.decodeFile(files, options);

                Bitmap resize;
                int maxsize = 200;
                resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);

                bag_btn.setImageBitmap(resize);
            }
            if(set.getShoes().equals("null") != true){
                String files = set.getShoes();
                Log.d("가져온파일",files);
                bitmap = BitmapFactory.decodeFile(files, options);

                Bitmap resize;
                int maxsize = 200;
                resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);

                shoes_btn.setImageBitmap(resize);
            }
            if(set.getCap().equals("null") != true){
                String files = set.getCap();
                Log.d("가져온파일",files);
                bitmap = BitmapFactory.decodeFile(files, options);

                Bitmap resize;
                int maxsize = 200;
                resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);

                cap_btn.setImageBitmap(resize);
            }
            if(set.getUpper().equals("null") != true){
                String files = set.getUpper();
                Log.d("가져온파일",files);
                bitmap = BitmapFactory.decodeFile(files, options);

                Bitmap resize;
                int maxsize = 200;
                resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);

                upper_btn.setImageBitmap(resize);
            }
            if(set.getLower().equals("null") != true){
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
    public void onClick(View view) {

    }
}

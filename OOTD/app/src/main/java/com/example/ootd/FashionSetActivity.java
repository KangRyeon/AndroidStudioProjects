package com.example.ootd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ootd.dto.FashionSetDTO;

public class FashionSetActivity extends AppCompatActivity implements View.OnClickListener{

    FashionSetDTO set;
    Bitmap bitmap;

    ImageButton accessory1_btn;
    ImageButton accessory2_btn;
    ImageButton accessory3_btn;
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

        accessory1_btn = (ImageButton)findViewById(R.id.accessory1_btn);
        accessory1_btn.setOnClickListener(this);

        accessory2_btn = (ImageButton)findViewById(R.id.accessory2_btn);
        accessory2_btn.setOnClickListener(this);

        accessory3_btn = (ImageButton)findViewById(R.id.accessory3_btn);
        accessory3_btn.setOnClickListener(this);

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

            if(!set.getAccessory1().equals("null")){ accessory1_btn.setImageBitmap(load_bitmap(set.getAccessory1())); }
            if(!set.getAccessory2().equals("null")){ accessory2_btn.setImageBitmap(load_bitmap(set.getAccessory2())); }
            if(!set.getAccessory3().equals("null")){ accessory3_btn.setImageBitmap(load_bitmap(set.getAccessory3())); }
            if(!set.getOuter().equals("null")){ outer_btn.setImageBitmap(load_bitmap(set.getOuter())); }
            if(!set.getBag().equals("null")){ bag_btn.setImageBitmap(load_bitmap(set.getBag())); }
            if(!set.getShoes().equals("null")){ shoes_btn.setImageBitmap(load_bitmap(set.getShoes())); }
            if(!set.getCap().equals("null")){ cap_btn.setImageBitmap(load_bitmap(set.getCap())); }
            if(!set.getUpper().equals("null")){ upper_btn.setImageBitmap(load_bitmap(set.getUpper())); }
            if(!set.getLower().equals("null")){ lower_btn.setImageBitmap(load_bitmap(set.getLower())); }

        }
    }
    // filepath 이미지를 load해 리턴
    public Bitmap load_bitmap(String filepath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;

        bitmap = BitmapFactory.decodeFile(filepath, options);

        Bitmap resize;
        int maxsize = 200;
        resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);

        return resize;
    }
    @Override
    public void onClick(View view) {

    }
}

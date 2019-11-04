package com.example.ootd;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.ootd.dto.FashionSetDTO;

import java.io.Serializable;

public class ClosetActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton accessory1_btn;
    ImageButton accessory2_btn;
    ImageButton accessory3_btn;
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

        setsave_btn = (Button)findViewById(R.id.setsave_btn);

        // 세트저장 버튼 누르면
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
            }
        });

        // fashion set class 받기
        Intent intent = getIntent();

        try {
            set = (FashionSetDTO) intent.getSerializableExtra("set");
            Log.d("ClosetActivity","이전 인텐트에서 보낸 set 있음");
        } catch(Exception e){
            Log.d("ClosetAcitivity","가져온게없음");
            set = null;
        }

        // fashion set class 가 있으면 무슨사진인지 가져와 뿌려줌
        if(set != null) {
            if(set.getAccessory1() != null){
                Bitmap resize = load_bitmap(set.getAccessory1());
                accessory1_btn.setImageBitmap(resize);
            }
            if(set.getAccessory2() != null){ accessory2_btn.setImageBitmap(load_bitmap(set.getAccessory2())); }
            if(set.getAccessory3() != null){ accessory3_btn.setImageBitmap(load_bitmap(set.getAccessory3())); }
            if(set.getOuter() != null){ outer_btn.setImageBitmap(load_bitmap(set.getOuter())); }
            if(set.getBag() != null){ bag_btn.setImageBitmap(load_bitmap(set.getBag())); }
            if(set.getShoes() != null){ shoes_btn.setImageBitmap(load_bitmap(set.getShoes())); }
            if(set.getCap() != null){ cap_btn.setImageBitmap(load_bitmap(set.getCap())); }
            if(set.getUpper() != null){ upper_btn.setImageBitmap(load_bitmap(set.getUpper())); }
            if(set.getLower() != null){ lower_btn.setImageBitmap(load_bitmap(set.getLower())); }
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
    public void onClick(View v) {
        String[] outer = {"cardigan", "jacket", "padding", "coat", "jumper", "hood zipup"};
        String[] upper = {"hood_T", "long_T", "pola", "shirt", "short_T", "sleeveless", "vest"};
        String[] lower = {"long_pants", "short_pants", "mini_skirt", "long_skirt"};
        String[] etc = {"accessory", "bag", "shoes", "cap"};
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
            case R.id.accessory1_btn:
                intent.putExtra("category","etc");
                break;
            case R.id.accessory2_btn:
                intent.putExtra("category","etc");
                break;
            case R.id.accessory3_btn:
                intent.putExtra("category","etc");
                break;
            case R.id.outer_btn:
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
                break;
            case R.id.lower_btn:
                intent.putExtra("category","lower");
                break;
        }
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Toast.makeText(this, "세트가 저장되었습니다.", Toast.LENGTH_SHORT).show();
    }
}

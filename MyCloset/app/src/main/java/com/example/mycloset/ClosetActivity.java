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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mycloset.dto.FashionSetDTO;

import java.io.Serializable;

public class ClosetActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton outer_btn;
    ImageButton bag_btn;
    ImageButton shose_btn;
    ImageButton cap_btn;
    ImageButton upper_btn;
    ImageButton lower_btn;

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

        shose_btn = (ImageButton)findViewById(R.id.shose_btn);
        shose_btn.setOnClickListener(this);

        cap_btn = (ImageButton)findViewById(R.id.cap_btn);
        cap_btn.setOnClickListener(this);

        upper_btn = (ImageButton)findViewById(R.id.upper_btn);
        upper_btn.setOnClickListener(this);

        lower_btn = (ImageButton)findViewById(R.id.lower_btn);
        lower_btn.setOnClickListener(this);

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

            if(set.getAccessory() != null){
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
            if(set.getShose() != null){
                String files = set.getShose();
                Log.d("가져온파일",files);
                bitmap = BitmapFactory.decodeFile(files, options);

                Bitmap resize;
                int maxsize = 200;
                resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);

                shose_btn.setImageBitmap(resize);
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

        switch(v.getId()){
            case R.id.outer_btn:     // ip 받아오는 버튼
                intent.putExtra("category","outer");
                break;
            case R.id.bag_btn:
                intent.putExtra("category","bag");
                break;
            case R.id.shose_btn:
                intent.putExtra("category","shose");
                break;
            case R.id.cap_btn:
                intent.putExtra("category","cap");
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
            case R.id.shose_btn:
                intent.putExtra("item","shose");
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
}

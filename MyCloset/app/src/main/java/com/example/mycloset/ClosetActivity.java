package com.example.mycloset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class ClosetActivity extends AppCompatActivity implements View.OnClickListener {

    Button outer_btn;
    Button bag_btn;
    Button shose_btn;
    Button cap_btn;
    Button upper_btn;
    Button lower_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closet);

        outer_btn = (Button)findViewById(R.id.outer_btn);
        outer_btn.setOnClickListener(this);

        bag_btn = (Button)findViewById(R.id.bag_btn);
        bag_btn.setOnClickListener(this);

        shose_btn = (Button)findViewById(R.id.shose_btn);
        shose_btn.setOnClickListener(this);

        cap_btn = (Button)findViewById(R.id.cap_btn);
        cap_btn.setOnClickListener(this);

        upper_btn = (Button)findViewById(R.id.upper_btn);
        upper_btn.setOnClickListener(this);

        lower_btn = (Button)findViewById(R.id.lower_btn);
        lower_btn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String[] outer = {"cardigan", "jacket", "padding", "coat", "jumper", "hood zipup"};
        String[] upper = {"hood_T", "long_T", "pola", "shirt", "short_T", "sleeveless", "vest"};
        String[] lower = {"long_pants", "short_pants", "Leggings", "mini_skirt", "long_skirt"};
        String[] onepeace = {"long_arm_mini_onepeace", "long_arm_long_onepeace", "short_arm_mini_onepeace", "short_arm_long_onepeace"};

/*
        // SharedPreferences를 sfile이라는 이름으로 생성
        SharedPreferences sf = getSharedPreferences("sfile",MODE_PRIVATE);

        // 에디터 열어 값 저장
        SharedPreferences.Editor editor = sf.edit();
        String category = "";

        category = "outer";
        editor.putString("category", category); // key, value를 이용하여 저장하는 형태
        editor.commit();
        Log.d("쉐얼드 프리퍼런스에 값 저장", "쉐얼드 프리퍼런스에 값 저장");


        switch(v.getId()){
            case R.id.outer_btn:
                category = "outer";
                break;
            case R.id.bag_btn:
                category = "bag";
                break;
            case R.id.shose_btn:
                category = "shose";
                break;
            case R.id.cap_btn:
                category = "cap";
                break;
            case R.id.upper_btn:
                category = "upper";
                break;
            case R.id.lower_btn:
                category = "lower";
                break;
        }
        editor.putString("category", category);
        editor.commit();
 */
        Intent intent;
        intent = new Intent(getApplicationContext(), ItemsetActivity.class);



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

        startActivity(intent);
    }
}

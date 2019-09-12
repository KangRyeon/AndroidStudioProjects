package com.example.mycloset;

import android.content.Intent;
import android.os.Bundle;
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

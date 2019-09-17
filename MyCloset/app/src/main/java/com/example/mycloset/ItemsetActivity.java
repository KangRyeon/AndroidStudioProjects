package com.example.mycloset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mycloset.dto.FashionSetDTO;

import java.io.Serializable;

public class ItemsetActivity extends AppCompatActivity {

    LinearLayout list;

    FashionSetDTO set;
    String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemset);

        String[] outer = {"cardigan", "jacket", "padding", "coat", "jumper", "hood zipup"};
        String[] upper = {"hood_T", "long_T", "pola", "shirt", "short_T", "sleeveless", "vest"};
        String[] lower = {"long_pants", "short_pants", "Leggings", "mini_skirt", "long_skirt"};
        String[] onepeace = {"long_arm_mini_onepeace", "long_arm_long_onepeace", "short_arm_mini_onepeace", "short_arm_long_onepeace"};


        // 이전 뷰에 있던 세트 정보 가져옴
        Intent intent = getIntent();
        try {
            set = (FashionSetDTO) intent.getSerializableExtra("set");
        } catch(Exception e){
            Log.d("오류", "못가져옴");
            Log.d("오류",e.toString());
        }

        list = (LinearLayout)findViewById(R.id.list);


        category = null;
        String item = null;
        String[] items = null;
        String[] items2 = null;

        try {
            category = intent.getExtras().getString("category");
        } catch(Exception e){
        }

        if(category.equals("bag") || category.equals("cap") || category.equals("shoes"))
            item = category;
        else if(category.equals("outer"))
            items = outer;
        else if(category.equals("lower"))
            items = lower;
        else {          // upper이 넘어온다면 상의와 원피스 같이 보여주기
            items = upper;
            items2 = onepeace;
        }
/*
        try {
            items = intent.getExtras().getStringArray("items");
        } catch(Exception e){
        }

        try {
            items2 = intent.getExtras().getStringArray("items2");
        } catch(Exception e){
        }
*/

        // bag, shoes, cap 이라면 category=bag, item=bag 보낼것.
        if(item != null) {
            Log.d("받아온 값1", item);
            Button btn = new Button(this);
            int i = 1;
            btn.setId(i);
            btn.setText(item);
            list.addView(btn);

            final String finalItem = item;
            Log.d("folder로 보낼값", item);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ItemsetActivity.this, ShowItemsActivity.class);
                    // 다음 뷰에 set 클래스 그대로 넘김
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("set", (Serializable) set);
                    intent.putExtras(bundle);

                    intent.putExtra("category", category);
                    intent.putExtra("folder", finalItem);       // 현재의 item 이름(bag, sleeveless, mini_skirt ...)
                    startActivity(intent);
                    //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
                }
            });
        }

        // outer, lower, upper이라면 category=outer, item=cardigan 보낼 것.
        if(items != null) {
            for(int i=0; i<items.length; i++) {
                Log.d("받아온 값2", items[i]);
                Button btn = new Button(this);
                btn.setId(i);
                btn.setText(items[i]);
                list.addView(btn);

                final String finalItem = items[i];
                Log.d("folder로 보낼값", items[i]);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ItemsetActivity.this, ShowItemsActivity.class);
                        // 다음 뷰에 set 클래스 그대로 넘김
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("set", (Serializable) set);
                        intent.putExtras(bundle);

                        intent.putExtra("category", category);
                        intent.putExtra("folder", finalItem);       // 현재의 item 이름(bag, sleeveless, mini_skirt ...)
                        startActivity(intent);
                        //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
                    }
                });

            }
        }

        // onepeace라면 category 따로 설정해줘야함. category=onepeace, item=long_arm_mini_onepeace 보낼것.
        if(items2 != null) {
            for(int i=0; i<items2.length; i++) {
                Log.d("받아온 값3", items2[i]);
                Button btn = new Button(this);
                btn.setId(i);
                btn.setText(items2[i]);
                list.addView(btn);

                final String finalItem = items2[i];
                Log.d("folder로 보낼값", items2[i]);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ItemsetActivity.this, ShowItemsActivity.class);
                        // 다음 뷰에 set 클래스 그대로 넘김
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("set", (Serializable) set);
                        intent.putExtras(bundle);

                        intent.putExtra("category", "onepeace");
                        intent.putExtra("folder", finalItem);       // 현재의 item 이름(bag, sleeveless, mini_skirt ...)
                        startActivity(intent);
                        //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
                    }
                });
            }
        }
    }
}

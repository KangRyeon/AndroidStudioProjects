package com.example.mycloset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ItemsetActivity extends AppCompatActivity {

    LinearLayout list;

    SharedPreferences sf;
    SharedPreferences.Editor editor;
    String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemset);

        String[] outer = {"cardigan", "jacket", "padding", "coat", "jumper", "hood zipup"};
        String[] upper = {"hood_T", "long_T", "pola", "shirt", "short_T", "sleeveless", "vest"};
        String[] lower = {"long_pants", "short_pants", "Leggings", "mini_skirt", "long_skirt"};
        String[] onepeace = {"long_arm_mini_onepeace", "long_arm_long_onepeace", "short_arm_mini_onepeace", "short_arm_long_onepeace"};


        String[] items = null;
        String[] items2 = null;
        /*
        // 저장한 sfile 찾음
        sf = getSharedPreferences("sfile",MODE_PRIVATE);
        category = sf.getString("category","");       // bag, shose, cap, outer, lower, upper 가져올 것.
        Log.d("쉐얼드 프리퍼런스 값 가져옴", category);

        // 저장된거 빼내면 지움
        editor = sf.edit();
        editor.remove("category");
        editor.commit();



        // 선택된 버튼에 따라서 item, items, items2 초기화
        if (category == "bag" || category == "shose" || category == "cap")
            item = category;
        else if (category == "outer")
            items = outer;
        else if (category == "lower")
            items = lower;
        else {      // 상의와 원피스는 한꺼번에 가져올 것.
            items = upper;
            items2 = onepeace;
        }
*/




        list = (LinearLayout)findViewById(R.id.list);

        Intent intent = getIntent();
        String item = null;

        try {
            item = intent.getExtras().getString("item");
        } catch(Exception e){
        }

        try {
            items = intent.getExtras().getStringArray("items");
        } catch(Exception e){
        }

        try {
            items2 = intent.getExtras().getStringArray("items2");
        } catch(Exception e){
        }


        // bag, shose, cap 이라면 category=bag, item=bag 보낼것.
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
                    //editor.putString("category", category);
                    //editor.putString("item", finalItem);
                    //editor.commit();
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
                        //editor.putString("category", category);
                        //editor.putString("item", finalItem);
                        //editor.commit();
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
                        //editor.putString("category", "onepeace");
                        //editor.putString("item", finalItem);
                        //editor.commit();
                        intent.putExtra("folder", finalItem);       // 현재의 item 이름(bag, sleeveless, mini_skirt ...)
                        startActivity(intent);
                        //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
                    }
                });
            }
        }

    }

}

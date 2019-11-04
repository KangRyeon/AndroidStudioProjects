package com.example.mycloset;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.mycloset.dto.FashionSetDTO;

import java.io.Serializable;

public class ItemsetActivity extends AppCompatActivity {

    GridLayout list;
    Button add_clothes_btn;
    FashionSetDTO set;
    String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemset);

        String[] outer = {"cardigan", "jacket", "padding", "coat", "jumper", "hood zipup"};
        String[] upper = {"hood_T", "long_T", "pola", "shirt", "short_T", "sleeveless", "vest"};
        int upper_draw[] = {R.drawable.hood_t, R.drawable.long_t, R.drawable.long_t, R.drawable.shirt_t, R.drawable.short_t, R.drawable.sleeveless, R.drawable.vest};
        String[] lower = {"long_pants", "short_pants", "leggings", "mini_skirt", "long_skirt"};
        String[] onepeace = {"long_arm_mini_onepeace", "long_arm_long_onepeace", "short_arm_mini_onepeace", "short_arm_long_onepeace"};
        String[] etc = {"bag", "cap", "shoes"};

        // 이전 뷰에 있던 세트 정보 가져옴
        Intent intent = getIntent();
        try {
            set = (FashionSetDTO) intent.getSerializableExtra("set");
        } catch(Exception e){
            Log.d("오류", "못가져옴");
            Log.d("오류",e.toString());
        }

        list = (GridLayout)findViewById(R.id.list);
        add_clothes_btn = (Button)findViewById(R.id.add_clothes_btn);

        // 옷 추가 버튼 클릭시 - 새로운 액티비티가 틀어짐. -> 원피스 upper 어케되는지 보기
        add_clothes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemsetActivity.this, SelectCameraGalleryActivity.class);
                intent.putExtra("category",category);
                startActivity(intent);
                //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
            }
        });

        category = null;
        String item = null;
        String[] items = null;
        String[] items2 = null;

        try {
            category = intent.getExtras().getString("category");
        } catch(Exception e){
        }

        if(category.equals("etc"))
            items = etc;
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
/*
        // bag, shoes, cap 이라면 category=etc, folder=bag 보낼것.
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


 */
        // outer, lower, upper이라면 category=outer, folder=cardigan 보낼 것.
        // etc라면 category=etc, folder=bag 으로 보낼것
        if(items != null) {
            for(int i=0; i<items.length; i++) {
                Log.d("받아온 값2", items[i]);
                Button btn = new Button(this);
                btn.setId(i);
                btn.setText(items[i]);
                Log.d("폰트", ""+getAssets());
                btn.setTypeface(Typeface.createFromAsset(getAssets(), "multilingual_hand.ttf"));

                // 버튼에 이미지 넣어줌.
                GridLayout.LayoutParams p = new GridLayout.LayoutParams();
                p.width=350;
                p.height=350;
                btn.setLayoutParams(p);

                btn.setBackground(ContextCompat.getDrawable(this, upper_draw[i]));
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
/*
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

 */
    }
}

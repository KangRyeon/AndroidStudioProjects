package com.example.mycloset;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class ItemsetActivity extends AppCompatActivity {

    LinearLayout list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_itemset);

        list = (LinearLayout)findViewById(R.id.list);

        Intent intent = getIntent();
        String item = null;
        String[] items = null;
        String[] items2 = null;
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

        if(item != null) {
            Log.d("받아온 값1", item);
            Button btn = new Button(this);
            int i = 1;
            btn.setId(i);
            btn.setText(item);
            list.addView(btn);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ItemsetActivity.this, ShowItemsActivity.class);
                    startActivity(intent);
                    //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
                }
            });
        }

        if(items != null) {
            for(int i=0; i<items.length; i++) {
                Log.d("받아온 값2", items[i]);
                Button btn = new Button(this);
                btn.setId(i);
                btn.setText(items[i]);
                list.addView(btn);
            }
        }

        if(items2 != null) {
            for(int i=0; i<items2.length; i++) {
                Log.d("받아온 값3", items2[i]);
                Button btn = new Button(this);
                btn.setId(i);
                btn.setText(items2[i]);
                list.addView(btn);
            }
        }

    }

}

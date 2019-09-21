package com.example.mycloset;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button add_clothes_btn;
    Button closet_btn;
    Button fashionsetlist_btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_clothes_btn = (Button)findViewById(R.id.add_clothes_btn);
        closet_btn = (Button)findViewById(R.id.closet_btn);
        fashionsetlist_btn = (Button)findViewById(R.id.fashionsetlist_btn);

        // 옷 추가 버튼 클릭시 - 새로운 액티비티가 틀어짐.
        add_clothes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectCameraGalleryActivity.class);
                startActivity(intent);
                //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
            }
        });

        // 옷장 버튼 클릭시
        closet_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ClosetActivity.class);
                startActivity(intent);
                //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
            }
        });

        // 내가 만든 세트 버튼 클릭시
        fashionsetlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FashionSetListActivity.class);
                startActivity(intent);
                //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
            }
        });
    }
}

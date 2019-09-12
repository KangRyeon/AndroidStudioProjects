package com.example.mycloset;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.mycloset.R;

public class MainActivity extends AppCompatActivity {

    Button add_clothes_btn;
    Button closet_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_clothes_btn = (Button)findViewById(R.id.add_clothes_btn);
        closet_btn = (Button)findViewById(R.id.closet_btn);;
        // 다음화면 버튼 클릭시 - 새로운 액티비티가 틀어짐.
        add_clothes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectCameraGalleryActivity.class);
                startActivity(intent);
                //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
            }
        });

        closet_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ClosetActivity.class);
                startActivity(intent);
                //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
            }
        });
    }
}

package com.example.makers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button add_clothes_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        add_clothes_btn = (Button)findViewById(R.id.add_clothes_btn);

        // 다음화면 버튼 클릭시 - 새로운 액티비티가 틀어짐.
        add_clothes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectCameraGalleryActivity.class);
                startActivity(intent);
                //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
            }
        });
    }

    public void ClosetClicked(View v){
        Intent myIntent = new Intent(getApplicationContext(),Sub0Activity.class);
        startActivity(myIntent);
    }

}

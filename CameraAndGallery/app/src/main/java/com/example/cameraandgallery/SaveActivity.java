package com.example.cameraandgallery;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class SaveActivity extends AppCompatActivity {

    Button save_btn;
    ImageView img_view;

    Bitmap bitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        save_btn = (Button)findViewById(R.id.save_btn);
        img_view = (ImageView)findViewById(R.id.img_view);


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        options.inJustDecodeBounds = false;
        String files = "/data/data/com.example.cameraandgallery/cache"+"/test.jpg";
        bitmap = BitmapFactory.decodeFile(files, options);
        image_handler.sendEmptyMessage(0);

        // 저장하기 버튼 클릭시 - startActivityForResult 끝난 후 onActivityResult 실행됨.
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("서버로 보내서 저장하는거까지 해야함","서버");
                Thread th = new Thread();
                th.start();
            }
        });
    }

    // handler = 백그라운드 thread에서 전달된 메시지 처리(UI변경 등을 여기서 해줌.)
    Handler image_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            img_view.setImageBitmap(bitmap);                      //백그라운드로 갤러리에서 골라온 사진을 이미지버튼에 뿌림. //이미지뷰도 가능
        }
    };


}

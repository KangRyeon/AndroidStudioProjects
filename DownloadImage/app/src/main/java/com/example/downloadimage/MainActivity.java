package com.example.downloadimage;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements Runnable{

    Button button1;
    ImageView img1;
    Bitmap bitmap;// 비트맵 객체

    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            // 서버에서 받아온 이미지를 핸들러를 경유해 이미지뷰에 비트맵 리소스 연결
            img1.setImageBitmap(bitmap);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button)findViewById(R.id.download_btn);
        img1 = (ImageView)findViewById(R.id.img_view);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("버튼눌림", "버튼눌림");
                Thread th =new Thread(MainActivity.this);
                // 동작 수행
                th.start();
            }
        });

    }

    // 백그라운드 스레드
    @Override
    public void run() {
        // http://192.168.0.127/resources/images/like1.png
        URL url =null;
        try{
            // 스트링 주소를 url 형식으로 변환
            url =new URL("http://192.168.55.193:8080/downloadImageFile");

            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);

            Log.d("비트맵으로 받아옴","비트맵으로 받아옴");
            Log.d("가로세로", bitmap.getWidth()+", "+bitmap.getHeight());
            is.close();
            conn.disconnect();

            handler.sendEmptyMessage(0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

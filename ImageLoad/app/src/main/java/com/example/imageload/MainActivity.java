package com.example.imageload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private Button chooseimage_btn;
    private ImageView img_view;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chooseimage_btn = (Button) findViewById(R.id.chooseimage_btn);
        img_view = (ImageView) findViewById(R.id.imageView);
        //files=getApplicationContext().getFilesDir().getPath().toString() +"/test.txt";

        // 이미지 고르는 버튼 : startActivityForResult 로 이미지 가져오고, onActivityResult 실행
        chooseimage_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent 생성
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*"); //이미지만 보이게
                //Intent 시작 - 갤러리앱을 열어서 원하는 이미지를 선택할 수 있다.
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
            }
        });
    }

    // 이미지 고른 후에 size 변경, 내부저장소의 files 밑에 test.jpg로 저장
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()); // 갤러리에서 비트맵 형태로 받음
                Log.d("이미지 가로", "" + bitmap.getWidth());
                Log.d("이미지 세로", "" + bitmap.getHeight());

                // 원본이미지를 width, height 1024이하로 맞춤.
                // 가로, 세로중에 큰것을 1024으로 하고, 비율에 따라서 크기 맞춤
                int image_max = 1024;
                if (bitmap.getWidth() > image_max || bitmap.getHeight() > image_max) {
                    if (bitmap.getWidth() >= bitmap.getHeight()) {
                        int new_height = (int) (bitmap.getHeight() * ((float) image_max / bitmap.getWidth()));
                        Log.d("이미지 새로운 가로", "" + (image_max / bitmap.getWidth()));
                        bitmap = Bitmap.createScaledBitmap(bitmap, image_max, new_height, true);
                        Log.d("이미지 새로운 세로", "" + bitmap.getHeight());
                    } else {
                        int new_width = (int) (bitmap.getWidth() * ((float) image_max / bitmap.getHeight()));
                        bitmap = Bitmap.createScaledBitmap(bitmap, new_width, image_max, true);
                    }
                }
                Log.d("이미지 새로운 가로", "" + bitmap.getWidth());
                Log.d("이미지 새로운 세로", "" + bitmap.getHeight());

                // 이미지를 회전시킨다.
                int rotate=90;
                Log.d("회전각도", ""+rotate);
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate); // 회전한 각도 입력
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

/*
                // 일단 files라는 폴더 먼저 생성함
                File newFolder = new File("/data/data/com.example.imageload/files/");
                try {
                    newFolder.mkdir();
                    Log.d("폴더생성", "폴더생성 성공");
                } catch (Exception e) {
                    Log.d("폴더생성", "폴더생성이 이미 되어있거나 실패");
                }

                // 빈파일 생성해 compress 함수 사용해 그 파일에 비트맵을 저장함.
                File tempFile = new File("/data/data/com.example.imageload/files/", "test.jpg");
                try {
                    tempFile.createNewFile();
                    FileOutputStream out = new FileOutputStream(tempFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();
                } catch (IOException e) {
                    Log.e("파일생성오류", "파일 없거나 못만듦");
                }
                Log.d("파일", "파일이 저장됨");

 */
                image_handler.sendEmptyMessage(0);

            } catch (Exception e) {
                Log.e("이미지불러오기", "불러오기오류");
            }
        }
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




package com.example.cameraandgallery;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SelectCameraGalleryActivity extends AppCompatActivity {

    Button camera_btn;
    Button gallery_btn;
    public final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectcameragallery);

        camera_btn = (Button)findViewById(R.id.camera_btn);
        gallery_btn = (Button)findViewById(R.id.gallery_btn);

        // 체크해야할 퍼미션
        int cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);    // PERMISSION_GRANTED 여야 권한설정된것.
        int writeExternalStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        // 어플 실행시 먼저 체크하고 실행됨.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // 퍼미션 체크가 PERMISSION_GRANTED 여야 권한설정이 완료된 것.
            if (cameraPermission == PackageManager.PERMISSION_GRANTED && writeExternalStoragePermission == PackageManager.PERMISSION_GRANTED) {
                Log.d("권한", "권한 설정 완료");
            } else {
                Log.d("권한", "권한 설정 요청");
                ActivityCompat.requestPermissions(SelectCameraGalleryActivity.this
                        , new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , 1);
            }
        }

        // 카메라 버튼 클릭시 - startActivityForResult 끝난 후 onActivityResult 실행됨.
        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 1);
            }
        });

        // 갤러리 버튼 클릭시 - startActivityForResult 끝난 후 onActivityResult 실행됨.
        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent 생성
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*"); //이미지만 보이게
                //Intent 시작 - 갤러리앱을 열어서 원하는 이미지를 선택할 수 있다.
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
            }
        });

    }

    // 사진 고르고 나서 실행되는 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // requestCode == 1 : 카메라
        if(requestCode == 1){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Log.d("결과1", "데이터 가져옴 뭔가 해야함");
        }

        // requestCode == 2 : 갤러리
        if(requestCode == 2) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()); // 갤러리에서 비트맵 형태로 받음
                Log.d("결과2", "데이터 가져옴 뭔가 해야함");

                String files = "/data/data/com.example.cameraandgallery/cache"+"/test.jpg";
                // "test.txt"라는 파일을 생성
                File file = new File(files);
                try {
                    file.createNewFile();
                    FileOutputStream out = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();
                } catch (IOException e) {
                    Log.e("파일생성오류", "파일 없거나 못만듦");
                }


                Intent intent = new Intent(SelectCameraGalleryActivity.this, SaveActivity.class);
                startActivity(intent);
                //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //img_view.setImageBitmap(bitmap);
    }

    // 권한 요청 결과 받는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("권한요청결과", "결과받는곳...");
    }
}

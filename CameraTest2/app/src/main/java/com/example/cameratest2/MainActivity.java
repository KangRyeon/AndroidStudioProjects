package com.example.cameratest2;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class MainActivity extends Activity {
    Button camera_btn;
    ImageView img_view;

    Bitmap bitmap; // 사진 보여질 bitmap
    File tempFile;
    Uri photoUri;  // 사진 uri 가짐(tempFile의 위치)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        camera_btn = (Button)findViewById(R.id.camera_btn);
        img_view = (ImageView)findViewById(R.id.img_view);

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
                ActivityCompat.requestPermissions(MainActivity.this
                        , new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}
                        , 1);
            }
        }

        // 카메라 버튼 클릭시 - startActivityForResult 끝난 후 onActivityResult 실행됨.
        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

                // 이름이 겹치지 않는 빈파일을 생성하고, 그 파일의 uri를 얻어 사진을 찍은 뒤 그 uri에 저장한다.
                try {
                    tempFile = File.createTempFile("test", ".jpg", new File("/data/data/com.example.cameratest2/cache"));
                    Log.d("파일생성","성공");
                    Log.d("사진찍기 전 파일크기", ""+tempFile.length());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("파일생성","실패");
                }

                // 빈파일이 제대로 생성되면 사진을 찍은 뒤 그 사진 자체를 그 uri에 저장한다.
                if (tempFile != null){
                    // Uri photoUri = Uri.fromFile(tempFile); 이전버전에서 사용하던 것.
                    photoUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.cameratest2.fileprovider", tempFile);
                    Log.d("저장될 곳",photoUri.toString());
                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, 1);
                }
            }
        });
    }

    // 사진 고르고 나서 실행되는 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d("파일크기", ""+tempFile.length());
        if(tempFile.length() > 0) {    // tempFile에 제대로 저장되면 0보다 값이 클것.
            // bitmap에 url로 받기
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
            } catch (Exception e) {
                Log.d("실패", "url로 bitmap불러오기 실패");
            }
        }
        else {                          // tempFile에 생성 안되면 받은 data로 한다.
            bitmap = (Bitmap) data.getExtras().get("data");
        }
        img_view.setImageBitmap(bitmap);
    }

    // 권한 요청 결과 받는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("권한요청결과", "결과");
        // 권한요청 결과 출력.
        for(int i=0; i<grantResults.length; i++){
            if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                Log.d("권한받음", "Permission: " + grantResults[i]);
            }
            else
                Log.d("권한못받음", "Permission: " + grantResults[i]);
        }
    }
}
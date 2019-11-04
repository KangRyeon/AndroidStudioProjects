package com.example.ootd;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class SelectCameraGalleryActivity extends AppCompatActivity {

    Button camera_btn;
    Button gallery_btn;

    File tempFile;
    String tempFilePath;
    String tempFileName;
    String category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectcameragallery);

        // 어플 실행시 먼저 체크하고 실행됨.
        checkPermission();

        Intent intent = getIntent();
        try {
            category = intent.getStringExtra("category");
            Log.d("SelectCameraGalleryActivity에서 받은 category", category);
        } catch(Exception e){
            Log.d("SelectCameraGalleryActivity","가져온게없음");
        }

        camera_btn = (Button)findViewById(R.id.camera_btn);
        gallery_btn = (Button)findViewById(R.id.gallery_btn);

        tempFilePath = getApplicationContext().getCacheDir().toString();    // "/data/data/com.example.cameraandgallery/cache" (캐시폴더위치)
        tempFileName = "/test.jpg";                                       // "/data/data/com.example.cameraandgallery/cache/test.jpg" (캐시폴더위치에 저장될 test.jpg)

        // tempFile을 미리 생성. (캐시폴더에 test.jpg)
        tempFile = createTempFile(tempFilePath, tempFileName);

        // 카메라 버튼 클릭시 - startActivityForResult 끝난 후 onActivityResult 실행됨.
        camera_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Log.d("카메라","카메라열림");

                if (tempFile != null){  // tempFile이 제대로 생성되었다면 그 파일에 찍은사진 저장.
                    //Uri photoUri = Uri.fromFile(tempFile);
                    Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.mycloset.fileprovider", tempFile);
                    Log.d("저장된곳",photoUri.toString());
                    intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, (long) (1024*768));
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                    startActivityForResult(intent, 1);
                }

            }
        });

        // 갤러리 버튼 클릭시 - startActivityForResult 끝난 후 onActivityResult 실행됨.
        gallery_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 2);
            }
        });

    }

    // 사진 고르고 나서 실행되는 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK) {

            // requestCode == 1 : 카메라
            if (requestCode == 1) {
                Log.d("결과1-카메라", "카메라 데이터 가져옴");
                Log.d("데이터저장:",tempFile.toString());

                // cache/test.jpg bitmap으로 가져와서 1024*1024로 변경하기
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = false;
                String files = getApplicationContext().getCacheDir()+"/test.jpg";
                Bitmap bitmap = BitmapFactory.decodeFile(files, options);

                int maxsize = 1024;
                int x,y;
                if(bitmap.getWidth() > bitmap.getHeight()) {
                    x = maxsize;
                    y = bitmap.getHeight() * maxsize / bitmap.getWidth();
                }
                else{
                    y = maxsize;
                    x = bitmap.getWidth() * maxsize / bitmap.getHeight();
                }

                bitmap = Bitmap.createScaledBitmap(bitmap, x, y, true);

                // 이미지를 회전시킨다.
                int rotate=90;
                Log.d("회전각도", ""+rotate);
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate); // 회전한 각도 입력
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                try {
                    FileOutputStream out = new FileOutputStream(tempFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();
                } catch (IOException e) {
                    Log.e("파일생성오류", "파일 없거나 못만듦");
                }

                Intent intent = new Intent(SelectCameraGalleryActivity.this, SaveActivity.class);
                intent.putExtra("category",category);
                startActivity(intent);
                finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
            }

            // requestCode == 2 : 갤러리
            if (requestCode == 2) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()); // 갤러리에서 비트맵 형태로 받음

                    int maxsize = 1024;
                    int x,y;
                    if(bitmap.getWidth() > bitmap.getHeight()) {
                        x = maxsize;
                        y = bitmap.getHeight() * maxsize / bitmap.getWidth();
                    }
                    else{
                        y = maxsize;
                        x = bitmap.getWidth() * maxsize / bitmap.getHeight();
                    }

                    bitmap = Bitmap.createScaledBitmap(bitmap, x, y, true);
                    Log.d("결과2-갤러리", "갤러리 데이터 가져옴");

                    // 이미지를 회전시킨다.
                    int rotate=90;
                    Log.d("회전각도", ""+rotate);
                    Matrix matrix = new Matrix();
                    matrix.postRotate(rotate); // 회전한 각도 입력
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                    // 내부저장소의 캐시폴더에 저장할 것.
                    try {
                        FileOutputStream out = new FileOutputStream(tempFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                        out.close();
                    } catch (IOException e) {
                        Log.e("파일생성오류", "파일 없거나 못만듦");
                    }

                    Intent intent = new Intent(SelectCameraGalleryActivity.this, SaveActivity.class);
                    intent.putExtra("category",category);
                    startActivity(intent);
                    finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 캐시폴더에 빈파일을 생성함.
    public File createTempFile(String filepath, String filename) {
        File file = null;
        try {
            //file = new File("/data/data/com.example.cameraandgallery/cache/"+"test.jpg");
            file = new File(filepath+tempFileName);
            Log.d("파일생성","성공");
            Log.d("캐시파일 경로", file.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("파일생성","실패");
        }
        return file;
    }

    // 퍼미션 체크
    public void checkPermission() {
        // 체크해야할 퍼미션
        int cameraPermission = checkSelfPermission(Manifest.permission.CAMERA);    // PERMISSION_GRANTED 여야 권한설정된것.
        int writeExternalStoragePermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

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
    }

    // 권한 요청 결과 받는 함수
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("권한요청결과", "결과받는곳...");
    }
}

package com.example.createfile;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.createfile.R;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends AppCompatActivity {

    // 버튼 가져옴
    private Button newfile_btn;

    // 저장할 폴더경로, 파일이름
    String folder_name;     // 폴더경로 및 이름
    String files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        newfile_btn = (Button) findViewById(R.id.newfile_btn);
        //folder_name = getApplicationContext().getFilesDir().getPath().toString() + "/test.txt";
        //getApplicationContext().getFilesDir().getPath().toString() +"/test.txt";
        folder_name = "/data/data/com.example.createfile/files";       // 파일이 저장될 폴더(files라는 폴더)
        files = folder_name + "/test.txt";                                  // 저장될 파일이름

        // 파일생성 버튼이 눌렸을 때
        newfile_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // data/data/패키지이름 밑에 "files" 라는 폴더 먼저 생성하고 "test.txt" 생성

                // "files"라는 폴더 생성
                File newFolder = new File(folder_name);
                try {
                    newFolder.mkdir();
                    Log.d("폴더생성", "폴더생성 성공");
                } catch (Exception e) {
                    Log.d("폴더생성", "폴더생성이 이미 되어있거나 실패");
                }

                // "test.txt"라는 파일을 생성
                File file = new File(files);
                try {
                    Log.d("파일생성 : ", files);
                    FileOutputStream fos = new FileOutputStream(file);
                    String str = "생성한 파일에 이렇게 적게 됩니다.";
                    fos.write(str.getBytes());
                    fos.close(); //스트림 닫기
                    Toast.makeText(MainActivity.this, "저장되었습니다.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
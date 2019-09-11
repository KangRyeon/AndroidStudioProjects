package com.example.cameraandgallery;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SaveActivity extends AppCompatActivity implements Runnable {

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
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        String files = "/data/data/com.example.cameraandgallery/cache"+"/test.jpg";
        bitmap = BitmapFactory.decodeFile(files, options);
        image_handler.sendEmptyMessage(0);

        // 저장하기 버튼 클릭시 - startActivityForResult 끝난 후 onActivityResult 실행됨.
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("서버로 보내서 저장하는거까지 해야함","서버");
                Thread th = new Thread(SaveActivity.this);
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

    // test.jpg 이미지를 서버로 보냄, 그 이미지에 해당하는 딥러닝 결과 받아옴
    @Override
    public void run() {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        StringBuffer sb = new StringBuffer();

        Log.d("버튼 눌림", "버튼 눌림");
        try {
            String urlString = "http://192.168.55.193:8080/uploadImage";
            String params = "";
            String sendFileName = "/data/data/com.example.cameraandgallery/cache/test.jpg";    // cache 폴더에 카메라, 갤러리에서 고른 이미지 있음.
            URL connectUrl = new URL(urlString);

            // 이미지를 서버로 보내고 딥러닝결과받기
            try {
                File sourceFile = new File(sendFileName);
                DataOutputStream dos;

                if (!sourceFile.isFile()) {
                    Log.e("uploadFile", "Source File not exist :" + sendFileName);
                } else {
                    FileInputStream mFileInputStream = new FileInputStream(sourceFile);
                    // open connection
                    HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();
                    conn.setDoInput(true);
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("new_file", sendFileName);

                    // write data
                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"new_file\";filename=\"" + sendFileName + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    int bytesAvailable = mFileInputStream.available();
                    int maxBufferSize = 1024 * 1024;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byte[] buffer = new byte[bufferSize];
                    int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                    // read image
                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = mFileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    mFileInputStream.close();

                    dos.flush(); // finish upload...

                    /*
                    if (conn.getResponseCode() == 200) {
                        InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                        BufferedReader reader = new BufferedReader(tmp);
                        StringBuffer stringBuffer = new StringBuffer();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuffer.append(line);
                        }
                    }
                    */
                    mFileInputStream.close();
                    dos.close();

                    Log.d("파일", "파일 다 전송함 딥러닝결과 받아오기 시작");
                    // 딥러닝 결과 받기
                    if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
                        Log.d("연결", "연결이 제대로 됨");
                        while (true) {
                            String line = br.readLine();
                            if (line == null)
                                break;
                            sb.append(line + "\n");
                        }
                        Log.d("받은것", "오잉"+sb.toString());
                        br.close();
                    }
                    conn.disconnect();

                    // 받아온 source를 JSONObject로 변환한다.
                    JSONObject jsonObj = new JSONObject(sb.toString());
                    JSONArray jArray = (JSONArray) jsonObj.get("deep_result");

                    // 0번째 JSONObject를 받아옴
                    JSONObject row = jArray.getJSONObject(0);
                    Log.d("받아온값1 : ", row.getString("result"));


                    // files 밑에 hood_T 같은 폴더 생성, hood_T.jpg로 저장
                    // 받아온 결과에 따라 이미지 저장하기
                    String folder_name = "/data/data/com.example.cameraandgallery/files/"+row.getString("result"); // /files/hood_T/ 폴더생ㅇ성ㅇ
                    String saveFileName = folder_name + "/" + row.getString("result") + ".jpg";     // /files/받아온이름.jpg(/files/hood_T.jpg)

                    // data/data/패키지이름 밑에 "files" 라는 폴더 먼저 생성하고 "hood_T.jpg" 생성
                    // "files"라는 폴더 생성
                    File newFolder = new File(folder_name);
                    try {
                        newFolder.mkdirs();
                        Log.d("폴더생성", "폴더생성 성공");
                    } catch (Exception e) {
                        Log.d("폴더생성", "폴더생성이 이미 되어있거나 실패");
                    }

                    // "hood_T.jpg"라는 파일을 생성
                    File file = new File(saveFileName);
                    try {
                        Log.d("파일다시저장 : ", saveFileName);
                        File testFile = new File(sendFileName);     // cache/test.jpg
                        File resultFile = new File(saveFileName);   // files/hood_T.jpg

                        if(testFile.renameTo(resultFile)){
                            //Toast.makeText(SaveActivity.this, "저장 성공", Toast.LENGTH_SHORT).show();
                            Log.d("파일다시저장", "test.jpg를 다른이름으로 저장 성공함.");
                        }else{
                            //Toast.makeText(SaveActivity.this, "저장 실패", Toast.LENGTH_SHORT).show();
                            Log.d("파일다시저장", "test.jpg를 다른이름으로 저장 실패함");
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
            // TODO: handle exception
        }
    }

}

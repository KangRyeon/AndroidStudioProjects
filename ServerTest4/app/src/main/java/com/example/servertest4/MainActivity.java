package com.example.servertest4;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.servertest4.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements Runnable {

    private Button send_btn;
    private Button chooseimage_btn;
    private TextView txt_view;
    private ImageView img_view;
    String str;
    Bitmap bitmap;

    //안드로이드 내 저장 파일 이름
    String files;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send_btn = (Button) findViewById(R.id.send_btn);
        chooseimage_btn = (Button) findViewById(R.id.chooseimage_btn);
        txt_view = (TextView) findViewById(R.id.txt_view);
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

        // 보내기 버튼을 눌렀을때 thread 실행 -> Override한 run() 함수를 실행시킴. -> run()함수는 실행할 것을 한 뒤 handler 실행
        send_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread th = new Thread(MainActivity.this);
                th.start();
            }
        });
    }

    // 이미지 고른 후에 size 변경, 내부저장소의 files 밑에 test.jpg로 저장
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData()); // 갤러리에서 비트맵 형태로 받음
                Log.d("이미지 가로",""+bitmap.getWidth());
                Log.d("이미지 세로",""+bitmap.getHeight());

                // 원본이미지를 width, height 1024이하로 맞춤.
                // 가로, 세로중에 큰것을 1024으로 하고, 비율에 따라서 크기 맞춤
                int image_max = 1024;
                if(bitmap.getWidth() > image_max || bitmap.getHeight() > image_max) {
                    if (bitmap.getWidth() >= bitmap.getHeight()) {
                        int new_height = (int) (bitmap.getHeight() * ((float) image_max / bitmap.getWidth()));
                        Log.d("이미지 새로운 가로",""+(image_max / bitmap.getWidth()));
                        bitmap = Bitmap.createScaledBitmap(bitmap, image_max, new_height, true);
                        Log.d("이미지 새로운 세로",""+bitmap.getHeight());
                    } else {
                        int new_width = (int) (bitmap.getWidth() * ((float) image_max / bitmap.getHeight()));
                        bitmap = Bitmap.createScaledBitmap(bitmap, new_width, image_max, true);
                    }
                }

                Log.d("이미지 새로운 가로",""+bitmap.getWidth());
                Log.d("이미지 새로운 세로",""+bitmap.getHeight());
                // 빈파일 생성해 compress 함수 사용해 그 파일에 비트맵을 저장함.
                File tempFile = new File("/data/data/com.example.servertest4/files/", "test.jpg");
                try {
                    tempFile.createNewFile();
                    FileOutputStream out = new FileOutputStream(tempFile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();
                } catch (IOException e) {
                    Log.e("파일생성오류", "파일 없거나 못만듦");
                }
                Log.d("파일", "파일이 저장됨");
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
            String fileName = "/data/data/com.example.servertest4/files/test.jpg";
            URL connectUrl = new URL(urlString);

            // 이미지를 서버로 보내고 딥러닝결과받기
            try {
                File sourceFile = new File(fileName);
                DataOutputStream dos;

                if (!sourceFile.isFile()) {
                    Log.e("uploadFile", "Source File not exist :" + fileName);
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
                    conn.setRequestProperty("new_file", fileName);

                    // write data
                    dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"new_file\";filename=\"" + fileName + "\"" + lineEnd);
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

                    str = "다전송함, 받아온값 : ";
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

                    str = "다전송함, 받아온값 : " + row.getString("result");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
            // TODO: handle exception
        }
        //핸들러에게 메시지 요청
        handler.sendEmptyMessage(0);
    }

    // handler = 백그라운드 thread에서 전달된 메시지 처리(UI변경 등을 여기서 해줌.)
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            txt_view.setText(str);
        }
    };
}


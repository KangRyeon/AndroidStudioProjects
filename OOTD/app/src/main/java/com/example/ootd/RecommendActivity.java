package com.example.ootd;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.ootd.dto.ClothesDTO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RecommendActivity extends AppCompatActivity implements Runnable {
    // 캐시폴더의 test.jpg이미지를 가져와 보여주고, 저장하기 버튼을 누르면 서버로 이미지를 보내고 딥러닝 결과 가져옴
    Button save_btn;
    Button rotate_btn;
    ImageView img_view;
    TextView txt_view;

    Bitmap bitmap;
    String str;
    JSONObject jsonObj;
    String category;
    ClothesDTO clothes[];
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        save_btn = (Button)findViewById(R.id.save_btn);
        rotate_btn = (Button)findViewById(R.id.rotate_btn);
        img_view = (ImageView)findViewById(R.id.img_view);
        txt_view = (TextView)findViewById(R.id.txt_view);

        clothes = new ClothesDTO[2];

        Intent intent = getIntent();
        try {
            category = intent.getStringExtra("category");
            Log.d("RecommendActivity에서 받은 category", category);
            txt_view.setText("category : "+category);
        } catch(Exception e){
            Log.d("RecommendActivity","가져온게없음");
        }

        str = "";

        // 어플이 시작되면 캐시에 저장되어있는 test.jpg 이미지를 가져와 회전시킨다.
        showCacheImage();

        // 회전시킨 bitmap 이미지를 ImageView로 보여준다.
        image_handler.sendEmptyMessage(0);

        // 분석하기 버튼 클릭시 - startActivityForResult 끝난 후 onActivityResult 실행됨.
        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("서버로 보내서 저장하는거까지 해야함","서버");
                Thread th = new Thread(RecommendActivity.this);
                th.start();
                //finish();
            }
        });

        rotate_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rotate=90;
                Log.d("회전각도", ""+rotate);
                Matrix matrix = new Matrix();
                matrix.postRotate(rotate); // 회전한 각도 입력
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                image_handler.sendEmptyMessage(0);

                String files = getApplicationContext().getCacheDir()+"/test.jpg";
                // 내부저장소의 캐시폴더에 저장할 것.
                try {
                    FileOutputStream out = new FileOutputStream(files);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                    out.close();
                } catch (IOException e) {
                    Log.e("파일생성오류", "파일 없거나 못만듦");
                }
            }
        });
    }

    public void showCacheImage() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;
        String files = getApplicationContext().getCacheDir()+"/test.jpg";
        bitmap = BitmapFactory.decodeFile(files, options);

        // 이미지를 회전시킨다.
        int rotate=90;
        Log.d("회전각도", ""+rotate);
        Matrix matrix = new Matrix();
        matrix.postRotate(rotate); // 회전한 각도 입력
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    // handler = 백그라운드 thread에서 전달된 메시지 처리(UI변경 등을 여기서 해줌.)
    Handler image_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            img_view.setImageBitmap(bitmap);                      //백그라운드로 갤러리에서 골라온 사진을 이미지버튼에 뿌림. //이미지뷰도 가능
        }
    };

    Handler loading_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            pd = ProgressDialog.show(RecommendActivity.this, "로딩중", "페이지 로딩 중입니다...");
        }
    };
    // test.jpg 이미지를 서버로 보냄, 그 이미지에 해당하는 딥러닝 결과 받아옴
    @Override
    public void run() {

        Log.d("버튼 눌림", "버튼 눌림");
        loading_handler.sendEmptyMessage(0);

        try {
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            String ip = pref.getString("ip_addr", "");   // http://192.168.55.193:8080
            Log.d("RecommendActivity", ip);

            String serverUri = ip+"/recommendSet";
            String sendFilePath = getApplicationContext().getCacheDir().toString();
            String sendFileName = "/test.jpg";                                      // cache 폴더에 카메라, 갤러리에서 고른 이미지 있음.

            // 서버에 filepath의 filename 파일을 보냄, 딥러닝 결과2개(upper, lower)를 받아 JSONObject로 넘김
            sendFileToServer(serverUri, sendFilePath, sendFileName);

            // 딥러닝 결과따라 캐시파일에 있던 test.jpg를 files밑에 자동으로 저장하기, text뷰에 보여줌
            // 딥러닝 결과따라 clothes 만들어 다음 intent에 넘겨주기
            saveCacheTofiles(jsonObj);


            Intent intent = new Intent(RecommendActivity.this, RecommendResultActivity.class);
            // 다음 인텐트에 clothes 보내줌
            if(clothes != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("clothes_upper", (Serializable) clothes[0]);
                bundle.putSerializable("clothes_lower", (Serializable) clothes[1]);
                intent.putExtras(bundle);
            }
            else {
                Log.d("RecommendActivity", "clothes 없대");
            }
            //pd.dismiss();
            startActivity(intent);

        } catch (Exception e) {
            Log.d("Test", "exception " + e.getMessage());
            // TODO: handle exception
        }

    }

    // 서버에 filepath의 filename 파일을 보냄, 딥러닝 결과를 받아 JSONObject로 넘김
    public void sendFileToServer(String serverUri, String filepath, String filename) throws MalformedURLException {
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";

        StringBuffer sb = new StringBuffer();
        URL connectUrl = new URL(serverUri);

        // 이미지를 서버로 보내고 딥러닝결과받기
        try {
            File sourceFile = new File(filepath+filename);
            DataOutputStream dos;

            if (!sourceFile.isFile()) {
                Log.e("uploadFile", "Source File not exist :" + filename);
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
                conn.setRequestProperty("new_file", filename);

                // write data
                dos = new DataOutputStream(conn.getOutputStream());
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"new_file\";filename=\"" + filename + "\"" + lineEnd);
                dos.writeBytes(lineEnd);

                int bytesAvailable = mFileInputStream.available();
                int maxBufferSize = 1024 * 1024;
                int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                byte[] buffer = new byte[bufferSize];
                int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);

                // 이미지 전송
                while (bytesRead > 0) {
                    dos.write(buffer, 0, bufferSize);
                    bytesAvailable = mFileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
                }

                dos.writeBytes(lineEnd);

                // 텍스트전송
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"id\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes("test" + lineEnd);  // id=test

                // 전송데이터 끝
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                mFileInputStream.close();
                dos.flush(); // finish upload...
                dos.close();

                // 딥러닝 결과받기(clothes upper/lower 두개)
                jsonObj = getJSONDataFromServer(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 서버에서 값 받아 JSONObjtect로 변환
    public JSONObject getJSONDataFromServer(HttpURLConnection conn) throws IOException, JSONException {
        StringBuffer sb = new StringBuffer();

        Log.d("파일", "파일 다 전송함 딥러닝결과 받아오기 시작");
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

        return jsonObj;
    }

    // 받은 jsonobject에 따라 캐시메모리에 있는 데이터를 files 밑에 저장하고, text뷰에 보여줌, clothes 객체 생성해줌
    public void saveCacheTofiles(JSONObject jsonObj) throws JSONException, IOException {
        JSONArray jArray = (JSONArray) jsonObj.get("deep_result");

        // 0번째 JSONObject를 받아옴(upper)
        JSONObject row = jArray.getJSONObject(0);
        Log.d("받아온값1 : ", row.getString("result"));
        clothes[0] = new ClothesDTO("test",row.getString("dress_num"),row.getString("category1"),row.getString("category2"),row.getString("color"),row.getString("pattern"),row.getString("length"));
        str = row.getString("result");

        // 1번째 JSONObject를 받아옴(lower)
        row = jArray.getJSONObject(1);
        Log.d("받아온값2 : ", row.getString("result"));
        clothes[1] = new ClothesDTO("test",row.getString("dress_num"),row.getString("category1"),row.getString("category2"),row.getString("color"),row.getString("pattern"),row.getString("length"));

    }

    // handler = 백그라운드 thread에서 전달된 메시지 처리(UI변경 등을 여기서 해줌.)
    Handler text_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            txt_view.setText(str);                      //백그라운드로 갤러리에서 골라온 사진을 이미지버튼에 뿌림. //이미지뷰도 가능
        }
    };

}

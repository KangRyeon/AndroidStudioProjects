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
import java.util.Arrays;

public class SaveActivity extends AppCompatActivity implements Runnable {
// 캐시폴더의 test.jpg이미지를 가져와 보여주고, 저장하기 버튼을 누르면 서버로 이미지를 보내고 딥러닝 결과 가져옴
    Button save_btn;
    Button rotate_btn;
    ImageView img_view;
    TextView txt_view;

    Bitmap bitmap;
    String str;
    JSONObject jsonObj;
    String category;
    ClothesDTO clothes;
    ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save);

        save_btn = (Button)findViewById(R.id.save_btn);
        rotate_btn = (Button)findViewById(R.id.rotate_btn);
        img_view = (ImageView)findViewById(R.id.img_view);
        txt_view = (TextView)findViewById(R.id.txt_view);

        Intent intent = getIntent();
        try {
                category = intent.getStringExtra("category");
                Log.d("SaveActivity에서 받은 category", category);
                txt_view.setText("category : "+category);
            } catch(Exception e){
                Log.d("SaveActivity","가져온게없음");
        }

        str = "";

        // 어플이 시작되면 캐시에 저장되어있는 test.jpg 이미지를 가져와 회전시킨다.
        showCacheImage();

        // 회전시킨 bitmap 이미지를 ImageView로 보여준다.
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
            pd = ProgressDialog.show(SaveActivity.this, "로딩중", "페이지 로딩 중입니다...");
        }
    };
    // test.jpg 이미지를 서버로 보냄, 그 이미지에 해당하는 딥러닝 결과 받아옴
    @Override
    public void run() {

        Log.d("버튼 눌림", "버튼 눌림");
        //loading_handler.sendEmptyMessage(0);

        try {
            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            String ip = pref.getString("ip_addr", "");   // http://192.168.55.193:8080
            Log.d("PopupActivity", ip);

            String serverUri = ip+"/uploadImage";
            String sendFilePath = getApplicationContext().getCacheDir().toString();
            String sendFileName = "/test.jpg";                                      // cache 폴더에 카메라, 갤러리에서 고른 이미지 있음.

            // 서버에 filepath의 filename 파일을 보냄, 딥러닝 결과를 받아 JSONObject로 넘김
            sendFileToServer(serverUri, sendFilePath, sendFileName);

            // 딥러닝 결과따라 캐시파일에 있던 test.jpg를 files밑에 자동으로 저장하기, text뷰에 보여줌
            saveCacheTofiles(jsonObj);

            Intent intent = new Intent(SaveActivity.this, SaveResultActivity.class);
            // 다음 인텐트에 clothes 보내줌
            if(clothes != null) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("clothes", (Serializable) clothes);
                intent.putExtras(bundle);
            }
            else {
                Log.d("SaveActivity", "clothes 없대");
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

                // 텍스트전송
                dos.writeBytes(twoHyphens + boundary + lineEnd);
                dos.writeBytes("Content-Disposition: form-data; name=\"category\"" + lineEnd);
                dos.writeBytes(lineEnd);
                dos.writeBytes(category + lineEnd);

                // 전송데이터 끝
                dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                mFileInputStream.close();
                dos.flush(); // finish upload...
                dos.close();

                // 딥러닝 결과받기
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

        // 0번째 JSONObject를 받아옴
        JSONObject row = jArray.getJSONObject(0);
        Log.d("받아온값1 : ", row.getString("result"));

        clothes = new ClothesDTO("test",row.getString("dress_num"),row.getString("category1"),row.getString("category2"),row.getString("color"),row.getString("pattern"),row.getString("length"));
        str = row.getString("result");

        String[] outer = {"cardigan", "jacket", "padding", "coat", "jumper", "hood_zipup"};
        String[] upper = {"hood_T", "long_T", "pola", "shirt", "short_T", "sleeveless", "vest"};
        String[] lower = {"long_pants", "short_pants", "Leggings", "mini_skirt", "long_skirt"};
        String[] onepeace = {"long_arm_mini_onepeace", "long_arm_long_onepeace", "short_arm_mini_onepeace", "short_arm_long_onepeace"};
        String[] etc = {"bag", "cap", "shoes"};
        // 받아온 결과가 어느 카테고리인지 확인하기

        Arrays.sort(outer);
        Arrays.sort(upper);
        Arrays.sort(lower);
        Arrays.sort(onepeace);
        int inOuter = Arrays.binarySearch(outer, str);
        int inUpper = Arrays.binarySearch(upper, str);
        int inLower = Arrays.binarySearch(lower, str);
        int inOnepeace = Arrays.binarySearch(onepeace, str);
        String category = null;

        if(inOuter >= 0) {
            Log.d("현재 받아온 것은 outer임", str + "은 outer임"+","+inOuter);
            category = "outer";
        }
        if(inUpper >= 0) {
            Log.d("현재 받아온 것은 upper임", str + "은 upper임"+","+inUpper);
            category = "upper";
        }
        if(inLower >= 0) {
            Log.d("현재 받아온 것은 lower임", str + "은 lower임"+","+inLower);
            category = "lower";
        }
        if(inOnepeace >= 0) {
            Log.d("현재 받아온 것은 onepeace임", str + "은 onepeace임"+","+inOnepeace);
            category = "onepeace";
        }
        if(str.equals("bag") || str.equals("cap") || str.equals("shoes")) {
            Log.d("카테고리가 폴더명과 같아야함.", str + "카테고리가 폴더명과 같아야함");
            category = str;
        }

        // 현재 보이는 이미지를 cache 밑에 test.jpg로 저장함.(회전시킨것)
        String folder_name = getApplicationContext().getCacheDir().toString();    // "/data/data/com.example.cameraandgallery/files/"+row.getString("result"); // /files/upper/hood_T/ 폴더생성
        String saveFileName = folder_name + "/" + "test.jpg";     // /cache/test.jpg

        File file = new File(saveFileName);
        //File file = File.createTempFile(row.getString("result")+"_",".jpg",new File(folder_name));  // 이름 랜덤으로(hood_T_1234325345.jpg)
        try {
            //Log.d("파일다시저장 : ", saveFileName);
            Log.d("파일다시저장 : ", file.getName());

            FileOutputStream out = new FileOutputStream(file.getPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
/*
        // 받아온 결과에 따라 이미지 저장하기 (files 밑에 hood_T 같은 폴더 생성, hood_T_201909231148.jpg로 저장)
        String folder_name = getApplicationContext().getFilesDir().toString()+"/"+category+"/"+row.getString("result");    // "/data/data/com.example.cameraandgallery/files/"+row.getString("result"); // /files/upper/hood_T/ 폴더생성
        String saveFileName = folder_name + "/" + row.getString("dress_num") + ".jpg";     // /files/받아온이름.jpg(/files/upper/hood_T/hood_T_201909231148.jpg)

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
        //File file = File.createTempFile(row.getString("result")+"_",".jpg",new File(folder_name));  // 이름 랜덤으로(hood_T_1234325345.jpg)
        try {
            //Log.d("파일다시저장 : ", saveFileName);
            Log.d("파일다시저장 : ", file.getName());
            // 현재의 bitmap을 file로 저장함, 원래의 파일을 지움 (file.renameTo(resultFile) 로 하면 이름이 바껴서 저장됨)
            File testFile = new File(getApplicationContext().getCacheDir().toString());     // cache/test.jpg
            testFile.delete();

            FileOutputStream out = new FileOutputStream(file.getPath());
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        } catch (Exception e) {
            e.printStackTrace();
        }

        text_handler.sendEmptyMessage(0);

 */
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

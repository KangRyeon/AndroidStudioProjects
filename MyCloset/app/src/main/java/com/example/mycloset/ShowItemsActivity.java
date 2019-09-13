package com.example.mycloset;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;

public class ShowItemsActivity  extends AppCompatActivity {

    LinearLayout list_layout;
    ImageView img_view;
    Button choose_btn;

    Bitmap bitmap;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showitems);

        list_layout = (LinearLayout) findViewById(R.id.list_layout);
        img_view = (ImageView) findViewById(R.id.img_view);
        choose_btn = (Button)findViewById(R.id.choose_btn);


        intent = getIntent();
        String folder="";

        try {
            folder = intent.getExtras().getString("folder");
            Log.d("이전 intent에서 받은값", folder);
        } catch(Exception e){
            Log.d("이전 intent에서 받은값", "업서..");
        }

        File file = new File(getApplicationContext().getFilesDir()+"/"+folder);
        Log.d("상위폴더",getApplicationContext().getFilesDir()+"/"+folder);

        File list[] = file.listFiles();

        for (int i=0; i<list.length; i++){
            Log.d("파일목록",list[i].getName());
            ImageButton btn = new ImageButton(this);
            btn.setId(i);
            //btn.setText(list[i].getName());
            btn.setMaxWidth(200);
            btn.setMaxHeight(200);
            btn.setMinimumWidth(200);
            btn.setMinimumHeight(200);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 1;
            options.inJustDecodeBounds = false;
            String files = getApplicationContext().getFilesDir()+"/"+folder+"/"+list[i].getName();
            Log.d("가져온파일",files);
            bitmap = BitmapFactory.decodeFile(files, options);

            Bitmap resize;
            int maxsize = 200;
            resize = Bitmap.createScaledBitmap(bitmap, (bitmap.getWidth() * 200) / bitmap.getHeight(), 200, true);
            btn.setImageBitmap(resize);
            list_layout.addView(btn);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("가져오기", "files에서 파일 가져오기");
                    img_view.setImageBitmap(bitmap);
                    //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
                }
            });
        }
    }
}

package com.example.ootd;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.ootd.dto.FashionSetDTO;
import com.example.ootd.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener, Runnable {

    Button recommend_btn;
    Button closet_btn;
    Button fashionsetlist_btn;

    PieChart pieChart;
    ArrayList<PieEntry> yValues;

    int result_total[];
    //String label[] = {"상의", "하의", "아우터", "기타"};
    String label[] = {"upper", "lower", "outer", "etc"};
    String[] outer = {"cardigan", "jacket", "padding", "coat", "jumper", "hood_zipup"};
    String[] upper = {"hood_T", "long_T", "pola", "shirt", "short_T", "sleeveless", "vest"};
    String[] lower = {"long_pants", "short_pants", "mini_skirt", "long_skirt"};
    String[] etc = {"bag", "cap", "shoes", "accessory"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recommend_btn = (Button) findViewById(R.id.recommend_btn);
        closet_btn = (Button) findViewById(R.id.closet_btn);
        fashionsetlist_btn = (Button) findViewById(R.id.fashionsetlist_btn);

        pieChart = (PieChart) findViewById(R.id.piechart);

        // 차트 세팅
        chart_setting("옷장", "Clothes");

        // 차트안에 들어갈 entry : 전체 옷 db검색결과 int로 받음.
        result_total = new int[4];
        Thread th = new Thread(MainActivity.this);
        th.start();


        // 옷 추천 버튼 클릭시 - 새로운 액티비티가 틀어짐.
        recommend_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SelectCameraGalleryRecommendActivity.class);
                startActivity(intent);
                //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
            }
        });

        // 옷장 버튼 클릭시
        closet_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ClosetActivity.class);
                startActivity(intent);
                //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
            }
        });

        // 내가 만든 세트 버튼 클릭시
        fashionsetlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FashionSetListActivity.class);
                startActivity(intent);
                //finish();      // finish() 를 하지 않으면 메인액티비가 꺼지지 않음
            }
        });
    }
    @Override
    public void run() {

        try {
            MyUrl myUrl = new MyUrl();

            SharedPreferences pref = getSharedPreferences("pref", MODE_PRIVATE);
            String ip = pref.getString("ip_addr", "");   // http://192.168.55.193:8080

            //writer.write("id=asdf");
            String id = pref.getString("id", "");   // test

            // 서버에 보낼 파라미터 설정 (writer.write("id=" + id); writer.write("&password="+pw);)
            String[][] values = {{"id", id}};

            // json data 받기
            JSONObject jsonObj;
            jsonObj = myUrl.getJsonResult(ip, "/selectCategoryTotalById", values);
            JSONArray jArray = (JSONArray) jsonObj.get("total_results");
            Log.d("row 개수", "" + jArray.length());

            // fashion set가 몇개인지
            JSONObject row = jArray.getJSONObject(0);
            String upper_total = row.getString("upper");
            String lower_total = row.getString("lower");
            String outer_total = row.getString("outer");
            String etc_total = row.getString("etc");
            result_total[0] = Integer.parseInt(upper_total);
            result_total[1] = Integer.parseInt(lower_total);
            result_total[2] = Integer.parseInt(outer_total);
            result_total[3] = Integer.parseInt(etc_total);

            //int label_num[] = {1, 2, 3, 5, 4, 5};
            int label_num[] = {result_total[0], result_total[1], result_total[2], result_total[3]};

            yValues = new ArrayList<PieEntry>();
            for (int i = 0; i < label.length; i++) {
                yValues.add(new PieEntry(label_num[i], label[i]));
            }

            // 위 차트안에 들어갈 값들에 대해 차트에 로드시킴.
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chart_load();
                }
            });

        } catch(Exception e) {
            e.printStackTrace();
            Log.d("MainActivity", "에러발생했습니다...");
        }
    }

    public void chart_setting(String center_txt, String label_name) {
        // 차트설정
        pieChart.setUsePercentValues(false);                        //true = 퍼센테이지로 보여줌.
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(20, 10, 20, 5); //위치

        // 차트 리스너 등록
        pieChart.setOnChartValueSelectedListener(this);

        pieChart.setRotationEnabled(false);                 // 안돌아가게 할 경우 false
        pieChart.setDragDecelerationFrictionCoef(0.91f);     // 돌아가는것 어느속도로할지(1 : 빠름, 0 : 느림)
        pieChart.setDragDecelerationEnabled(true);           // 그래프 터치 후 돌아가게 할지
        pieChart.setDrawHoleEnabled(true);                   // 가운데에 빈공간 만들지
        pieChart.setHoleRadius(45f);                         // 가운데 공간 얼마나 할지
        pieChart.setHoleColor(Color.WHITE);                 // 가운데 빈공간 색
        pieChart.setTransparentCircleRadius(61);             // 가운데 공간에 투명한 원(클수록 큼)
        pieChart.setTransparentCircleColor(Color.WHITE);    //가운데 공간 투명한 원 색

        // 가운데 들어갈 단어 설정
        pieChart.setDrawCenterText(true);
        pieChart.setCenterText(center_txt);
        pieChart.setCenterTextSize(30);

        // 표시될 라벨, 라벨 위치
        Description description = new Description();
        description.setText(label_name); // 라벨
        description.setTextSize(30);
        description.setPosition(650, 250); //description.setPosition(1060,90);
        pieChart.setDescription(description);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL); // HORIZONTAL
        l.setDrawInside(false);
        l.setXEntrySpace(7f); // 라벨 사이 x거리
        l.setYEntrySpace(0f); // 라벨 사이 y거리
        l.setYOffset(0); // 라벨 위에서부터 얼마나 떨굴지

        // 차트안에 들어갈 라벨 색, 크기
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setEntryLabelTextSize(15);
        pieChart.animateY(1000, Easing.EaseInOutCubic); //애니메이션 Easing.EaseInOutQuad
    }

    public void chart_load() {
        // 각 라벨 설정
        PieDataSet dataSet = new PieDataSet(yValues, "");
        dataSet.setSliceSpace(3); // 차트 사이 거리
        dataSet.setSelectionShift(5); // 차트 눌렀을때 얼마나 올라오게 할지
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS); // VORDIPLOM_COLORS, JOYFUL_COLORS, COLORFUL_COLORS, LIBERTY_COLORS, PASTEL_COLORS

        // color를 기존 5가지말고 추가로 설정할 때. (아래 모든 color를 리스트로만듦(겹치는색없이 진행))
        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());

        dataSet.setColors(colors);

        PieData data = new PieData((dataSet));
        data.setValueTextSize(12);
        data.setValueTextColor(Color.WHITE); // 그래프 안 수치표시 글자색
        pieChart.setData(data);
    }


    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        Log.d("눌려짐", "값: " + e.getY() + ", 인덱스: " + h.getX() + ", 데이터셋에서 인덱스: " + h.getDataSetIndex());

        int cate = (int) h.getX();
        Log.d("int로 변한값", "" + cate);
        Intent intent = new Intent(MainActivity.this, PopupChartActivity.class);
        intent.putExtra("category", label[cate]);
        startActivity(intent);
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

}

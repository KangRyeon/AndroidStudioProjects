package com.example.mycloset;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;

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

import java.util.ArrayList;

public class PopupChartActivity extends Activity implements OnChartValueSelectedListener {
    PieChart pieChart;
    ArrayList<PieEntry> yValues;

    String category;

    String[] outer = {"cardigan", "jacket", "padding", "coat", "jumper", "hood_zipup"};
    String[] upper = {"hood_T", "long_T", "pola", "shirt", "short_T", "sleeveless", "vest"};
    String[] lower = {"long_pants", "short_pants", "Leggings", "mini_skirt", "long_skirt"};
    String[] onepeace = {"long_arm_mini_onepeace", "long_arm_long_onepeace", "short_arm_mini_onepeace", "short_arm_long_onepeace"};
    String[] etc = {"bag", "cap", "shoes", "accessory"};

    String[] now_category;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //타이틀바 없애기
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_popupchart);

        Intent intent = getIntent();
        try {
            category = intent.getStringExtra("category");  // 상의 or 하의 or 아우터 or 기타
            Log.d("SelectCameraGalleryActivity","이전 인텐트에서 보낸 카테고리 있음");
            Log.d("SelectCameraGalleryActivity에서 받은 category", category);
        } catch(Exception e){
            Log.d("SelectCameraGalleryActivity","가져온게없음");
        }

        pieChart = (PieChart)findViewById(R.id.piechart);

        // 차트 세팅
        chart_setting(category, "Clothes");

        // 차트안에 들어갈 entry
        //int label_num[] = {1, 2, 3, 5, 4, 5, 6};
        String label[] = {"상의", "하의", "아우터", "기타"};

        int label_num[] = {1, 2, 3, 5, 4, 5,6,1,2};

        yValues = new ArrayList<PieEntry>();
        if(category.equals("상의"))
            now_category = upper;
        else if(category.equals("하의"))
            now_category = lower;
        else if(category.equals("아우터"))
            now_category = outer;
        else
            now_category = etc;
        for(int i=0; i<now_category.length; i++) {
            yValues.add(new PieEntry(label_num[i], now_category[i]));
        }


        // 위 차트안에 들어갈 값들에 대해 차트에 로드시킴.
        chart_load(yValues);
    }
    public void chart_setting(String center_txt, String label_name) {
        // 차트설정
        pieChart.setUsePercentValues(false);                        //true = 퍼센테이지로 보여줌.
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(20,20,20,5); //위치

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
        description.setPosition(550,120); //description.setPosition(1060,90);
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

    public void chart_load(ArrayList<PieEntry> values) {
        // 각 라벨 설정
        PieDataSet dataSet = new PieDataSet(yValues,"");
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
        Log.d("눌려짐","값: " + e.getY() + ", 인덱스: " + h.getX()+ ", 데이터셋에서 인덱스: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }
}

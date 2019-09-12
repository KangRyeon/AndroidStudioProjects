package com.example.makers;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Sub0Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub0);
    }

    public void OuterClicked(View v){
        Intent subIntent = new Intent(getApplicationContext(),Sub1Activity.class);
        startActivity(subIntent);
    }

    public void TopClicked(View v){
        Intent subIntent = new Intent(getApplicationContext(),Sub1Activity.class);
        startActivity(subIntent);
    }

}

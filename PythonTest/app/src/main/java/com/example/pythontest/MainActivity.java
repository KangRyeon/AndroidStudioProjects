package com.example.pythontest;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    EditText etInput;
    Button btExecute;
    TextView tvResult;
    String command;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etInput = (EditText) findViewById(R.id.et_command);
        btExecute = (Button) findViewById(R.id.bt_execute);
        tvResult = (TextView) findViewById(R.id.tv_result);

        btExecute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO
                ShellExecuter exe = new ShellExecuter();
                command = etInput.getText().toString();

                String mOutput = exe.Executer(command);
                tvResult.setText(mOutput);
            }
        });
    }
}


//실행 클래스 - 입력한 명령에 대한 결과를 스트링으로 돌려 준다.
class ShellExecuter{
    public ShellExecuter() {
    }

    public String Executer(String command) {

        StringBuffer output = new StringBuffer();

        java.lang.Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor(); //프로세스의 명령이 끝날때까지 대기한다.
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line = reader.readLine()) != null) {
                output.append(line + "\n");
            }
            Log.d("myLog", line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String response = output.toString();
        return response;
    }
}
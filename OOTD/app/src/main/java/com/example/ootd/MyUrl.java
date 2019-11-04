package com.example.ootd;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MyUrl {

    public JSONObject getJsonResult(String ip, String requestUrl, String[][] values) {
        try {
            // ip = "http://192.168.55.193:8080"
            // requestUrl = "/loginCheck"
            // values = [["id","test"],["password","test1234"]]

            Log.d("서버에 요청", ip+requestUrl);

            URL connectUrl = new URL(ip + requestUrl);       // 스프링프로젝트의 home.jsp 주소
            DataOutputStream dos;
            HttpURLConnection conn = (HttpURLConnection) connectUrl.openConnection();       // URL 연결한 객체 생성
            Log.d("URL객체 생성", "URL객체 생성");
            if (conn != null) {
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");

                // 원래 DataOutputStream 사용했으나 utf-8전송위해 아래껄로 바꿈
                OutputStreamWriter outStream = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
                PrintWriter writer = new PrintWriter(outStream);

                Log.d("서버보내기 시작", "서버보내기 시작");

                for(int i=0; i<values.length; i++) {
                    if (i == 0)
                        writer.write(values[i][0] + "=" + values[i][1]);  // writer.write("id=" + id);
                    else // i가 처음이 아니면 &붙여줘야
                        writer.write("&" + values[i][0] + "=" + values[i][1]);  // writer.write("&password="+pw);
                }

                writer.flush();
                writer.close();

                Log.d("서버보내기 끝", "서버보내기 끝");

                // json data 받기
                JSONObject jsonObj;
                jsonObj = getJSONDataFromServer(conn);

                return jsonObj;
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("MyUrl 에러", "에러발생했습니다...");
        }
        return null;
    }

    // 서버에서 값 받아 JSONObjtect로 변환
    public JSONObject getJSONDataFromServer(HttpURLConnection conn) throws IOException, JSONException
    {
        StringBuffer sb = new StringBuffer();

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
            while (true) {
                String line = br.readLine();
                if (line == null)
                    break;
                sb.append(line + "\n");
            }
            Log.d("서버에서 받은것", ""+sb.toString());
            br.close();
        }
        conn.disconnect();

        // 받아온 source를 JSONObject로 변환한다.
        JSONObject jsonObj = new JSONObject(sb.toString());

        return jsonObj;
    }
}

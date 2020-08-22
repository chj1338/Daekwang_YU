package com.yu.daekwang.daekwang_yu;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

public class SubEventActivity extends AsyncTask<String, Void, String> {
    String sendMsg, receiveMsg;
    private String ipAddr = "116.126.134.159";

    @Override
    protected String doInBackground(String... strings) {
        try {
            String str;

            // 접속할 서버 주소 (이클립스에서 android.jsp 실행시 웹브라우저 주소)
            //URL url = new URL("http://192.168.0.8/androidTest.do");
            String domainURL = strings[0];
            URL url = new URL("http://" + ipAddr + ":8080" + domainURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestMethod("POST");
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(3000);
            OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream());

            // 전송할 데이터. POST 방식으로 작성
            sendMsg = strings[1];
//Log.d("=============== ", sendMsg);
            osw.write(sendMsg);
            osw.flush();

            //jsp와 통신 성공 시 수행
            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();

                // jsp에서 보낸 값을 받는 부분
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();
            } else {
                // 통신 실패
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            receiveMsg = "{\"resultData\":[{\"eventSn\":\"000000000\",\"eventTitle\":\"서버 접속 불가\",\"eventDt\":\"Connect Error\",\"eventPlace\":null,\"eventContent\":null,\"eventDeptMng\":null,\"eventImgNm\":\"null\"}],\"resultMsg\":\"SUCCESS\",\"resultCd\":\"1000\"}";
        } catch (IOException e) {
            e.printStackTrace();
            receiveMsg = "{\"resultData\":[{\"eventSn\":\"000000000\",\"eventTitle\":\"서버 접속 불가\",\"eventDt\":\"Connect Error\",\"eventPlace\":null,\"eventContent\":null,\"eventDeptMng\":null,\"eventImgNm\":\"null\"}],\"resultMsg\":\"SUCCESS\",\"resultCd\":\"1000\"}";
        } catch (Exception e) {
            e.printStackTrace();
            receiveMsg = "{\"resultData\":[{\"eventSn\":\"000000000\",\"eventTitle\":\"서버 접속 불가\",\"eventDt\":\"Connect Error\",\"eventPlace\":null,\"eventContent\":null,\"eventDeptMng\":null,\"eventImgNm\":\"null\"}],\"resultMsg\":\"SUCCESS\",\"resultCd\":\"1000\"}";
        }

//        Log.d("========= receiveMsg : ", receiveMsg);

        //jsp로부터 받은 리턴 값
        return receiveMsg;
    }

}
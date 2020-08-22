package com.yu.daekwang.daekwang_yu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONArray;
import org.json.JSONObject;

public class DbTestActivity extends AppCompatActivity {
    Button registerBtn;
    EditText idet, pwet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db_test);

        setTitle("ORACLE");

        registerBtn = (Button)findViewById(R.id.register_btn);
        idet = (EditText)findViewById(R.id.register_id);
        pwet = (EditText)findViewById(R.id.register_pw);

        registerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    String result;
                    String id = idet.getText().toString();
                    String pw = pwet.getText().toString();

                    RegisterActivity task = new RegisterActivity();
//                    result = task.execute(id, pw).get();
                    result = task.execute(id, pw).get();
                    Log.d("======", result);

                    JSONObject root = new JSONObject(result);
                    JSONArray nameList = root.getJSONArray("resultData");
                    Log.d("======", nameList.length()+"");
                    for(int i=0; i<nameList.length(); i++) {
                        JSONObject jsonObject = nameList.getJSONObject(i);
                        Log.d(i + "====== LOGINID : ", jsonObject.get("loginId").toString() );
                        Log.d(i + "====== LOGINPW :", jsonObject.get("loginPw").toString() );
                        Log.d(i + "====== USERNM : ", jsonObject.get("userNm").toString() );
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("DBtest", ".....ERROR.....!");
                }
            }
        });

    }

}

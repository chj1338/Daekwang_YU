package com.yu.daekwang.daekwang_yu;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity {
    String actionTitle="대광교회 (유강신목사님)";
    private String filePath = getExternalPath() + "/Daekwang/";     // 파일 저장경로

    TableLayout tableLayout;

    Spinner spnYear;

    private int rowCnt = 100; // 불러올 row 수
    private int colCnt = 3; // 컬럼수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();  // 외장메모리 권한

        tableLayout = (TableLayout)findViewById(R.id.tableLayout1);
        tableLayout.removeAllViewsInLayout();
        tableLayout.setStretchAllColumns(true);

        spnYear = (Spinner)findViewById(R.id.spnYear) ;

        tableLayout = (TableLayout) findViewById(R.id.tableLayout1);
        tableLayout.removeAllViewsInLayout();
        tableLayout.setStretchAllColumns(true);

        // spinner1 child 셋팅
        ArrayList<String> yearItems = new ArrayList<>();

        for(int i=2014; i<=2020; i++) {
            yearItems.add(i+"");
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, yearItems);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.spinner_item);
        spnYear.setAdapter((SpinnerAdapter) adapter);

        // 년도 콤보에 올해 셋팅
        spnYear.setSelection(6);

        makeNotice();

        // spinner1 리스너
        spnYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                makeNotice();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    // 설교리스트 그리드 그리기
    public void makeNotice() {
        tableLayout.removeAllViewsInLayout();

        String paramYear = spnYear.getSelectedItem().toString();
        String result;

        try {
            //raw 폴더 읽기
            InputStream is = getResources().openRawResource(R.raw.yu_sermon_list);
            BufferedReader br2 = new BufferedReader(new InputStreamReader(is));

            ArrayList<String> readStr = new ArrayList<String>();
            String line = "";

            while((line=br2.readLine())!=null) {
                String tempTitle = line.trim().replaceAll(" ", "");

                if(tempTitle.indexOf(paramYear) != -1) {
                    readStr.add(line);
                }
            }
            br2.close();

            // 표그리기
            int trCt = rowCnt;
            int tdCt = colCnt;

            // 그리드 생성
            TableRow row[] = new TableRow[trCt];            // 테이블 ROW 생성
            final TextView text[][] = new TextView[trCt][tdCt];   // 데이터

            // column 속성 정의
            TableRow.LayoutParams textParam_1 = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1);
            TableRow.LayoutParams textParam_2 = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 2);
            TableRow.LayoutParams textParam_3 = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 3);
            TableRow.LayoutParams textParam_4 = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 4);
            textParam_1.setMargins(2, 2, 2, 2);
            textParam_2.setMargins(2, 2, 2, 2);
            textParam_3.setMargins(2, 2, 2, 2);
            textParam_4.setMargins(2, 2, 2, 2);

            // Header
            TableRow headerRow = new TableRow(this);
            headerRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));    // row 속성 정의
            String headerName[] = {"날짜", "제목", "성경"};
            for (int k = 0; k < tdCt; k++) {
                TextView tempTv = new TextView(this);
                tempTv.setText(headerName[k]);
                tempTv.setTextSize(15);                     // 폰트사이즈
                tempTv.setGravity(Gravity.CENTER);    // 폰트정렬
                tempTv.setBackgroundColor(Color.rgb(195, 195, 195));
                headerRow.addView(tempTv);

                if (k == 0) {
                    tempTv.setLayoutParams(textParam_2);
                } else if (k == 1) {
                    tempTv.setLayoutParams(textParam_4);
                } else if (k == 2) {
                    tempTv.setLayoutParams(textParam_2);
                }
            }
            tableLayout.addView(headerRow);

            // 본문
            for (int tr = 0; tr < readStr.size(); tr++) {
                row[tr] = new TableRow(this);
                row[tr].setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.MATCH_PARENT));    // row 속성 정의

                for (int td = 0; td < tdCt; td++) {
                    text[tr][td] = new TextView(this);
//Log.d("=====" + td + " : ", readStr.get(tr));
                    String colText[] = readStr.get(tr).split(";");
//Log.d("=====" + td + " : ", colText[0] + " : " + colText[1] + " : " + colText[2] + " : " + colText[3]);
                    if (tr < readStr.size()) {
                        if (td == 0) {
                            text[tr][td].setLayoutParams(textParam_2);
                            text[tr][td].setText(colText[0].substring(0, 4) + "-" + colText[0].substring(4, 6) + "-" + colText[0].substring(6, 8));
                            text[tr][td].setGravity(Gravity.CENTER);
                            text[tr][td].setBackgroundColor(Color.rgb(200, 191, 231));
                        } else if (td == 1) {
                            text[tr][td].setLayoutParams(textParam_4);
                            text[tr][td].setText(colText[1]);
                            text[tr][td].setGravity(Gravity.LEFT);      // 폰트정렬
                        } else if (td == 2) {
                            text[tr][td].setLayoutParams(textParam_2);
                            text[tr][td].setText(colText[2]);
                            text[tr][td].setBackgroundColor(Color.rgb(200, 191, 231));
                            text[tr][td].setGravity(Gravity.LEFT);      // 폰트정렬
                        }
                    } else {
                        text[tr][td].setText("");
                    }

                    // 속성 지정
                    text[tr][td].setId(td);
                    text[tr][td].setTextSize(15);               // 폰트사이즈
                    //text[tr][td].setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize);
                    text[tr][td].setWidth(dpToPx(40));          // 가로 사이즈

                    //final String colURL = "https://youtu.be/" + colText[3];
                    final String sermonDay = colText[0];
                    final String title = colText[1];
                    final String bible = colText[2];
                    final String colURL = colText[3];
//Log.d("==================", colURL);
                    text[tr][td].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //Intent intentMain = new Intent(Intent.ACTION_VIEW, Uri.parse(colURL));
                            //startActivity(intentMain);

                            Intent intent = new Intent(MainActivity.this, VideoActivity.class);
                            intent.putExtra("sermonDay", sermonDay);
                            intent.putExtra("title", title);
                            intent.putExtra("bible", bible);
                            intent.putExtra("videoSrc", colURL);
                            startActivity(intent);
                        }
                    });

                    row[tr].addView(text[tr][td]);
                }
                tableLayout.addView(row[tr]);

                // 파란줄 추가
                TableRow tempRow = new TableRow(this);
                for (int k = 0; k < tdCt; k++) {
                    TextView tempTv = new TextView(this);
                    tempTv.setText("");
                    tempTv.setTextSize(1);                     // 폰트사이즈
                    tempTv.setBackgroundColor(Color.BLUE);
                    tempRow.addView(tempTv);
                }
                tableLayout.addView(tempRow);
            }

            // 여백 추가
            TableRow tempRow = new TableRow(this);
            for (int k = 0; k < 3; k++) {
                TextView tempTv = new TextView(this);
                tempTv.setText(" ");
                tempTv.setTextSize(20);                     // 폰트사이즈
                tempRow.addView(tempTv);
            }
            tableLayout.addView(tempRow);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 목사님 소개
    public void btnGoMinisterInfo(View v) {
        Intent intent001 = new Intent(this, ImgZoomActivity.class);
        String imgSrc = "ministerInfo";
        intent001.putExtra("imgSrc", imgSrc);
        startActivity(intent001);
    }

    // 대광교회 홈페이지
    public void btnGoHome(View v) {
        String colURL = "http://daekwang.org";
        Intent intentMain = new Intent(Intent.ACTION_VIEW, Uri.parse(colURL));
        startActivity(intentMain);
    }

    // 교회소개
    public void btnGoDkInfo(View v) {
        Intent intent001 = new Intent(this, ImgZoomActivity.class);
        String imgSrc = "dkInfo";
        intent001.putExtra("imgSrc", imgSrc);
        startActivity(intent001);
    }

    // 전화걸기
    public void btnGoTel(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("tel:031-261-0691"));
        startActivity(intent);
    }

    // dp -> px (화면이나 길이용, 폰트용은 다름)
    public  int dpToPx(int dp) {
        DisplayMetrics displayMetrics = this.getApplicationContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    // dp -> px (화면이나 길이용, 폰트용은 다름)
    public int pxToDp(int px) {
        DisplayMetrics displayMetrics = this.getApplicationContext().getResources().getDisplayMetrics();
        int dp = Math.round(px / (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return dp;
    }

    // 권한 체크
    void checkPermission() {
        // 외장메모리 권한
        int ext_stor_perm_info = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(ext_stor_perm_info == PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(getApplicationContext(), "SDCard 쓰기 권한 있음", Toast.LENGTH_SHORT).show();
        } else {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//                Toast.makeText(getApplicationContext(), "권한의 필요성 설명", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            } else {
                ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        }
    }


    // 외장메모리 경로 확인
    public String getExternalPath(){
        String sdPath = "";
        String ext = Environment.getExternalStorageState();
        if(ext.equals(Environment.MEDIA_MOUNTED)){
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
            //sdPath = "/mnt/sdcard/";
        } else {
            sdPath = filePath + "";
        }

        //        Toast.makeText(this,sdPath,Toast.LENGTH_SHORT).show();
        return sdPath;
    }


    // 뒤로가기 두번 누르면 종료
    private final long FINSH_INTERVAL_TIME = 2000; //2초
    private long backPressedTime = 0;
    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - backPressedTime;

        if (0 <= intervalTime && FINSH_INTERVAL_TIME >= intervalTime) {
            // 직전 마지막 화면은 살아있기 때문에 강제로 메인화면 호출 후 종료해준다.
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

            moveTaskToBack(true);
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        } else {
            backPressedTime = tempTime;
            Toast.makeText(getApplicationContext(), "\'뒤로\' 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }
}

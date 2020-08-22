package com.yu.daekwang.daekwang_yu;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;     // 핀치줌 용 오픈소스 (gradle compile 에 추가)

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ImgZoomActivity extends AppCompatActivity {
    private String filePath = getExternalPath() + "/Daekwang/";     // 파일 저장경로

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_img_zoom);

        ImageView imageView = (ImageView) findViewById(R.id.bannerView);
        PhotoViewAttacher mAttacher = new PhotoViewAttacher(imageView); // 핀치줌 용 오픈소스

        Button btnClose = (Button)findViewById(R.id.btnClose);

        Intent intent = getIntent(); // 값을 받아온다.
        String imgSrc = intent.getStringExtra("imgSrc");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
        filePath += sdf.format(new Date()) + "/";

        Bitmap tempBm;
        if(imgSrc.equals("ministerInfo")) { // 목사님 소개
            BitmapDrawable drawable =
//                    (BitmapDrawable) getResources().getDrawable(R.drawable.minister_img_01);
                    (BitmapDrawable) getResources().getDrawable(R.drawable.minister_img_03);
            tempBm = drawable.getBitmap();
        } else if(imgSrc.equals("dkInfo")) { // 교회 소개
            BitmapDrawable drawable =
                    (BitmapDrawable) getResources().getDrawable(R.drawable.dk_info_01);
            tempBm = drawable.getBitmap();
        } else {
            tempBm = BitmapFactory.decodeFile(filePath + imgSrc);
        }

        final Bitmap bm = tempBm;
        File file = new File(filePath + imgSrc);
        imageView.setImageBitmap(bm);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

}

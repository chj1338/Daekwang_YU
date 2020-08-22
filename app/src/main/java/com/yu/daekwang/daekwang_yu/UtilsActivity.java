package com.yu.daekwang.daekwang_yu;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UtilsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_utils);
    }

    public String findBibleSplit(String bookParam) {
        String readStr ="";

        try {
            String bookNm = "";         // 성경이름
            String bookPage = "";       // 몇장
            String bookMinLine = "";    // 몇절 부터
            String bookMaxLine = "";    // 몇절 까지

            String bookLine[] = bookParam.split(",");

            if(bookLine.length == 0) { // 내용이 없음.
                return readStr;
            } else if(bookLine.length == 1) {  // 단일성경 '마 1:1' 혹은 '마 1:1~5'  의 형태
                String tempStr1[] = bookLine[0].split(" ");
                bookNm = tempStr1[0];
                String tempStr2[] = tempStr1[1].split(":");
                bookPage = tempStr2[0];
                String tempStr3[] = tempStr2[1].split("~");
                bookMinLine = tempStr3[0];

                if(tempStr3.length > 1) {
                    bookMaxLine = tempStr3[1];
                } else {
                    bookMaxLine = tempStr3[0];
                }

                readStr = findBible(bookNm, bookPage, bookMinLine, bookMaxLine);
            } else if(bookLine.length > 1)  {  // 여러성경 '마 1:1,27', '마 1:1~5,37', '마 1:1~5, 31~37'  의 형태
                // , 앞부분 찾기
                String tempStr1[] = bookLine[0].split(" ");
                bookNm = tempStr1[0];
                String tempStr2[] = tempStr1[1].split(":");
                bookPage = tempStr2[0];
                String tempStr3[] = tempStr2[1].split("~");
                bookMinLine = tempStr3[0];

                if(tempStr3.length > 1) {
                    bookMaxLine = tempStr3[1];
                } else {
                    bookMaxLine = tempStr3[0];
                }

                readStr += findBible(bookNm, bookPage, bookMinLine, bookMaxLine);

                // , 뒷부분 찾기
                String temp2Str1[] = bookLine[1].split(" ");

                if(temp2Str1.length > 2) { // '책 장 절'이 있는 경우
                    bookNm = temp2Str1[1];

                    String temp2Str2[] = temp2Str1[2].split(":");
                    if(temp2Str2.length > 1) {
                        bookPage = temp2Str2[0];
                    }

                    String temp2Str3[] = temp2Str2[1].split("~");
                    bookMinLine = temp2Str3[0];

                    if(temp2Str3.length > 1) {
                        bookMaxLine = temp2Str3[1];
                    } else {
                        bookMaxLine = temp2Str3[0];
                    }
                } else if(bookLine[1].indexOf(":") > -1) { // '장절'이 있는 경우
                    String temp2Str2[] = temp2Str1[1].split(":");

                    if(temp2Str2.length > 1) {
                        bookPage = temp2Str2[0];
                    }

                    String temp2Str3[] = temp2Str2[1].split("~");
                    bookMinLine = temp2Str3[0];

                    if(temp2Str3.length > 1) {
                        bookMaxLine = temp2Str3[1];
                    } else {
                        bookMaxLine = temp2Str3[0];
                    }
                } else if(bookLine[1].indexOf("~") > -1) { // '절'만 있는 경우
                    String temp2Str3[] = temp2Str1[1].split("~");
                    bookMinLine = temp2Str3[0];

                    if (temp2Str3.length > 1) {
                        bookMaxLine = temp2Str3[1];
                    } else {
                        bookMaxLine = temp2Str3[0];
                    }
                } else {
                    bookMinLine = bookLine[1];
                    bookMaxLine = bookLine[1];
                }

                readStr += findBible(bookNm, bookPage, bookMinLine, bookMaxLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        readStr += "\n";

        return readStr;
    }

    public String findBible(String bookNm, String bookPage, String bookMinLine, String bookMaxLine) {
        String readStr = "";

        try {
            String bookSe = ""; // 신구약 구분

            //raw 폴더 읽기
            InputStream is = null;
            BufferedReader br = null;

            // 신구약 찾기
            is = getResources().openRawResource(R.raw.book_se);
            br = new BufferedReader(new InputStreamReader(is));

            String line = "";
            while((line=br.readLine())!=null) {
                if (line.indexOf(bookNm) > -1) {
                    String tempStr[] = line.split(";");
                    bookSe = tempStr[1];
                }
            }

            // 성경구절 찾기
            if(bookSe.equals("OLD")) {
                is = getResources().openRawResource(R.raw.bible_old);
            } else {
                is = getResources().openRawResource(R.raw.bible_new);
            }

            br = new BufferedReader(new InputStreamReader(is));

            int temMinLine = Integer.parseInt(bookMinLine);
            int temMaxLine = Integer.parseInt(bookMaxLine);

            while((line=br.readLine()) != null) {
                String findStr = bookNm + " " + bookPage + ":" + temMinLine;

                if (temMinLine <= temMaxLine && line.indexOf(findStr) > -1) {
                    readStr += line + "\n\n";
                    temMinLine++;
                } else if(temMinLine > temMaxLine) {
                    break;
                }
            }

            br.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return readStr;
    }
}
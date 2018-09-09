package com.example.iternity.gachon_class;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity implements OnClickListener {
    AlertDialog.Builder[] builder = new AlertDialog.Builder[11];
    Button[] btnSearch = new Button[11];
    final String[][] items = {
            {"9F", "8F", "7F","6F", "5F", "4F", "3F", "2F", "1F", "B1F"},  //가천관
            {"6F", "5F", "4F", "3F", "2F", "1F", "B1F", "B2F"},  //비전타워
            {"5F", "4F", "3F", "2F", "1F"}, //공과대학1
            {"5F", "4F", "3F", "2F", "1F"}, //공과대학2
            {"4F", "3F", "2F", "1F", "B1F", "B2F"}, //바이오나노대학
            {"5F", "4F", "3F", "2F", "1F"}, //한의과대학
            {"6F", "5F", "4F", "3F", "2F", "1F"},   //IT대학
            {"5F", "4F", "3F", "2F", "1F"}, //예술대학1
            {"5F", "4F", "3F", "2F", "1F"}, //예술대학2
            {"6F", "5F", "4F", "3F", "2F", "1F"},   //글로벌센터
            {"5F", "4F", "3F", "2F", "1F"}  //교육대학원
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        init();
    }

    private void init() {
        btnSearch[0] = (Button) findViewById(R.id.btnSearch01);
        btnSearch[1] = (Button) findViewById(R.id.btnSearch02);
        btnSearch[2] = (Button) findViewById(R.id.btnSearch03);
        btnSearch[3] = (Button) findViewById(R.id.btnSearch04);
        btnSearch[4] = (Button) findViewById(R.id.btnSearch05);
        btnSearch[5] = (Button) findViewById(R.id.btnSearch06);
        btnSearch[6] = (Button) findViewById(R.id.btnSearch07);
        btnSearch[7] = (Button) findViewById(R.id.btnSearch08);
        btnSearch[8] = (Button) findViewById(R.id.btnSearch09);
        btnSearch[9] = (Button) findViewById(R.id.btnSearch10);
        btnSearch[10] = (Button) findViewById(R.id.btnSearch11);

        // 다이어로그 생성 밑 설정
        for (int i = 0; i < 11; i++) {
            final int no = i;
            builder[i] = new AlertDialog.Builder(this);

            builder[i].setItems(items[i], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position) {
                    Toast.makeText(getApplicationContext(), btnSearch[no].getText().toString() + " " +items[no][position] + " 선택!", Toast.LENGTH_SHORT).show();
                }
            });
            builder[i].create();
            btnSearch[i].setTag(i);
            btnSearch[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        // 클릭한 버튼을 저장
        Button b = (Button)v;
        // 클릭한 버튼의 tag정보를 가져옴
        int tag = Integer.parseInt(v.getTag().toString());
        // 다이얼로그의 제목 지정
        builder[tag].setTitle(b.getText().toString());
        builder[tag].show();
    }
}

package com.stafor.iternity.gachon_class;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity implements OnClickListener {
    public static Activity Search_Activity;
    AlertDialog.Builder[] builder = new AlertDialog.Builder[11];
    ImageButton[] btnSearch = new ImageButton[11];
    ImageView imgView, imgView_search;
    final String[] buildings = {"가천관", "비전타워", "공과대학1", "공과대학2", "바이오나노대학",
                                "한의과대학", "IT대학", "예술대학1", "예술대학2", "글로벌센터", "교육대학원"};
    final String[][] items = {
            {"9F", "8F", "7F","6F", "5F", "4F", "3F", "B1F", "B2F"},  //가천관
            {"6F", "5F", "4F", "3F", "2F", "B1F", "B2F"},  //비전타워
            {"7F", "6F", "5F", "4F", "3F", "2F", "1F"}, //공과대학1
            {"6F", "5F", "4F", "3F", "2F", "1F"}, //공과대학2
            {"5F", "4F", "3F", "2F", "1F", "B1F"}, //바이오나노대학
            {"3F", "2F"}, //한의과대학
            {"6F", "5F", "4F", "3F", "2F", "1F"},   //IT대학
            {"7F", "6F", "5F", "4F", "3F", "2F", "1F", "B0F"}, //예술대학1
            {"4F", "3F", "2F", "1F", "B1F"}, //예술대학2
            {"6F", "5F", "4F", "3F", "2F", "1F"},   //글로벌센터
            {"6F", "5F", "4F", "3F", "2F", "1F"}  //교육대학원
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Search_Activity = SearchActivity.this;

        init();
    }

    private void init() {
        imgView = (ImageView) findViewById(R.id.img_campus);
        imgView_search = (ImageView) findViewById(R.id.imgView_search);
        btnSearch[0] = (ImageButton) findViewById(R.id.btnSearch01);
        btnSearch[1] = (ImageButton) findViewById(R.id.btnSearch02);
        btnSearch[2] = (ImageButton) findViewById(R.id.btnSearch03);
        btnSearch[3] = (ImageButton) findViewById(R.id.btnSearch04);
        btnSearch[4] = (ImageButton) findViewById(R.id.btnSearch05);
        btnSearch[5] = (ImageButton) findViewById(R.id.btnSearch06);
        btnSearch[6] = (ImageButton) findViewById(R.id.btnSearch07);
        btnSearch[7] = (ImageButton) findViewById(R.id.btnSearch08);
        btnSearch[8] = (ImageButton) findViewById(R.id.btnSearch09);
        btnSearch[9] = (ImageButton) findViewById(R.id.btnSearch10);
        btnSearch[10] = (ImageButton) findViewById(R.id.btnSearch11);

        imgView.setOnClickListener(this);
        imgView_search.setOnClickListener(this);

        // 다이어로그 생성 밑 설정
        for (int i = 0; i < 11; i++) {
            final int no = i;
            builder[i] = new AlertDialog.Builder(this);

            builder[i].setItems(items[i], new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position) {
                    Intent myIntent = new Intent(getApplicationContext(), FloorActivity.class);
                    myIntent.putExtra("building", buildings[no]);
                    myIntent.putExtra("floor", items[no][position]);
                    startActivity(myIntent);
                }
            });
            builder[i].create();
            btnSearch[i].setTag(i);
            btnSearch[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.img_campus) { // 상단 클릭시 줌인/줌아웃 액티비티
            Intent intent = new Intent(getApplicationContext(), ImageActivity.class);
            intent.putExtra("img", R.drawable.image_campus2);
            startActivity(intent);
        } else if (v.getId() == R.id.imgView_search) {  // 메인화면으로 이동
            finish();
        }
        else {    // 기타 버튼 클릭시
            // 클릭한 버튼을 저장
            ImageButton b = (ImageButton)v;
            // 클릭한 버튼의 tag정보를 가져옴
            int tag = Integer.parseInt(v.getTag().toString());
            // 다이얼로그의 제목 지정
            for (int i = 0; i < 11; i++) {
                if (btnSearch[i].getId() == b.getId()) {
                    builder[tag].setTitle(buildings[i]);
                }
            }
            builder[tag].show();
        }
    }
}

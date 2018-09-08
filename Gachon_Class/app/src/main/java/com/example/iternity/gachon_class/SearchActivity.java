package com.example.iternity.gachon_class;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

public class SearchActivity extends AppCompatActivity {
    AlertDialog.Builder builder[] = new AlertDialog.Builder[9];
    Button btnSearch[] = new Button[9];
    int topFloor[] = {};
    int bottomFloor[] = {};
    final String[] items = {"5F", "4F", "3F", "2F", "1F", "B1F", "B2F", "B3F"};

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

        // 다이어로그 생성 밑 설정
        builder[0] = new AlertDialog.Builder(this);
        builder[0].setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                Toast.makeText(getApplicationContext(), items[position] + " 선택!", Toast.LENGTH_SHORT).show();
            }
        });
        builder[0].create();
    }
}

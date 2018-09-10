package com.example.iternity.gachon_class;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageButton btn_menu_1, btn_menu_2, btn_menu_3, btn_menu_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_menu_1 = (ImageButton) findViewById(R.id.btnMain01);
        btn_menu_2 = (ImageButton) findViewById(R.id.btnMain02);
        btn_menu_3 = (ImageButton) findViewById(R.id.btnMain03);
        btn_menu_4 = (ImageButton) findViewById(R.id.btnMain04);

        btn_menu_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(myIntent);
            }
        });
        btn_menu_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), ChatActivity.class);
                startActivity(myIntent);
            }
        });
        btn_menu_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), BookmarkActivity.class);
                startActivity(myIntent);
            }
        });
        btn_menu_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(myIntent);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 취소키를 누르면 다이어로그 창을 띄움
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this)
                    .setTitle("종료하기")
                    .setMessage(R.string.finish)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            moveTaskToBack(true);	// protect Other Activity after this Activity finish
                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    })
                    .setNegativeButton("취소", null)
                    .show();
        }

        return super.onKeyDown(keyCode, event);
    }

}

package com.example.iternity.gachon_class;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button btn_menu_1, btn_menu_2, btn_menu_3, btn_menu_4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_menu_1 = (Button) findViewById(R.id.btnMain01);
        btn_menu_2 = (Button) findViewById(R.id.btnMain02);
        btn_menu_3 = (Button) findViewById(R.id.btnMain03);
        btn_menu_4 = (Button) findViewById(R.id.btnMain04);

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


}

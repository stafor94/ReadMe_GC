package com.example.iternity.gachon_class;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SettingActivity extends AppCompatActivity {
    Button btn_logout, btn_reset_email;
    DBHelper_Login dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        dbHelper = new DBHelper_Login(this);

        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_reset_email = (Button) findViewById(R.id.btn_reset_email);

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(mIntent);
                finish();
            }
        });

        btn_reset_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHelper.delete();
                Intent mIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(mIntent);
                finish();
            }
        });
    }
}

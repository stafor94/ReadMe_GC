package com.example.iternity.gachon_class;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TimeTableActivity extends AppCompatActivity {
    LinearLayout layout_mon, layout_tue, layout_wed, layout_thu, layout_fri;
    final int CLASS = 1;
    final int DAY = 2;
    final int START = 3;
    final int END = 4;
    final int SUBJECT = 5;
    final int PROFESSOR = 6;
    final int MAJOR = 7;
    String building, classRoom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        init();
    }
    public void init() {
        layout_mon = (LinearLayout) findViewById(R.id.layout_mon);
        layout_tue = (LinearLayout) findViewById(R.id.layout_tue);
        layout_wed = (LinearLayout) findViewById(R.id.layout_wed);
        layout_thu = (LinearLayout) findViewById(R.id.layout_thu);
        layout_fri = (LinearLayout) findViewById(R.id.layout_fri);

        fillTable("월");
        fillTable("화");
        fillTable("수");
        fillTable("목");
        fillTable("금");
    }

    public void fillTable(String day) {
//        int tableSize, padding;
//        String start, end;
//        Intent myIntent = getIntent();
//        building = myIntent.getStringExtra("building");
//        classRoom = myIntent.getStringExtra("classroom");
//
//        tableSize = dbHelper.checkClassRoom(day, classRoom);
//        for (int i = 0; i < tableSize; i++) {
//            TextView tv = new TextView(this);
//            tv.setText(dbHelper.printData(day, classRoom, i, CLASS));
//            start = dbHelper.printData(day, classRoom, i, START);
//            end = dbHelper.printData(day, classRoom, i, END);
//            padding = Integer.parseInt(end) - Integer.parseInt(start);
//            tv.setPadding(0, padding * 10, 0, padding * 10);
//            layout_mon.addView(tv);
//        }
    }
}

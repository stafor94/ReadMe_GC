package com.example.iternity.gachon_class;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class BookmarkActivity extends AppCompatActivity {
    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;
    final String[] items = {"시간표 조회", "예약문의", "즐겨찾기 취소"};
    AlertDialog.Builder builder;
    private String nowSelect = null;

    DBHelper_Bookmark dbHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        mListView = (ListView) findViewById(R.id.listBookmark);

        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);

        dbHelper = new DBHelper_Bookmark(getApplicationContext());
        getBookmarkList();

        // 다이어로그 생성 밑 설정
        builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0:
                        Intent mIntent = new Intent(getApplicationContext(), TimeTableActivity.class);
                        mIntent.putExtra("lectureRoom", nowSelect);
                        startActivity(mIntent);
                        break;
                    case 1:// 예약문의
                        String building = nowSelect.split("-")[0];
                        CustomDialog_office customDialogOffice = new CustomDialog_office(BookmarkActivity.this);
                        customDialogOffice.callFunction(building);
                        break;
                    case 2:
                        if (nowSelect != null)
                            dbHelper.delete(nowSelect); // 해당 강의실을 북마크에서 삭제한다
                            getBookmarkList();
                        break;
                }
            }
        });
        builder.create();
    }

    private class ViewHolder {
        public Button mBtn;
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<String> mListData = new ArrayList<String>();

        public ListViewAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mListData.size();
        }

        @Override
        public Object getItem(int position) {
            return mListData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final int pos = position;
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.bookmarkitem, null);

                holder.mBtn = (Button) convertView.findViewById(R.id.bookmarkClassroom);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            final String mData = mListData.get(position);
            if (mData.equals("")) {
                Toast.makeText(getApplicationContext(), "등록된 강의실이 없습니다!", Toast.LENGTH_SHORT).show();
                finish();
            }

            holder.mBtn.setText(mData);
            holder.mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nowSelect = mData;  // 클릭한 강의실 이름을 저장
                    builder.setTitle(mData);
                    builder.show();
                }
            });

            return convertView;
        }

        public void addItem(String lectureRoom){
            String addInfo = lectureRoom;
            mListData.add(addInfo);
        }

        public void remove(int position){
            mListData.remove(position);
        }

        public void clear() { mListData.clear(); }
    }

    // SQLite에 저장된 북마크 정보를 리스트뷰로 가져와 보여준다
    private void getBookmarkList() {
        try {
            mAdapter.clear();   // ArrayList를 초기화한다.
            String[] lectureRoom = dbHelper.getResult().split(","); // SQLite에서 ","를 구분자로하여 북마크 등록된 강의실을 불러온다.
            for (int i = 0; i < lectureRoom.length; i++) {  // 각 강의실을 추가한다
                mAdapter.addItem(lectureRoom[i]);
                mAdapter.notifyDataSetChanged();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }
}

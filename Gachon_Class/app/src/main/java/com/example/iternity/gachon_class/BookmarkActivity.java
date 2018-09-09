package com.example.iternity.gachon_class;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import java.util.ArrayList;
import java.util.Collections;

public class BookmarkActivity extends AppCompatActivity {
    private ListView mListView = null;
    private ListViewAdapter mAdapter = null;
    private static String IP_ADDRESS = "192.168.43.111";
    private static String TAG = "phptest";
    private String mJsonString;
    final String[] items = {"수업정보 조회", "시간표 조회", "알림설정", "예약문의"};
    AlertDialog.Builder builder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmark);

        mListView = (ListView) findViewById(R.id.listBookmark);

        mAdapter = new ListViewAdapter(this);
        mListView.setAdapter(mAdapter);

        GetData task = new GetData();
        task.execute( "http://" + IP_ADDRESS + "/getjson.php", "");

        // 다이어로그 생성 밑 설정
        builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                Toast.makeText(getApplicationContext(), items[position] + " 선택!", Toast.LENGTH_SHORT).show();
                switch (position) {
                    case 0:
                        break;
                    case 1:
                        Intent myIntent = new Intent(getApplicationContext(), TimeTableActivity.class);
                        startActivity(myIntent);
                        break;
                    case 2:
                        break;
                    case 3:
                        break;
                }
            }
        });
        builder.create();
    }

    private class ViewHolder {
        public Button mBtn;
        public Button mDel;
    }

    private class ListViewAdapter extends BaseAdapter {
        private Context mContext = null;
        private ArrayList<ListData> mListData = new ArrayList<ListData>();

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
                holder.mDel = (Button) convertView.findViewById(R.id.bookmarkItemDel);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            final ListData mData = mListData.get(position);

            holder.mBtn.setText(mData.getBuilding() + '-' + mData.getNum());
            holder.mDel.setText("삭제");

            holder.mBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.setTitle(mData.getBuilding() + '-' + mData.getNum());
                    builder.show();
                }
            });

            return convertView;
        }

        public void addItem(String building, String num){
            ListData addInfo = new ListData();
            addInfo.setBuilding(building);
            addInfo.setNum(num);

            mListData.add(addInfo);
        }

        public void remove(int position){
            mListData.remove(position);
        }
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(BookmarkActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            Log.d(TAG, "response - " + result);

            if (result != null) {
                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];
            String postParameters = "country=" + params[1];

            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString().trim();
            } catch (Exception e) {
                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResult(){
        String TAG_JSON="room";
        String TAG_BUILDING = "building";
        String TAG_NUM ="num";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);

                String building = item.getString(TAG_BUILDING);
                String num = item.getString(TAG_NUM);

                Log.d(TAG, "item: " + building + '-' + num);
                mAdapter.addItem(building, num);
                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }
}

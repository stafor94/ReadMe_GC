package com.example.iternity.gachon_class;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class FloorActivity extends AppCompatActivity {
    private ListView mListView = null;
    private MyAdapter mAdapter = null;
    private static String IP_ADDRESS = "stafor.cafe24.com";
    private String mJsonString;
    private String building, floor, lectureRoom;
    Intent myIntent;
    AlertDialog.Builder builder;
    final String[] items = {"시간표 조회", "예약문의", "즐겨찾기 등록"};
    DBHelper_Bookmark dbHelper;
    MyTimer myTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor);

        /* 위젯과 멤버변수 참조 획득 */
        mListView = (ListView) findViewById(R.id.listFloor);
        /* 리스트뷰에 어댑터 등록 */
        mAdapter = new MyAdapter(this);
        mListView.setAdapter(mAdapter);

        // DBHelper
        dbHelper = new DBHelper_Bookmark(getApplicationContext());

        /* 건물명, 층 수 받기 */
        myIntent = getIntent();
        building = myIntent.getStringExtra("building");
        floor = myIntent.getStringExtra("floor");

        if (floor.charAt(0) == 'B') {
            floor = floor.substring(0,2);
        } else {
            floor = floor.substring(0,1);
        }
        // php연결
        GetData task = new GetData();
        task.execute( "http://" + IP_ADDRESS + "/getRooms.php", building, floor);

        // 다이어로그 생성 밑 설정
        builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                switch (position) {
                    case 0: // 시간표 조회
                        Intent mIntent = new Intent(getApplicationContext(), TimeTableActivity.class);
                        mIntent.putExtra("lectureRoom", lectureRoom);   // 강의실 명을 intent에 넣어 전달
                        startActivity(mIntent);
                        break;
                    case 1: // 예약문의
                        CustomDialog_office customDialogOffice = new CustomDialog_office(FloorActivity.this);
                        customDialogOffice.callFunction(building);
                        break;
                    case 2: // 즐겨찾기 등록
                        if (!dbHelper.isExist(lectureRoom)) {   // 중복된 강의실이 아니면
                            dbHelper.insert(lectureRoom);   // DB에 추가한다
                            Toast.makeText(getApplicationContext(), "즐겨찾기에 등록했습니다!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "이미 등록한 강의실 입니다.", Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        });
        builder.create();
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(FloorActivity.this,
                    "Please Wait", null, true, true);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (result != null) {
                mJsonString = result;
                showResult();
            } else {
                Toast.makeText(getApplicationContext(),"서버와 연결이 원활하지 않습니다.", Toast.LENGTH_LONG).show();
                finish();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            // 1. PHP 파일을 실행시킬 수 있는 주소와 전송할 데이터를 준비합니다.
            // POST 방식으로 데이터 전달시에는 데이터가 주소에 직접 입력되지 않습니다.
            String serverURL = params[0];

            // HTTP 메시지 본문에 포함되어 전송되기 때문에 따로 데이터를 준비해야 합니다.
            // 전송할 데이터는 “이름=값” 형식이며 여러 개를 보내야 할 경우에는 항목 사이에 &를 추가합니다.
            // 여기에 적어준 이름을 나중에 PHP에서 사용하여 값을 얻게 됩니다.
            String postParameters = "building=" + params[1] + "&floor=" + params[2];

            try {
                // 2. HttpURLConnection 클래스를 사용하여 POST 방식으로 데이터를 전송합니다.
                URL url = new URL(serverURL); // 주소가 저장된 변수를 이곳에 입력합니다.

                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(4000); //4초안에 응답이 오지 않으면 예외가 발생합니다.
                httpURLConnection.setConnectTimeout(4000); //4초안에 연결이 안되면 예외가 발생합니다.
                httpURLConnection.setRequestMethod("POST"); //요청 방식을 POST로 합니다.
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8")); //전송할 데이터가 저장된 변수를 이곳에 입력합니다.
                outputStream.flush();
                outputStream.close();

                // 응답을 읽습니다.
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("ReadMe", "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    // 정상적인 응답 데이터
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    // 에러 발생
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
                Log.d("ReadMe", "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResult() {
        myTimer = new MyTimer();
        String TAG_JSON ="lecture";
        String TAG_LectureRoom = "강의실";
        String TAG_Time = "강의시간";

        String DoW = myTimer.getDayOfWeek();    // 현재요일
        int CT = myTimer.getCurrentTime();  // 현재시간
        Log.d("ReadMe", "CT = " + CT);
        int[] mTimes;   // 시작시간과 종료시간을 담을 변수

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);
            String subject = "";

            for(int i=0; i< jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);

                String lectureRoom = item.getString(TAG_LectureRoom); // 강의실
                String time = item.getString(TAG_Time); // 강의시간
                mTimes = myTimer.convertTime(time); // 강의 시작시간, 종료시간

                if (!subject.equals(lectureRoom)) { // 강의실이 등록되어있지 않으면
                    mAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_running), lectureRoom);

                    subject = lectureRoom;
                }
                if (CT > mTimes[0] && CT < mTimes[1] && time.contains(DoW)) { // 현재시간이 수업시간이면
                    if (subject.equals(lectureRoom)) {
                        mAdapter.removeItem(mAdapter.getCount() - 1);
                    }
                    mAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_stop), lectureRoom);
                }


                mAdapter.notifyDataSetChanged();
            }
        } catch (JSONException e) {
            Log.d("ReadMe", "showResult : ", e);
        }

    }

    private class ViewHolder {
        public ImageView mImg;
        public TextView mTv;
    }

    private class MyAdapter extends BaseAdapter {
        private Context mContext = null;
        /* 아이템을 세트로 담기 위한 어레이 */
        private ArrayList<MyItem> mItems = new ArrayList<MyItem>();

        public MyAdapter(Context mContext) {
            super();
            this.mContext = mContext;
        }

        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public MyItem getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            final int pos = position;

            /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
            if (convertView == null) {
                holder = new ViewHolder();

                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.floor_listview_custom, null);

                holder.mImg = (ImageView) convertView.findViewById(R.id.iv_img);
                holder.mTv = (TextView) convertView.findViewById(R.id.tv_name);

                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }

            /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
            final MyItem myItem = getItem(position);

            /* 각 위젯에 세팅된 아이템을 뿌려준다 */
            holder.mImg.setImageDrawable(myItem.getIcon());
            holder.mTv.setText(myItem.getName());

            /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */
            holder.mTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    builder.setTitle(myItem.getName());
                    lectureRoom = myItem.getName();
                    builder.show();
                }
            });
            return convertView;
        }

        /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
        public void addItem(Drawable img, String name) {
            MyItem mItem = new MyItem();

            /* MyItem에 아이템을 setting한다. */
            mItem.setIcon(img);
            mItem.setName(name);

            /* mItems에 MyItem을 추가한다. */
            mItems.add(mItem);
        }

        public void removeItem(int index) {
            mItems.remove(index);
        }
    }

    private class MyItem {
        private Drawable icon;
        private String name;

        public Drawable getIcon() {
            return icon;
        }

        public void setIcon(Drawable icon) {
            this.icon = icon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }
}
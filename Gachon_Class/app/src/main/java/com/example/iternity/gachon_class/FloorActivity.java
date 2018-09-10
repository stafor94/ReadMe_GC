package com.example.iternity.gachon_class;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
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
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FloorActivity extends AppCompatActivity {
    private ListView mListView = null;
    private MyAdapter mAdapter = null;
    private static String IP_ADDRESS = "192.168.43.111";
    private String mJsonString;
    private String building, floor;
    Intent myIntent;
    URLConnector task;
    AlertDialog.Builder builder;
    final String[] items = {"수업정보 조회", "시간표 조회", "알림설정", "예약문의"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor);

        /* 위젯과 멤버변수 참조 획득 */
        mListView = (ListView) findViewById(R.id.listFloor);
        /* 리스트뷰에 어댑터 등록 */
        mAdapter = new MyAdapter(this);
        mListView.setAdapter(mAdapter);

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
        task.execute( "http://" + IP_ADDRESS + "/getjson3.php", building, floor);

        // 다이어로그 생성 밑 설정
        builder = new AlertDialog.Builder(this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                Toast.makeText(getApplicationContext(), items[position] + " 선택!", Toast.LENGTH_SHORT).show();
                switch (position) {
                    case 0:
                        // 커스텀 다이얼로그를 생성한다. 사용자가 만든 클래스이다.
                        CustomDialog customDialog = new CustomDialog(FloorActivity.this);

                        // 커스텀 다이얼로그를 호출한다.
                        customDialog.callFunction();
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

    private class GetData extends AsyncTask<String, String, String> {
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

                httpURLConnection.setReadTimeout(5000); //5초안에 응답이 오지 않으면 예외가 발생합니다.
                httpURLConnection.setConnectTimeout(5000); //5초안에 연결이 안되면 예외가 발생합니다.
                httpURLConnection.setRequestMethod("POST"); //요청 방식을 POST로 합니다.
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();

                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8")); //전송할 데이터가 저장된 변수를 이곳에 입력합니다.
                outputStream.flush();
                outputStream.close();

                // 응답을 읽습니다.
                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("TAG", "response code - " + responseStatusCode);

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
                Log.d("TAGo", "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResult(){
        String TAG_JSON="lecture";
        String TAG_ClassRoom = "강의실";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);

                String classRoom = item.getString(TAG_ClassRoom);
                mAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.icon_stop), classRoom);
                mAdapter.notifyDataSetChanged();

                Log.d("결과", "item: " + classRoom);
            }
        } catch (JSONException e) {
            Log.d("결과", "showResult : ", e);
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
//
//            /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
//            ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img) ;
//            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name) ;

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
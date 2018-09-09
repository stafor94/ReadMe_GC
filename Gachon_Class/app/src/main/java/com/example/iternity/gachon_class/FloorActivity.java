package com.example.iternity.gachon_class;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

public class FloorActivity extends AppCompatActivity {
    private ListView mListView;
    private static String IP_ADDRESS = "192.168.43.111";
    private String mJsonString;
    URLConnector task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* 위젯과 멤버변수 참조 획득 */
        mListView = (ListView)findViewById(R.id.listView);

        /* 아이템 추가 및 어댑터 등록 */
        dataSetting();
    }

    private void dataSetting(){
        MyAdapter mMyAdapter = new MyAdapter();
        // php연결
        GetData task = new GetData();
        task.execute( "http://" + IP_ADDRESS + "/getjson3.php", "");
        for (int i=0; i<10; i++) {
            mMyAdapter.addItem(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_bubble_self), "name_" + i, "contents_" + i);
        }

        /* 리스트뷰에 어댑터 등록 */
        mListView.setAdapter(mMyAdapter);
    }

    private class GetData extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            Log.d("TAG", "response - " + result);
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
                Log.d("TAG", "response code - " + responseStatusCode);

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
                Log.d("TAG", "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResult(){
        String TAG_JSON="room";
        String TAG_ClassRoom = "강의실";
        final Message outMessage=new Message();
        String myMsg = "[조회 결과]\n";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0; i<jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);

                String classRoom = item.getString(TAG_ClassRoom);

                Log.d("TAG", "item: " + classRoom);
                myMsg = myMsg + classRoom;
                if (i != jsonArray.length() - 1) {
                    myMsg = myMsg + " / ";
                }

            }
        } catch (JSONException e) {
            Log.d("TAG", "showResult : ", e);
        }

    }

    public class MyAdapter extends BaseAdapter {

        /* 아이템을 세트로 담기 위한 어레이 */
        private ArrayList<MyItem> mItems = new ArrayList<>();

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

            Context context = parent.getContext();

            /* 'listview_custom' Layout을 inflate하여 convertView 참조 획득 */
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.floor_listview_custom, parent, false);
            }

            /* 'listview_custom'에 정의된 위젯에 대한 참조 획득 */
            ImageView iv_img = (ImageView) convertView.findViewById(R.id.iv_img) ;
            TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name) ;
            TextView tv_contents = (TextView) convertView.findViewById(R.id.tv_contents) ;

            /* 각 리스트에 뿌려줄 아이템을 받아오는데 mMyItem 재활용 */
            MyItem myItem = getItem(position);

            /* 각 위젯에 세팅된 아이템을 뿌려준다 */
            iv_img.setImageDrawable(myItem.getIcon());
            tv_name.setText(myItem.getName());
            tv_contents.setText(myItem.getContents());

            /* (위젯에 대한 이벤트리스너를 지정하고 싶다면 여기에 작성하면된다..)  */


            return convertView;
        }

        /* 아이템 데이터 추가를 위한 함수. 자신이 원하는대로 작성 */
        public void addItem(Drawable img, String name, String contents) {

            MyItem mItem = new MyItem();

            /* MyItem에 아이템을 setting한다. */
            mItem.setIcon(img);
            mItem.setName(name);
            mItem.setContents(contents);

            /* mItems에 MyItem을 추가한다. */
            mItems.add(mItem);

        }
    }

    public class MyItem {

        private Drawable icon;
        private String name;
        private String contents;

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

        public String getContents() {
            return contents;
        }

        public void setContents(String contents) {
            this.contents = contents;
        }

    }
}
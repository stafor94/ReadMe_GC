package com.example.iternity.gachon_class;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import java.sql.Time;
import java.util.ArrayList;

public class TimeTableActivity extends AppCompatActivity {
    LinearLayout[] layout = new LinearLayout[5];
    private static String IP_ADDRESS = "192.168.43.111";
    private String mJsonString;
    final String TAG = "ReadMe";
    int[] lastTime = {0, 0, 0, 0, 0};    // 각 요일별 입력된 마지막 시간을 담는 변수

    int[] colors = {Color.rgb(0,0,0), Color.rgb(100,0,0), Color.rgb(0,100,0),
            Color.rgb(0,0,100), Color.rgb(50,50,0), Color.rgb(0,50,50),
            Color.rgb(50,0,50), Color.rgb(200,100,0), Color.rgb(100,0,200)};
    int colorCnt = 0;

    ArrayList<Lecture> lectures = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table);

        // Intent를 통해서 강의실 이름을 전달받는다
        Intent mIntent = getIntent();
        String lectureRoom = mIntent.getStringExtra("lectureRoom");

        // php연결
        GetData task = new GetData();
        task.execute( "http://" + IP_ADDRESS + "/getjson4.php", lectureRoom);

        init();
    }
    public void init() {
        layout[0] = (LinearLayout) findViewById(R.id.layout_mon);
        layout[1] = (LinearLayout) findViewById(R.id.layout_tue);
        layout[2] = (LinearLayout) findViewById(R.id.layout_wed);
        layout[3] = (LinearLayout) findViewById(R.id.layout_thu);
        layout[4] = (LinearLayout) findViewById(R.id.layout_fri);
    }

    public void fillTable(Lecture lecture) {
        float mScale = getResources().getDisplayMetrics().density;
        float alphaLen;
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        String time = lecture.getTime();
        TextView tv = new TextView(this);
        tv.setText(lecture.getSubject());
        tv.setBackgroundColor(colors[colorCnt++]);
        if(colorCnt == colors.length)
            colorCnt = 0;   // 색상의 index를 바꾼다

        String[] times = time.split(" ,");  // 각 강의시간을 2자리 문자열로 나눈다
        String start = times[0].substring(1);    // 강의 시작시간
        String end = times[times.length - 1].substring(1);   // 강의 마지막시간

        int len;    // 강의시간 길이
        int calHeight;  // textView 높이

        if (isNumeric(start) && isNumeric(end)) {
            param.topMargin = (int) ((Integer.parseInt(start) - 1 - lastTime[getDayCode(time)]) * 60 * mScale);
            len = Integer.parseInt(end) - Integer.parseInt(start) + 1; // 강의시간 길이
            calHeight = (int)(len * 60 * mScale);  // pixel -> dp 변환
            lastTime[getDayCode(time)] = Integer.parseInt(end); // 해당 요일에 현재까지 만들어진 마지막 시간
        } else if(start.contains("A")){
            alphaLen = 1.5f;
            lastTime[getDayCode(time)] = 2; // 해당 요일에 현재까지 만들어진 마지막 시간
            if (start.contains("B")) {
                alphaLen += 1.5f;
                lastTime[getDayCode(time)] = 3; // 해당 요일에 현재까지 만들어진 마지막 시간
            }
            calHeight = (int) (alphaLen * 60 * mScale);
        } else if(start.contains("D")) {
            alphaLen = 1.5f;
            lastTime[getDayCode(time)] = 6; // 해당 요일에 현재까지 만들어진 마지막 시간
            if (start.contains("E")) {
                alphaLen += 1.5f;
                lastTime[getDayCode(time)] = 8; // 해당 요일에 현재까지 만들어진 마지막 시간
            }
            calHeight = (int) (alphaLen * 60 * mScale);
        } else
            return;

        tv.setLayoutParams(param);
        tv.setHeight(calHeight);    // textView 높이 지정
        layout[getDayCode(time)].addView(tv);
    }

    public static boolean isNumeric(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

    public int getDayCode(String time) {
        if (time.contains("월")) return 0;
        else if (time.contains("화")) return 1;
        else if (time.contains("수")) return 2;
        else if (time.contains("목")) return 3;
        else if (time.contains("금")) return 4;
        else return 0;
    }

    public void showDetail() {
        // 커스텀 다이얼로그를 생성한다. 사용자가 만든 클래스이다.
        CustomDialog customDialog = new CustomDialog(TimeTableActivity.this);

        // 커스텀 다이얼로그를 호출한다.
        customDialog.callFunction();
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(TimeTableActivity.this,
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
            String postParameters = "lectureRoom=" + params[1];

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
                Log.d(TAG, "response code - " + responseStatusCode);

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
                Log.d(TAG, "GetData : Error ", e);
                errorString = e.toString();

                return null;
            }
        }
    }

    private void showResult(){
        String TAG_JSON="lecture";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);
                lectures.add(new Lecture(item.getString("학수번호"), item.getString("강의명"), item.getString("교수"), item.getString("강의시간")));
                fillTable(lectures.get(i));

                Log.d(TAG, "item: " + item.getString("학수번호") + " - " + item.getString("강의명") + " - " + item.getString("강의시간") + " - " + item.getString("교수"));
            }
            fillTable(lectures.get(0));
        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }

    }

    private class Lecture {
        private String academicNum;
        private String subject;
        private String professor;
        private String time;

        public Lecture(String academicNum, String subject, String professor, String time) {
            super();
            this.academicNum = academicNum;
            this.subject = subject;
            this.professor = professor;
            this.time = time;
        }

        public String getAcademicNum() {
            return academicNum;
        }

        public void setAcademicNum(String academicNum) {
            this.academicNum = academicNum;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public String getProfessor() {
            return professor;
        }

        public void setProfessor(String professor) {
            this.professor = professor;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }
}

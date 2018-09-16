package com.example.iternity.gachon_class;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
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

public class CustomDialog_office {
    private Context context;
    LinearLayout linearLayout;
    private static String IP_ADDRESS = "stafor.cafe24.com";
    private String mJsonString;
    Dialog dlg;

    public CustomDialog_office(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(String building) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_inquiry);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dlg.getWindow().getAttributes());
        lp.width = 800;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        // 커스텀 다이얼로그를 노출한다.
        dlg.show();
        Window window = dlg.getWindow();
        window.setAttributes(lp);
        window.setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));

        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        linearLayout = (LinearLayout) dlg.findViewById(R.id.linear_office);
        final Button btnOk = (Button) dlg.findViewById(R.id.dlg_ok);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });

        // php연결
        GetData task = new GetData();
        task.execute( "http://" + IP_ADDRESS + "/getOffice.php", building);
    }

    public void setTextView(String name, String tel) {
        float mScale = context.getResources().getDisplayMetrics().density;
        // Margin 설정을 위한 param
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        // 두 TextView를 담을 레이아웃 생성
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);

        // 학과명과 전화번호를 담은 TextView 생성
        TextView tv_name = new TextView(context);
        TextView tv_tel = new TextView(context);
        // 문자 지정
        tv_name.setText(name);
        tv_tel.setText(tel);
        // 문자 정렬
        tv_name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv_tel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        // 가중치 부여
        tv_name.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        tv_tel.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
        // 글자색 부여
        tv_name.setTextColor(Color.BLUE);
        tv_tel.setTextColor(Color.BLUE);

        // 레이아웃에 추가
        layout.addView(tv_name);
        layout.addView(tv_tel);

        param.bottomMargin = (int) (24 * mScale);  // Margin Bottom 설정
        layout.setLayoutParams(param);
        linearLayout.addView(layout);
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(context,
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
                Toast.makeText(context,"서버와 연결이 원활하지 않습니다.", Toast.LENGTH_LONG).show();
                dlg.dismiss();
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
            String postParameters = "building=" + params[1];

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
        String TAG_JSON ="office";
        String TAG_Department = "학과";
        String TAG_Tel = "전화번호";

        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

            for(int i=0; i< jsonArray.length(); i++){
                JSONObject item = jsonArray.getJSONObject(i);

                String name = item.getString(TAG_Department); // 학과
                String tel = item.getString(TAG_Tel); // 전화번호
                setTextView(name, tel);
            }
        } catch (JSONException e) {
            Log.d("ReadMe", "showResult : ", e);
        }

    }
}

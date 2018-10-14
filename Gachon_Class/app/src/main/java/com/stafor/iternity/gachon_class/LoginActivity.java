package com.stafor.iternity.gachon_class;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
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
import java.util.Random;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, Dialog.OnCancelListener {
    private static String IP_ADDRESS = "stafor.cafe24.com";
    private String mJsonString;
    LinearLayout layout_login, layout_signup;
    Button btn_login, btn_signup, btn_auth;
    CheckBox chk_auto;
    TextView time_counter, tv_link;
    EditText edit_login, edit_signup, edit_auth;

    LayoutInflater dialog;  // LayoutInflater
    View dialogLayout;   // layout을 담을 View
    Dialog authDialog;  // dialog

    String email = "";  // SQLite DB에 저장된 email
    String mEmail = ""; // 사용자가 입력한 email

    int authNum;    // 인증번호
    boolean auto = false;
    private boolean flag_login = false, flag_signup = false;
    CountDownTimer countDownTimer;
    final int MILLISINFUTURE = 300 * 1000;  // 총 시간 (300초 = 5분)
    final int COUNT_DOWN_INTERVAL = 1000;   // onTick 메소드를 호출할 간격 (1초)

    DBHelper_Login dbHelper_Login;
    DBHelper_Bookmark dbHelper_Bookmark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper_Login = new DBHelper_Login(this);
        dbHelper_Bookmark = new DBHelper_Bookmark(this);

        getMemberList();
        init();
    }

    private void init() {
        layout_login = (LinearLayout) findViewById(R.id.layout_login);
        layout_signup = (LinearLayout) findViewById(R.id.layout_signup);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_signup = (Button) findViewById(R.id.btn_signup);
        chk_auto = (CheckBox) findViewById(R.id.chk_auto);
        edit_login = (EditText) findViewById(R.id.edit_login);
        edit_signup = (EditText) findViewById(R.id.edit_signup);

        btn_login.setOnClickListener(this);
        btn_signup.setOnClickListener(this);
    }

    // SQLite에 저장된 북마크 정보를 리스트뷰로 가져와 보여준다
    private void getMemberList() {
        try {
            if (dbHelper_Login.isExist()) { // SQLite DB에 데이터가 존재하면
                email = dbHelper_Login.getResult().split("&")[0];
                auto = (dbHelper_Login.getResult().split("&")[1].equals("0")) ? false : true; // 0이면 false, 1이면 true

                if (auto)   // 자동 로그인 상태이면
                    login();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        Intent mIntent = new Intent(this, MainActivity.class);
        mIntent.putExtra("email", email);
        startActivity(mIntent);
        finish();
    }

    // 카운트 다운 메소드
    public void countDownTimer() {
        time_counter = (TextView) dialogLayout.findViewById(R.id.tv_time_counter);  // 시간을 표시할 TextView
        edit_auth = (EditText) dialogLayout.findViewById(R.id.edit_auth);   // 인증번호를 입력받는 EditText
        btn_auth = (Button) dialogLayout.findViewById(R.id.btn_auth);   // 인증번호 확인 버튼
        tv_link = (TextView) dialogLayout.findViewById(R.id.tv_link);

        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            @Override
            public void onTick(long millisUntilFinished) {
                long emailAuthCount = millisUntilFinished / 1000;

                if ((emailAuthCount - ((emailAuthCount / 60) * 60)) >= 10) { //초가 10보다 크면 그냥 출력
                    time_counter.setText((emailAuthCount / 60) + " : " + (emailAuthCount - ((emailAuthCount / 60) * 60)));
                } else { //초가 10보다 작으면 앞에 '0' 붙여서 같이 출력. ex) 02,03,04...
                    time_counter.setText((emailAuthCount / 60) + " : 0" + (emailAuthCount - ((emailAuthCount / 60) * 60)));
                }

                // emailAuthCount은 종료까지 남은 시간임. 1분 = 60초 되므로,
                // 분을 나타내기 위해서는 종료까지 남은 총 시간에 60을 나눠주면 그 몫이 분이 된다.
                // 분을 제외하고 남은 초를 나타내기 위해서는, (총 남은 시간 - (분*60) = 남은 초) 로 하면 된다.
            }

            @Override
            public void onFinish() {
                authDialog.cancel();
            }
        }.start();

        btn_auth.setOnClickListener(this);
        tv_link.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (flag_login) {   // 로그인 화면 활성화 시
                    String mEmail = edit_login.getText().toString();
                    if (mEmail.equals(email) && !mEmail.equals("")) {   // SQLite DB의 email과 같고, 공백이 아니면
                        if (chk_auto.isChecked()) { // 자동 로그인 체크시
                            dbHelper_Login.update(1);  // SQLite DB에서 정보 수정
                        }
                        login();    // 로그인 완료
                    } else {    // Guest 계정인지 확인
                        new GetGuest().execute( "http://" + IP_ADDRESS + "/getGuest.php", mEmail);
                    }
                } else {    // 로그인 화면 비활성화 시
                    flag_login = true;  // 로그인 열림 상태
                    flag_signup = false;  // 간편가입 닫힘 상태
                    layout_login.setVisibility(View.VISIBLE);   // 로그인 위젯을 보여준다
                    layout_signup.setVisibility(View.GONE);   // 간편가입 위젯을 감춘다
                    edit_login.setFocusableInTouchMode(true);   // focus 가능하게 함
                    edit_login.requestFocus();  // focus 요청
                }
                break;
            case R.id.btn_signup:
                if (dbHelper_Login.isExist()) {
                    new android.support.v7.app.AlertDialog.Builder(this)
                            .setTitle("기존 아이디 삭제")
                            .setMessage("이미 등록된 아이디가 있습니다. 기존 아이디를 삭제하겠습니까?")
                            .setPositiveButton("삭제", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dbHelper_Login.delete();    // 사용자 정보 제거
                                    dbHelper_Bookmark.clear();  // 즐겨찾기 정보 제거
                                    dbHelper_Login = new DBHelper_Login(getApplicationContext());
                                    email = "";

                                    flag_signup = true;  // 간편가입 열림 상태
                                    flag_login = false;  // 로그인 닫힘 상태
                                    layout_signup.setVisibility(View.VISIBLE);   // 간편가입 위젯을 보여준다
                                    layout_login.setVisibility(View.GONE);   // 로그인 위젯을 감춘다
                                    edit_signup.setFocusableInTouchMode(true);   // focus 가능하게 함
                                    edit_signup.requestFocus();  // focus 요청

                                    Toast.makeText(getApplicationContext(), "아이디가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                    break;
                }
                if (flag_signup) {
                    mEmail = edit_signup.getText().toString();

                    if (mEmail.equals("")) {    // 공백 입력 시
                        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.setMessage("입력된 이메일이 없습니다!");
                        alert.show();
                        edit_signup.requestFocus();  // focus 요청
                        break;
                    } else if (mEmail.contains("!") || mEmail.contains("@") || mEmail.contains("#") || mEmail.contains("$") || mEmail.contains("%") ||  // 특수문자 사용 시
                            mEmail.contains("^") || mEmail.contains("&") || mEmail.contains("*") || mEmail.contains("(") || mEmail.contains(")")){
                        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.setMessage("특수문자는 입력 불가능합니다!");
                        alert.show();
                        edit_signup.setText(""); // EditText를 비운다
                        edit_signup.requestFocus();  // focus 요청
                        break;
                    } else if (mEmail.equals(email)) { // 이미 등록된 이메일이면
                        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.setMessage("이미 등록된 이메일 입니다!");
                        alert.show();
                        break;
                    }
                    dialog = LayoutInflater.from(this);
                    dialogLayout = dialog.inflate(R.layout.dialog_auth, null); // LayoutInflater를 통해 XML에 정의된 Resource들을 View의 형태로 반환 시켜 줌
                    authDialog = new Dialog(this); // Dialog 객체 생성
                    authDialog.setContentView(dialogLayout); // Dialog에 inflate한 View를 탑재 하여줌
                    authDialog.setCanceledOnTouchOutside(false); // Dialog 바깥 부분을 선택해도 닫히지 않게 설정함.
                    authDialog.setOnCancelListener(this);   // Dialog를 닫을 때 일어날 일을 정의하기 위해 설정

                    new android.support.v7.app.AlertDialog.Builder(this)
                            .setTitle("웹메일 인증")
                            .setMessage("웹메일 인증을 진행하겠습니까?")
                            .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    authNum = new Random().nextInt(90000) + 10000; // 10001~99999
                                    new SendAuth().execute( "http://" + IP_ADDRESS + "/authMail.php", mEmail + "@gc.gachon.ac.kr", String.valueOf(authNum));
                                }
                            })
                            .setNegativeButton("취소", null)
                            .show();
                } else {
                    flag_signup = true;  // 간편가입 열림 상태
                    flag_login = false;  // 로그인 닫힘 상태
                    layout_signup.setVisibility(View.VISIBLE);   // 간편가입 위젯을 보여준다
                    layout_login.setVisibility(View.GONE);   // 로그인 위젯을 감춘다
                    edit_signup.setFocusableInTouchMode(true);   // focus 가능하게 함
                    edit_signup.requestFocus();  // focus 요청
                }
                break;
            case R.id.btn_auth:
                int user_input = 0;
                if (!edit_auth.getText().toString().equals("")) { //사용자 입력이 존재하면
                    user_input = Integer.parseInt(edit_auth.getText().toString());
                }

                if (user_input == authNum) {
                    new InsertMember().execute( "http://" + IP_ADDRESS + "/insertMember.php", edit_signup.getText().toString());
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.setMessage("잘못된 인증번호 입니다!");
                    alert.show();
                    edit_signup.setText(""); // EditText를 비운다
                    edit_signup.requestFocus();  // focus 요청
                }
                authDialog.cancel();

                break;
            case R.id.tv_link:
                Intent mIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://mail.gachon.ac.kr/pageLinker.ds?act=frame"));
                startActivity(mIntent);
                break;
        }
    }

    // 다이얼로그를 닫을 때 카운트 다운 타이머의 cancel() 메소드 호출
    @Override
    public void onCancel(DialogInterface dialog) {
        countDownTimer.cancel();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 취소키를 누르면 다이어로그 창을 띄움
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle("종료하기")
                    .setMessage(R.string.finish)
                    .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            moveTaskToBack(true);	// protect Other Activity after this Activity finish
                            finish();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        }
                    })
                    .setNegativeButton("취소", null)
                    .show();
        }

        return super.onKeyDown(keyCode, event);
    }

    private class InsertMember extends AsyncTask<String, Void, Boolean> {
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                dbHelper_Login.insert(edit_signup.getText().toString()); // SQLite DB에 해당 이메일을 생성
                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.setMessage("가입이 완료되었습니다!");
                alert.show();

                email = dbHelper_Login.getResult().split("&")[0]; // 이메일 정보 받아옴

                // 로그인 위젯을 띄우고 focus 요청
                flag_login = true;
                flag_signup = false;
                layout_login.setVisibility(View.VISIBLE);
                layout_signup.setVisibility(View.GONE);
                edit_login.setText(edit_signup.getText().toString());
                edit_login.requestFocus();
            } else {
                Toast.makeText(getApplicationContext(),"서버와 연결이 원활하지 않습니다!", Toast.LENGTH_LONG).show();
                try {
                    countDownTimer.cancel();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String serverURL = params[0];
            String postParameters = "email=" + params[1];

            try {
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
                    return true;
                }
                else{
                    // 에러 발생
                    inputStream = httpURLConnection.getErrorStream();
                    return false;
                }
            } catch (Exception e) {
                Log.d("ReadMe", "InsertMember : Error ", e);
                errorString = e.toString();

                return false;
            }
        }
    }

    private class SendAuth extends AsyncTask<String, Void, Boolean> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(LoginActivity.this,
                    "Please Wait", "서버에 연결하는 중 입니다.", true, false);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            progressDialog.dismiss();

            if (result) {
                countDownTimer();   // 카운트 다운 시작
                // Dialog 크기 조정
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(authDialog.getWindow().getAttributes());
                lp.width = 800;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                authDialog.show(); //Dialog를 나타내어 준다.
                Window window = authDialog.getWindow();
                window.setAttributes(lp);
                window.setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
            } else {
                Toast.makeText(getApplicationContext(),"서버와 연결이 원활하지 않습니다.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected Boolean doInBackground(String... params) {
            String serverURL = params[0];
            String postParameters = "email=" + params[1] + "&authNum=" + params[2];;

            try {
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
                    return true;
                }
                else{
                    // 에러 발생
                    inputStream = httpURLConnection.getErrorStream();
                    return false;
                }

            } catch (Exception e) {
                Log.d("ReadMe", "InsertMember : Error ", e);
                errorString = e.toString();

                return false;
            }
        }
    }

    private class GetGuest extends AsyncTask<String, Void, String> {
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                mJsonString = result;
                showResult();
            } else {
                Toast.makeText(getApplicationContext(),"서버와 연결이 원활하지 않습니다.", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected String doInBackground(String... params) {
            String serverURL = params[0];
            String postParameters = "email=" + params[1];

            try {
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
                Log.d("ReadMe", "InsertMember : Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }
    private void showResult() {
        String TAG_JSON ="GUEST";
        String TAG_DueDate = "만료일";

        try {
            if (!mJsonString.equals("")) {  // 로그인 성공 시
                JSONObject jsonObject = new JSONObject(mJsonString);
                JSONArray jsonArray = jsonObject.getJSONArray(TAG_JSON);

                JSONObject item = jsonArray.getJSONObject(0);
                String dueDate = item.getString(TAG_DueDate); // 만료일

                Toast.makeText(getApplicationContext(), "Guest 계정 만료일 : " + dueDate, Toast.LENGTH_LONG).show();
                email = "GUEST";
                login();    // 로그인 완료
            } else {    // 로그인 실패 시
                AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.setMessage("이메일 정보가 존재하지 않습니다!");
                alert.show();
                edit_login.setText(""); // EditText를 비운다
                edit_login.requestFocus();  // focus 요청
                //키보드 보이게 하는 부분
                InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        } catch (JSONException e) {
            Log.d("ReadMe", "showResult : ", e);
        }

    }
}

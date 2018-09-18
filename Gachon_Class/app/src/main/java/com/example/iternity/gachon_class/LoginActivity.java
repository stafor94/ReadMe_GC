package com.example.iternity.gachon_class;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, Dialog.OnCancelListener {
    LinearLayout layout_login, layout_signup;
    Button btn_login, btn_signup, btn_auth;
    CheckBox chk_auto;
    TextView time_counter;
    EditText edit_login, edit_signup, edit_auth;

    LayoutInflater dialog;  // LayoutInflater
    View dialogLayout;   // layout을 담을 View
    Dialog authDialog;  // dialog

    String email = "";  // SQLite DB에 저장된 email

    final int authNum = new Random().nextInt(90000) + 10000; // 10001~99999
    boolean auto = false;
    private boolean flag_login = false, flag_signup = false;
    CountDownTimer countDownTimer;
    final int MILLISINFUTURE = 180 * 1000;  // 총 시간 (300초 = 5분)
    final int COUNT_DOWN_INTERVAL = 1000;   // onTick 메소드를 호출할 간격 (1초)

    DBHelper_Login dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper_Login(this);

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
            if (dbHelper.isExist()) { // SQLite DB에 데이터가 존재하면
                email = dbHelper.getResult().split("&")[0];
                auto = (dbHelper.getResult().split("&")[1].equals("0")) ? false : true; // 0이면 false, 1이면 true

                if (auto)   // 자동 로그인 상태이면
                    login();
            }
        } catch (SQLiteException e) {
            e.printStackTrace();
        }
    }

    private void login() {
        Toast.makeText(getApplicationContext(), "로그인 성공!", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                if (flag_login) {
                    String mEmail = edit_login.getText().toString();
                    if (mEmail.equals(email) && !mEmail.equals("")) {   // SQLite DB의 email과 같고, 공백이 아니면
                        if (chk_auto.isChecked()) { // 자동 로그인 체크시
                            dbHelper.update(1);  // SQLite DB에서 정보 수정
                        }
                        login();    // 로그인 완료
                    } else {
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
                    }
                } else {
                    flag_login = true;  // 로그인 열림 상태
                    layout_login.setVisibility(View.VISIBLE);   // 로그인 위젯을 보여준다
                    edit_login.setFocusableInTouchMode(true);   // focus 가능하게 함
                    edit_login.requestFocus();  // focus 요청
                }
                break;
            case R.id.btn_signup:
                if (dbHelper.isExist()) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.setMessage("이미 등록된 이메일이 있습니다!");
                    alert.show();
                    break;
                }
                if (flag_signup) {
                    String mEmail = edit_signup.getText().toString();
                    if (mEmail.contains("gachon.ac.kr")) {
                        if (mEmail.equals(email)) { // 이미 등록된 이메일이면
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

                        // Dialog 크기 조정
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(authDialog.getWindow().getAttributes());
                        lp.width = 800;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        authDialog.show(); //Dialog를 나타내어 준다.
                        Window window = authDialog.getWindow();
                        window.setAttributes(lp);
                        window.setBackgroundDrawable(new ColorDrawable(Color.DKGRAY));
                        countDownTimer();
                        GMailSender sender = new GMailSender("ojland17@gmail.com", "dhlwnskfk");
                        Log.d("ReadMe", "To : " + edit_signup.getText().toString());
                        try {
                            sender.sendMail(
                                    "가천 Class 인증메일",    // Subject
                                    "인증번호 : " + authNum,    // Body
                                    "ojland17@gmail.com",   // Sender
                                    edit_signup.getText().toString() // Receiver
                            );
                        } catch (Exception e) {
                            Log.e("ReadMe", e.getMessage(), e);
                        }
                    } else if (mEmail.equals("")) {
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
                    } else {
                        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        alert.setMessage("가천대학교 이메일만 입력이 가능합니다!");
                        alert.show();
                        edit_signup.setText(""); // EditText를 비운다
                        edit_signup.requestFocus();  // focus 요청
                    }
                } else {
                    flag_signup = true;  // 로그인 열림 상태
                    layout_signup.setVisibility(View.VISIBLE);   // 로그인 위젯을 보여준다
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
                    dbHelper.insert(edit_signup.getText().toString()); // SQLite DB에 해당 이메일을 생성
                    AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.setMessage("가입이 완료되었습니다!");
                    alert.show();

                    // 로그인 위젯을 띄우고 focus 요청
                    flag_login = true;
                    flag_signup = false;
                    layout_login.setVisibility(View.VISIBLE);
                    layout_signup.setVisibility(View.GONE);
                    edit_login.requestFocus();
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
}

package com.stafor.iternity.gachon_class;

import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends AppCompatActivity {
    Button btn_logout, btn_reset_email;
    TextView tv_email, tv_devEmail, tv_version;
    DBHelper_Login dbHelper_Login;
    DBHelper_Bookmark dbHelper_Bookmark;

    String email;   // 사용자 이메일

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 사용자의 이메일 정보를 가져온다
        Intent mIntent = getIntent();
        email = mIntent.getStringExtra("email");

        dbHelper_Login = new DBHelper_Login(this);
        dbHelper_Bookmark = new DBHelper_Bookmark(this);

        tv_email = (TextView) findViewById(R.id.tv_email);
        tv_devEmail = (TextView) findViewById(R.id.tv_devEmail);
        tv_version = (TextView) findViewById(R.id.tv_version);
        btn_logout = (Button) findViewById(R.id.btn_logout);
        btn_reset_email = (Button) findViewById(R.id.btn_reset_email);

        tv_email.setText(email);    // 사용자 이메일 표시

        tv_devEmail.setOnLongClickListener(new View.OnLongClickListener () {
            @Override
            public boolean onLongClick(View v) {
                final ClipboardManager clipboardManager =  (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                clipboardManager.setText(tv_devEmail.getText());
                Toast.makeText(getApplicationContext(), "이메일 복사 완료!", Toast.LENGTH_SHORT).show();
                return false;
                }
        });

        tv_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alert.setMessage(R.string.version);
                alert.show();
            }
        });
        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // 로그아웃
                dbHelper_Login.update(0);
                AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
                alert.setCancelable(false); // Dialog 바깥 부분을 선택해도 닫히지 않게 설정함.
                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        restart();
                    }
                });
                alert.setMessage("자동 로그인이 해제되었습니다!");
                alert.show();
            }
        });

        btn_reset_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {   // 회원탈퇴
                new android.support.v7.app.AlertDialog.Builder(SettingActivity.this)
                        .setTitle("회원탈퇴")
                        .setMessage("정말로 탈퇴하시겠습니까?")
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dbHelper_Login.delete();    // 사용자 정보 제거
                                dbHelper_Bookmark.clear();  // 즐겨찾기 정보 제거

                                AlertDialog.Builder alert = new AlertDialog.Builder(SettingActivity.this);
                                alert.setCancelable(false); // Dialog 바깥 부분을 선택해도 닫히지 않게 설정함.
                                alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        restart();
                                    }
                                });
                                alert.setMessage("탈퇴되었습니다!");
                                alert.show();
                            }
                        })
                        .setNegativeButton("취소", null)
                        .show();
            }
        });
    }

    // App 재시작
    public void restart() {
        Intent i = getBaseContext().getPackageManager().
                getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(i);
    }
}

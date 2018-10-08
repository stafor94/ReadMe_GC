package com.stafor.iternity.gachon_class;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class CustomDialog_detail {
    private Context context;

    public CustomDialog_detail(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction(String academicNum, String subject, String time, String professor) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        final Dialog dlg = new Dialog(context);

        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_lecture);

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
        final TextView tv1 = (TextView) dlg.findViewById(R.id.dlg_name);
        final TextView tv2 = (TextView) dlg.findViewById(R.id.dlg_prof);
        final TextView tv3 = (TextView) dlg.findViewById(R.id.dlg_dept);
        final TextView tv4 = (TextView) dlg.findViewById(R.id.dlg_comp);
        tv1.setText(subject);
        tv2.setText(time);
        tv3.setText(professor);
        tv4.setText(academicNum);
        final Button btnOk = (Button) dlg.findViewById(R.id.dlg_ok);

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
    }
}

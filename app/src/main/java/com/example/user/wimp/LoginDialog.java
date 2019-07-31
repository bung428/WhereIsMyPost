package com.example.user.wimp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

public class LoginDialog extends Dialog implements View.OnClickListener {

    private Context context;
    public EditText id, pw;
    public Button okBtn, cancleBtn;

    private LoginDialogListner loginDialogListner;

    public interface LoginDialogListner{
        void onPositiveClicked(String id, String pw);
    }

    //호출할 리스너 초기화
    public void setDialogListener(LoginDialogListner loginDialogListner){
        this.loginDialogListner = loginDialogListner;
    }

    public LoginDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_dialog);

        id = findViewById(R.id.id);
        pw = findViewById(R.id.pwd);
        okBtn = findViewById(R.id.okButton);
        cancleBtn = findViewById(R.id.cancelButton);

        okBtn.setOnClickListener(this);
        cancleBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.okButton :
                loginDialogListner.onPositiveClicked(id.getText().toString(), pw.getText().toString());
                dismiss();
                break;
            case R.id.cancelButton :
                cancel();
                break;
        }
    }
}

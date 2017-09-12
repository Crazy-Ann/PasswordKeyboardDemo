package com.yjt.password.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.yjt.keyboard.DynamicKeyBoardView;
import com.yjt.keyboard.listener.OnKeyboardListener;
import com.yjt.password.PasswordView;
import com.yjt.password.constant.PasswordType;
import com.yjt.password.listener.OnPasswordChangedListener;

public class MainActivity extends AppCompatActivity implements OnPasswordChangedListener, OnKeyboardListener {

    private PasswordView pvPassowrd;
    private DynamicKeyBoardView dkbvPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pvPassowrd = (PasswordView) findViewById(R.id.pvPassowrd);
        dkbvPassword = (DynamicKeyBoardView) findViewById(R.id.dkbvPassword);

        pvPassowrd.setPasswordType(PasswordType.NUMBER);
        pvPassowrd.togglePasswordVisibility();
        //控制使用自定義鍵盤or系統鍵盤
        pvPassowrd.setHasAddTextChangedListener(false);

        pvPassowrd.setOnPasswordChangedListener(this);
        dkbvPassword.setOnKeyboardListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dkbvPassword.shuffleKeyboard();
    }

    @Override
    public void onPasswordChange(String password) {
        Log.i("onPasswordChange", password);
    }

    @Override
    public void onInputFinish(String password) {
        Toast.makeText(this, password, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onInsert(String data) {
        pvPassowrd.setPassword(pvPassowrd.getPassword() + data);
    }

    @Override
    public void onDelete() {
        pvPassowrd.onPasswordDelete();
    }
}

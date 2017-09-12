package com.yjt.password.listener;

import com.yjt.password.constant.PasswordType;

public interface PasswordListener {

    void setError(String error);

    String getPassword();

    void clearPassword();

    void setPassword(String password);

    void setPasswordVisibility(boolean isVisible);

    void togglePasswordVisibility();

    void setOnPasswordChangedListener(OnPasswordChangedListener onPasswordChangedListener);

    void setPasswordType(PasswordType passwordType);
}

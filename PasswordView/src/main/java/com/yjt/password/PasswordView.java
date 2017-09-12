package com.yjt.password;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yjt.password.constant.PasswordType;
import com.yjt.password.constant.Regex;
import com.yjt.password.listener.OnPasswordChangedListener;
import com.yjt.password.listener.OnPasswordDeleteListener;
import com.yjt.password.listener.PasswordListener;
import com.yjt.password.util.ViewUtil;

public class PasswordView extends LinearLayout implements PasswordListener, OnPasswordDeleteListener, View.OnClickListener, TextWatcher {

    private ColorStateList colorStateList;
    private int textSize = 14;
    private int lineWidth;
    private int lineColor;
    private int gridColor;
    private Drawable lineDrawable;
    private Drawable outerLineDrawable;
    private int passwordLength;
    private String passwordTransformation;
    private int passwordType;
    private String[] passwords;
    private TextView[] textViews;
    private boolean hasAddTextChangedListener;

    private PasswordEditText petPassword;
    private OnPasswordChangedListener onPasswordChangedListener;
    private PasswordTransformationMethod passwordTransformationMethod;

    public PasswordView(Context context) {
        super(context);
        setBackgroundDrawable(context);
        initialize(context, null, 0);
    }

    public PasswordView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context, attrs, 0);
    }

    public PasswordView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PasswordView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initialize(context, attrs, defStyleAttr);
    }

    private void initialize(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PasswordView, defStyleAttr, 0);
        colorStateList = typedArray.getColorStateList(R.styleable.PasswordView_password_textColor);
        if (colorStateList == null)
            colorStateList = ColorStateList.valueOf(getResources().getColor(android.R.color.primary_text_light));
        int textSize = typedArray.getDimensionPixelSize(R.styleable.PasswordView_password_textSize, -1);
        if (textSize != -1) {
            this.textSize = ViewUtil.getInstance().px2sp(context, textSize);
        }
        lineWidth = (int) typedArray.getDimension(R.styleable.PasswordView_password_lineWidth, ViewUtil.getInstance().dp2px(getContext(), 1));
        lineColor = typedArray.getColor(R.styleable.PasswordView_password_lineColor, Color.BLACK);
        gridColor = typedArray.getColor(R.styleable.PasswordView_password_gridColor, Color.WHITE);
        lineDrawable = typedArray.getDrawable(R.styleable.PasswordView_password_lineColor);
        if (lineDrawable == null) {
            lineDrawable = new ColorDrawable(lineColor);
        }
        outerLineDrawable = generateBackgroundDrawable();
        passwordLength = typedArray.getInt(R.styleable.PasswordView_password_length, 6);
        passwordTransformation = typedArray.getString(R.styleable.PasswordView_password_transformation);
        if (TextUtils.isEmpty(passwordTransformation)) {
            passwordTransformation = Regex.PASSWORD.getRegext();
        }
        passwordType = typedArray.getInt(R.styleable.PasswordView_password_type, 0);
        typedArray.recycle();
        passwords = new String[passwordLength];
        textViews = new TextView[passwordLength];
        setBackgroundDrawable(context);
    }

    private void setBackgroundDrawable(Context context) {
        super.setBackgroundDrawable(outerLineDrawable);
        setShowDividers(SHOW_DIVIDER_NONE);
        setOrientation(HORIZONTAL);
        passwordTransformationMethod = new CustomPasswordTransformationMethod(passwordTransformation);
        inflaterViews(context);
    }

    private void inflaterViews(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_password, this);

        petPassword = ViewUtil.getInstance().findView(this, R.id.petPassword);
        petPassword.setMaxEms(passwordLength);
        if (hasAddTextChangedListener) {
            petPassword.addTextChangedListener(this);
        }
        petPassword.setOnPasswordDeleteListener(this);
        setTextViewAttribute(petPassword);
        textViews[0] = petPassword;

        int index = 1;
        while (index < passwordLength) {
            View view = inflater.inflate(R.layout.view_divider, null);
            view.setBackgroundDrawable(lineDrawable);
            addView(view, new LayoutParams(lineWidth, LayoutParams.MATCH_PARENT));
            TextView textView = (TextView) inflater.inflate(R.layout.view_textview, null);
            setTextViewAttribute(textView);
            LayoutParams textViewParams = new LayoutParams(0, LayoutParams.MATCH_PARENT, 1f);
            addView(textView, textViewParams);
            textViews[index] = textView;
            index++;
        }
        setOnClickListener(this);
    }

    private void setTextViewAttribute(TextView textView) {
        if (colorStateList != null) {
            textView.setTextColor(colorStateList);
        }
        textView.setTextSize(textSize);
        switch (passwordType) {
            case 1:
                textView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            case 2:
                textView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                break;
            case 3:
                textView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD);
                break;
            default:
                textView.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                break;
        }
        textView.setTransformationMethod(passwordTransformationMethod);
    }

    private GradientDrawable generateBackgroundDrawable() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(gridColor);
        drawable.setStroke(lineWidth, lineColor);
        return drawable;
    }

    public void setHasAddTextChangedListener(boolean hasAddTextChangedListener) {
        this.hasAddTextChangedListener = hasAddTextChangedListener;
    }

    private void notifyTextChanged() {
        if (onPasswordChangedListener == null) {
            return;
        }
        String password = getPassword();
        onPasswordChangedListener.onPasswordChange(password);
        if (password.length() == passwordLength) {
            onPasswordChangedListener.onInputFinish(password);
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("state", super.onSaveInstanceState());
        bundle.putStringArray("passwords", passwords);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            passwords = bundle.getStringArray("passwords");
            state = bundle.getParcelable("state");
            petPassword.removeTextChangedListener(this);
            setPassword(getPassword());
            petPassword.addTextChangedListener(this);
        }
        super.onRestoreInstanceState(state);
    }

    @Override
    public void setError(String error) {
        petPassword.setError(error);
    }

    @Override
    public String getPassword() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < passwords.length; i++) {
            if (passwords[i] != null) {
                stringBuilder.append(passwords[i]);
            }
        }
        return stringBuilder.toString();
    }

    @Override
    public void clearPassword() {
        for (int i = 0; i < passwords.length; i++) {
            passwords[i] = null;
            textViews[i].setText(null);
        }
    }

    @Override
    public void setPassword(String password) {
        clearPassword();
        if (TextUtils.isEmpty(password)) {
            return;
        }
        char[] array = password.toCharArray();
        for (int i = 0; i < array.length; i++) {
            if (i < this.passwords.length) {
                this.passwords[i] = String.valueOf(array[i]);
                textViews[i].setText(this.passwords[i]);
            }
        }
        if (!hasAddTextChangedListener) {
            if (password.length() == passwordLength) {
                onPasswordChangedListener.onInputFinish(password);
            }
        }
    }

    @Override
    public void setPasswordVisibility(boolean visible) {
        for (TextView textView : textViews) {
            textView.setTransformationMethod(visible ? null : passwordTransformationMethod);
            if (textView instanceof EditText) {
                EditText editText = (EditText) textView;
                editText.setSelection(editText.getText().length());
            }
        }
    }

    @Override
    public void togglePasswordVisibility() {
        boolean currentVisible = getPassWordVisibility();
        setPasswordVisibility(!currentVisible);
    }

    private boolean getPassWordVisibility() {
        return textViews[0].getTransformationMethod() == null;
    }

    @Override
    public void setOnPasswordChangedListener(OnPasswordChangedListener onPasswordChangedListener) {
        this.onPasswordChangedListener = onPasswordChangedListener;
    }

    @Override
    public void setPasswordType(PasswordType passwordType) {
        boolean visible = getPassWordVisibility();
        int inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD;
        switch (passwordType) {
            case TEXT:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
                break;
            case TEXTVISIBLE:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD;
                break;
            case TEXTWEB:
                inputType = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD;
                break;
        }

        for (TextView textView : textViews) {
            textView.setInputType(inputType);
        }
        setPasswordVisibility(visible);
    }

    @Override
    public void setBackground(Drawable background) {
    }

    @Override
    public void setBackgroundColor(int color) {
    }

    @Override
    public void setBackgroundResource(int resid) {
    }

    @Override
    public void setBackgroundDrawable(Drawable background) {
    }

    @Override
    public void onPasswordDelete() {
        for (int i = passwords.length - 1; i >= 0; i--) {
            if (passwords[i] != null) {
                passwords[i] = null;
                textViews[i].setText(null);
                notifyTextChanged();
                break;
            } else {
                textViews[i].setText(null);
            }
        }
    }

    @Override
    public void onClick(View view) {
//        petPassword.setFocusable(true);
//        petPassword.setFocusableInTouchMode(true);
//        petPassword.requestFocus();
//        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(petPassword, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
        if (charSequence == null) {
            return;
        }
        String newCharSequence = charSequence.toString();
        if (newCharSequence.length() == 1) {
            passwords[0] = newCharSequence;
            notifyTextChanged();
        } else if (newCharSequence.length() == 2) {
            String substring = newCharSequence.substring(1);
            for (int i = 0; i < passwords.length; i++) {
                if (passwords[i] == null) {
                    passwords[i] = substring;
                    textViews[i].setText(substring);
                    notifyTextChanged();
                    break;
                }
            }
            petPassword.removeTextChangedListener(this);
            petPassword.setText(passwords[0]);
            if (petPassword.getText().length() >= 1) {
                petPassword.setSelection(1);
            }
            petPassword.addTextChangedListener(this);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}

package com.yjt.password;

import android.text.method.PasswordTransformationMethod;
import android.view.View;


public class CustomPasswordTransformationMethod extends PasswordTransformationMethod {

    private String transformation;

    public CustomPasswordTransformationMethod(String transformation) {
        this.transformation = transformation;
    }

    @Override
    public CharSequence getTransformation(CharSequence source, View view) {
        return new PasswordCharSequence(source);
    }

    private class PasswordCharSequence implements CharSequence {
        
        private CharSequence charSequence;

        public PasswordCharSequence(CharSequence source) {
            charSequence = source;
        }

        @Override
        public int length() {
            return charSequence.length();
        }

        @Override
        public char charAt(int index) {
            return transformation.charAt(0);
        }

        @Override
        public CharSequence subSequence(int start, int end) {
            return charSequence.subSequence(start, end);
        }
    }
}

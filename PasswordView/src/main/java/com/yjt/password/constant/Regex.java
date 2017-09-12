package com.yjt.password.constant;

public enum Regex {

    PASSWORD("●");

    private String mRegext;

    Regex(String regex) {
        this.mRegext = regex;
    }

    public String getRegext() {
        return mRegext;
    }

}

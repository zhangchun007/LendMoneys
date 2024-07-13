package com.haiercash.gouhua.base;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class BaseTextWatcher implements TextWatcher {

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        textWatch(1, s, start, count, after);
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        textWatch(2, s, start, before, count);
    }

    @Override
    public void afterTextChanged(Editable s) {
        textWatch(3, s, 0, 0, 0);
    }

    public abstract void textWatch(int type, CharSequence str, int s, int bc, int ac);
}

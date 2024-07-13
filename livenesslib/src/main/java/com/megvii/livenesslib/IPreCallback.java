package com.megvii.livenesslib;

import com.megvii.meglive_sdk.listener.PreCallback;

public interface IPreCallback extends PreCallback {
    void onPreStart();

    void onPreFinish(String var1, int var2, String var3);
}

package com.megvii.livenesslib;

import com.megvii.meglive_sdk.listener.DetectCallback;

public interface IDetectCallback extends DetectCallback {
    void onDetectFinish(String var1, int var2, String var3, String var4);
}

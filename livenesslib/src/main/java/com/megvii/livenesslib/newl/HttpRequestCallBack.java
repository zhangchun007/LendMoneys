package com.megvii.livenesslib.newl;


public interface HttpRequestCallBack {
    void onSuccess(String responseBody);

    void onFailure(int statusCode, byte[] responseBody);
}

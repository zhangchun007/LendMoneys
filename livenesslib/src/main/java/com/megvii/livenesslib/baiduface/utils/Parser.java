/*
 * Copyright (C) 2017 Baidu, Inc. All Rights Reserved.
 */
package com.megvii.livenesslib.baiduface.utils;


import com.megvii.livenesslib.baiduface.exception.FaceException;

/**
 * JSON解析
 * @param <T>
 */
public interface Parser<T> {
    T parse(String json) throws FaceException;
}

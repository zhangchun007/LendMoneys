package com.haiercash.gouhua.utils;

import android.content.Context;

import com.app.haiercash.base.utils.json.JsonUtils;
import com.haiercash.gouhua.beans.homepage.HomeConfig;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HomePersistenceUtil {
    private Context context;

    public HomePersistenceUtil(Context context) {
        this.context = context;
    }

    public HomeConfig readJson() {
        try {
            //InputStreamReader 将字节输入流转换为字符流
            //注意：address.json 是因人而异的
            InputStreamReader isr = new InputStreamReader(context.getAssets().open("persistencedata.json"), "UTF-8");
            //包装字符流,将字符流放入缓存里
            BufferedReader br = new BufferedReader(isr);
            String line;
            //StringBuilder和StringBuffer功能类似,存储字符串
            StringBuilder builder = new StringBuilder();
            while ((line = br.readLine()) != null) {
                //append 被选元素的结尾(仍然在内部)插入指定内容,缓存的内容依次存放到builder中
                builder.append(line);
            }
            br.close();
            isr.close();

            //builder.toString() 返回表示此序列中数据的字符串 (就是json串，后面自行解析就行)
            //这里我用的是fastJson,具体解析方式自行决定就好,数据格式也自行决定就好

            return JsonUtils.fromJson(builder.toString(), HomeConfig.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

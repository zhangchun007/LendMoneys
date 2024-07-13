package com.haiercash.gouhua.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.provider.CallLog;

import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.permission.PermissionUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.beans.risk.CallLogBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com<br/>
 * 版    本：1.0<br/>
 * 创建日期：2021/6/10<br/>
 * 描    述：抓取通话记录<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class CallsLog {
    /**
     * 通话记录:姓名 呼叫时间 号码  呼叫类型 通话时长
     */
    //static List<Object> callRecord = new ArrayList<>();//存对方手机号
    //static List<Object> callRecordStartTime = new ArrayList<>();//存通话起始时间
    //static List<Object> callRecordTime = new ArrayList<>();//存通话时长
    //static List<Object> callRecordTipe = new ArrayList<>();//存类型（主叫/补叫）
    public static List<Object> getCallLogList(Context context, String sendType, String applySeq) {
        List<Object> listMap = new ArrayList<>();
        if (SystemUtils.isAllow && PermissionUtils.getRequestPermission(context, Manifest.permission.READ_CALL_LOG)) {
            Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
            //  修复  java.lang.NullPointerException: Attempt to invoke interface method 'boolean android.database.Cursor.moveToFirst()' on a null object reference
            if (cursor == null) {
                return listMap;
            }
            String version = SystemUtils.getAppVersion(AppApplication.CONTEXT);
            String deviceId = SystemUtils.getDeviceID(context);
            while (cursor.moveToNext()) {
                Map<String, String> map = new HashMap<>();
                String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)); //号码
                long dateLong = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));//呼叫时间
                String time = TimeUtil.longToString(dateLong);
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)); //通话时长
                String name = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME));//联系人
                int type = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));//呼叫类型
                //通话时间+手机号+主叫被叫+通话时长
                String str = (CheckUtil.isEmpty(name) ? "" : name) + " " + time + " " + number + " " + type + " " + duration;
                map.put("dataTyp", "06");//风险数据类型
                map.put("content", EncryptUtil.simpleEncrypt(number));//风险数据内容\存对方手机号
//            map.put("remark1", );
                map.put("remark2", sendType);
//            map.put("remark3", );
                map.put("remark4", "AND");
                map.put("remark5", version);//APP版本占用
                map.put("reserved1", deviceId);//存设备号
                map.put("reserved2", time);//存通话起始时间
                map.put("reserved3", duration + "");//通话时长
                map.put("reserved4", type + "");//存类型（主叫/补叫） 来电:1,拨出:2,未接:3
                map.put("reserved5", name);//存通话人姓名
                map.put("reserved6", EncryptUtil.simpleEncrypt(applySeq));
                //map.put("reserved7", "");
                listMap.add(map);
                //callRecord.add(EncryptUtil.simpleEncrypt(number));
                //callRecordStartTime.add(EncryptUtil.simpleEncrypt(time));
                //callRecordTime.add(EncryptUtil.simpleEncrypt(duration + ""));
                //callRecordTipe.add(EncryptUtil.simpleEncrypt(type + ""));
                Logger.e("------>" + str + "\n");
            }
            cursor.close();
        }
        return listMap;
    }


    public static List<CallLogBean> getCallLogList(Context context) {
        List<CallLogBean> listMap = new ArrayList<>();
        if (SystemUtils.isAllow && PermissionUtils.getRequestPermission(context, Manifest.permission.READ_CALL_LOG)) {
            Cursor cursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, null);
            //  修复  java.lang.NullPointerException: Attempt to invoke interface method 'boolean android.database.Cursor.moveToFirst()' on a null object reference
            if (cursor == null) {
                return listMap;
            }
            while (cursor.moveToNext()) {
                CallLogBean bean = new CallLogBean();
                String number = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.NUMBER)); //号码
                long dateLong = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DATE));//呼叫时间
                String time = TimeUtil.longToString(dateLong);
                long duration = cursor.getLong(cursor.getColumnIndexOrThrow(CallLog.Calls.DURATION)); //通话时长
                String name = cursor.getString(cursor.getColumnIndexOrThrow(CallLog.Calls.CACHED_NAME));//联系人
                int type = cursor.getInt(cursor.getColumnIndexOrThrow(CallLog.Calls.TYPE));//呼叫类型
                //通话时间+手机号+主叫被叫+通话时长
                //String str = (CheckUtil.isEmpty(name) ? "" : name) + " " + time + " " + number + " " + type + " " + duration;
                bean.call_begin_time = time;
                bean.call_type = type + "";//存类型（主叫/补叫） 来电:1,拨出:2,未接:3
                bean.duration = duration + "";
                bean.to_phone_name = name;
                bean.to_phone_no = number;
                listMap.add(bean);
            }
            cursor.close();
        }
        return listMap;
    }
}

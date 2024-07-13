package com.haiercash.gouhua.service;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.app.haiercash.base.utils.system.Contact_Data;
import com.haiercash.gouhua.utils.RiskInfoUtils;

public class RequestRiskInfoServer extends IntentService {
    public RequestRiskInfoServer() {
        super("RequestRiskInfoServer");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null) {
            return;
        }
        String type = intent.getStringExtra("type");
        String sendType = intent.getStringExtra("remark");
        String applySeq = intent.getStringExtra("applySeq");
        if ("sms".equals(type)) {
            System.out.println("--->RequestRiskInfoServer--->" + type);
            //短信采集
            //GetMessageContent.getSmsFromPhone(this);
            //RiskInfoUtils.requestRiskInfoSms(this, sendType);
            //GetMessageContent.cleanMessageData();
        } else if ("contacts".equals(type)) {
            //联系人采集
            Contact_Data.getPhoneContacts(this);
            RiskInfoUtils.requestRiskInfoContacts(this, sendType, applySeq);
            Contact_Data.cleanContactData();
        } else if ("call_log".equals(type)) {
            //通话记录采集
            RiskInfoUtils.requestRiskInfoCallLog(this, sendType, applySeq);
        } else {
            //AppList
            RiskInfoUtils.requestRiskAppList(this, sendType, applySeq);
        }
    }
}

package com.haiercash.gouhua.uihelper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseDialog;
import com.haiercash.gouhua.utils.AIServer;

/**
 * Author: Sun<br/>
 * Date :    2018/11/16<br/>
 * FileName: CallPhoneNumberHelper<br/>
 * Description:<br/>
 * 4.0.0新增客服入口
 */
public class CallPhoneNumberHelper {

    public static BaseDialog callServiceNumber(final BaseActivity activity, String message, final String button2_call, String button1_cancel) {
        return callServiceNumber(activity, null, message, button2_call, button1_cancel, 2);
    }

    public static BaseDialog callServiceNumber(final BaseActivity activity, String title, String message, final String button2_call, String button1_cancel, int dialogStyleFlag) {
        return activity.showDialog(title, message, button1_cancel, button2_call, (dialog, which) -> {
            if (which == 2) {
                callPhone(activity);
            }
        }).setStandardStyle(dialogStyleFlag);
    }

    @SuppressLint("MissingPermission")
    public static void callPhone(final BaseActivity activity) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + activity.getString(R.string.about_us_phone_number)));
        activity.startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    public static void callPhone(final BaseActivity activity, String mobileNo) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + mobileNo));
        activity.startActivity(intent);
    }

    public static void callCustomerService(final BaseActivity activity, View view) {
        CustomerServicePopupWindow window = new CustomerServicePopupWindow(activity, null, pos -> {
            if (pos == CustomerServicePopupWindow.TYPE_PHONE) {
                callPhone(activity);
            } else if (pos == CustomerServicePopupWindow.TYPE_ONLINE) {
                AIServer.showAiWebServer(activity, "我的");
            }
        });
        window.showAtLocation(view);

    }

}

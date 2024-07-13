package com.haiercash.gouhua.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.adaptor.bean.ScenePopupBean;
import com.haiercash.gouhua.jsweb.JsWebBaseActivity;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.UMengUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author
 */
public class ScenePopupDialog extends Dialog implements View.OnClickListener, INetResult {
    public static final int GOODS_NAME_LIMIT = 10;
    private TextView mTvTitle;
    private TextView mTvSubTitle;
    private TextView mTvGoodName;
    private TextView mTvGoodPrice;
    private TextView mTvGiveUp;
    private Button mBtnContinue;
    private LinearLayout mLlContainer;
    private ScenePopupBean scenePopupBean;
    private  static ScenePopupDialog mScenePopupDialog;

    public synchronized static ScenePopupDialog getInstance(Context context){
        if (mScenePopupDialog == null){
            mScenePopupDialog = new ScenePopupDialog(context);
        }
        return mScenePopupDialog;
    }

    private ScenePopupDialog(@NonNull Context context) {
        this(context, 0);
    }

    private ScenePopupDialog(@NonNull Context context, int themeResId) {
        super(context, R.style.Theme_Light_FullScreenDialogAct);
        initView();
    }

    private void initView() {
        setCancelable(false);
        setContentView(R.layout.dialog_scene_popup);
        mLlContainer = findViewById(R.id.ll_container);
        mTvTitle = findViewById(R.id.tv_title);
        mTvSubTitle = findViewById(R.id.tv_sub_title);
        mTvGoodName = findViewById(R.id.tv_goods_name);
        mTvGoodPrice = findViewById(R.id.tv_goods_price);
        mTvGiveUp = findViewById(R.id.tv_give_up);
        mBtnContinue = findViewById(R.id.bt_continue);
        mTvGiveUp.setOnClickListener(this);
        mBtnContinue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_give_up) {
            NetHelper netHelper = new NetHelper();
            netHelper.getService(ApiUrl.ORDER_REJECT_MARK, null);
            dismiss();
            HashMap<String, Object> params = new HashMap<>(4);
            params.put("order_num",scenePopupBean.getOrderType());
            params.put("purpose",scenePopupBean.getSceneType());
            UMengUtil.commonClickEvent("sceneOrderRemind_exit","退出申请","首页",params,"");
        } else if (v.getId() == R.id.bt_continue) {
            Intent intent = new Intent(getContext(), JsWebBaseActivity.class);
            intent.putExtra("jumpKey", scenePopupBean.getH5Url());
            intent.putExtra("isCloseUmengBridging", scenePopupBean.isCloseUmengBridging());
            intent.putExtra("appUserAgent", scenePopupBean.getAppUserAgent());
            getContext().startActivity(intent);
            dismiss();
            HashMap<String, Object> params = new HashMap<>(4);
            params.put("order_num",scenePopupBean.getOrderType());
            params.put("purpose",scenePopupBean.getSceneType());
            params.put("button_name","继续申请");
            UMengUtil.commonClickEvent("sceneOrderRemind_continue","继续申请","首页",params,"");
        }
    }

    public void showScenePopup(ScenePopupBean scenePopupBean) {
        if (mScenePopupDialog != null && mScenePopupDialog.isShowing()) {
            return;
        }

        this.scenePopupBean = scenePopupBean;
        mTvTitle.setText(scenePopupBean.getPopupTitle());
        mTvSubTitle.setText(scenePopupBean.getPopupTitleDesc());
        List<String> goodList = scenePopupBean.getGoodsNameList();
        StringBuilder goodsName = new StringBuilder();
        if (goodList.size() > 0) {
            String s = goodList.get(0);
            if (s.length() > GOODS_NAME_LIMIT) {
                goodsName.append(s.substring(0, 10)).append("...");
            } else {
                goodsName.append(s);
            }
        }
        if (goodList.size() > 1) {
            goodsName.append("等");
        }
        mTvGoodName.setText(goodsName.toString());
        mTvGoodPrice.setText(String.format("%s元", scenePopupBean.getPrice()));
        this.show();
        HashMap<String, Object> params = new HashMap<>(4);
        params.put("order_num",scenePopupBean.getOrderType());
        params.put("purpose",scenePopupBean.getSceneType());
        UMengUtil.commonExposureEvent("sceneOrderRemind","首页","",params,"");
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        if (ApiUrl.ORDER_REJECT_MARK == url) {

        }
    }

    @Override
    public void onError(BasicResponse error, String url) {

    }
}

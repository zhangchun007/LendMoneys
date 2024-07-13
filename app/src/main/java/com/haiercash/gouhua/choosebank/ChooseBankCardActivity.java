package com.haiercash.gouhua.choosebank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.bankcard.AddBankCardInformaticaActivity;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.bankcard.QueryCardBean;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Author: Sun<br/>
 * Date :    2018/1/10<br/>
 * FileName: ChooseBankCardActivity<br/>
 * Description:<br/>
 */

public class ChooseBankCardActivity extends BaseActivity {
    private static final String DEFAULT_CARD = "default";
    private static final String TAG_TITLE = "title";
    @BindView(R.id.ll_des_card)
    LinearLayout llDesCard;
    @BindView(R.id.tv_title_card)
    TextView tvTitleCard;

    @Override
    protected int getLayout() {
        return R.layout.activity_cashier_choose_bankcard;
    }

    /**
     * 跳转至选择银行卡列表
     */
    public static void toChooseBankCard(BaseActivity context, QueryCardBean cardBean, int requestCode) {
        toChooseBankCard(context, "选择银行卡", cardBean, requestCode);
    }

    /**
     * 跳转至选择银行卡列表
     */
    public static void toChooseBankCard(BaseActivity context, String title, QueryCardBean cardBean, int requestCode) {
        Intent intent = new Intent(context, ChooseBankCardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        Bundle bundle = new Bundle();
        bundle.putSerializable(DEFAULT_CARD, cardBean);
        bundle.putSerializable(TAG_TITLE, title);
        intent.putExtras(bundle);
        context.startActivityForResult(intent, requestCode);
        context.overridePendingTransition(0, 0);
    }


    public static void toChooseBankCard(BaseActivity context, String title, int requestCode) {
        toChooseBankCard(context, title, null, requestCode);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (AddBankCardInformaticaActivity.ADD_BANK_REQUEST_CODE == requestCode && resultCode == RESULT_OK) {
            setResult(resultCode, data);
            finish();
            overridePendingTransition(0, 0);
        } else if (resultCode == RESULT_OK) {
            setResult(resultCode, data);
            finish();
            overridePendingTransition(0, 0);
        }
    }

//    /**
//     * 跳转至选择银行卡列表
//     *
//     * @param context
//     * @param cardBean
//     * @param requestCode
//     */
//    public static void toChooseBankCard(BaseFragment context, QueryCardBean cardBean, int requestCode) {
//        Intent intent = new Intent(context.getContext(), ChooseBankCardActivity.class);
//        Bundle bundle = new Bundle();
//        bundle.putSerializable(DEFAULT_CARD, cardBean);
//        intent.putExtras(bundle);
//        context.startActivityForResult(intent, requestCode);
//    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        initView();
    }


    private void initView() {
        if ("选择收款银行卡".equals(getIntent().getStringExtra(TAG_TITLE))) {
            llDesCard.setVisibility(View.VISIBLE);
        } else {
            llDesCard.setVisibility(View.GONE);
        }
        tvTitleCard.setText(getIntent().getStringExtra(TAG_TITLE));
    }


    @Override
    @OnClick({R.id.iv_back})
    public void onClick(View view) {
        if (view.getId() == R.id.iv_back) {
            finish();
            overridePendingTransition(0, 0);
        }
    }

}

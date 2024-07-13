package com.haiercash.gouhua.choosebank;

import android.app.Activity;
import android.content.Intent;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.bankcard.QueryCardBean;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.repayment.RepaymentConfirmActivity;
import com.haiercash.gouhua.utils.SpHp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * Author: Sun
 * Date :    2018/5/11
 * FileName: ChooseBankCardFragment
 * Description:
 */

public class ChooseBankCardFragment extends BaseFragment {
    @BindView(R.id.recyclerView_banklist)
    RecyclerView recyclerViewBanklist;
    private List<QueryCardBean> quCardList = new ArrayList<>();
    private CashierChooseAdapter adapter;
    private QueryCardBean mChooseCardBean;
    private static final String DEFAULT_CARD = "default";

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_choose_bankcard;
    }

    @Override
    protected void initEventAndData() {
        recyclerViewBanklist.setLayoutManager(new LinearLayoutManager(mActivity));
        AddBankFooterView bankFootView = new AddBankFooterView(this);
        bankFootView.initViewData(null);
        adapter = new CashierChooseAdapter(quCardList);
        adapter.addFooterView(bankFootView.getHeadView());
        recyclerViewBanklist.setAdapter(adapter);
        mChooseCardBean = (QueryCardBean) mActivity.getIntent().getSerializableExtra(DEFAULT_CARD);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            QueryCardBean cardBean = quCardList.get(position);
            if (cardBean.isEnable() && mActivity instanceof RepaymentConfirmActivity) {
                ((RepaymentConfirmActivity) mActivity).updateRepaymentConfirmUi(cardBean);
            } else if (cardBean.isEnable() && mActivity instanceof ChooseBankCardActivity) {
                Intent intent = new Intent();
                intent.putExtra("card", cardBean);
                intent.putExtra("isChooseBank", true);
                mActivity.setResult(Activity.RESULT_OK, intent);
                mActivity.finish();
            }
        });
    }

    public void updateView(String cardNum) {
        for (QueryCardBean queryCardBean : quCardList) {
            boolean choose = cardNum.equals(queryCardBean.getCardNo());
            queryCardBean.setChoosed(choose);
            if (choose) {
                this.mChooseCardBean = queryCardBean;
            }
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestCardList();
    }

    /*查询银行卡列表*/
    private void requestCardList() {
        //HttpDAO getCardHttpDAO = new HttpDAO(this, QueryCardBean.class, "info");
        String custNo = SpHp.getUser(SpKey.USER_CUSTNO);
        if (CheckUtil.isEmpty(custNo)) {
            return;
        }
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("custNo", custNo);
        netHelper.getService(ApiUrl.url_QueryBankCard, map, HashMap.class, true);
    }

    @Override
    public void onSuccess(Object response, String flag) {
        if (response == null) {
            onError("服务器开小差了，请稍后再试");
            return;
        }
        showProgress(false);
        if (ApiUrl.url_QueryBankCard.equals(flag)) {
            List<QueryCardBean> list = JsonUtils.fromJsonArray(response, "info", QueryCardBean.class);
            // int position = Integer.valueOf(SpHelper.getInstance().readMsgFromSp(SpKey.CHOOSE_SHOW_CONTROL, SpKey.CHECK_POSITION, "-1"));
            String cardNo = SpHelper.getInstance().readMsgFromSp(SpKey.CHOOSE_SHOW_CONTROL, SpKey.CHECK_POSITION, "");
            for (int i = 0; i < list.size(); i++) {
                QueryCardBean item = list.get(i);
                if (!hasThisCard(item)) {
                    //默认选择
                    if (cardNo.equals(item.getCardNo())) {
                        if (this.mChooseCardBean != null) {
                            this.mChooseCardBean.setChoosed(false);
                        }
                        this.mChooseCardBean = item;
                        this.mChooseCardBean.setChoosed(true);
                    }
                    quCardList.add(item);
                }
            }
            //如果没有，则选第一个
            if (this.mChooseCardBean == null && quCardList.size() > 0) {
                this.mChooseCardBean = quCardList.get(0);
                this.mChooseCardBean.setChoosed(true);
            }
            SpHelper.getInstance().deleteMsgFromSp(SpKey.CHOOSE_SHOW_CONTROL, SpKey.CHECK_POSITION);
            adapter.notifyDataSetChanged();
            //默认选择
            if (mActivity instanceof RepaymentConfirmActivity) {
                ((RepaymentConfirmActivity) mActivity).updateRepaymentConfirmUi(this.mChooseCardBean);
            }
        }
    }


    private boolean hasThisCard(QueryCardBean cardBean) {
        boolean result = false;
        for (QueryCardBean queryCardBean : quCardList) {
            if (queryCardBean.getCardNo().equals(cardBean.getCardNo())) {
                result = true;
                break;
            }
        }
        return result;
    }

}

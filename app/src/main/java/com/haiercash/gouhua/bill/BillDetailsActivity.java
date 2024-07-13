package com.haiercash.gouhua.bill;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.EncryptUtil;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CalendarsUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.time.TimeUtil;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.contract.CheckContractActivity;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.adaptor.bill.BillDetailAdapter;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.base.BaseDialog;
import com.haiercash.gouhua.beans.bankcard.QueryCardBean;
import com.haiercash.gouhua.beans.repayment.AllRePay;
import com.haiercash.gouhua.beans.repayment.ConList;
import com.haiercash.gouhua.beans.repayment.LmpmshdBean;
import com.haiercash.gouhua.choosebank.ChooseBankCardActivity;
import com.haiercash.gouhua.databinding.ActivityBillingDetailBinding;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.uihelper.ContractPop;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Author: Sun
 * Date :    2018/3/26
 * FileName: BillDetailsActivity
 * Description: 账单详情    R.layout.activity_billing_detail
 */
public class BillDetailsActivity extends BaseActivity {
    private ActivityBillingDetailBinding binding;
    private AllRePay mAllRePay;//账单信息
    private String applSeq;

    private BillDetailAdapter mAdapter;
    private final int TAG_REQUEST_CHOOSE_BANK = 0x10;
    private boolean isNotAllowPermission;
    private boolean isToastSuccess = true;
    private List<ConList> conLists;
    private ContractPop contractPop;

    @Override
    protected ActivityBillingDetailBinding initBinding(LayoutInflater inflater) {
        return binding = ActivityBillingDetailBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle("账单详情");

        initView();
        setonClickByViewId(R.id.ll_change, R.id.tv_contracts_list, R.id.tvBillOrderNum, R.id.rl_sys_card, R.id.llCalendarShow, R.id.llCalendar);
        showProgress(true);
        getBillDetails();
        getContractsList();
    }

    private void initView() {
        //展示提示一次
        if ("2".equals(SpHelper.getInstance().readMsgFromSp(SpKey.BILL_DETAIL_SHOW, SpKey.BILL_DETAIL_SHOW_STATE, "1"))) {
            binding.ivDetail.setVisibility(View.GONE);
        } else {
            binding.ivDetail.setVisibility(View.VISIBLE);
        }

        binding.llDetailSimple.setVisibility(View.GONE);
        mAdapter = new BillDetailAdapter(getIntent().getStringExtra("couponBindPerdNo"));
        binding.rvList.setNestedScrollingEnabled(false);
        binding.rvList.setHasFixedSize(true);
        binding.rvList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvList.setAdapter(mAdapter);
        mAdapter.addChildClickViewIds(R.id.iv_info);
        mAdapter.setOnItemChildClickListener((adapter, view, position) -> showExpenseInfo(position));
    }


    private void updateView() {
        //head
        binding.tvBillDate.setText(mAllRePay.getApplyDt());
        binding.tvBillOrderNum.setText(mAllRePay.getLoanNo());
        binding.tvBillMoney.setText(mAllRePay.getPrcpAmt());
        binding.tvBillMoney.setTypeface(FontCustom.getMediumFont(this));
        binding.tvBillTime.setText(UiUtil.getStr(mAllRePay.getApprvTnr(), "个月"));

        //借款信息
        binding.tvBillMoneyStyle.setText(mAllRePay.getMtdDesc());

        if (!TextUtils.isEmpty(mAllRePay.getApplCardNo())) {
            binding.tvBillMoneyMessage.setText(UiUtil.getStr(mAllRePay.getApplBankName(), "(", mAllRePay.getApplCardNo().substring(mAllRePay.getApplCardNo().length() - 4), ")"));
        }
        //系统扣款卡
        binding.tvSysCardNum.setText(mAllRePay.getRepayBankName());
        if (!TextUtils.isEmpty(mAllRePay.getRepayApplAcNo())) {
            binding.tvSysCardNum.setText(UiUtil.getStr(binding.tvSysCardNum.getText(), "(", mAllRePay.getRepayApplAcNo().substring(mAllRePay.getRepayApplAcNo().length() - 4), ")"));
        }
        //借据结清  Y:已结清，N：未结清
        if ("Y".equals(mAllRePay.getLoanSetlInd())) {
            binding.tvSysCardNum.setTextColor(ContextCompat.getColor(mContext, R.color.change_login_edt));
            binding.tvSysCardChange.setVisibility(View.GONE);
            binding.tvSysCardChange1.setVisibility(View.VISIBLE);
            binding.llCalendar.setVisibility(View.GONE);
            binding.llCalendarShow.setVisibility(View.GONE);
        } else {
            if (mAllRePay.getLoanNo().equals(SpHelper.getInstance().readMsgFromSp(SpKey.CREATE_CALENDAR, SpKey.CREATE_CALENDAR_SHOW))) {
                binding.llCalendar.setVisibility(View.VISIBLE);
                binding.llCalendarShow.setVisibility(View.GONE);
            } else if ("false".equals(SpHelper.getInstance().readMsgFromSp(SpKey.CREATE_CALENDAR, SpKey.CREATE_CALENDAR_SHOW))) {
                binding.llCalendar.setVisibility(View.GONE);
                binding.llCalendarShow.setVisibility(View.VISIBLE);
            } else {
                binding.llCalendar.setVisibility(View.GONE);
                binding.llCalendarShow.setVisibility(View.VISIBLE);
            }
        }
        //账单为结清状态时银行卡置灰并且icon不展示
        if (CheckUtil.isEmpty(mAllRePay.getUnRepayedNum()) || Integer.parseInt(mAllRePay.getUnRepayedNum()) == 0) {
            binding.tvSysCardChange.setVisibility(View.GONE);
            binding.tvSysCardChange1.setVisibility(View.GONE);
        }
        binding.tvLastTime.setText(UiUtil.getStr("已还", mAllRePay.getRepayedNum(), "期 |"));
        binding.tvHaveTime.setText(UiUtil.getStr(" 剩", mAllRePay.getUnRepayedNum(), "期"));
        mAdapter.setNewData(mAllRePay.getLmpmshdlist());
        //mAdapter.notifyDataSetChanged();
        // 修复notifyItemChanged 闪烁的问题
        if (binding.rvList.getItemAnimator() != null) {
            ((SimpleItemAnimator) binding.rvList.getItemAnimator()).setSupportsChangeAnimations(false);
            binding.rvList.getItemAnimator().setChangeDuration(0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAG_REQUEST_CHOOSE_BANK && resultCode == Activity.RESULT_OK) {
            tempCardBean = (QueryCardBean) data.getSerializableExtra("card");
            showDialogChangeSystem();
        }
    }

    /**
     * 修改系统代扣卡提示
     */
    private void showDialogChangeSystem() {
        BaseDialog dialog = BaseDialog.getDialog(this, "取消", (DialogInterface.OnClickListener) null, "确定", (dialog1, which) -> requestCardGrant()).setButtonTextColor(2, R.color.colorPrimary);
        String message = "您该笔账单的系统扣款卡将由" + binding.tvSysCardNum.getText().toString() + "更换为" + tempCardBean.getBankName() + "(" + tempCardBean.getCardNo().substring(tempCardBean.getCardNo().length() - 4) + ")";
        ViewGroup group = new LinearLayout(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_changge_systemcard, group);
        dialog.setDialogContentView(view);
        TextView textView = view.findViewById(R.id.tv_dialog_message);
        textView.setText(message);
        TextView agreement = view.findViewById(R.id.tv_message);
        agreement.setOnClickListener(v -> getAgreement());
        dialog.show();
    }


    /**
     * 银行卡授权协议
     */
    private void getAgreement() {
        String loanNo = getIntent().getStringExtra("loanNo");
        String url = ApiUrl.urlCardContracts + "?custNo=" + SpHp.getUser(SpKey.USER_CUSTNO) + "&cardNo=" + tempCardBean.getCardNo() + "&Authorization=" + "Bearer" + SpHp.getLogin(SpKey.LOGIN_ACCESSTOKEN)
                + "&channel=18&channel_no=42" + "&access_token=" + SpHp.getLogin(SpKey.LOGIN_ACCESSTOKEN) + "&custName=" + EncryptUtil.encrypt(SpHp.getUser(SpKey.USER_CUSTNAME)) + "&idCardNo=" + SpHp.getUser(SpKey.USER_CERTNO);
        if (tempCardBean != null && !TextUtils.isEmpty(tempCardBean.getBankName())) {
            url += "&bankName=" + EncryptUtil.encrypt(tempCardBean.getBankName());
        }

        if (tempCardBean != null && !TextUtils.isEmpty(tempCardBean.getCardNo())) {
            url += "&cardNo=" + tempCardBean.getCardNo();
            url += "&bankCardNo=" + tempCardBean.getCardNo();
        }
        if (!TextUtils.isEmpty(loanNo)) {
            url += "&loanNo=" + EncryptUtil.encrypt(loanNo);
        }
        WebSimpleFragment.WebService(this, url, "银行卡扣款授权书");
    }


    /**
     * 更改系统卡请求
     */
    private void changeSystemCard() {
        Map<String, String> map = new HashMap<>();
        map.put("loanNo", mAllRePay.getLoanNo());
        map.put("acctNo", RSAUtils.encryptByRSA(tempCardBean.getCardNo()));
        map.put("custNo", SpHp.getUser(SpKey.USER_CUSTNO));
        map.put("accBankCde", tempCardBean.getBankCode());
        map.put("acctBchDescc", tempCardBean.getBankName());
        if (mAllRePay.getLmpmshdlist() != null && mAllRePay.getLmpmshdlist().get(0) != null) {
            map.put("loanRepayDate", mAllRePay.getLmpmshdlist().get(0).getPsDueDt());
        }
        netHelper.postService(ApiUrl.URL_CHANGE_SYSTEM_CARD, map);
    }

    /**
     * 获取相关借款协议
     */
    private void getContractsList() {
        Map<String, String> map = new HashMap<>();
        if (!CheckUtil.isEmpty(applSeq)) {
            map.put("applseq", applSeq);
            netHelper.getService(ApiUrl.URL_CONTRACTS_LIST, map);
        }
    }


    /**
     * 根据流水号查询账单信息
     */
    private void getBillDetails() {
        applSeq = getIntent().getStringExtra("applSeq");
        String loanNo = getIntent().getStringExtra("loanNo");
        Map<String, String> map = new HashMap<>();
        map.put("applSeq", CheckUtil.isEmpty(applSeq) ? "" : EncryptUtil.simpleEncrypt(applSeq));
        //借据号还款记录详情过来
        map.put("loanNo", CheckUtil.isEmpty(loanNo) ? "" : EncryptUtil.simpleEncrypt(loanNo));
        netHelper.getService(ApiUrl.url_getBillDetails, map, AllRePay.class, true);
    }

    @Override
    public void onError(@SuppressWarnings("rawtypes") BasicResponse error, String url) {
        super.onError(error, url);
        binding.ivChange.setEnabled(false);
    }

    @Override
    public void onSuccess(Object success, String url) {
        if (isDestroyed() || isFinishing()) {
            return;
        }
        //获取账单信息
        if (ApiUrl.url_getBillDetails.equals(url)) {
            showProgress(false);
            if (success == null) {
                onError("服务器开小差了，请稍后再试");
                return;
            }
            mAllRePay = (AllRePay) success;
            if (TextUtils.isEmpty(applSeq)) {
                applSeq = mAllRePay.getApplSeq();
                getContractsList();
            }
            mAllRePay.setApplSeq(applSeq);
            updateView();
        } else if (ApiUrl.urlCardGrant.equals(url)) {
            changeSystemCard();
        } else if (ApiUrl.URL_CONTRACTS_LIST.equals(url)) {
            conLists = JsonUtils.fromJsonArray(success, ConList.class);
        } else {
            showProgress(false);
            mAllRePay.setRepayApplAcNo(tempCardBean.getCardNo());
            mAllRePay.setRepayBankName(tempCardBean.getBankName());
            binding.tvSysCardNum.setText(UiUtil.getStr(tempCardBean.getBankName(), "(", tempCardBean.getCardNo().substring(tempCardBean.getCardNo().length() - 4), ")"));
            UiUtil.toast("系统扣款卡更换成功");
        }
    }

    private void changeBillStyle() {
        if (binding.llDetailSimple.getVisibility() == View.GONE) {
            binding.llDetailSimple.setVisibility(View.VISIBLE);
            binding.ivChange.setImageResource(R.drawable.ic_bill_change_up);
        } else {
            binding.llDetailSimple.setVisibility(View.GONE);
            binding.ivChange.setImageResource(R.drawable.ic_bill_change_down);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ll_change) { //切换样式
            //展示提示一次
            SpHelper.getInstance().saveMsgToSp(SpKey.BILL_DETAIL_SHOW, SpKey.BILL_DETAIL_SHOW_STATE, "2");
            binding.ivDetail.setVisibility(View.GONE);
            changeBillStyle();
        } else if (view.getId() == R.id.tv_contracts_list) { //借款相关合同
            if (CheckUtil.isEmpty(conLists)) {
                goToCheckContract();
            } else {
                if (contractPop == null) {
                    contractPop = new ContractPop(this, conLists);
                    contractPop.setOnPosClickListener(new ContractPop.IClickListener() {
                        @Override
                        public void clickPos(int pos) {
                            //借款合同    stone 修改 借款合同必须以PDF文件显示
                            Intent intent = new Intent();
                            intent.setClass(BillDetailsActivity.this, CheckContractActivity.class);
                            intent.putExtra("applseq", applSeq);
                            intent.putExtra("docType", conLists.get(pos).getDocType());
                            intent.putExtra("docDesc", conLists.get(pos).getDocDesc());
                            startActivity(intent);
                        }
                    });
                }
                contractPop.showAtLocation(view);

            }
        } else if (view.getId() == R.id.tvBillOrderNum) {
            UiUtil.copyValueToShearPlate(this, binding.tvBillOrderNum.getText().toString());
        } else if (view.getId() == R.id.rl_sys_card) { //更换系统卡
            if (CheckUtil.isEmpty(mAllRePay.getUnRepayedNum()) || Integer.parseInt(mAllRePay.getUnRepayedNum()) == 0) {//已结清
                return;
            }
            if ("Y".equals(mAllRePay.getLoanSetlInd())) {
                return;
            }
            changeSysCard();
        } else if (view.getId() == R.id.llCalendar) {
            showBtn2Dialog("您已开启剩余还款日历提醒", "知道了", (dialogInterface, i) -> dialogInterface.dismiss()).setButtonTextColor(2, R.color.colorPrimary);
        } else if (view.getId() == R.id.llCalendarShow) {
            if (isNotAllowPermission) {
                showDialog("请在设置-够花中允许访问日历");
            } else {
                //是否开启日历权限
                requestPermission(aBoolean -> {
                    if (aBoolean) {
                        addCalendars();
                    } else {
                        isNotAllowPermission = true;
                    }
                }, R.string.permission_calendar, Manifest.permission.WRITE_CALENDAR, Manifest.permission.READ_CALENDAR);
            }
        }
    }

    private void addCalendars() {
        showDialog("是否开启剩余还款日历提醒", "取消", "开启", (dialogInterface, which) -> {
            if (which != 2) {
                return;
            }
            if (mAllRePay != null && mAllRePay.getLmpmshdlist() != null && mAllRePay.getLmpmshdlist().size() > 0) {
                for (int i = 0; i < mAllRePay.getLmpmshdlist().size(); i++) {
                    if (!"Y".equals(mAllRePay.getLmpmshdlist().get(i).getSetlInd())) {
                        String title = "【够花】还款提醒," + mAllRePay.getLmpmshdlist().get(i).getPsDueDt() + "前需还款" + CheckUtil.formattedAmount1(mAllRePay.getLmpmshdlist().get(i).getPsInstmAmt()) + "元";
                        String description = "最后还款日" + mAllRePay.getLmpmshdlist().get(i).getPsDueDt() + "前，还需还款" + CheckUtil.formattedAmount1(mAllRePay.getLmpmshdlist().get(i).getPsInstmAmt()) + "元，具体信息请及时打开够花查看及还款";
                        long startTime = TimeUtil.getDataBeforThreeDay(mAllRePay.getLmpmshdlist().get(i).getPsDueDt() + " 17:00");
                        long endTime = TimeUtil.getDataBeforThreeDay(mAllRePay.getLmpmshdlist().get(i).getPsDueDt() + " 20:00");
                        CalendarsUtils.addCalendarEventRemind(BillDetailsActivity.this, title, description + "https://shop.haiercash.com/static/gh/appDownloadMsg.html", startTime, endTime, 0, "按时还款享提额惊喜！请尽快结清近7日账单 避免逾期影响您的征信哟～", new CalendarsUtils.onCalendarRemindListener() {
                            @Override
                            public void onFailed(Status error_code) {
                                SpHelper.getInstance().saveMsgToSp(SpKey.CREATE_CALENDAR, SpKey.CREATE_CALENDAR_SHOW, "false");
                                if (isToastSuccess) {
                                    UiUtil.toast("添加失败，请稍后重试");
                                    isToastSuccess = false;
                                }
                                binding.llCalendar.setVisibility(View.GONE);
                                binding.llCalendarShow.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onSuccess() {
                                SpHelper.getInstance().saveMsgToSp(SpKey.CREATE_CALENDAR, SpKey.CREATE_CALENDAR_SHOW, mAllRePay.getLoanNo());
                                if (isToastSuccess) {
                                    UiUtil.toast("添加成功");
                                    isToastSuccess = false;
                                }
                                binding.llCalendar.setVisibility(View.VISIBLE);
                                binding.llCalendarShow.setVisibility(View.GONE);
                            }
                        });
                    }
                }
            }
        }).setStandardStyle(4);
    }

    private QueryCardBean tempCardBean;

    /*6.94银行卡变更授权书签章*/
    private void requestCardGrant() {
        showProgress(true);
        String loanNo = getIntent().getStringExtra("loanNo");
        Map<String, String> map = new HashMap<>();
        map.put("custNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));
        map.put("custName", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNAME)));
        map.put("certNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CERTNO)));
        map.put("cardNo", RSAUtils.encryptByRSA(tempCardBean.getCardNo()));
        map.put("bankName", RSAUtils.encryptByRSA(tempCardBean.getBankName()));
        map.put("loanNo", RSAUtils.encryptByRSA(loanNo));
        map.put("isRsa", "Y");
        netHelper.getService(ApiUrl.urlCardGrant, map);
    }

    private void changeSysCard() {
        QueryCardBean cardBean = new QueryCardBean();
        cardBean.setCardNo(mAllRePay.getRepayApplAcNo());
        cardBean.setBankName(mAllRePay.getRepayBankName());
        SpHelper.getInstance().saveMsgToSp(SpKey.CHOOSE_SHOW_CONTROL, SpKey.CHECK_POSITION, cardBean.getCardNo());
        ChooseBankCardActivity.toChooseBankCard(this, "更换系统代扣卡", cardBean, TAG_REQUEST_CHOOSE_BANK);
    }

    private void goToCheckContract() {
        //借款合同    stone 修改 借款合同必须以PDF文件显示
        Intent intent = new Intent();
        intent.setClass(this, CheckContractActivity.class);
        intent.putExtra("applseq", applSeq);
        startActivity(intent);
    }

    private int lineNumber = 0;
    private String rightMessage = "";
    private String leftMessage = "";

    /**
     * 费用详细说明
     */
    private void showExpenseInfo(int position) {
        leftMessage = "";
        rightMessage = "";
        lineNumber = 0;
        LmpmshdBean bean = mAdapter.getData().get(position);
        String title = "第" + (position + 1) + "期 / " + "共" + mAdapter.getData().size() + "期";
        BaseDialog dialog = BaseDialog.getDialog(this, title, "", "知道了", null).setButtonTextColor(1, R.color.colorPrimary);
        ViewGroup group = new LinearLayout(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_expenses_info, group);
        TextView rightText = view.findViewById(R.id.tv_dialog_extra_message);
        TextView leftText = view.findViewById(R.id.tv_dialog_message);
        //本金展示
        addTextValue("本金", bean.psPrcpAmt, true);
        // 利息展示
        addTextValue("利息", bean.psNormInt, true);
        // 手续费（应还手续费）---v5.0.7修改为分期利息
        addTextValue("分期利息", bean.psFeeAmtNew, false);//addTextValue("手续费", bean.psFeeAmt, true);
        // 罚息展示
        addTextValue("罚息", bean.psOdIntAmt, false);
        //// 违约金展示
        //addTextValue("违约金", bean.penalFeeAmt, false);
        ////滞纳金
        //addTextValue("滞纳金", bean.lateFeeAmt, false);
        //逾期手续费
        addTextValue("违约金及其他", bean.odFeeAmt, false);
        if ("N".equals(bean.getSetlInd()) && "Y".equals(bean.psOdInd)) {
            //已还本金
            addTextValue("已还本金", bean.setlPrcp, false);
            //已还利息
            addTextValue("已还利息", bean.setlNormInt, false);
            //已还罚息
            addTextValue("已还罚息", bean.setlOdIntAmt, false);
            ////已收违约金
            //addTextValue("已收违约金", bean.setlPenalFeeAmt, false);
            ////已还滞纳金
            //已收逾期手续费
            addTextValue("已收违约金及其他", bean.setlOdFeeAmt, false);
            //已还手续费----v5.0.7修改为分期利息
            addTextValue("已还分期利息", bean.setlFeeAmtNew, false);//addTextValue("已还手续费", bean.setlFeeAmt, false);
        }
        leftText.setText(leftMessage);
        rightText.setText(rightMessage);
        if (lineNumber > 6) {
            int maxHeight = UiUtil.dip2px(this, 205);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, maxHeight);
            view.setLayoutParams(params);
        }
        dialog.setDialogContentView(view);
        dialog.show();
    }

    private void addTextValue(String title, String money, boolean isMust) {
        if (isMust || (!CheckUtil.isEmpty(money) && !CheckUtil.isZero(money))) {
            leftMessage += title + "\n";
            rightMessage += CheckUtil.formattedAmount1(money) + "元\n";
            lineNumber++;
        }
    }
}

package com.haiercash.gouhua.activity.borrowmoney;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;

import com.app.haiercash.base.bean.ArrayBean;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.encrypt.HmacSHA256Utils;
import com.app.haiercash.base.utils.encrypt.RSAUtils;
import com.app.haiercash.base.utils.json.JsonUtils;
import com.app.haiercash.base.utils.log.Logger;
import com.app.haiercash.base.utils.picker.IPickerSelectCallBack;
import com.app.haiercash.base.utils.picker.PopSelectPick;
import com.app.haiercash.base.utils.sp.SpHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.activity.RepaymentTypeActivity;
import com.haiercash.gouhua.activity.comm.ResourceHelper;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.AppApplication;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.CheckIfMsgCompleteBean;
import com.haiercash.gouhua.beans.IrrRate;
import com.haiercash.gouhua.beans.ResourceBean;
import com.haiercash.gouhua.beans.SaveOrderBean;
import com.haiercash.gouhua.beans.bankcard.QueryCardBean;
import com.haiercash.gouhua.beans.borrowmoney.LoanRat;
import com.haiercash.gouhua.beans.borrowmoney.LoanRatAndProduct;
import com.haiercash.gouhua.beans.getpayss.GetPaySsAndCoupons;
import com.haiercash.gouhua.beans.getpayss.GetPaySsBeanRtn;
import com.haiercash.gouhua.beans.getpayss.LoanCoupon;
import com.haiercash.gouhua.choosebank.ChooseBankCardActivity;
import com.haiercash.gouhua.databinding.ActivityBorrowMoneyBinding;
import com.haiercash.gouhua.interfaces.OnPopClickListener;
import com.haiercash.gouhua.interfaces.SpKey;
import com.haiercash.gouhua.jsweb.JsWebPopActivity;
import com.haiercash.gouhua.jsweb.WebHelper;
import com.haiercash.gouhua.uihelper.BorrowMoneyHelper;
import com.haiercash.gouhua.uihelper.LoanCouponPop;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.SpHp;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;
import com.haiercash.gouhua.utils.WyDeviceIdUtils;
import com.haiercash.gouhua.view.NumBerKeyBoard;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2018/9/18<br/>
 * 描    述：去借钱<br/>
 * 修订历史：<br/>
 * ================================================================
 */
public class GoBorrowMoneyActivity extends BaseActivity implements IPickerSelectCallBack, BorrowMoneyHelper.ShowBankCardListener {
    private ActivityBorrowMoneyBinding binding;
    private static final String NAME = "借款";
    private String custNo;//客户编号
    private double maxAmount;//最大限额，和sp中存的最大额度一致
    private double minAmount;//最小限额，根据贷款品种确定
    private String inputAmount = "0.00";//用户输入的想要借的钱数
    private String loanUsage = "FMY"; // 借款用途
    private PopSelectPick popSelectPick;
    private final int requestCode = 0x10;
    private NumBerKeyBoard numBerKeyBoard;
    private QueryCardBean cardBean;
    private String totalNormInt;
    private String totalFeeAmt;
    private String mRepaymentTotalAmt;//试算的总额本息
    private volatile List<LoanCoupon> mLoanCouponList;//获取的免息券列表
    private volatile LoanCoupon mCurrentLoanCoupon;//当前选择的券对象
    private LoanCoupon mBannerVipCoupon;
    private LoanCouponPop mLoanCouponPop;
    private String mBannerName, mCid, mGroupId;
    private String mBannerJumpUrl;

    private String mtdCde;//还款方式
    private String applyTnr;//借款期限
    private String applyTnrTyp;//期限类型
    private String typCde;//贷款品种代码
    private String typLevelTwo;//贷款品种小类
    private List<ArrayBean> listData;
    private boolean isFromClick;  //如果用途获取失败，支持点击重新获取
    private boolean isSubmitButton;//是否为点击下一步按钮进行的试算

    //免息券的数量
    private String couponNumber = "";
    private String deductionItem;

    //是否是从资源位点击的
    private boolean isFromSourceClick = false;

    //可贷款的产品
    private List<LoanRatAndProduct> loanCodeBeanList;

    @Override
    protected ActivityBorrowMoneyBinding initBinding(LayoutInflater inflater) {
        return binding = ActivityBorrowMoneyBinding.inflate(inflater);
    }

    @Override
    protected void onViewCreated(@Nullable Bundle savedInstanceState) {
        setTitle(NAME);
        //setBackColorBarView(getResources().getColor(R.color.white));
        getTitleBarView().setAlpha(0);
        setLeftIvVisibility(false);
        //初始隐藏下方信息视图
        setContentInfoShow(false);
        setonClickByViewId(R.id.tvRepaymentPlan, R.id.tv_bank_select, R.id.tv_purpose, R.id.tv_confirm,
                R.id.headLeft, R.id.etLoanAmountBtn, R.id.tvCoupon, R.id.ivBanner,
                R.id.llBannerCoupon, R.id.tv_repay_type);
        SpHp.saveSpOther(SpKey.BORROW_BANNER_HAS_JUMP, "false");//初始化会员半弹窗页面的返回弹窗的标记
        custNo = SpHp.getUser(SpKey.USER_CUSTNO);
        maxAmount = CheckUtil.mDoubleValueOf(SpHp.getUser(SpKey.USER_EDU_AVLIABLE));
        binding.tvConfirm.setEnabled(false);
        setEdtHintValue("可借金额");
        binding.layoutBannerAndCoupon.tvBannerCoupon.setTypeface(FontCustom.getMediumFont(this));
        binding.etLoanAmount.setDeleteIconDrawableId(R.drawable.iv_delete_borrow);
        numBerKeyBoard = new NumBerKeyBoard(this, 1);
        numBerKeyBoard.showSoftKeyboard();
        numBerKeyBoard.hideKeyboard();
        cardBean = new QueryCardBean();
        if (CheckUtil.isEmpty(custNo)) {
            showDialog("账号异常，请退出重试");
        }
        getEnterKey();
        //CheckUtil.formatMoneyThousandth(binding.etLoanAmount);
        binding.etLoanAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(binding.etLoanAmount.getText())) {
                    //hint
                    binding.etLoanAmount.setTextSize(20);
                    binding.etLoanAmount.setTypeface(Typeface.DEFAULT);
                } else {
                    //normal
                    binding.etLoanAmount.setTextSize(45);
                    binding.etLoanAmount.setTypeface(FontCustom.getDinFont(mContext));
                }

                if (binding.etLoanAmount.getInputText().startsWith("0")) {
                    binding.etLoanAmount.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                CheckUtil.afterViewTextChanged(binding.etLoanAmount, this);
                inputAmount = binding.etLoanAmount.getInputText().replaceAll(",", "");
                showCouponNum(inputAmount);
                if (CheckUtil.isEmpty(inputAmount)) {
                    inputAmount = "0.00";
                }
                //失焦时才处理，非失焦时只处理错误提示
                setButtonEnable(!binding.etLoanAmount.hasFocus());
            }
        });
        binding.nesContent.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            private float maxY = 0, blankHeight = 0, headHeight = 0;

            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int[] location = new int[2];
                binding.ivLogo.getLocationInWindow(location);
                int locationY = location[1];
                if (headHeight == 0) {
                    headHeight = getTitleBarView().getHeight();
                }
                if (maxY < locationY) {
                    maxY = locationY;
                    blankHeight = maxY - headHeight;
                }
                float alpha = 1.00F - ((locationY - headHeight) / blankHeight);
                getTitleBarView().setAlpha(alpha);
            }
        });
        setBarLeftImage(0, (v -> finish()));
        binding.rgRateList.setOnCheckedChangeListener((group, checkedId) -> {
            TextView radioButton = findViewById(checkedId);
            clearEdtFocusAndRefreshRemark(radioButton);
            try {
                typCde = loanCodeBeanList.get(getCheckedPos()).getProducts().get(0).getTypCde();
                typLevelTwo = loanCodeBeanList.get(getCheckedPos()).getProducts().get(0).getTypLvlCde();
                minAmount = Double.parseDouble(loanCodeBeanList.get(getCheckedPos()).getProducts().get(0).getMinAmt());
                applyTnr = loanCodeBeanList.get(getCheckedPos()).getProducts().get(0).getTnrOpt();
                mtdCde = loanCodeBeanList.get(getCheckedPos()).getProducts().get(0).getMtdCde();
                binding.edLoanAmountTip.setHint(loanCodeBeanList.get(getCheckedPos()).getProducts().get(0).getMtdDesc());
                setRepaymentTypeUI();
            } catch (Exception e) {
                Logger.e("GoBorrowMoneyActivity中rgRateList的setOnCheckedChangeListener下split解析失败");
            }
            applyTnrTyp = applyTnr;
            setButtonEnable();
        });
        binding.etLoanAmount.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                binding.etLoanAmount.addIcon();
                numBerKeyBoard.attachTo((EditText) v);
            } else {
                binding.etLoanAmount.removeIcon();
                setButtonEnable();
            }
        });
        //查询贷款品种
        showProgress(true);
        Map<String, String> map = new HashMap<>();
        map.put("custNo", RSAUtils.encryptByRSA(custNo));
        //map.put("typGrp", "02");
        map.put("speSeq", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_EDU_SPE_SEQ)));
        netHelper.postService(ApiUrl.URL_GET_STANDARD_PRODUCT_INFO, map);

        Map<String, String> map1 = new HashMap<>();
        map1.put("custNo", RSAUtils.encryptByRSA(custNo));
        map1.put("acctName", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNAME)));
        map1.put("isRsa", "Y");
        netHelper.postService(ApiUrl.URL_GETBANCARDLIST, map1, HashMap.class, true);
        getPurpose();
    }

    /**
     * 获取优惠卷数量
     */
    private void getCouponsNum(List<LoanRatAndProduct> codeBeanList) {
        if (codeBeanList == null || codeBeanList.size() == 0) return;
        String tnrOptList = "";
        for (int i = 0; i < codeBeanList.size(); i++) {
            if (i != codeBeanList.size() - 1) {
                tnrOptList += codeBeanList.get(i).getTnrOpt();
                tnrOptList += ",";
            } else {
                tnrOptList += codeBeanList.get(i).getTnrOpt();
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("custNo", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_CUSTNO)));
        map.put("userId", RSAUtils.encryptByRSA(SpHp.getLogin(SpKey.LOGIN_USERID)));
        map.put("tnrOptList", tnrOptList);
        netHelper.postService(ApiUrl.POST_QUERY_COUPON_NUMBER, map);
    }

    /**
     * 输入框为空且优惠券数量大于0才展示
     */
    private void showCouponNum(String inputAmount) {
        if (TextUtils.isEmpty(inputAmount) && !CheckUtil.isZero(couponNumber)) {
            binding.layoutCouponNum.setVisibility(View.VISIBLE);
            binding.tvCouponNumTips.setText("您有" + couponNumber + "张免息券待使用");
            binding.edLoanAmountTip.setVisibility(View.GONE);
        } else {
            binding.layoutCouponNum.setVisibility(View.GONE);
            binding.edLoanAmountTip.setVisibility(View.VISIBLE);
        }
    }

    //获取借款用途
    private void getPurpose() {
        if (popSelectPick == null) {
            popSelectPick = new PopSelectPick(this, null, this);
        }
        showProgress(true);
        netHelper.getService(ApiUrl.URL_PURPOSE_MAP, null);
    }

    /**
     * 确定键
     */
    private void getEnterKey() {
        numBerKeyBoard.setOnOkClick(() -> {
            if (CheckUtil.isEmpty(inputAmount)) {
                return;
            }
            if (Double.parseDouble(inputAmount) > maxAmount || Double.parseDouble(inputAmount) < minAmount) {
                setMinOrMaxPriceText();
                return;
            }
            binding.etLoanAmount.clearFocus();
            numBerKeyBoard.hideKeyboard();
        });
    }

    @Override
    protected void onResume() {
//        binding.vExtra.setVisibility(View.GONE);//取消半透明蒙层
        super.onResume();
        //每次回来刷新最新状态（如果能刷新）
        if (isFromSourceClick) {
            setButtonEnable();
            isFromSourceClick = false;
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.etLoanAmountBtn) {//全部借出
            binding.etLoanAmount.setText(CheckUtil.formattedAmount(String.valueOf(maxAmount)));
        } else if (view.getId() == R.id.tvCoupon) {//免息券
            showLoanCouponPop(view);
        } else if (view.getId() == R.id.llBannerCoupon || view.getId() == R.id.ivBanner) {//资源位
            isFromSourceClick = true;
            if (view.getId() == R.id.llBannerCoupon) {
                HashMap<String, Object> map = new HashMap<>();
                map.put("free_ticket_money", mBannerVipCoupon != null ? mBannerVipCoupon.getDiscValue() : "");//待激活券金额
                UMengUtil.commonClickEvent("PlanBorrowHowMuchActivate_Click", "待激活", getPageName(), map, getPageCode());
            } else {
                UMengUtil.commonClickBannerEvent("PlanBorrowHowMuchMember_Click", getPageName(), mBannerName, mCid, mGroupId, getPageCode());
            }
            if (URLUtil.isNetworkUrl(mBannerJumpUrl)) {
                /* H5需要以下参数值:
                 * couponDisValue当前优惠金额，
                 * borrowBannerHasJump为true表示已经从banner跳转过，即不需要挽回弹窗，为false或没有值则需要挽回弹窗
                 */
                HashMap<String, String> map = new HashMap<>();
                if (mBannerVipCoupon != null) {
                    map.put("couponDisValue", mBannerVipCoupon.getDiscValue());
                }
                map.put("borrowBannerHasJump", SpHp.getOther(SpKey.BORROW_BANNER_HAS_JUMP, "false"));
                Intent intent = new Intent();
                intent.setClass(this, JsWebPopActivity.class);
                intent.putExtra("jumpKey", WebHelper.addUrlParam(mBannerJumpUrl, map));
//                binding.vExtra.setVisibility(View.VISIBLE);//半透明蒙层
                SpHp.saveSpOther(SpKey.BORROW_BANNER_HAS_JUMP, "true");
                startActivity(intent);
                //从下往上弹出
                //注意：overridePendingTransition一定要在startActivity 或者finish 之后调用，否则没有效果！而且可能会有各种其他问题！
//                overridePendingTransition(R.anim.activity_up_in, 0);
            } else {
                WebHelper.startActivityForUrl(this, mBannerJumpUrl);
            }
        } else if (view.getId() == R.id.tvRepaymentPlan) {//还款计划
            clearEdtFocusAndRefreshRemark(view);
            if (Double.parseDouble(inputAmount) > maxAmount || Double.parseDouble(inputAmount) < minAmount) {
                UiUtil.toast("可借金额范围" + CheckUtil.mIntegerParseInt(String.valueOf(minAmount)) + " - " + CheckUtil.mIntegerParseInt(String.valueOf(maxAmount)) + "元");
                return;
            }
            BorrowMoneyHelper.showRepaymentDetail(this, inputAmount, maxAmount, minAmount, typCde, typLevelTwo, applyTnrTyp, applyTnr, mtdCde, mCurrentLoanCoupon);
        } else if (view.getId() == R.id.tv_bank_select) {//选择银行卡
            SpHelper.getInstance().saveMsgToSp(SpKey.CHOOSE_SHOW_CONTROL, SpKey.CHECK_POSITION, cardBean.getCardNo());
            ChooseBankCardActivity.toChooseBankCard(this, "选择收款银行卡", null, requestCode);
        } else if (view.getId() == R.id.tv_purpose) {//贷款用途
            if (popSelectPick != null && listData != null) {
                popSelectPick.showSelect(binding.tvMinPrice, "借款用途", listData);
            } else {
                isFromClick = true;
                getPurpose();
            }
        } else if (view.getId() == R.id.tv_confirm) {//下一步
            clearEdtFocusAndRefreshRemark(view);
            if (BorrowMoneyHelper.confirmSaveOrder(inputAmount, maxAmount, minAmount, typCde, typLevelTwo, applyTnrTyp, applyTnr, cardBean.getCardNo(), GoBorrowMoneyActivity.this)) {
                showProgress(true);
                //要先通过 还款试算  取得息费
                getPaySs(true);
            } else {
                postConfirmEvent("false", BorrowMoneyHelper.errTv);
            }
        } else if (view.getId() == R.id.headLeft) {
            onBackPressed();
        } else if (view.getId() == R.id.tv_repay_type) {
            int checkoutPos = getCheckedPos();
            startActivityForResult(new Intent(this, RepaymentTypeActivity.class).putExtra("LoanRatList", JsonUtils.toJson(loanCodeBeanList.get(checkoutPos).getProducts())), 0x12);
        }
    }

    private void showLoanCouponPop(View view) {
        String couponNo = mCurrentLoanCoupon != null ? mCurrentLoanCoupon.getCouponNo() : null;
        if (mLoanCouponPop == null) {
            mLoanCouponPop = new LoanCouponPop(this, mLoanCouponList, couponNo, new OnPopClickListener() {
                @Override
                public void onViewClick(View v, int flagTag, Object obj) {
                    if (flagTag == 2) {
                        mCurrentLoanCoupon = obj instanceof LoanCoupon ? (LoanCoupon) obj : null;
                        setCouponUseUi(mCurrentLoanCoupon);
                        setRepaymentPlanUi(mRepaymentTotalAmt, mCurrentLoanCoupon);
                    }
                }
            });
        } else {
            mLoanCouponPop.updateData(mLoanCouponList, couponNo);
        }
        if (!mLoanCouponPop.isShowing()) {
            mLoanCouponPop.showAtLocation(view);
        }
    }

    private void setButtonEnable() {
        setButtonEnable(true);
    }

    private void setButtonEnable(boolean needPaySs) {
        boolean result = true;
        if (!setMinOrMaxPriceText(needPaySs)) {
            result = false;
        } else if (binding.rgRateList.getCheckedRadioButtonId() == -1) {
            result = false;
        }
//        else if (TextUtils.isEmpty(cardBean.getCardNo())) {
//            result = false;
//        }
        binding.tvConfirm.setTextColor(0xFFFFFFFF);
        binding.tvConfirm.setEnabled(result);
        if (needPaySs && result) { // 只有按钮允许点的时候才能进行试算
            getPaySs(false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && this.requestCode == requestCode) {
            QueryCardBean queryCardBean = (QueryCardBean) data.getSerializableExtra("card");
            boolean isChooseBank = data.getBooleanExtra("isChooseBank", false);
            if (queryCardBean != null) {
                cardBean.setBankCode(queryCardBean.getBankCode());
                cardBean.setCardNo(queryCardBean.getCardNo());
                cardBean.setBankName(queryCardBean.getBankName());
                cardBean.setChoosed(isChooseBank);
            }
            setBankDetail();
        } else if (requestCode == 50 && data != null && data.getBooleanExtra("closeThis", false)) {
            if (mLoanCouponPop != null) {
                mLoanCouponPop.dismiss();
            }
        } else if (resultCode == RESULT_OK && requestCode == 0x12 && data != null) {
            LoanRat loanRat = (LoanRat) data.getSerializableExtra("loanRat");
            if (loanRat != null) {
                RadioButton rbChecked = (RadioButton) findViewById(binding.rgRateList.getCheckedRadioButtonId());
                minAmount = Double.parseDouble(loanRat.getMinAmt());
                typCde = loanRat.getTypCde();
                typLevelTwo = loanRat.getTypLvlCde();
                mtdCde = loanRat.getMtdCde();
                String showDayRate = loanRat.getYearRateDesc();
                rbChecked.setTag(loanRat.getTnrOpt() + "#" + loanRat.getMtdCde() + "#" + showDayRate);
                setRepaymentTypeUI();
                setMinOrMaxPriceText();
                setButtonEnable();
            }
        }
    }


    /**
     * 保存利率和最小借款金额
     */
    private void updateCustLoanCodeAndRatCRM(List<LoanRatAndProduct> codeBeanList) {
        binding.rgRateList.removeAllViews();
        int margin = UiUtil.dip2px(this, 15);
        int screenW = SystemUtils.getDeviceWidth(this);
        int rbW = (screenW - 6 * margin) * 2 / 7 - 7 * 2;
        if (rbW <= UiUtil.dip2px(this, 90)) {//设置控件最小宽度为60DP
            rbW = UiUtil.dip2px(this, 90);
        }
        int max = 0;
        for (int i = 0; i < codeBeanList.size(); i++) {
            if (!"D".equals(codeBeanList.get(i).getTnrOpt())) {
                LoanRat bean = codeBeanList.get(i).getProducts().get(0);
                String showDayRate = bean.getYearRateDesc();
                if (minAmount <= 0F) {
                    //String intRatText = bean.getIntRate();
                    //SpHp.saveUser(SpKey.USER_INT_RAT, intRatText);
                    minAmount = Double.parseDouble(bean.getMinAmt());
                }
//                typCde = bean.getTypCde();
//                typLevelTwo = bean.getTypLvlCde();
                String tnrOpt = bean.getTnrOpt() + "期";
                RadioButton rb = new RadioButton(this);
                rb.setLayoutParams(new LinearLayout.LayoutParams(rbW, UiUtil.dip2px(this, 36)));
                rb.setText(tnrOpt);
                rb.setBackgroundResource(R.drawable.bg_rate_select);
                rb.setButtonDrawable(null);
                rb.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
                rb.setTextColor(ContextCompat.getColorStateList(this, R.color.rate_select));
                rb.setGravity(Gravity.CENTER);
                rb.setTag(bean.getTnrOpt() + "#" + bean.getMtdCde() + "#" + showDayRate);
                binding.rgRateList.addView(rb);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) binding.rgRateList.getChildAt(binding.rgRateList.getChildCount() - 1).getLayoutParams();
                layoutParams.setMarginEnd(margin);
                //默认选中最大期RadioButton
                try {
                    if (!CheckUtil.isEmpty(bean.getTnrOpt()) && Integer.parseInt(bean.getTnrOpt()) > max) {
                        max = Integer.parseInt(bean.getTnrOpt());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        typCde = codeBeanList.get(0).getProducts().get(0).getTypCde();
        typLevelTwo = codeBeanList.get(0).getProducts().get(0).getTypLvlCde();
        for (int i = 0; i < binding.rgRateList.getChildCount(); i++) {
            if (((RadioButton) binding.rgRateList.getChildAt(i)).getText().toString().equals(max + "期")) {
                ((RadioButton) binding.rgRateList.getChildAt(i)).setChecked(true);
                break;
            }
        }
        setEdtHintValue("至高可借" + CheckUtil.showNewThound(String.valueOf(maxAmount)) + "元");
    }

    /**
     * 还款计划试算
     */
    private void getPaySs(boolean isSubmitButton) {
        this.isSubmitButton = isSubmitButton;
        if (BorrowMoneyHelper.checkGetPaySs(inputAmount, maxAmount, minAmount, typCde, typLevelTwo,
                applyTnrTyp, applyTnr)) {
            showProgress(true);
            Map<String, String> map = new HashMap<>();
            map.put("apprvAmt", inputAmount);//贷款金额
            map.put("typCde", typCde);//贷款品种代码
            map.put("applyTnrTyp", applyTnrTyp);//期限类型
            map.put("applyTnr", applyTnr);//期限
            map.put("mtdCde", mtdCde);//还款方式     无则传空、新版APP标准化产品则必传
            map.put("custNo", RSAUtils.encryptByRSA(custNo));
            map.put("speSeq", RSAUtils.encryptByRSA(SpHp.getUser(SpKey.USER_EDU_SPE_SEQ)));
            netHelper.postService(ApiUrl.url_huankuanshisuan_useCoupon, map, GetPaySsAndCoupons.class);
        } else {
            binding.tvRepaymentPlan.setText("");
        }
    }


    /**
     * 获取选中的rb
     *
     * @return
     */
    private int getCheckedPos() {
        int checkoutPos = 0;
        for (int i = 0; i < binding.rgRateList.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) binding.rgRateList.getChildAt(i);
            if (radioButton.isChecked()) {
                checkoutPos = i;
                break;
            }
        }
        return checkoutPos;
    }

    //进来获取焦点
    private void requestFocusEdAmount() {
        //最开始为空才是最开始进来
        if (CheckUtil.isEmpty(binding.etLoanAmount.getInputText().replace(" ", ""))) {
            binding.etLoanAmount.requestFocus();
            binding.etLoanAmount.setSelection(0);
            numBerKeyBoard.showSoftKeyboard();
        }
    }

    @Override
    public void onSuccess(Object success, String url) {
        if (ApiUrl.URL_GETBANCARDLIST.equals(url)) {
            if (success != null) {
                List<QueryCardBean> list = JsonUtils.fromJsonArray(success, "info", QueryCardBean.class);
                if (!CheckUtil.isEmpty(list) && "SIGN_SUCCESS".equals(list.get(0).getSignStatus())) {
                    cardBean = list.get(0);
                    cardBean.setChoosed(true);
                }
            }
            setBankDetail();
        } else if (ApiUrl.URL_GET_STANDARD_PRODUCT_INFO.equals(url)) {
            loanCodeBeanList = JsonUtils.fromJsonArray(success, "tnrOptList", LoanRatAndProduct.class);
            if (CheckUtil.isEmpty(loanCodeBeanList)) {
                showDialog("没有匹配到可选择的贷款产品");
                return;
            }
            updateCustLoanCodeAndRatCRM(loanCodeBeanList);
            getCouponsNum(loanCodeBeanList);
            postExposure("PlanBorrowHowMuch_Exposure");
            showProgress(false);
            requestFocusEdAmount();
        } else if (ApiUrl.URL_PURPOSE_MAP.equals(url)) {
            showProgress(false);
            if (!isFromClick) {
                requestFocusEdAmount();
            }
            listData = BorrowMoneyHelper.getBorrowPurpose(success);
            if (listData != null && listData.size() > 0) {
                for (int i = 0; i < listData.size(); i++) {
                    if (listData.get(i).getType().equals("Y")) {
                        Collections.swap(listData, i, 0);
                        binding.tvPurpose.setText(listData.get(0).name);
                        break;
                    }
                }
                if (isFromClick) {
                    popSelectPick.showSelect(binding.tvMinPrice, "借款用途", listData);
                    isFromClick = false;
                }
            }
        } else if (ApiUrl.url_huankuanshisuan_useCoupon.equals(url)) {
            GetPaySsAndCoupons paySsAndCoupons = (GetPaySsAndCoupons) success;
            GetPaySsBeanRtn payRtn = paySsAndCoupons.getPaySsResult();
            if (isSubmitButton) {
                //利息总额
                totalNormInt = payRtn.getTotalNormInt();
                //费用总额
                totalFeeAmt = payRtn.getTotalFeeAmt();
                postConfirmEvent("true", "");
                //检验信息完整度
                netHelper.postService(ApiUrl.url_CheckIfMsgComplete_XJD, BorrowMoneyHelper.getCompleteParam(), CheckIfMsgCompleteBean.class, true);
            } else {
                if (mLoanCouponList != null) {
                    mLoanCouponList.clear();
                    mLoanCouponList = null;
                }
                mCurrentLoanCoupon = null;
                showProgress(false);
                requestFocusEdAmount();
                //无匹配普通免息券，未开通会员且配置了资源位图片，且当前借款条件符合会员免息券使用条件；由后端判断的
                if ("Y".equals(paySsAndCoupons.getNeedShowRes())) {
                    //需要展示会员资源位且有待激活VIP券时,调用资源位查询接口查询
                    if (!CheckUtil.isEmpty(paySsAndCoupons.getLoanCoupons()) && paySsAndCoupons.getLoanCoupons().get(0) != null) {
                        mBannerVipCoupon = paySsAndCoupons.getLoanCoupons().get(0);
                        ResourceHelper.requestBorrowResource(netHelper);
                    } else {
                        binding.layoutBannerAndCoupon.clBanner.setVisibility(View.GONE);
                    }
                } else {
                    binding.layoutBannerAndCoupon.clBanner.setVisibility(View.GONE);
                    mLoanCouponList = paySsAndCoupons.getLoanCoupons();
                    LoanCoupon bestCoupon = null;
                    //可能是账户内券，也可能是待激活VIP券（不可用）
                    if (mLoanCouponList != null) {
                        for (LoanCoupon loanCoupon : mLoanCouponList) {
                            if (bestCoupon == null) {
                                bestCoupon = loanCoupon;
                            } else if (loanCoupon != null && loanCoupon.isBest()) {
                                bestCoupon = loanCoupon;
                            }
                        }
                    }
                    mCurrentLoanCoupon = bestCoupon;
                }
                setCouponUi(mCurrentLoanCoupon);
                mRepaymentTotalAmt = payRtn.getRepaymentTotalAmt();
                setRepaymentPlanUi(mRepaymentTotalAmt, mCurrentLoanCoupon);
            }
        } else if (ApiUrl.POST_QUERY_RESOURCE_BY_PAGE.equals(url)) {
            ResourceBean resourceBean = (ResourceBean) success;
            if (resourceBean == null || CheckUtil.isEmpty(resourceBean.getContents())
                    || resourceBean.getContents().get(0) == null
                    || CheckUtil.isEmpty(resourceBean.getContents().get(0).getPicUrl())) {
                binding.layoutBannerAndCoupon.clBanner.setVisibility(View.GONE);
            } else {
                mBannerName = resourceBean.getResourceName();
                mCid = resourceBean.getCid();
                mGroupId = resourceBean.getGroupId();
                ResourceBean.ContentBean resourceContentBean = resourceBean.getContents().get(0);
                mBannerJumpUrl = resourceContentBean.getH5Url();
                GlideUtils.loadFit(GoBorrowMoneyActivity.this, binding.layoutBannerAndCoupon.ivBanner, resourceContentBean.getPicUrl());
                binding.layoutBannerAndCoupon.tvBannerCoupon.setText(mBannerVipCoupon != null ? UiUtil.getStr(CheckUtil.showThound(mBannerVipCoupon.getDiscValue()), "元") : "");
                binding.layoutBannerAndCoupon.clBanner.setVisibility(View.VISIBLE);
                postExposure("PlanBorrowHowMuchMember_Exposure");
            }
        } else if (ApiUrl.url_CheckIfMsgComplete_XJD.equals(url)) {
            if (BorrowMoneyHelper.checkIfMsgComplete(this, success)) {
                //保存订单
                if (mCurrentLoanCoupon != null)
                    deductionItem = mCurrentLoanCoupon.getDeductionItem();
                Map<String, String> map = BorrowMoneyHelper.getOrderParam(custNo, typCde, inputAmount, applyTnr, applyTnrTyp, totalNormInt, totalFeeAmt, cardBean.getCardNo(), loanUsage, mCurrentLoanCoupon, deductionItem);
                //设置网易设备指纹数据
                WyDeviceIdUtils.getInstance().getWyDeviceIDTokenFromNative(AppApplication.CONTEXT, (token, code, msg) -> {
                    if (!TextUtils.isEmpty(token)) {
                        map.put("ydunToken", token);
                    }
                    //必须放在map最后一行，是对整个map参数进行签名对
                    map.put("sign", HmacSHA256Utils.buildNeedSignValue(map));
                    netHelper.postService(ApiUrl.urlSaveOrderInfo, map, SaveOrderBean.class, true);
                });
            } else {
                showProgress(false);
            }
        } else if (ApiUrl.urlSaveOrderInfo.equals(url)) {
            showProgress(false);
            SaveOrderBean bean = (SaveOrderBean) success;
            //需重新签署个人征信授权书 Y 是 N 否
            SpHelper.getInstance().saveMsgToSp(SpKey.PERSONAL_CREDIT_AUTHORIZATION, SpKey.PERSONAL_CREDIT_AUTHORIZATION_STATUE, bean.getNeedResignCredit());
            //联合放款信息
            Intent intent = new Intent(this, BorrowAgreementActivity.class);
            intent.putExtra("bankCardNo", cardBean.getCardNo());
            intent.putExtra("bankcard", cardBean);
            intent.putExtra("tag", "XJD");
            intent.putExtra("orderNo", bean.getOrderNo());
            //新增可选免息券
            if (mCurrentLoanCoupon != null && !CheckUtil.isEmpty(mCurrentLoanCoupon.getCouponNo())) {
                intent.putExtra("coupon", mCurrentLoanCoupon);
            }
            intent.putExtra("applSeq", bean.getApplSeq());
            intent.putExtra("uniteLoanInfoDate", bean.getUniteLoanInfo());
            intent.putExtra("forcePreviewInfo", bean.getForcePreviewInfo());
            intent.putExtra("borrowMoney", inputAmount);
            intent.putExtra("borrowTnr", applyTnr);
            intent.putExtra("typCde", typCde);
            startActivity(intent);
        } else if (ApiUrl.url_getProductIrrInfo.equals(url)) {
            showProgress(false);
            Logger.e(JsonUtils.toJson(success));
            List<IrrRate> list = JsonUtils.fromJsonArray(success, "irrList", IrrRate.class);
            if (!list.isEmpty()) {
                showDialog("利率说明", String.format("年化利率：%s", list.get(0).irrRate),
                        null, "我知道了", (dialog, which) -> dialog.dismiss())
                        .setButtonTextColor(2, R.color.colorPrimary);
            } else {
                showDialog("服务器异常，请稍后重试");
            }
        } else if (ApiUrl.POST_QUERY_COUPON_NUMBER.equals(url)) {
            if (success != null) {
                try {
                    JSONObject jsonObject = new JSONObject(success.toString());
                    couponNumber = jsonObject.optString("number");
                    if (!CheckUtil.isZero(couponNumber)) {
                        binding.edLoanAmountTip.setVisibility(View.GONE);
                        binding.layoutCouponNum.setVisibility(View.VISIBLE);
                        binding.tvCouponNumTips.setText("您有" + couponNumber + "张免息券待使用");
                    } else {
                        binding.layoutCouponNum.setVisibility(View.GONE);
                        binding.edLoanAmountTip.setVisibility(View.VISIBLE);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onError(@SuppressWarnings("rawtypes") BasicResponse error, String url) {
        if (ApiUrl.URL_GETBANCARDLIST.equals(url)) {
            Log.e("setBankDetail", "bankName===3");
            binding.tvBankDetail.setText(SpannableStringUtils.getBuilder(this, "请选择")
                    .setForegroundColor(ContextCompat.getColor(mContext, R.color.ffd8d8d8)).create());
            return;
        } else if (ApiUrl.url_huankuanshisuan_useCoupon.equals(url)) {
            postConfirmEvent("false", "");
            if (!isSubmitButton) {
                setCouponUi(null);
                setRepaymentPlanUi("", null);
            }
        } else if (ApiUrl.POST_QUERY_COUPON_NUMBER.equals(url)) {
            binding.layoutCouponNum.setVisibility(View.GONE);
            return;
        }
        super.onError(error, url);
    }

    /**
     * 重置焦点并且重新设置顶部提示文案
     *
     * @param view 接受新焦点的View
     */
    private void clearEdtFocusAndRefreshRemark(View view) {
        if (binding.etLoanAmount.hasFocus()) {
            binding.etLoanAmount.clearFocus();
            numBerKeyBoard.hideKeyboard();
            view.requestFocus();
        }
    }

    /**
     * 设置 输入框的Hint值
     *
     * @param source Hint文案
     */
    private void setEdtHintValue(String source) {
        binding.etLoanAmount.setHint(source);
//        SpannableString ss = new SpannableString(source);//定义hint的值
//        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(20, true);//设置字体大小 true表示单位是sp
//        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        binding.etLoanAmount.setHint(new SpannedString(ss));
        //binding.etLoanAmount.setPadding(dp2Px(12), dp2Px(15), dp2Px(12), 0);
//        binding.etLoanAmount.setPadding(0, 0, 0, 0);
    }

    /**
     * 设置”输入的借款金额“下方的提示，有设置hint用以展示正常提示
     */
    private void setEdLoanAmountTip(String tip) {
        if (CheckUtil.isEmpty(tip)) {
            RadioButton rbChecked = (RadioButton) findViewById(binding.rgRateList.getCheckedRadioButtonId());
            if (rbChecked == null || TextUtils.isEmpty(rbChecked.getTag().toString())) return;
            String tnrAndMtdCde = String.valueOf(rbChecked.getTag());
            if (!TextUtils.isEmpty(tnrAndMtdCde)) {
                String[] splitArray = tnrAndMtdCde.split("#", 3);
                tip = splitArray[2];
            }
            binding.vLine.setBackgroundResource(R.color.color_e8eaef);
            binding.edLoanAmountTip.setTypeface(Typeface.DEFAULT);
            binding.edLoanAmountTip.setCompoundDrawables(null, null, null, null);
            binding.edLoanAmountTip.setTextColor(Color.parseColor("#303133"));
            binding.edLoanAmountTip.setText(tip);
        } else {
            binding.vLine.setBackgroundResource(R.color.color_ff5151);
            binding.edLoanAmountTip.setTypeface(Typeface.DEFAULT_BOLD);
            Drawable drawableLeft = ContextCompat.getDrawable(this, R.drawable.ic_tip_error);
            if (drawableLeft != null) {
                drawableLeft.setBounds(0, 0, UiUtil.dip2px(this, 14), UiUtil.dip2px(this, 14));
            }
            binding.edLoanAmountTip.setCompoundDrawables(drawableLeft, null, null, null);
            binding.edLoanAmountTip.setTextColor(0xffff5151);
            binding.edLoanAmountTip.setText(tip);
        }
    }

    private boolean setMinOrMaxPriceText() {
        return setMinOrMaxPriceText(true);
    }

    /**
     * 设置价格提示文案,低于最低则提示，高于最高则提示
     */
    private boolean setMinOrMaxPriceText(boolean needShow) {
        String mpText = "计划借多少";
        String tip = "";
        boolean result = false;
        if (CheckUtil.isEmpty(inputAmount) || Double.parseDouble(inputAmount) <= 0) {
            if (needShow) {
                setContentInfoShow(false);
            }
        } else if (Double.parseDouble(inputAmount) < minAmount) {
            tip = "起借金额" + CheckUtil.showNewThound(String.valueOf(minAmount)).replace(".00", "") + "元";
            if (needShow) {
                setContentInfoShow(false);
            }
        } else if (Double.parseDouble(inputAmount) > maxAmount) {
            tip = "至高可借金额" + CheckUtil.showNewThound(String.valueOf(maxAmount)).replace(".00", "") + "元";
            if (needShow) {
                setContentInfoShow(false);
            }
        } else {
            mpText = "可借范围" + CheckUtil.formattedAmount1(String.valueOf(minAmount)) + "-" + CheckUtil.formattedAmount1(String.valueOf(maxAmount)) + "元";
            //输入的借款金额无问题，下方内容才会显示出来
            if (needShow) {
                setContentInfoShow(true);
            }
            result = true;
        }
        binding.tvMinPrice.setText(mpText);
        setEdLoanAmountTip(tip);
        return result;
    }

    /**
     * 底部内容显示与否
     */
    private void setContentInfoShow(boolean show) {
        binding.llRateSelect.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.vFirstGroup.setVisibility(show ? View.VISIBLE : View.GONE);
        binding.vSecondGroup.setVisibility(show ? View.VISIBLE : View.GONE);
        setRepaymentTypeUI();
    }

    /**
     * 设置用券UI
     *
     * @param loanCoupon 可用券
     */
    private void setCouponUi(LoanCoupon loanCoupon) {
        boolean has = loanCoupon != null;
        binding.layoutBannerAndCoupon.rlCouponUse.setVisibility(has ? View.VISIBLE : View.GONE);
        if (has) {
            setCouponUseUi(loanCoupon);
        } else if (mLoanCouponList != null) {
            mCurrentLoanCoupon = null;
            mLoanCouponList.clear();
            mLoanCouponList = null;
        }
    }

    /**
     * 优惠券使用情况
     *
     * @param selectLoanCoupon 选择的券
     */
    @SuppressLint("SetTextI18n")
    private void setCouponUseUi(LoanCoupon selectLoanCoupon) {
        mCurrentLoanCoupon = selectLoanCoupon;
        if (mCurrentLoanCoupon == null) {
            binding.layoutBannerAndCoupon.tvCouponUse.setTextColor(0xffff5151);
            binding.layoutBannerAndCoupon.tvCouponUse.setText("可选1张");
        } else {
            if (!"Y".equals(mCurrentLoanCoupon.getCanUseState())) {
                binding.layoutBannerAndCoupon.tvCouponUse.setTextColor(getResources().getColor(R.color.color_909199));
                binding.layoutBannerAndCoupon.tvCouponUse.setText(mCurrentLoanCoupon.getHintInfo());
                binding.layoutBannerAndCoupon.tvCouponUseDesc.setVisibility(View.GONE);
                binding.layoutBannerAndCoupon.llUnavailableInfo.setVisibility(View.VISIBLE);
                binding.layoutBannerAndCoupon.tvReason.setText(CheckUtil.isEmpty(mCurrentLoanCoupon.getUnUseDesc()) ? "当前券不可用" : mCurrentLoanCoupon.getUnUseDesc());
            } else {
                binding.layoutBannerAndCoupon.llUnavailableInfo.setVisibility(View.GONE);
                binding.layoutBannerAndCoupon.tvCouponUseDesc.setVisibility(View.VISIBLE);
                binding.layoutBannerAndCoupon.tvCouponUse.setTextColor(0xff303133);
                binding.layoutBannerAndCoupon.tvCouponUse.setText("-" + CheckUtil.showThound(mCurrentLoanCoupon.getDiscValue()));
            }
        }
        if (mCurrentLoanCoupon != null && mCurrentLoanCoupon.isShowVipUi()) {
            binding.layoutBannerAndCoupon.tvCouponUseDesc.setBackgroundResource(R.drawable.bg_borrow_coupon_desc_vip);
            binding.layoutBannerAndCoupon.tvCouponUseDesc.setPadding(UiUtil.dip2px(this, 7), UiUtil.dip2px(this, 1.5f), UiUtil.dip2px(this, 7), UiUtil.dip2px(this, 1.5f));
            binding.layoutBannerAndCoupon.tvCouponUseDesc.setTextColor(0xFF653D23);
            binding.layoutBannerAndCoupon.tvCouponUseDesc.setText(mCurrentLoanCoupon.isBest() ? "已匹配当前最优券" : "会员专享");
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.img_vip_tag_borrow_page_use_coupon);
            if (drawable != null) {
                drawable.setBounds(0, 0, UiUtil.dip2px(this, 14), UiUtil.dip2px(this, 13));
            }
            //使用setCompoundDrawablesRelative需要先设置setBounds才显示
            binding.layoutBannerAndCoupon.tvCouponUseDesc.setCompoundDrawablesRelative(drawable, null, null, null);
        } else if (mCurrentLoanCoupon != null && mCurrentLoanCoupon.isBest()) {
            binding.layoutBannerAndCoupon.tvCouponUseDesc.setBackgroundResource(R.drawable.bg_borrow_coupon_desc);
            binding.layoutBannerAndCoupon.tvCouponUseDesc.setPadding(UiUtil.dip2px(this, 7), UiUtil.dip2px(this, 1.5f), UiUtil.dip2px(this, 7), UiUtil.dip2px(this, 1.5f));
            binding.layoutBannerAndCoupon.tvCouponUseDesc.setTextColor(0xffff5151);
            binding.layoutBannerAndCoupon.tvCouponUseDesc.setText("已匹配当前最优券");
        } else {
            binding.layoutBannerAndCoupon.tvCouponUseDesc.setBackgroundResource(0);
            binding.layoutBannerAndCoupon.tvCouponUseDesc.setCompoundDrawables(null, null, null, null);
            binding.layoutBannerAndCoupon.tvCouponUseDesc.setPadding(0, 0, 0, 0);
            binding.layoutBannerAndCoupon.tvCouponUseDesc.setTextColor(0xff909199);
            binding.layoutBannerAndCoupon.tvCouponUseDesc.setText("还款抵扣利息");
        }
    }

    /**
     * 设置还款计划Ui
     *
     * @param originalRepaymentTotalAmt 原始本息总额
     * @param loanCoupon                用于计算显示的券
     */
    private void setRepaymentPlanUi(String originalRepaymentTotalAmt, LoanCoupon loanCoupon) {
        if (CheckUtil.isEmpty(originalRepaymentTotalAmt)) {
            binding.tvRepaymentPlan.setText("");
            mRepaymentTotalAmt = null;
            return;
        }
        String useCouponRepaymentTotalAmt = loanCoupon != null ? loanCoupon.getDiscRepayAmt() : "";
        SpannableStringBuilder ssb = new SpannableStringBuilder(String.format("本息总额¥%s", CheckUtil.showThound(originalRepaymentTotalAmt)));
        //是否使用免息券而有了优惠后金额
        if (!CheckUtil.isEmpty(useCouponRepaymentTotalAmt) && loanCoupon != null && "Y".equals(loanCoupon.getCanUseState())) {
            int start = ssb.length();
            if (loanCoupon != null && "Y".equals(loanCoupon.getCanUseState()) && "N".equals(loanCoupon.getBatchDeduction())) {
                ssb.append(SpannableStringUtils.getBuilder(this, "\n主动还款可享")
                        .setForegroundColor(ContextCompat.getColor(mContext, R.color.color_909199)).create());
                ssb.append(SpannableStringUtils.getBuilder(this, String.format("优惠后¥%s", CheckUtil.showThound(useCouponRepaymentTotalAmt)))
                        .setForegroundColor(ContextCompat.getColor(mContext, R.color.color_ffff5151)).create());
            } else {
                ssb.append(SpannableStringUtils.getBuilder(this, String.format("\n优惠后¥%s", CheckUtil.showThound(useCouponRepaymentTotalAmt)))
                        .setForegroundColor(ContextCompat.getColor(mContext, R.color.color_ffff5151)).create());
            }

            ssb.setSpan(new AbsoluteSizeSpan(getResources().getDimensionPixelSize(R.dimen.dimen_12sp)), start, ssb.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        binding.tvRepaymentPlan.setText(ssb);
    }

    /**
     * 设置怎么环UI
     */
    private void setRepaymentTypeUI() {
        int checkPos = getCheckedPos();
        if (loanCodeBeanList != null && loanCodeBeanList.size() > 0) {
            if (loanCodeBeanList.get(checkPos).getProducts().size() <= 1) {
                binding.tvRepayType.setText(loanCodeBeanList.get(checkPos).getProducts().get(0).getMtdDesc());
                binding.tvRepayType.setClickable(false);
                binding.tvRepayType.setCompoundDrawables(null, null, null, null);
            } else {
                binding.tvRepayType.setClickable(true);
                Drawable drawableEnd = getResources().getDrawable(
                        R.drawable.the_arrow_r_grey);
                binding.tvRepayType.setCompoundDrawablesWithIntrinsicBounds(null, null, drawableEnd, null);
                binding.tvRepayType.setCompoundDrawablePadding(UiUtil.dip2px(this, 5));

                RadioButton rbChecked = (RadioButton) findViewById(binding.rgRateList.getCheckedRadioButtonId());
                String tnrAndMtdCde = String.valueOf(rbChecked.getTag());
                if (!TextUtils.isEmpty(tnrAndMtdCde)) {
                    String[] splitArray = tnrAndMtdCde.split("#", 3);
                    String mtdCode = splitArray[1];
                    for (int i = 0; i < loanCodeBeanList.get(checkPos).getProducts().size(); i++) {
                        if (loanCodeBeanList.get(checkPos).getProducts().get(i).getMtdCde().equals(mtdCode)) {
                            binding.tvRepayType.setText(loanCodeBeanList.get(checkPos).getProducts().get(i).getMtdDesc());
                            break;
                        }
                    }
                }
            }
        }

    }

    /**
     * 设置银行卡信息
     */
    private void setBankDetail() {
        String bankName = cardBean.getBankName();
        if (CheckUtil.isEmpty(bankName)) {
            Log.e("setBankDetail", "bankName===0" + bankName);
            binding.tvBankDetail.setText(SpannableStringUtils.getBuilder(this, "请选择").setForegroundColor(ContextCompat.getColor(mContext, R.color.ffd8d8d8)).create());
            return;
        }
        Drawable drawableLeft = ContextCompat.getDrawable(this, UiUtil.getBankCardImageRes(bankName));
        bankName += "(" + cardBean.getCardNo().substring(cardBean.getCardNo().length() - 4) + ")";
        if (drawableLeft != null) {
            drawableLeft.setBounds(0, 0, 45, 45);
        }
        Drawable drawableRight = ContextCompat.getDrawable(this, R.drawable.the_arrow_r_grey);
        if (drawableRight != null) {
            drawableRight.setBounds(0, 0, drawableRight.getIntrinsicWidth(), drawableRight.getIntrinsicHeight());
        }
        binding.tvBankDetail.setCompoundDrawables(drawableLeft, null, drawableRight, null);
        binding.tvBankDetail.setText(bankName);
        Log.e("setBankDetail", "bankName===1" + bankName);
        setButtonEnable();
    }


    @Override
    public void timeSelect(Object... time) {
        ArrayBean bean = (ArrayBean) time[0];
        loanUsage = bean.getCode();
        binding.tvPurpose.setText(bean.getName());
    }

    @Override
    protected String getPageCode() {
        return "PlanBorrowHowMuchPage_gouhua";
    }

    private String getPageName() {
        return "计划借多少页";
    }

    //点击下一步上报事件
    private void postConfirmEvent(String success, String failReason) {
        double borrow_memory;
        double interest;
        double service_charge;
        try {
            borrow_memory = TextUtils.isEmpty(inputAmount) ? 0 : Double.parseDouble(inputAmount);
            interest = TextUtils.isEmpty(totalNormInt) ? 0 : Double.parseDouble(totalNormInt);
            service_charge = TextUtils.isEmpty(totalFeeAmt) ? 0 : Double.parseDouble(totalFeeAmt);
        } catch (Exception e) {
            borrow_memory = 0;
            interest = 0;
            service_charge = 0;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("button_name", "下一步");
        map.put("borrow_memory", borrow_memory);
        map.put("borrow_cycle", applyTnr);
        map.put("interest", interest);
        map.put("service_charge", service_charge);
        map.put("account_type", (cardBean == null || TextUtils.isEmpty(cardBean.getBankName())) ? "未获取到" : cardBean.getBankName());
        map.put("purpose", binding.tvPurpose.getText().toString());
        UMengUtil.commonCompleteEvent("PlanBorrowHowMuch", map, success, failReason, getPageCode());
    }

    public void postExposure(String eventId) {
        Map<String, Object> map = new HashMap<>();
        map.put("prd_no", typCde);
        map.put("rem_amt", String.valueOf(maxAmount));
        UMengUtil.commonExposureEvent(eventId, getPageName(), mBannerName, mCid, mGroupId, map, getPageCode());
    }

    @Override
    protected void onDestroy() {
        if (mLoanCouponPop != null) {
            mLoanCouponPop.onDestroy();
            mLoanCouponPop = null;
        }
        if (popSelectPick != null) {
            popSelectPick.onDestroy();
            popSelectPick = null;
        }
        super.onDestroy();
    }

    @Override
    public void showBankCardDialog() {
        ChooseBankCardActivity.toChooseBankCard(this, "选择收款银行卡", null, requestCode);
    }
}
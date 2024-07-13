package com.haiercash.gouhua.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.beans.borrowmoney.RecordBean;
import com.haiercash.gouhua.beans.borrowmoney.RepayBean;
import com.haiercash.gouhua.bill.BillDetailsActivity;
import com.haiercash.gouhua.utils.UiUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ================================================================
 * 作    者：stone<br/>
 * 邮    箱：shixiangfei@haiercash.com/stonexiangfeishi@sina.cn<br/>
 * 版    本：1.0<br/>
 * 创建日期：2017/8/16<br/>
 * 描    述：借款记录->详情/还款记录->详情<br/>
 * 修订历史：入参:PageType:BORROW_DETAIL 借款记录->详情<br/>
 * PageType:REPAY_DETAIL 还款记录->详情<br/>
 * ================================================================
 */
public class BorrowRepayDetailFragment extends BaseFragment {
    public static final int ID = BorrowRepayDetailFragment.class.hashCode();
    /**
     * 借款记录->详情
     */
    public static final int BORROW_DETAIL = 1;
    /**
     * 还款记录->详情
     */
    public static final int REPAY_DETAIL = 2;

    @BindView(R.id.tv_money)
    TextView tv_money;//金额
    @BindView(R.id.tv_state)
    TextView tv_state;//状态显示
    @BindView(R.id.tv_repay_state_des)
    TextView tv_repay_state_des;
    @BindView(R.id.tv_periods)
    TextView tv_periods;//期限
    @BindView(R.id.tv_get_bank)
    TextView tv_bank;//银行卡

    @BindView(R.id.tv_record_title)
    TextView tv_record_title;
    //节点1
    @BindView(R.id.iv_progress1)
    ImageView iv_progress1;//
    @BindView(R.id.tv_title1)
    TextView tv_title1;
    //节点2
    @BindView(R.id.ll_layout2)
    View ll_layout2;
    //节点4
    @BindView(R.id.ll_layout4)
    View ll_layout4;
    @BindView(R.id.ll_layout3)
    View ll_layout3;
    @BindView(R.id.ll_layout5)
    View ll_layout5;
    @BindView(R.id.iv_progress5)
    ImageView iv_progress5;
    @BindView(R.id.iv_progress2)
    ImageView iv_progress2;//
    //节点3
    @BindView(R.id.v_line_result)
    View v_line_result;
    @BindView(R.id.iv_progress3)
    ImageView iv_progress3;
    @BindView(R.id.iv_progress4)
    ImageView iv_progress4;
    @BindView(R.id.tv_title3)
    TextView tv_title3;//节点名称
    @BindView(R.id.tv_title5)
    TextView tv_title5;//节点名称
    @BindView(R.id.tv_fail_time)
    TextView tvFailTime;//处理时间
    @BindView(R.id.tv_fail_time1)
    TextView TvFailTime1;//申请时间
    @BindView(R.id.tv_fail_time3)
    TextView TvFailTime3;//最终时间
    @BindView(R.id.tv_fail_time5)
    TextView TvFailTime5;//最终时间
    @BindView(R.id.tv_fail_time4)
    TextView TvFailTime4;//处理时间
    @BindView(R.id.ll_fail_case)
    LinearLayout llFailCase;
    @BindView(R.id.ll_recode_detail)
    LinearLayout llRecodeDetail;
    @BindView(R.id.tv_fail_case)
    TextView tvFailCase;
    @BindView(R.id.tv_rem)
    TextView tvRem;
    @BindView(R.id.tv_progress4)
    TextView tvProgress4;
    @BindView(R.id.tv_progress2)
    TextView tvProgress2;
    private RepayBean recordBean;

    public static BorrowRepayDetailFragment newInstance(Bundle extra) {
        final BorrowRepayDetailFragment f = new BorrowRepayDetailFragment();
        if (extra != null) {
            f.setArguments(extra);
        }
        return f;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_borrowing_record_detail;
    }

    @Override
    protected void initEventAndData() {
        if (getArguments() == null) {
            mActivity.finish();
        }
        tv_money.setTypeface(FontCustom.getMediumFont(mActivity));
        int pageType = getArguments().getInt("PageType");
        if (pageType == BORROW_DETAIL) {
            tv_state.setText("借款金额(元)");
            tv_record_title.setText("处理进度");
            ll_layout2.setVisibility(View.VISIBLE);
            tv_repay_state_des.setVisibility(View.GONE);
            mActivity.setTitle("借款详情");
            String applySeq = getArguments().getString("applySeq");
            if (CheckUtil.isEmpty(applySeq)) {
                UiUtil.toast("参数解析错误，请稍后重试！");
                mActivity.finish();
                return;
            }
            showProgress(true);
            Map<String, String> map = new HashMap<>();
            map.put("applSeq", applySeq);
            netHelper.getService(ApiUrl.URL_BORROWING_RECORD_DETAIL, map, RecordBean.class);
        } else if (pageType == REPAY_DETAIL) {
            tv_state.setText("还款金额(元)");
            mActivity.setTitle("还款详情");
            tv_repay_state_des.setVisibility(View.VISIBLE);
            recordBean = (RepayBean) getArguments().getSerializable("Record");
            initRepayData();
        }
    }

    @OnClick({R.id.ll_recode_detail})
    public void onViewClick(View view) {
        if (view.getId() == R.id.ll_recode_detail) {//账单详情
            if (recordBean != null) {
                Intent intent = new Intent(getActivity(), BillDetailsActivity.class);
                intent.putExtra("loanNo", recordBean.getBusinessPayNo());
                startActivity(intent);
            }
        }
    }

    /**
     * 还款记录->详情
     */
    private void initRepayData() {
        String status = recordBean.getStatus();
        tv_record_title.setText("还款进度");
        tv_money.setText(CheckUtil.formattedAmount1(recordBean.getAmount()));
        mView.findViewById(R.id.tl_bank).setVisibility(View.GONE);
        tv_repay_state_des.setText("");
        //还款银行卡显示
        mView.findViewById(R.id.ll_refund_bank).setVisibility(View.VISIBLE);
        //还款方式展示
        TextView tvRefundBank = mView.findViewById(R.id.tv_refund_bank);
        if ("WECHAT_C_A_01".equals(recordBean.getInterId())) {
            tvRefundBank.setText("微信支付");
        } else if ("ALI_C_A_01".equals(recordBean.getInterId())) {
            tvRefundBank.setText("支付宝支付");
        } else {
            tvRefundBank.setText(UiUtil.getStr(recordBean.getBankName(), "(", recordBean.getRepayCardNo().substring(recordBean.getRepayCardNo().length() - 4), ")"));
        }
        TvFailTime1.setText(recordBean.getTradeTime());
        iv_progress1.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_blue));
        llRecodeDetail.setVisibility(View.VISIBLE);
        if ("01".equals(recordBean.getBizType())) {
            tv_title1.setText("自动还款");
            if (CheckUtil.isEmpty(recordBean.getSerno())) {
                mView.findViewById(R.id.ll_serial_num).setVisibility(View.GONE);
                mView.findViewById(R.id.iv_bttom).setVisibility(View.GONE);
            } else {
                //还款记录新增还款流水显示
                mView.findViewById(R.id.ll_serial_num).setVisibility(View.VISIBLE);
                mView.findViewById(R.id.iv_bttom).setVisibility(View.VISIBLE);
                //银行流水号
                TextView tvSerialNum = mView.findViewById(R.id.tv_serial_num);
                tvSerialNum.setText(recordBean.getSerno());
            }
            if ("FAIL".equals(status)) {
                llFailCase.setVisibility(View.VISIBLE);
                ll_layout2.setVisibility(View.VISIBLE);
                ll_layout4.setVisibility(View.GONE);
                ll_layout3.setVisibility(View.GONE);
                ll_layout5.setVisibility(View.VISIBLE);
                //失败原因
                tvFailCase.setText(CheckUtil.isEmpty(recordBean.getErrorDesc()) ? "其他" : recordBean.getErrorDesc());
                //还款流水隐藏
                mView.findViewById(R.id.ll_serial_num).setVisibility(View.GONE);
                mView.findViewById(R.id.iv_bttom).setVisibility(View.GONE);
                tvRem.setText("还款失败");
                tvRem.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_recod_red));
                iv_progress2.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_blue));
                iv_progress5.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_fail));
                tvProgress2.setText("处理中");
                tv_title5.setText("还款失败");
                tvFailTime.setText(recordBean.getTradeTime());
                tv_title5.setTextColor(ContextCompat.getColor(mActivity, R.color.fffc594c));
                TvFailTime5.setText(recordBean.getRepayFinalStatusTime());
            } else if ("SUCCESS".equals(status)) {
                llFailCase.setVisibility(View.GONE);
                ll_layout2.setVisibility(View.GONE);
                ll_layout3.setVisibility(View.GONE);
                ll_layout5.setVisibility(View.VISIBLE);
                ll_layout4.setVisibility(View.GONE);
                tvRem.setText("还款成功");
                tvRem.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_recode_blue));
                iv_progress5.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_success));
                tv_title5.setText("还款成功");
                tv_title5.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                TvFailTime5.setText(recordBean.getRepayFinalStatusTime());
            } else {
                ll_layout2.setVisibility(View.GONE);
                ll_layout4.setVisibility(View.VISIBLE);
                ll_layout3.setVisibility(View.VISIBLE);
                ll_layout5.setVisibility(View.GONE);
                llFailCase.setVisibility(View.GONE);
                //还款流水隐藏
                mView.findViewById(R.id.ll_serial_num).setVisibility(View.GONE);
                mView.findViewById(R.id.iv_bttom).setVisibility(View.GONE);
                tvRem.setText("处理中");
                tvRem.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_recode_blue));
                iv_progress3.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_gray));
                mView.findViewById(R.id.v_line_progress2).setBackgroundColor(Color.parseColor("#aaaaaa"));
                v_line_result.setBackgroundColor(Color.parseColor("#aaaaaa"));
                tvProgress4.setText("处理中");
                tv_title3.setText("还款结果");
                tv_title3.setTextColor(ContextCompat.getColor(mActivity, R.color.text_gray_light));
                TvFailTime4.setText(recordBean.getTradeTime());
            }
        } else if ("02".equals(recordBean.getBizType())) {
            tv_title1.setText("主动还款");
            tvFailTime.setText(recordBean.getTradeTime());
            if ("FAIL".equals(status)) {
                ll_layout2.setVisibility(View.VISIBLE);
                ll_layout4.setVisibility(View.GONE);
                ll_layout3.setVisibility(View.GONE);
                ll_layout5.setVisibility(View.VISIBLE);
                llFailCase.setVisibility(View.VISIBLE);
                //失败原因
                tvFailCase.setText(CheckUtil.isEmpty(recordBean.getErrorDesc()) ? "其他" : recordBean.getErrorDesc());
                //还款流水隐藏
                mView.findViewById(R.id.ll_serial_num).setVisibility(View.GONE);
                mView.findViewById(R.id.iv_bttom).setVisibility(View.GONE);
                tvRem.setText("还款失败");
                tvRem.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_recod_red));
                iv_progress2.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_blue));
                iv_progress5.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_fail));
                tvProgress2.setText("处理中");
                tv_title5.setText("还款失败");
                tv_title5.setTextColor(ContextCompat.getColor(mActivity, R.color.fffc594c));
                TvFailTime5.setText(recordBean.getRepayFinalStatusTime());
            } else if ("SUCCESS".equals(status)) {
                ll_layout2.setVisibility(View.VISIBLE);
                ll_layout4.setVisibility(View.GONE);
                ll_layout3.setVisibility(View.GONE);
                ll_layout5.setVisibility(View.VISIBLE);
                llFailCase.setVisibility(View.GONE);
                tvRem.setText("还款成功");
                tvRem.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_recode_blue));
                iv_progress2.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_blue));
                iv_progress5.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_success));
                tv_title5.setText("还款成功");
                tv_title5.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
                TvFailTime5.setText(recordBean.getRepayFinalStatusTime());
                if (CheckUtil.isEmpty(recordBean.getSerno())) {
                    mView.findViewById(R.id.ll_serial_num).setVisibility(View.GONE);
                    mView.findViewById(R.id.iv_bttom).setVisibility(View.GONE);
                } else {
                    //还款记录新增还款流水显示
                    mView.findViewById(R.id.ll_serial_num).setVisibility(View.VISIBLE);
                    mView.findViewById(R.id.iv_bttom).setVisibility(View.VISIBLE);
                    //银行流水号
                    TextView tvSerialNum = mView.findViewById(R.id.tv_serial_num);
                    tvSerialNum.setText(recordBean.getSerno());
                }
            } else {//if ("PROCESSING".equals(status))
                ll_layout2.setVisibility(View.GONE);
                ll_layout4.setVisibility(View.VISIBLE);
                ll_layout3.setVisibility(View.VISIBLE);
                ll_layout5.setVisibility(View.GONE);
                llFailCase.setVisibility(View.GONE);
                //还款流水隐藏
                mView.findViewById(R.id.ll_serial_num).setVisibility(View.GONE);
                mView.findViewById(R.id.iv_bttom).setVisibility(View.GONE);
                tvRem.setText("处理中");
                tvRem.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_recode_blue));
                iv_progress3.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_gray));
                mView.findViewById(R.id.v_line_progress2).setBackgroundColor(Color.parseColor("#aaaaaa"));
                v_line_result.setBackgroundColor(Color.parseColor("#aaaaaa"));
                tvProgress4.setText("处理中");
                tv_title5.setText("还款结果");
                tv_title5.setTextColor(ContextCompat.getColor(mActivity, R.color.text_gray_light));
                TvFailTime4.setText(recordBean.getTradeTime());
            }
        }
    }

    /**
     * 借款记录->详情 更新数据
     */
    private void initBorrowData(RecordBean ben) {
        llRecodeDetail.setVisibility(View.GONE);
        llFailCase.setVisibility(View.GONE);
        mView.findViewById(R.id.tl_bank).setVisibility(View.VISIBLE);
        //隐藏还款流水号
        mView.findViewById(R.id.ll_serial_num).setVisibility(View.GONE);
        mView.findViewById(R.id.iv_bttom).setVisibility(View.GONE);
        tv_money.setText(ben.getApplyAmt());
        tv_periods.setText(UiUtil.getStr(ben.getApplyTnr(), "个月"));
        tv_bank.setText(UiUtil.getStr(CheckUtil.clearEmpty(ben.getBankName()), "(", CheckUtil.clearEmpty(ben.getCardLast4No()), ")"));

        //0：借款成功
        //1：借款处理中
        //2：借款失败
        if ("0".equals(ben.getBorrowingApplStatus())) {
            ll_layout2.setVisibility(View.VISIBLE);
            ll_layout4.setVisibility(View.GONE);
            ll_layout3.setVisibility(View.GONE);
            ll_layout5.setVisibility(View.VISIBLE);
            TvFailTime1.setText(ben.getApplyTimeWithYear());
            TvFailTime4.setText(ben.getProcessingDateWithYear());
            tvRem.setText("借款成功");
            iv_progress1.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_blue));
            iv_progress2.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_blue));
            iv_progress5.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_success));
            tv_title5.setText("借款成功");
            tv_title5.setTextColor(ContextCompat.getColor(mActivity, R.color.text_black));
            String time = ben.getLoanSuccessTime();
            if (TextUtils.isEmpty(time)) {
                time = ben.getFinalStatusTimeWithYear();
            }
            TvFailTime5.setText(time);
        } else if ("1".equals(ben.getBorrowingApplStatus())) {
            ll_layout2.setVisibility(View.GONE);
            ll_layout4.setVisibility(View.VISIBLE);
            ll_layout3.setVisibility(View.VISIBLE);
            ll_layout5.setVisibility(View.GONE);
            TvFailTime4.setText(ben.getProcessingDateWithYear());
            TvFailTime1.setText(ben.getApplyTimeWithYear());
            tvRem.setText("审核中");
            iv_progress1.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_blue));
            iv_progress4.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progressing));
            iv_progress3.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_gray));
            tvRem.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_recode_blue));
            tv_title3.setText("借款结果");
            mView.findViewById(R.id.v_line_progress2).setBackgroundColor(Color.parseColor("#aaaaaa"));
            v_line_result.setBackgroundColor(Color.parseColor("#aaaaaa"));
        } else if ("2".equals(ben.getBorrowingApplStatus())) {
            ll_layout2.setVisibility(View.VISIBLE);
            ll_layout4.setVisibility(View.GONE);
            ll_layout3.setVisibility(View.GONE);
            ll_layout5.setVisibility(View.VISIBLE);
            TvFailTime1.setText(ben.getApplyTimeWithYear());
            tvFailTime.setText(ben.getProcessingDateWithYear());
            tvRem.setText("借款失败");
            iv_progress1.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_blue));
            iv_progress2.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_blue));
            iv_progress5.setImageDrawable(ContextCompat.getDrawable(mActivity, R.drawable.iv_borrow_record_progress_fail_blue));
            tvRem.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_recod_red));
            tv_title5.setText("借款失败");
            tv_title5.setTextColor(ContextCompat.getColor(mActivity, R.color.colorPrimary));
            TvFailTime5.setText(ben.getFinalStatusTimeWithYear());
        }
    }

    /**********************************************************************************************/

    @Override
    public void onSuccess(Object response, String flag) {
        showProgress(false);
        if (ApiUrl.URL_BORROWING_RECORD_DETAIL.equals(flag)) {
            RecordBean ben = (RecordBean) response;
            initBorrowData(ben);
        }
    }
}

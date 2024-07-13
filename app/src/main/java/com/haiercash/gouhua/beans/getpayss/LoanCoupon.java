package com.haiercash.gouhua.beans.getpayss;

import android.text.SpannableStringBuilder;

import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.SpannableStringUtils;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.AppApplication;

import java.io.Serializable;

/**
 * 借款可选绑定优惠券
 */
public class LoanCoupon implements Serializable {
    private String batchNo;//免息券批次号
    private String kind;//权益名称,1固定天数,2固定金额,3折扣借款券（1,3为历史数据）抵扣券4,折扣券5,免息券7
    private String couponNo;//券号
    private String batchDesc;//免息券描述
    private String remark;//券备注
    private String batchDetailDesc;//免息券详情/规则
    private String usedCondition;//使用条件
    private String userId;//用户号
    private String custNo;//客户号
    private String isBest;//是否最优免息券,Y是N否
    private String parValue;//面额,kind为1,2,3返回该字段
    private String discountNumber;//打折,kind为5折扣券返回该字段
    private String maxDisCountRate;//最高抵扣借款金额比率,kind为4抵扣券返回该字段
    private String maxFeeDecrease;//最高抵扣借款金额,kind为4抵扣券返回该字段
    private String discValue;//优惠金额
    private String calVol;//优惠期数
    private String bindStartDt;//绑定有效期（开始）
    private String bindEndDt;//绑定有效期（结束）
    /*该字段用于前端在借款确认页的还款计划一栏展示优惠后金额使用，没有可用优惠券则前端不用展示该金额,
    该金额为还款计划返回的应还总金额减去优惠金额
     */
    private String discRepayAmt;//优惠后金额

    private String deductionItem;//息费减免项 利息： interest 提前还款手续费： prePayFee 目前仅支持利息

    /**
     * 获取全部免息券接口才返回该字段
     * 过期状态,0：正常,1：即将过期（距到期日3天）,2：当日过期（到期日为当日）
     */
    private String expireState;
    //中收 新增VIP
    private String couponSource;//Y展示会员标识，N或者空不展示会员标识

    private String reduceDays;//减免天数 kind为7 x天免息券返回该字段
    private String batchDeduction;//是否支持批扣 是Y 否N
    private String unUseDesc;//不可用原因描
    private String maxDiscValue;//最高优惠金额

    private String canUseState;//可使用状态  Y 可使用  N 不可使用
    private String discInstmAmt;//优惠后的总利息 存在最优免息券时返回
    private String discTotalNormInt;//优惠后的期供金额 如果有最优免息券，并且存在可优惠的期号时返回，前端根据calVol返回的期数在指定期数上展示减免后的金额
    private String kindSign;//货币符号
    private String kindVal;//数值
    private String kindUnit;//单位

    public String getKindSign() {
        return kindSign;
    }

    public void setKindSign(String kindSign) {
        this.kindSign = kindSign;
    }

    public String getKindVal() {
        return kindVal;
    }

    public void setKindVal(String kindVal) {
        this.kindVal = kindVal;
    }

    public String getKindUnit() {
        return kindUnit;
    }

    public void setKindUnit(String kindUnit) {
        this.kindUnit = kindUnit;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getDiscInstmAmt() {
        return discInstmAmt;
    }

    public void setDiscInstmAmt(String discInstmAmt) {
        this.discInstmAmt = discInstmAmt;
    }

    public String getDiscTotalNormInt() {
        return discTotalNormInt;
    }

    public void setDiscTotalNormInt(String discTotalNormInt) {
        this.discTotalNormInt = discTotalNormInt;
    }

    public String getCanUseState() {
        return canUseState;
    }

    public void setCanUseState(String canUseState) {
        this.canUseState = canUseState;
    }

    public String getReduceDays() {
        return reduceDays;
    }

    public void setReduceDays(String reduceDays) {
        this.reduceDays = reduceDays;
    }

    public String getBatchDeduction() {
        return batchDeduction;
    }

    public void setBatchDeduction(String batchDeduction) {
        this.batchDeduction = batchDeduction;
    }

    public String getUnUseDesc() {
        return unUseDesc;
    }

    public void setUnUseDesc(String unUseDesc) {
        this.unUseDesc = unUseDesc;
    }

    public String getMaxDiscValue() {
        return maxDiscValue;
    }

    public void setMaxDiscValue(String maxDiscValue) {
        this.maxDiscValue = maxDiscValue;
    }

    public String getCouponSource() {
        return couponSource;
    }

    public void setCouponSource(String couponSource) {
        this.couponSource = couponSource;
    }

    public boolean isShowVipUi() {
        return "Y".equals(couponSource);
    }

    public String getExpireState() {
        return expireState;
    }

    public void setExpireState(String expireState) {
        this.expireState = expireState;
    }

    public String getMaxFeeDecrease() {
        return maxFeeDecrease;
    }

    public void setMaxFeeDecrease(String maxFeeDecrease) {
        this.maxFeeDecrease = maxFeeDecrease;
    }

    public String getDeductionItem() {
        return deductionItem;
    }

    public void setDeductionItem(String deductionItem) {
        this.deductionItem = deductionItem;
    }

    public LoanCoupon() {
    }

    public String getKindName() {
        if (kind == null) {
            return "";
        }
        switch (kind) {
            case "1":
                return "固定天数券";
            case "3":
                return "折扣借款券";
            case "2":
                return "额度券";
            case "4":
                return "抵扣券";
            case "5":
                return "折扣券";
            case "7":
                return "免息券";
            default:
                return "";
        }
    }

    public boolean isBest() {
        return "Y".equalsIgnoreCase(isBest);
    }

    public String getCouponTypeName() {
        return isKindSeven() ? "免息" : "本笔可减";
    }

    private String getCouponValue() {
        switch (kind) {
            case "1":
                return parValue != null && !parValue.contains("天") ? parValue + "天" : parValue;
            case "4":
                return CheckUtil.isEmpty(maxFeeDecrease) ? parValue : maxFeeDecrease;
            case "5":
                return getShowDiscountNumber();
            case "7":
                return getShowDiscountDay();
            default:
                return hasDisValue() ? discValue : parValue;
        }
    }

    /**
     * 获取不可用信息   8折|不可用   最高减50.00不可用。。。
     * 1固定天数,2固定金额,3折扣借款券（1,3为历史数据）抵扣券4,折扣券5,免息券7
     */
    public SpannableStringBuilder getHintInfo() {

        String info = "";
        switch (kind) {
            case "1":
                info = parValue != null && !parValue.contains("天") ? parValue + "天" : parValue;
                break;
            case "2":
            case "3":
                info = "最高减" + parValue;
                break;
            case "4":
                info = "最高减" + (CheckUtil.isEmpty(maxFeeDecrease) ? parValue : maxFeeDecrease);
                break;
            case "5":
                info = getShowDiscountNumber();
                break;
            case "7":
                info = reduceDays + "天免息";
                break;
            default:
                return SpannableStringUtils.getBuilder(AppApplication.CONTEXT, "不可用").create();
        }

        return SpannableStringUtils.getBuilder(AppApplication.CONTEXT, info)
                .append(" | ").setForegroundColor(AppApplication.CONTEXT.getResources().getColor(R.color.ffd8d8d8))
                .append("不可用").create();

    }

    private String getShowDiscountDay() {
        return reduceDays != null && !reduceDays.contains("天") ? reduceDays + "天" : reduceDays;
    }

    private String getShowDiscountNumber() {
        return discountNumber != null && !discountNumber.contains("折") ? discountNumber + "折" : discountNumber;
    }

    /**
     * 是否显示前面的金钱符号
     */
    public boolean isFixedValue() {
        return "5".equals(kind) || "7".equals(kind);
    }

    /**
     * 获取真正的显示面额，绑定选择时有实际优惠金额就显示实际优惠金额，无则显示本身面额
     */
    public String getShowParValue() {
        return getCouponValue();
    }

    private boolean hasDisValue() {
        return !CheckUtil.isEmpty(discValue) && !CheckUtil.isZero(discValue);
    }

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getCouponNo() {
        return couponNo;
    }

    public void setCouponNo(String couponNo) {
        this.couponNo = couponNo;
    }

    public String getBatchDesc() {
        return batchDesc;
    }

    public void setBatchDesc(String batchDesc) {
        this.batchDesc = batchDesc;
    }

    public String getBatchDetailDesc() {
        return batchDetailDesc;
    }

    public void setBatchDetailDesc(String batchDetailDesc) {
        this.batchDetailDesc = batchDetailDesc;
    }

    public String getUsedCondition() {
        return usedCondition;
    }

    public void setUsedCondition(String usedCondition) {
        this.usedCondition = usedCondition;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getIsBest() {
        return isBest;
    }

    public void setIsBest(String isBest) {
        this.isBest = isBest;
    }

    public String getParValue() {
        return parValue;
    }

    public void setParValue(String parValue) {
        this.parValue = parValue;
    }

    public String getDiscountNumber() {
        return discountNumber;
    }

    public void setDiscountNumber(String discountNumber) {
        this.discountNumber = discountNumber;
    }

    public String getMaxDisCountRate() {
        return maxDisCountRate;
    }

    public void setMaxDisCountRate(String maxDisCountRate) {
        this.maxDisCountRate = maxDisCountRate;
    }

    public String getDiscValue() {
        return discValue;
    }

    public void setDiscValue(String discValue) {
        this.discValue = discValue;
    }

    public String getCalVol() {
        return calVol;
    }

    public void setCalVol(String calVol) {
        this.calVol = calVol;
    }

    public String getBindStartDt() {
        return bindStartDt;
    }

    public void setBindStartDt(String bindStartDt) {
        this.bindStartDt = bindStartDt;
    }

    public String getBindEndDt() {
        return bindEndDt;
    }

    public void setBindEndDt(String bindEndDt) {
        this.bindEndDt = bindEndDt;
    }

    public String getDiscRepayAmt() {
        return discRepayAmt;
    }

    public void setDiscRepayAmt(String discRepayAmt) {
        this.discRepayAmt = discRepayAmt;
    }

    //是否是免息券
    public boolean isKindSeven() {
        return !CheckUtil.isEmpty(kind) && "7".equals(kind);
    }

    //构建假的测试数据
    public LoanCoupon setTestBean(int no) {
        kind = "5";
        batchNo = no + "";
        couponNo = no + "";
        batchDesc = "免息券描述";
        batchDetailDesc = "规则";
        isBest = no == 1 ? "Y" : "N";
        parValue = "50";
        discValue = "50";
        calVol = "2";
        bindStartDt = "2022-01-12";
        bindEndDt = "2022-02-12";
        discRepayAmt = "5450";
        return this;
    }
}

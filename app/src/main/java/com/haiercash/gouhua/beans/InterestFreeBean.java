package com.haiercash.gouhua.beans;

import com.app.haiercash.base.utils.system.CheckUtil;

import java.io.Serializable;
import java.util.List;

public class InterestFreeBean implements Serializable {
    private List<RepayCouponsBean> repayCoupons;

    public List<RepayCouponsBean> getCoupons() {
        return repayCoupons;
    }

    public void setCoupons(List<RepayCouponsBean> coupons) {
        this.repayCoupons = coupons;
    }

    public static class RepayCouponsBean implements Serializable {
        private String batchNo;//批次号
        private String couponNo;//券号
        /**
         * 如kind为固定天数 则 返回如 30天
         * 如kind为固定金额 则返回如 30
         * 如kind为折扣券 则返回如 5%
         * 客户端不做处理，直接展示
         */
        private String parValue;//面值,kind为1,2,3返回该字段,某些接口kind为其他值时也可能会返回，注意合并区分
        private String useStartDt;//使用有效期截止日期
        private String useEndDt;//使用有效期截止日期
        private String bindStartDt;//绑定有效期（开始）
        private String bindEndDt;//绑定有效期（结束）
        private String validStartDt;//有效期（开始）,前端展示
        private String validEndDt;//有效期（结束）,前端展示
        private String bindApplSeq;//绑定贷款申请流水号
        private String bindPerNoMsg;//绑定期数文案
        private String bindDt;//绑定日期
        private String bindPerdNo;//绑定期数
        private String kind;//券种类 1固定天数  2固定金额  3折扣券 4抵扣券 5折扣券 7免息券
        private String discountNumber;//打折,kind为5折扣券返回该字段
        private String maxDisCountRate;//最高抵扣借款金额比率,kind为4抵扣券返回该字段
        private String maxFeeDecrease;//最高抵扣借款金额,kind为4抵扣券返回该字段
        private String isBest;//是否最优免息券,Y是N否,分接口返回此字段
        private String useState;//0跳转借款页  1跳转还款页   2不可用
        /*该字段用于前端在借款确认页的还款计划一栏展示优惠后金额使用，没有可用优惠券则前端不用展示该金额,
         *该金额为还款计划返回的应还总金额减去优惠金额
         * 有些接口返回没有该字段
         * 获取某个订单的全部券接口中前端传了当期应还金额，且URM返回的优惠金额不为空，才会返回discRepayAmt
         */
        private String discRepayAmt;//优惠后金额
        //获取某个订单的全部券接口才返回该字段，前端传了当期应还金额，且URM返回的优惠金额不为空，才会返回discRepayAmt
        private String discValue;//优惠金额
        private String coupUseAmt;//真实优惠金额（只用于七日待还列表下方已绑定或者最优券的已优惠金额的显示，其他地方或其他情况不返）
        /**
         * UNUSE 可使用
         * USED 已使用
         * EXPIRED 已过期
         * UNKNOW 未知状态
         */
        private String state;
        private String describe1;//优惠券名称
        private String describe2;//优惠券金额描述
        private String describe3;//优惠券使用描述
        private boolean isCheck = false;//本地特意新增属性
        private String batchDesc; //优惠券名称
        private String batchDetailDesc; //优惠券使用详解
        /**
         * 正常 1
         * 领兑未生效 2
         * <p>
         * 生效未使用 4
         * 生效未绑定 4A
         * 生效已绑定（需绑定） 3A
         * <p>
         * 生效已使用（无需绑定） 3
         * 绑定已使用（需绑定） 3B
         * <p>
         * 超期未使用（无需绑定） 5
         * 超期未绑定 5A
         * 绑定超期未使用 5B
         */
        private String subState;
        /**
         * 获取全部免息券接口才返回该字段
         * 过期状态,0：正常,1：即将过期（距到期日3天）,2：当日过期（到期日为当日）
         */
        private String expireState;

        //中收 新增VIP
        private String couponSource;//Y展示会员标识，N或者空不展示会员标识

        private String canUseState;//可使用状态  Y 可使用  N 不可使用

        private String reduceDays;//减免天数 kind为7 x天免息券返回该字段

        private String batchDeduction;//是否支持批扣 是Y 否N

        private String unUseDesc;//不可用原因描述

        private String kindSign;//货币符号
        private String kindVal;//数值
        private String kindUnit;//单位

        public RepayCouponsBean() {
        }

        public RepayCouponsBean(String batchNo, String couponNo, String parValue, String useStartDt, String useEndDt, String bindStartDt, String bindEndDt, String validStartDt, String validEndDt, String bindApplSeq, String bindPerNoMsg, String bindDt, String bindPerdNo, String kind, String discountNumber, String maxDisCountRate, String maxFeeDecrease, String isBest, String useState, String discRepayAmt, String discValue, String coupUseAmt, String state, String describe1, String describe2, String describe3, boolean isCheck, String batchDesc, String batchDetailDesc, String subState, String expireState, String couponSource, String canUseState, String reduceDays, String batchDeduction, String unUseDesc, String kindSign, String kindVal, String kindUnit) {
            this.batchNo = batchNo;
            this.couponNo = couponNo;
            this.parValue = parValue;
            this.useStartDt = useStartDt;
            this.useEndDt = useEndDt;
            this.bindStartDt = bindStartDt;
            this.bindEndDt = bindEndDt;
            this.validStartDt = validStartDt;
            this.validEndDt = validEndDt;
            this.bindApplSeq = bindApplSeq;
            this.bindPerNoMsg = bindPerNoMsg;
            this.bindDt = bindDt;
            this.bindPerdNo = bindPerdNo;
            this.kind = kind;
            this.discountNumber = discountNumber;
            this.maxDisCountRate = maxDisCountRate;
            this.maxFeeDecrease = maxFeeDecrease;
            this.isBest = isBest;
            this.useState = useState;
            this.discRepayAmt = discRepayAmt;
            this.discValue = discValue;
            this.coupUseAmt = coupUseAmt;
            this.state = state;
            this.describe1 = describe1;
            this.describe2 = describe2;
            this.describe3 = describe3;
            this.isCheck = isCheck;
            this.batchDesc = batchDesc;
            this.batchDetailDesc = batchDetailDesc;
            this.subState = subState;
            this.expireState = expireState;
            this.couponSource = couponSource;
            this.canUseState = canUseState;
            this.reduceDays = reduceDays;
            this.batchDeduction = batchDeduction;
            this.unUseDesc = unUseDesc;
            this.kindSign = kindSign;
            this.kindVal = kindVal;
            this.kindUnit = kindUnit;
        }

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

        public String getBindPerNoMsg() {
            return bindPerNoMsg;
        }

        public void setBindPerNoMsg(String bindPerNoMsg) {
            this.bindPerNoMsg = bindPerNoMsg;
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

        public boolean isShowVipUi() {
            return "Y".equals(couponSource);
        }

        public String getCouponSource() {
            return couponSource;
        }

        public void setCouponSource(String couponSource) {
            this.couponSource = couponSource;
        }

        public String getMaxFeeDecrease() {
            return maxFeeDecrease;
        }

        public void setMaxFeeDecrease(String maxFeeDecrease) {
            this.maxFeeDecrease = maxFeeDecrease;
        }

        public String getExpireState() {
            return expireState;
        }

        public void setExpireState(String expireState) {
            this.expireState = expireState;
        }

        public String getBatchNo() {
            return batchNo;
        }

        public void setBatchNo(String batchNo) {
            this.batchNo = batchNo;
        }

        public String getDiscValue() {
            return discValue;
        }

        public void setDiscValue(String discValue) {
            this.discValue = discValue;
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

        public String getIsBest() {
            return isBest;
        }

        public void setIsBest(String isBest) {
            this.isBest = isBest;
        }

        public String getDiscRepayAmt() {
            return discRepayAmt;
        }

        public void setDiscRepayAmt(String discRepayAmt) {
            this.discRepayAmt = discRepayAmt;
        }

        public String getSubState() {
            return subState;
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

        //是否是免息券
        public boolean isKindSeven() {
            return !CheckUtil.isEmpty(kind) && "7".equals(kind);
        }

        //是否是5或7
        public boolean isKindFiveOrSeven() {
            return !CheckUtil.isEmpty(kind) && "5".equals(kind) || !CheckUtil.isEmpty(kind) && "7".equals(kind);
        }

        public boolean isBest() {
            return "Y".equalsIgnoreCase(isBest);
        }

        /**
         * @return 已绑定状态
         */
        public boolean hasBind() {
            return !CheckUtil.isEmpty(bindApplSeq);
        }

        public String getUseState() {
            return useState;
        }

        public void setUseState(String useState) {
            this.useState = useState;
        }

        public void setSubState(String subState) {
            this.subState = subState;
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

        public String getValidStartDt() {
            return validStartDt;
        }

        public void setValidStartDt(String validStartDt) {
            this.validStartDt = validStartDt;
        }

        public String getValidEndDt() {
            return validEndDt;
        }

        public void setValidEndDt(String validEndDt) {
            this.validEndDt = validEndDt;
        }

        public String getBindApplSeq() {
            return bindApplSeq;
        }

        public void setBindApplSeq(String bindApplSeq) {
            this.bindApplSeq = bindApplSeq;
        }

        public String getBindDt() {
            return bindDt;
        }

        public void setBindDt(String bindDt) {
            this.bindDt = bindDt;
        }

        public String getBindPerdNo() {
            return bindPerdNo;
        }

        public void setBindPerdNo(String bindPerdNo) {
            this.bindPerdNo = bindPerdNo;
        }

        public String getCouponTypeName() {
            return "本笔可减";
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

        public String getShowDiscountDay() {
            String number = CheckUtil.isEmpty(reduceDays) ? parValue : reduceDays;
            return number != null && !number.contains("天") ? number + "天" : number;
        }

        private String getShowDiscountNumber() {
            String number = CheckUtil.isEmpty(discountNumber) ? parValue : discountNumber;
            return number != null && !number.contains("折") ? number + "折" : number;
        }

        /**
         * 是否显示前面的金钱符号
         */
        public boolean isFixedValue() {
            return hasDisValue() || "2".equals(kind) || "3".equals(kind) || "4".equals(kind);
        }

        /**
         * 获取真正的显示面额，绑定选择时有实际优惠金额就显示实际优惠金额，无则显示本身面额
         */
        public String getShowParValue() {
            return getCouponValue();
        }

        private boolean hasDisValue() {
            return !CheckUtil.isEmpty(discValue);
        }

        public void setCouponNo(String couponNo) {
            this.couponNo = couponNo;
        }

        public String getCouponNo() {
            return couponNo;
        }

        public void setUseEndDt(String useEndDt) {
            this.useEndDt = useEndDt;
        }

        public String getUseEndDt() {
            return useEndDt;
        }

        public void setKind(String kind) {
            this.kind = kind;
        }

        public String getKind() {
            return kind;
        }

        public void setParValue(String parValue) {
            this.parValue = parValue;
        }

        public String getParValue() {
            return parValue;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getState() {
            return state;
        }

        public void setDescribe1(String describe1) {
            this.describe1 = describe1;
        }

        public String getDescribe1() {
            return describe1;
        }

        public void setDescribe2(String describe2) {
            this.describe2 = describe2;
        }

        public String getDescribe2() {
            return describe2;
        }

        public void setDescribe3(String describe3) {
            this.describe3 = describe3;
        }

        public String getDescribe3() {
            return describe3;
        }

        public void setBatchDesc(String batchDesc) {
            this.batchDesc = batchDesc;
        }

        public String getBatchDesc() {
            return batchDesc;
        }

        public void setBatchDetailDesc(String batchDetailDesc) {
            this.batchDetailDesc = batchDetailDesc;
        }

        public String getBatchDetailDesc() {
            return batchDetailDesc;
        }

        public boolean isCheck() {
            return isCheck;
        }

        public void setCheck(boolean check) {
            isCheck = check;
        }

        public String getUseStartDt() {
            return useStartDt;
        }

        public void setUseStartDt(String useStartDt) {
            this.useStartDt = useStartDt;
        }

        public String getCoupUseAmt() {
            return coupUseAmt;
        }

        public void setCoupUseAmt(String coupUseAmt) {
            this.coupUseAmt = coupUseAmt;
        }
    }
}

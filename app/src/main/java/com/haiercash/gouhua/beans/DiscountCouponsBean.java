package com.haiercash.gouhua.beans;

import java.util.List;

public class DiscountCouponsBean {

    private List<CouponsBean> couponsList;

    public List<CouponsBean> getCouponsList() {
        return couponsList;
    }

    public void setCouponsList(List<CouponsBean> couponsList) {
        this.couponsList = couponsList;
    }

    public static class CouponsBean {
        public String mcid;                //优惠券主表id
        public String latinosName;         //权益名称
        public String latinosAmt;          //权益面额
        public String latinosDesc;         //权益描述R.id.llBottom
        public String latinosSub;          //权益描述副标题
        public String usedCondition;       //使用条件
        public String exchange;            //兑换标志
        public String exchangeAmt;         //兑换消耗够花券
        public String receiveStartDt;      //领取开始日期
        public String receiveEndDt;        //领取结束日期
        public String couponStartDt;       //优惠券有效开始日期
        public String couponEndDt;         //优惠券有效结束日期
        public String exchangeStartDt;     //兑换开始日期
        public String exchangeEndDt;       //兑换结束日期
        public String latinosDay;          //权益有效时间
        public String latinosStartDt;      //权益生效日期
        public String latinosEndDt;        //权益失效日期
        public String latinosType;         //权益类型   COUPON:优惠券 MJQ满减券  (MJQ相当于自有，COUPON相当于三方券)
        public String latinosPic;          //图片地址
        public String latinosIconPic;      //图片小图地址
        public String cdid;                //优惠券明细表id
        public String couponCode;          //券码
        public String couponPasswd;        //卡密
        public String receiveFlag;         //领用标志
        public String receiveDt;           //领用时间
        public String exchangeFlage;       //兑换标志
        public String exchangeDt;          //兑换时间
        public String updateDt;            //更新时间
        public String htmlUrl;             //H5请求优惠券信息地址
        public String useSence;            //使用场景
        public String link;                //跳转链接
        public String oriPrice;            //权益价格
        public String refPrice;            //参考价
        public String state;               //有效状态： valid 生效、invalid 过期，used 已用

        //中收 新增VIP
        public String passType;//卡密类型,CODE_PAS：券码+卡密；PAS：卡密；URL：链接
        public String couponSource;//Y展示会员标识，N或者空不展示会员标识

        public boolean isShowVipUi() {
            return "Y".equals(couponSource);
        }
    }
}

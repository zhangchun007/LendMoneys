package com.haiercash.gouhua.base;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.utils.image.ImageUtils;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.app.haiercash.base.utils.system.SystemUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.haiercash.gouhua.MainActivity;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.beans.Credit;
import com.haiercash.gouhua.beans.PopDialogNewBean;
import com.haiercash.gouhua.beans.getpayss.LoanCoupon;
import com.haiercash.gouhua.fragments.main.MainFragmentNew;
import com.haiercash.gouhua.interfaces.PopCallBack;
import com.haiercash.gouhua.utils.GlideUtils;
import com.haiercash.gouhua.utils.UMengUtil;
import com.haiercash.gouhua.utils.UiUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import oupson.apng.decoder.ApngDecoder;

/**
 * 通过接口查询的通用整合弹窗（PopupA一张图片+关闭按钮）（PopupB一张图片+横向俩按钮+可能带倒计时）
 * （PopupC一张图片+免息券数据+横向俩按钮+可能按钮带倒计时）PopupD一张图片+免息券数据+纵向俩按钮可能带倒计时）
 * 弹窗图片宽度为手机屏幕宽度，宽高比定死375:494
 * 横向两个按钮的弹窗模板：单个按钮宽高比定死120:40，两个按钮横向区域比率57:120:40:120:57，按钮父控件宽高比375:40
 * 横向两个按钮区域距离弹窗图片上边框352dp*屏幕宽度/375dp
 * 纵向两个按钮的弹窗模板：按钮横向居中，宽度为220dp*屏幕宽度/375dp，纵向整体按钮组宽高比定死220:95
 * 纵向两个按钮区域距离弹窗图片上边框318dp*屏幕宽度/375dp
 * 按钮文字大小也是16sp*屏幕宽度/375dp，都是按照比率进行适配
 * 图片支持apng动图，ApngDecoder自动支持apng、gif、静图
 * 模板C和D的免息券数据V都需要按照等比率设置宽高间距，文字大小也是按照等比率设置
 */
public class BaseControlDialog extends Dialog implements View.OnClickListener {
    protected BaseActivity mContext;// 上下文
    private ImageView popImageView;//内容图
    private View vCoupon;//免息券信息
    private TextView tvCouponDesc, tvCouponRemark, tvCouponKind;//券描述、券备注、“免息”
    private TextView tvMoneyChar, tvCouponValue, tvCouponUnit;//“¥”、面值、天/折
    private TextView btn1, btn2;
    private View vBtnGroup;
    //纵向按钮组
    private View vBtnGroupVertical;
    private TextView btn1Vertical, btn2Vertical;
    private ImageView ivClose;
    private PopDialogNewBean popDialogNewBean;
    private PopCallBack callBack;
    private String pageName, pageCode, actionPage;
    private CountDownTimer mCountDownTimer1, mCountDownTimer2;
    private ConstraintLayout template5;
    private ConstraintLayout template6;
    private TextView tv_real_price;
    private TextView tv_original_price;
    private TextView tv_price;
    private TextView tv_content;
    private TextView tvLoanCredit;

    public BaseControlDialog(@NonNull BaseActivity context, PopCallBack callBack) {
        super(context, R.style.Theme_Light_FullScreenDialogAct);
        this.callBack = callBack;
        initViewAndEvent(context);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
    }

    public void setCallback(PopCallBack callBack) {
        this.callBack = callBack;
    }

    public void setUmParam(String pageName, String pageCode, String actionPage) {
        this.pageName = pageName;
        this.pageCode = pageCode;
        this.actionPage = actionPage;
    }

    private void initViewAndEvent(BaseActivity context) {
        mContext = context;
        setContentView(R.layout.dialog_control);
        //设置window背景，默认的背景会有inset值，不能全屏。替换默认的背景即可。
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //一定要在setContentView之后调用，否则无效
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popImageView = findViewById(R.id.dialog_img);
        tvLoanCredit = findViewById(R.id.tv_loan_credit);
        popImageView.setOnClickListener(this);
        vCoupon = findViewById(R.id.v_coupon);
        tvCouponDesc = findViewById(R.id.tv_coupon_desc);
        tvCouponDesc.setTypeface(FontCustom.getMediumFont(mContext));
        tvCouponRemark = findViewById(R.id.tv_coupon_remark);
        tvCouponKind = findViewById(R.id.tv_coupon_kind);
        tvMoneyChar = findViewById(R.id.tv_money_char);
        tvCouponValue = findViewById(R.id.tv_coupon_value);
        tvCouponUnit = findViewById(R.id.tv_coupon_unit);
        vBtnGroup = findViewById(R.id.v_btn_group);
        btn1 = findViewById(R.id.btn_1);
        btn1.setTypeface(FontCustom.getMediumFont(mContext));
        btn1.setOnClickListener(this);
        btn2 = findViewById(R.id.btn_2);
        btn2.setTypeface(FontCustom.getMediumFont(mContext));
        btn2.setOnClickListener(this);
        //纵向按钮组
        vBtnGroupVertical = findViewById(R.id.v_btn_group_vertical);
        btn1Vertical = findViewById(R.id.btn_1_vertical);
        btn1Vertical.setTypeface(FontCustom.getMediumFont(mContext));
        btn1Vertical.setOnClickListener(this);
        btn2Vertical = findViewById(R.id.btn_2_vertical);
        btn2Vertical.setTypeface(FontCustom.getMediumFont(mContext));
        btn2Vertical.setOnClickListener(this);
        //关闭
        ivClose = findViewById(R.id.iv_close);
        ivClose.setOnClickListener(this);
        setOnDismissListener(dialog -> {
            if (callBack != null && popDialogNewBean != null) {
                callBack.postRecord(popDialogNewBean.getPopupId());
                callBack = null;
            }
        });
        //按照图片缩放比率适配，宽度是屏幕宽度
        int screenW = SystemUtils.getDeviceWidth(mContext);
        ConstraintLayout.LayoutParams loanCreditLayoutParams = (ConstraintLayout.LayoutParams) tvLoanCredit.getLayoutParams();
        loanCreditLayoutParams.topMargin = 205 * screenW / 375;
        ConstraintLayout.LayoutParams vBtnGroupLayoutParams = (ConstraintLayout.LayoutParams) vBtnGroup.getLayoutParams();
        vBtnGroupLayoutParams.topMargin = 352 * screenW / 375;
        vBtnGroup.setLayoutParams(vBtnGroupLayoutParams);
        ConstraintLayout.LayoutParams vBtnGroupVirtualLayoutParams = (ConstraintLayout.LayoutParams) vBtnGroupVertical.getLayoutParams();
        vBtnGroupVirtualLayoutParams.topMargin = 318 * screenW / 375;
        vBtnGroupVirtualLayoutParams.width = 220 * screenW / 375;
        vBtnGroupVirtualLayoutParams.height = 95 * screenW / 375;
        vBtnGroupVertical.setLayoutParams(vBtnGroupVirtualLayoutParams);
        float btnTextSize = 16f * screenW / UiUtil.dip2px(mContext, 375);
        btn1.setTextSize(btnTextSize);
        btn2.setTextSize(btnTextSize);
        btn1Vertical.setTextSize(btnTextSize);
        btn2Vertical.setTextSize(btnTextSize);
        tvCouponDesc.setMaxWidth(180 * screenW / 375);
        tvCouponDesc.setTextSize(15f * screenW / UiUtil.dip2px(mContext, 375));
        tvCouponRemark.setMaxWidth(150 * screenW / 375);
        tvCouponRemark.setTextSize(12f * screenW / UiUtil.dip2px(mContext, 375));
        tvCouponKind.setTextSize(12f * screenW / UiUtil.dip2px(mContext, 375));
        tvMoneyChar.setTextSize(15f * screenW / UiUtil.dip2px(mContext, 375));
        tvCouponValue.setTextSize(24f * screenW / UiUtil.dip2px(mContext, 375));
        tvCouponUnit.setTextSize(22f * screenW / UiUtil.dip2px(mContext, 375));

        //模版5
        template5 = findViewById(R.id.layout_template5);
        tv_price = findViewById(R.id.tv_price);
        tv_content = findViewById(R.id.tv_content);
        //模版6
        template6 = findViewById(R.id.layout_template6);
        tv_original_price = findViewById(R.id.tv_original_price);
        tv_real_price = findViewById(R.id.tv_real_price);
    }

    public void showDialog(PopDialogNewBean popDialogNewBean) {
        if (popDialogNewBean == null) {
            return;
        }
        this.popDialogNewBean = popDialogNewBean;
        String templateType = popDialogNewBean.getTemplateType();
        PopDialogNewBean.ButtonInfo buttonInfo1 = null, buttonInfo2 = null;
        if (isTemplateType2(templateType) || isTemplateType3(templateType)) {
            //模块二、三：一张图片+横向俩按钮，按钮也可能带倒计时
            ivClose.setVisibility(View.GONE);
            vBtnGroupVertical.setVisibility(View.GONE);
            template5.setVisibility(View.GONE);
            template6.setVisibility(View.GONE);
            if (popDialogNewBean.getButtonList() != null && popDialogNewBean.getButtonList().size() > 0 &&
                    (buttonInfo1 = popDialogNewBean.getButtonList().get(0)) != null) {
                setBtn(btn1, buttonInfo1);
            } else {
                setBtn(btn1, null);
            }
            if (popDialogNewBean.getButtonList() != null && popDialogNewBean.getButtonList().size() > 1 &&
                    (buttonInfo2 = popDialogNewBean.getButtonList().get(1)) != null) {
                setBtn(btn2, buttonInfo2);
            } else {
                setBtn(btn2, null);
            }
            vBtnGroup.setVisibility(View.VISIBLE);
            setCouponInfo(templateType, popDialogNewBean.getCouponInfo());
            loadDialogImage(templateType, popDialogNewBean.getContentImageUrl(), buttonInfo1, buttonInfo2);
        } else if (isTemplateType4(templateType)) {
            //模块四：一张图片+纵向俩按钮+可能带倒计时
            ivClose.setVisibility(View.GONE);
            vBtnGroup.setVisibility(View.GONE);
            template5.setVisibility(View.GONE);
            template6.setVisibility(View.GONE);
            if (popDialogNewBean.getButtonList() != null && popDialogNewBean.getButtonList().size() > 0 &&
                    (buttonInfo1 = popDialogNewBean.getButtonList().get(0)) != null) {
                setBtn(btn1Vertical, buttonInfo1);
            } else {
                setBtn(btn1Vertical, null);
            }
            if (popDialogNewBean.getButtonList() != null && popDialogNewBean.getButtonList().size() > 1 &&
                    (buttonInfo2 = popDialogNewBean.getButtonList().get(1)) != null) {
                setBtn(btn2Vertical, buttonInfo2);
            } else {
                setBtn(btn2Vertical, null);
            }
            vBtnGroupVertical.setVisibility(View.VISIBLE);
            setCouponInfo(templateType, popDialogNewBean.getCouponInfo());
            loadDialogImage(templateType, popDialogNewBean.getContentImageUrl(), buttonInfo1, buttonInfo2);
        } else if (isTemplateType1(templateType)) {//模块一：一张图片+关闭按钮
            ivClose.setVisibility(View.VISIBLE);
            template5.setVisibility(View.GONE);
            template6.setVisibility(View.GONE);
            GlideUtils.loadFitGif(mContext, ivClose, popDialogNewBean.getCancelIcon(),
                    R.drawable.icon_dialog_bottom_close, R.drawable.icon_dialog_bottom_close);
            vBtnGroup.setVisibility(View.GONE);
            vBtnGroupVertical.setVisibility(View.GONE);
            setCouponInfo(templateType, popDialogNewBean.getCouponInfo());
            loadDialogImage(templateType, popDialogNewBean.getContentImageUrl(), null, null);
        } else if (isTemplateType7(templateType)) {
            if (smartH5BeanCredit == null) {
                return;
            }
            tvLoanCredit.setVisibility(View.VISIBLE);
            ivClose.setVisibility(View.VISIBLE);
            template5.setVisibility(View.GONE);
            template6.setVisibility(View.GONE);
            GlideUtils.loadFitGif(mContext, ivClose, popDialogNewBean.getCancelIcon(),
                    R.drawable.icon_dialog_bottom_close, R.drawable.icon_dialog_bottom_close);
            vBtnGroup.setVisibility(View.GONE);
            vBtnGroupVertical.setVisibility(View.GONE);
            tvLoanCredit.setText(CheckUtil.showThound(smartH5BeanCredit.getApplyAmount()));
            tvLoanCredit.setTypeface(FontCustom.getDinFont(getContext()));
            setCouponInfo(templateType, popDialogNewBean.getCouponInfo());
            loadDialogImage(templateType, popDialogNewBean.getContentImageUrl(), null, null);
            postExposure();
        } else if (isTemplateType5(templateType)) {//模版5
            if (TextUtils.isEmpty(popDialogNewBean.getDynamicContentOne()) || TextUtils.isEmpty(popDialogNewBean.getDynamicContentTwo())) {
                return;
            }
            if (popDialogNewBean.getDynamicContentOne().endsWith("元")) {
                tv_price.setText(popDialogNewBean.getDynamicContentOne());
            } else {
                tv_price.setText(popDialogNewBean.getDynamicContentOne() + "元");
            }
            tv_content.setText(popDialogNewBean.getDynamicContentTwo());
            vBtnGroupVertical.setVisibility(View.GONE);
            vCoupon.setVisibility(View.GONE);
            template5.setVisibility(View.VISIBLE);
            template6.setVisibility(View.GONE);
            popImageView.setVisibility(View.VISIBLE);
            popImageView.setImageResource(R.drawable.img_dialog_template5);
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) popImageView.getLayoutParams();
            layoutParams.width = SystemUtils.getDeviceWidth(mContext);
            layoutParams.height = (int) (SystemUtils.getDeviceWidth(mContext) * 494 / 375);
            popImageView.setLayoutParams(layoutParams);

            //动态设置内容宽高跟位置
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) template5.getLayoutParams();
            params.width = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            params.height = ConstraintLayout.LayoutParams.WRAP_CONTENT;
            int marginTop = (int) layoutParams.height * 191 / 494;
            params.setMargins(0, marginTop, 0, 0);
            template5.setLayoutParams(params);

            ivClose.setVisibility(View.VISIBLE);
            GlideUtils.loadFitGif(mContext, ivClose, popDialogNewBean.getCancelIcon(),
                    R.drawable.icon_dialog_bottom_close, R.drawable.icon_dialog_bottom_close);
            show();
        } else if (isTemplateType6(templateType)) {
            if (TextUtils.isEmpty(popDialogNewBean.getDynamicContentOne()) || TextUtils.isEmpty(popDialogNewBean.getDynamicContentTwo())) {
                return;
            }
            tv_original_price.setText(popDialogNewBean.getDynamicContentOne());
            tv_real_price.setText(popDialogNewBean.getDynamicContentTwo());
            vBtnGroupVertical.setVisibility(View.GONE);
            vCoupon.setVisibility(View.GONE);
            popImageView.setVisibility(View.VISIBLE);
            template6.setVisibility(View.VISIBLE);
            template5.setVisibility(View.GONE);
            popImageView.setImageResource(R.drawable.img_dialog_template6);
            //动态设置图片宽高
            ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) popImageView.getLayoutParams();
            layoutParams.width = SystemUtils.getDeviceWidth(mContext);
            layoutParams.height = (int) (SystemUtils.getDeviceWidth(mContext) * 494 / 375);
            popImageView.setLayoutParams(layoutParams);

            //动态设置内容宽高跟位置
            ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) template6.getLayoutParams();
            params.width = (int) (SystemUtils.getDeviceWidth(mContext) * 208 / 375);
            params.height = (int) (params.width * 62 / 208);
            int marginLeft = (int) SystemUtils.getDeviceWidth(mContext) * 85 / 375;
            int marginTop = (int) layoutParams.height * 239 / 494;
            params.setMargins(marginLeft, marginTop, 0, 0);
            template6.setLayoutParams(params);
            //设置现额度距离
            ConstraintLayout.LayoutParams params1 = (ConstraintLayout.LayoutParams) tv_real_price.getLayoutParams();
            int params1_marginLeft = (int) params.width * 128 / 208;
            params1.setMargins(params1_marginLeft, 0, 0, 0);
            tv_real_price.setLayoutParams(params1);

            ivClose.setVisibility(View.VISIBLE);
            GlideUtils.loadFitGif(mContext, ivClose, popDialogNewBean.getCancelIcon(),
                    R.drawable.icon_dialog_bottom_close, R.drawable.icon_dialog_bottom_close);
            show();
        }//不做兜底展示，防止新增模板而走了兜底模板
    }

    private void setCouponInfo(String templateType, LoanCoupon couponInfo) {
        //免息券
        if (couponInfo != null) {
            tvCouponDesc.setText(couponInfo.getBatchDesc());
            tvCouponRemark.setText(couponInfo.getRemark());
            String kindSign = couponInfo.getKindSign();//货币符号
            String kindVal = couponInfo.getKindVal();//数值
            String kindUnit = couponInfo.getKindUnit();//单位
            switch (couponInfo.getKind()) {
                case "1":
                    tvCouponValue.setText(!CheckUtil.isEmpty(kindVal) ? kindVal : couponInfo.getParValue());
                    tvCouponValue.setVisibility(View.VISIBLE);
                    tvMoneyChar.setVisibility(View.GONE);
                    tvCouponUnit.setText(!CheckUtil.isEmpty(kindUnit) ? kindUnit : mContext.getResources().getString(R.string.dialog_control_coupon_day));
                    tvCouponUnit.setVisibility(View.VISIBLE);
                    break;
                case "2":
                case "3":
                    tvCouponValue.setText(!CheckUtil.isEmpty(kindVal) ? kindVal : couponInfo.getParValue());
                    tvCouponValue.setVisibility(View.VISIBLE);
                    tvMoneyChar.setVisibility(View.VISIBLE);
                    if (!CheckUtil.isEmpty(kindSign)) {
                        tvMoneyChar.setText(kindSign);
                    }
                    tvCouponUnit.setVisibility(View.GONE);
                    break;
                case "4":
                    tvCouponValue.setText(!CheckUtil.isEmpty(kindVal) ? kindVal : couponInfo.getMaxFeeDecrease());
                    tvCouponValue.setVisibility(View.VISIBLE);
                    tvMoneyChar.setVisibility(View.VISIBLE);
                    if (!CheckUtil.isEmpty(kindSign)) {
                        tvMoneyChar.setText(kindSign);
                    }
                    tvCouponUnit.setVisibility(View.GONE);
                    break;
                case "5":
                    tvCouponValue.setText(!CheckUtil.isEmpty(kindVal) ? kindVal : couponInfo.getDiscountNumber());
                    tvCouponValue.setVisibility(View.VISIBLE);
                    tvMoneyChar.setVisibility(View.GONE);
                    tvCouponUnit.setText(!CheckUtil.isEmpty(kindUnit) ? kindUnit : mContext.getResources().getString(R.string.dialog_control_coupon_dis));
                    tvCouponUnit.setVisibility(View.VISIBLE);
                    break;
                case "7":
                    tvCouponValue.setText(!CheckUtil.isEmpty(kindVal) ? kindVal : couponInfo.getReduceDays());
                    tvCouponValue.setVisibility(View.VISIBLE);
                    tvMoneyChar.setVisibility(View.GONE);
                    tvCouponUnit.setText(!CheckUtil.isEmpty(kindUnit) ? kindUnit : mContext.getResources().getString(R.string.dialog_control_coupon_day));
                    tvCouponUnit.setVisibility(View.VISIBLE);
                    break;
                default:
                    tvCouponValue.setVisibility(View.GONE);
                    tvMoneyChar.setVisibility(View.GONE);
                    tvCouponUnit.setVisibility(View.GONE);
                    break;
            }
        }
        //按照图片缩放比率适配，宽度是屏幕宽度
        int screenW = SystemUtils.getDeviceWidth(mContext);
        if (couponInfo != null && isTemplateType3(templateType)) {
            ConstraintLayout.LayoutParams tvCouponDescLayoutParams = (ConstraintLayout.LayoutParams) tvCouponDesc.getLayoutParams();
            tvCouponDescLayoutParams.setMarginStart(65 * screenW / 375);
            tvCouponDescLayoutParams.topMargin = 249 * screenW / 375;
            tvCouponDesc.setLayoutParams(tvCouponDescLayoutParams);
            ConstraintLayout.LayoutParams tvCouponRemarkLayoutParams = (ConstraintLayout.LayoutParams) tvCouponRemark.getLayoutParams();
            tvCouponRemarkLayoutParams.setMarginStart(65 * screenW / 375);
            tvCouponRemarkLayoutParams.topMargin = 275 * screenW / 375;
            tvCouponRemark.setLayoutParams(tvCouponRemarkLayoutParams);
            ConstraintLayout.LayoutParams tvCouponValueLayoutParams = (ConstraintLayout.LayoutParams) tvCouponValue.getLayoutParams();
            tvCouponValueLayoutParams.topMargin = 245 * screenW / 375;
            tvCouponValue.setLayoutParams(tvCouponValueLayoutParams);
            ConstraintLayout.LayoutParams tvCouponKindLayoutParams = (ConstraintLayout.LayoutParams) tvCouponKind.getLayoutParams();
            tvCouponKindLayoutParams.topMargin = 275 * screenW / 375;
            tvCouponKindLayoutParams.setMarginEnd(75 * screenW / 375);
            tvCouponKind.setLayoutParams(tvCouponKindLayoutParams);
            vCoupon.setVisibility(View.VISIBLE);
        } else if (couponInfo != null && isTemplateType4(templateType)) {
            ConstraintLayout.LayoutParams tvCouponDescLayoutParams = (ConstraintLayout.LayoutParams) tvCouponDesc.getLayoutParams();
            tvCouponDescLayoutParams.setMarginStart(63 * screenW / 375);
            tvCouponDescLayoutParams.topMargin = 238 * screenW / 375;
            tvCouponDesc.setLayoutParams(tvCouponDescLayoutParams);
            ConstraintLayout.LayoutParams tvCouponRemarkLayoutParams = (ConstraintLayout.LayoutParams) tvCouponRemark.getLayoutParams();
            tvCouponRemarkLayoutParams.setMarginStart(63 * screenW / 375);
            tvCouponRemarkLayoutParams.topMargin = 264 * screenW / 375;
            tvCouponRemark.setLayoutParams(tvCouponRemarkLayoutParams);
            ConstraintLayout.LayoutParams tvCouponValueLayoutParams = (ConstraintLayout.LayoutParams) tvCouponValue.getLayoutParams();
            tvCouponValueLayoutParams.topMargin = 232 * screenW / 375;
            tvCouponValue.setLayoutParams(tvCouponValueLayoutParams);
            ConstraintLayout.LayoutParams tvCouponKindLayoutParams = (ConstraintLayout.LayoutParams) tvCouponKind.getLayoutParams();
            tvCouponKindLayoutParams.topMargin = 264 * screenW / 375;
            tvCouponKindLayoutParams.setMarginEnd(75 * screenW / 375);
            tvCouponKind.setLayoutParams(tvCouponKindLayoutParams);
            vCoupon.setVisibility(View.VISIBLE);
        } else {
            vCoupon.setVisibility(View.GONE);
        }
    }

    private void loadDialogImage(String templateType, String imageUrl, PopDialogNewBean.ButtonInfo buttonInfo1, PopDialogNewBean.ButtonInfo buttonInfo2) {
        Glide.with(mContext).downloadOnly().load(imageUrl).into(new CustomTarget<File>() {
            @Override
            public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                try {
                    if ("home".equals(actionPage) && mContext instanceof MainActivity &&
                            !(((MainActivity) mContext).getCurrentFragment() instanceof MainFragmentNew)) {
                        //确保首页弹出只能首页弹出
                        return;
                    }
                    //下载到本地，判断是否为apng动图还是gif或者其他，加载框架不一样，后面如果有都支持的话，可以替换
                    String imageFileType = ImageUtils.getBitmapType(resource.getAbsolutePath());
                    if (imageFileType.toLowerCase().endsWith("apng")) {
                        ApngDecoder.decodeApngAsyncInto(mContext, resource, popImageView);
                    } else {
                        Glide.with(mContext).load(resource).into(popImageView);
                    }
                    destroyCountDownTimer1();
                    destroyCountDownTimer2();
                    show();
                    boolean isTemplateType4 = isTemplateType4(templateType);
                    mCountDownTimer1 = startCounter(isTemplateType4 ? btn1Vertical : btn1, buttonInfo1);
                    mCountDownTimer2 = startCounter(isTemplateType4 ? btn2Vertical : btn2, buttonInfo2);
                } catch (Exception e) {
                    //
                }
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {

            }
        });
    }

    private boolean isTemplateType1(String templateType) {
        return "PopupA".equals(templateType);
    }

    private boolean isTemplateType7(String templateType) {
        return "PopupG".equals(templateType);
    }

    private boolean isTemplateType2(String templateType) {
        return "PopupB".equals(templateType);
    }

    private boolean isTemplateType3(String templateType) {
        return "PopupC".equals(templateType);
    }

    private boolean isTemplateType4(String templateType) {
        return "PopupD".equals(templateType);
    }

    private boolean isTemplateType5(String templateType) {
        return "PopupE".equals(templateType);
    }

    private boolean isTemplateType6(String templateType) {
        return "PopupF".equals(templateType);
    }

    private void setBtn(TextView btn, final PopDialogNewBean.ButtonInfo buttonInfo) {
        try {
            if (buttonInfo == null) {
                btn.setVisibility(View.INVISIBLE);
            } else {
                btn.setText(buttonInfo.getCopyWriteName());
                try {
                    btn.setTextColor(Color.parseColor(buttonInfo.getCopyWriteColor()));
                } catch (Exception e) {
                    //
                }
                btn.setBackground(null);//重置，防止仍显示上次的数据
                GlideUtils.getNetDrawable(mContext, buttonInfo.getImageUrl(), new INetResult() {
                    @Override
                    public <T> void onSuccess(T t, String url) {
                        try {
                            btn.setBackground((Drawable) t);
                        } catch (Exception e) {
                            //
                        }
                    }

                    @Override
                    public void onError(BasicResponse error, String url) {

                    }
                });
                btn.setVisibility(View.VISIBLE);
            }
        } catch (Exception e) {
            //防止view突然空
        }
    }

    @Override
    public void show() {
        if (!isShowing()) {//防止重复展示和曝光
            super.show();
            postExposureEvent();
        } else {
            super.show();
        }
    }

    @Override
    public void dismiss() {
        super.dismiss();
        //消失时注意取消倒计时
        destroyCountDownTimer1();
        destroyCountDownTimer2();
        //callBack=null;
    }

    //释放
    private void destroyCountDownTimer1() {
        if (mCountDownTimer1 != null) {
            mCountDownTimer1.cancel();
            mCountDownTimer1 = null;
        }
    }

    private void destroyCountDownTimer2() {
        if (mCountDownTimer2 != null) {
            mCountDownTimer2.cancel();
            mCountDownTimer2 = null;
        }
    }

    //倒计时开始，当然也可能没有
    private CountDownTimer startCounter(TextView maybeCountDownTimerTV, PopDialogNewBean.ButtonInfo buttonInfo) {
        long totalTime;
        try {
            totalTime = Long.parseLong(buttonInfo.getControlTimeLimit());
        } catch (Exception e) {
            totalTime = 0;
        }
        if (totalTime <= 0) {
            return null;
        }
        return new CountDownTimer(totalTime * 1000, 1000) {
            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                setBtnText(maybeCountDownTimerTV, buttonInfo.getCopyWriteName() + "（" + (millisUntilFinished / 1000 + 1) + "S）");
            }

            @Override
            public void onFinish() {
                //弹窗展示中状态才能自动跳转
//                if (isShowing() && callBack != null) {
//                    callBack.clickJump(buttonInfo.getRouteType(), buttonInfo.getControlTimeRoute());
//                    dismiss();
//                }
                //暂时所有倒计时结束只是关闭弹窗，不自动跳转
                dismiss();
            }
        }.start();
    }

    private void setBtnText(TextView btnText, String text) {
        try {
            btnText.setText(text);
        } catch (Exception e) {
            //因为是倒计时，所以结束或者进行时可能出现UI对象没有了
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dialog_img) {
            if (popDialogNewBean != null && isTemplateType1(popDialogNewBean.getTemplateType()) || isTemplateType5(popDialogNewBean.getTemplateType()) || isTemplateType6(popDialogNewBean.getTemplateType())) {
                //模板1点击图片才有埋点
                postClickEvent("Confirm");
                if (callBack != null) {
                    callBack.clickJump(popDialogNewBean.getRouteType(), popDialogNewBean.getRouteAddress(), popDialogNewBean.getPopupId());
                }
            }
            if (isTemplateType7(popDialogNewBean.getTemplateType()) && smartH5BeanCredit != null) {
                postClick();
                if (callBack != null) {
                    callBack.clickJump(popDialogNewBean.getRouteType(), smartH5BeanCredit.getLoanJumpUrl(), popDialogNewBean.getPopupId());
                }
            }
        } else if (v.getId() == R.id.btn_1) {
            if (callBack != null && popDialogNewBean != null && popDialogNewBean.getButtonList() != null &&
                    popDialogNewBean.getButtonList().size() > 0 && popDialogNewBean.getButtonList().get(0) != null) {
                PopDialogNewBean.ButtonInfo buttonInfo = popDialogNewBean.getButtonList().get(0);
                postClickEvent(CheckUtil.isEmpty(buttonInfo.getCopyWriteName()) ? "Cancel" : buttonInfo.getCopyWriteName());
                callBack.clickJump(buttonInfo.getRouteType(), buttonInfo.getRouteAddress(), popDialogNewBean.getPopupId());
            } else {
                postClickEvent("Cancel");
            }
            dismiss();
        } else if (v.getId() == R.id.btn_2) {//模板
            if (callBack != null && popDialogNewBean != null && popDialogNewBean.getButtonList() != null &&
                    popDialogNewBean.getButtonList().size() > 1 && popDialogNewBean.getButtonList().get(1) != null) {
                PopDialogNewBean.ButtonInfo buttonInfo = popDialogNewBean.getButtonList().get(1);
                postClickEvent(CheckUtil.isEmpty(buttonInfo.getCopyWriteName()) ? "Confirm" : buttonInfo.getCopyWriteName());
                callBack.clickJump(buttonInfo.getRouteType(), buttonInfo.getRouteAddress(), popDialogNewBean.getPopupId());
            } else {
                postClickEvent("Confirm");
            }
            dismiss();
        } else if (v.getId() == R.id.btn_1_vertical) {//模板4通常上面的是确定，下面的是取消
            if (callBack != null && popDialogNewBean != null && popDialogNewBean.getButtonList() != null &&
                    popDialogNewBean.getButtonList().size() > 0 && popDialogNewBean.getButtonList().get(0) != null) {
                PopDialogNewBean.ButtonInfo buttonInfo = popDialogNewBean.getButtonList().get(0);
                postClickEvent(CheckUtil.isEmpty(buttonInfo.getCopyWriteName()) ? "Confirm" : buttonInfo.getCopyWriteName());
                callBack.clickJump(buttonInfo.getRouteType(), buttonInfo.getRouteAddress(), popDialogNewBean.getPopupId());
            } else {
                postClickEvent("Confirm");
            }
            dismiss();
        } else if (v.getId() == R.id.btn_2_vertical) {//模板4通常上面的是确定，下面的是取消
            if (callBack != null && popDialogNewBean != null && popDialogNewBean.getButtonList() != null &&
                    popDialogNewBean.getButtonList().size() > 1 && popDialogNewBean.getButtonList().get(1) != null) {
                PopDialogNewBean.ButtonInfo buttonInfo = popDialogNewBean.getButtonList().get(1);
                postClickEvent(CheckUtil.isEmpty(buttonInfo.getCopyWriteName()) ? "Cancel" : buttonInfo.getCopyWriteName());
                callBack.clickJump(buttonInfo.getRouteType(), buttonInfo.getRouteAddress(), popDialogNewBean.getPopupId());
            } else {
                postClickEvent("Cancel");
            }
            dismiss();
        } else if (v.getId() == R.id.iv_close) {
            postClickEvent("Cancel");
            dismiss();
        }
    }

    private void postExposureEvent() {
        if (popDialogNewBean == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        if (!CheckUtil.isEmpty(pageName)) {
            map.put("page_name_cn", pageName);
        }
        if (!CheckUtil.isEmpty(popDialogNewBean.getPopupName())) {
            map.put("pop_name", popDialogNewBean.getPopupName());
        }
        if (!CheckUtil.isEmpty(popDialogNewBean.getPopupId())) {
            map.put("pop_id", popDialogNewBean.getPopupId());
        }
        if ("home".equals(actionPage)) {
            UMengUtil.onEventObject("Gh_Home_Dialog_Exposure", map, pageCode);
        }
    }

    private void postClickEvent(String btnName) {
        if (popDialogNewBean == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        if (!CheckUtil.isEmpty(pageName)) {
            map.put("page_name_cn", pageName);
        }
        if (!CheckUtil.isEmpty(popDialogNewBean.getPopupName())) {
            map.put("pop_name", popDialogNewBean.getPopupName());
        }
        if (!CheckUtil.isEmpty(popDialogNewBean.getPopupId())) {
            map.put("pop_id", popDialogNewBean.getPopupId());
        }
        if (!CheckUtil.isEmpty(btnName)) {
            map.put("button_name", btnName);
        }
        if ("home".equals(actionPage)) {
            UMengUtil.onEventObject("Gh_Home_Dialog_Click", map, pageCode);
        }
    }

    private Credit smartH5BeanCredit;

    public void setPopupGInfo(Credit smartH5BeanCredit) {
        this.smartH5BeanCredit = smartH5BeanCredit;
    }

    public void postExposure() {
        HashMap<String, Object> map = new HashMap<>();
        String clientGroup = smartH5BeanCredit.getClientGroup();
        String purpose = "";
        if ("1".equals(clientGroup)) {
            purpose = "hea";
        } else if ("2".equals(clientGroup)) {
            purpose = "edu";
        }
        map.put("purpose", purpose);
        UMengUtil.commonExposureEvent("scene2cash_popup", "首页", "", map, "");
    }

    public void postClick() {
        HashMap<String, Object> map = new HashMap<>();
        String clientGroup = smartH5BeanCredit.getClientGroup();
        String purpose = "";
        if ("1".equals(clientGroup)) {
            purpose = "hea";
        } else if ("2".equals(clientGroup)) {
            purpose = "edu";
        }
        map.put("purpose", purpose);
        UMengUtil.commonClickEvent("scene2cash_popup_continue", "立即使用", "首页", map, "");
    }
}

package com.haiercash.gouhua.unity;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.app.haiercash.base.net.token.TokenHelper;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.app.haiercash.base.utils.system.FontCustom;
import com.haiercash.gouhua.R;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.base.BaseActivity;
import com.haiercash.gouhua.beans.unity.ActionBean;
import com.haiercash.gouhua.beans.unity.ComponentBean;
import com.haiercash.gouhua.beans.unity.ComponentInfoBean;
import com.haiercash.gouhua.beans.unity.PersonalRepayBean;
import com.haiercash.gouhua.beans.unity.RepayCardBean;
import com.haiercash.gouhua.beans.unity.ShowConditionBean;
import com.haiercash.gouhua.fragments.main.MainEduBorrowUntil;
import com.haiercash.gouhua.network.NetHelper;
import com.haiercash.gouhua.utils.UMengUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 还款卡片模块组件
 * @Author: zhangchun
 * @CreateDate: 2023/11/15
 * @Version: 1.0
 */
public class HRRepaymentComponent extends FrameLayout implements View.OnClickListener, INetResult {
    private HRTitleBarComponent titleBarComponent;
    private TextView tvTitle, tvContent, tvBubble, tvSubTitle, tvSubSubble, btnAction, tvNoPayTitle, tvNoPayment;
    private View viewSpace;
    private ConstraintLayout llNoPay, llMonthPay;
    private Map<String, Object> mPersonMap;
    private NetHelper netHelper;
    private Context mContext;
    private HashMap<String, Object> edCardClickEvent;//卡片点击事件
    private RepayCardBean repayCard;  //还款数据
    private ActionBean repayAction;

    public HRRepaymentComponent(@NonNull Context context) {
        super(context);
        init(context);
    }

    public HRRepaymentComponent(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HRRepaymentComponent(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        if (context == null) return;
        mContext = context;
        netHelper = new NetHelper(this, this);
        View view = LayoutInflater.from(context).inflate(R.layout.layout_repayment_component, this);
        titleBarComponent = view.findViewById(R.id.tv_title_bar);
        llMonthPay = view.findViewById(R.id.ll_month_pay);
        tvTitle = view.findViewById(R.id.tv_title);
        tvContent = view.findViewById(R.id.tv_content);
        tvContent.setTypeface(FontCustom.getDinFont(context));
        tvBubble = view.findViewById(R.id.tv_bubble);
        tvSubTitle = view.findViewById(R.id.tv_sub_title);
        tvSubSubble = view.findViewById(R.id.tv_sub_bubble);
        btnAction = view.findViewById(R.id.btn_action);
        btnAction.setOnClickListener(this);
        viewSpace = view.findViewById(R.id.viewSpace);
        llNoPay = view.findViewById(R.id.ll_no_pay);
        llNoPay.setOnClickListener(this);
        tvNoPayTitle = view.findViewById(R.id.tv_no_pay_title);
        tvNoPayment = view.findViewById(R.id.tv_no_payment);
        setVisibility(GONE);
    }


    /**
     * 设置数据
     *
     * @param componentBean
     */
    public void setData(ComponentBean componentBean, Map<String, Object> personMap) {
        this.mPersonMap = personMap;
        if (!UserStateUtils.isLogIn()
                || componentBean == null
                || !ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(componentBean.getDefaultShow(), mPersonMap))) {
            setVisibility(GONE);
            return;
        }
        ComponentInfoBean componentInfoBean = componentBean.getData();
        //设置标题
        boolean showTitle =false;
        if (componentInfoBean.getTitle()!=null){
            showTitle=  ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(componentInfoBean.getTitle().getDefaultShow(), mPersonMap));
        }
        boolean showMore = false;
        if (componentInfoBean.getShowMore()!=null){
            showMore=ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(componentInfoBean.getShowMore().getDefaultShow(), mPersonMap));
        }
        if (!showTitle && !showMore) {
            titleBarComponent.setVisibility(GONE);
        } else {
            titleBarComponent.setVisibility(VISIBLE);
            titleBarComponent.setData(componentInfoBean.getTitle(), showTitle, componentInfoBean.getShowMore(), showMore,mPersonMap);
        }

        if (componentInfoBean.getSourceData() != null && componentInfoBean.getSourceData().getRepayCard() != null) {
            repayCard = componentInfoBean.getSourceData().getRepayCard();
            if (!CheckUtil.isEmpty(repayCard.getAction())) {
                edCardClickEvent = repayCard.getAction().getEvent();
            }
            setVisibility(VISIBLE);
            postExposure(repayCard);
            repayAction = repayCard.getAction();
            String userAmount = ReplaceHolderUtils.replaceKeysWithValues(repayCard.getAmount(), mPersonMap);
            if (isCurrentMonthRepayed()) {//
                llNoPay.setVisibility(VISIBLE);
                llMonthPay.setVisibility(GONE);
                llMonthPay.setVisibility(GONE);
                tvNoPayTitle.setText(ReplaceHolderUtils.replaceKeysWithValues(repayCard.getText(), mPersonMap));
                tvNoPayment.setText(userAmount);
//                llNoPay.setOnClickListener(v -> JumpUtils.jumpAction(getContext(), repayCard.getAction()));
            } else {//本月待还
                llNoPay.setVisibility(GONE);
                llMonthPay.setVisibility(VISIBLE);
                //主标题
                tvTitle.setText(ReplaceHolderUtils.replaceKeysWithValues(repayCard.getText(), mPersonMap));
                tvContent.setText(userAmount);

                //副标题
                if (TextUtils.isEmpty(ReplaceHolderUtils.replaceKeysWithValues(repayCard.getSubText(), mPersonMap))) {
                    tvSubTitle.setVisibility(GONE);
                } else {
                    tvSubTitle.setVisibility(VISIBLE);
                    tvSubTitle.setText(ReplaceHolderUtils.replaceKeysWithValues(repayCard.getSubText(), mPersonMap));
                    tvSubTitle.setTextColor(Color.parseColor(getSubTextColor(repayCard.getSubTextColor(), mPersonMap)));
                }

                //主气泡
                if (ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(repayCard.getBubbleText().getDefaultShow(), mPersonMap))) {
                    tvBubble.setVisibility(VISIBLE);
                    viewSpace.setVisibility(VISIBLE);
                    tvBubble.setText(ReplaceHolderUtils.replaceKeysWithValues(repayCard.getBubbleText().getPopText(), mPersonMap));
                } else {
                    tvBubble.setVisibility(GONE);
                    viewSpace.setVisibility(GONE);
                }

                //副气泡
                if (ReplaceHolderUtils.isShowView(ReplaceHolderUtils.defaultShowWithValues(repayCard.getSubBubbleText().getDefaultShow(), mPersonMap))) {
                    tvSubSubble.setVisibility(VISIBLE);
                    tvSubSubble.setText(ReplaceHolderUtils.replaceKeysWithValues(repayCard.getSubBubbleText().getPopText(), mPersonMap));
                } else {
                    tvSubSubble.setVisibility(GONE);
                }

                //按钮
                btnAction.setText(ReplaceHolderUtils.replaceKeysWithValues(repayCard.getBtnText(), mPersonMap));
//                btnAction.setOnClickListener(v -> {
//                    JumpUtils.jumpAction(getContext(), repayCard.getAction());
//                });
            }
        } else {
            setVisibility(GONE);
        }
    }

    //曝光事件

    /**
     * "exposure": {
     * "event_id": "Xj_Me_RepayCard_Exposure",
     * "page_name_cn": "个人中心",
     * "overdue_day": "${repayInfo.remainDay}",
     * "recently_repay_day": "${repayInfo.subText}",
     * "overdue_flag": "${repayInfo.loanIsOd}",
     * "repay_state": "${repayInfo.repayStatus}"
     * }
     *
     * @param repayCard
     */
    private void postExposure(RepayCardBean repayCard) {
        HashMap<String, Object> map = new HashMap<>(repayCard.getExposure());
        if (!CheckUtil.isEmpty(map) && !CheckUtil.isEmpty(map.get("event_id"))) {
            String overdueFlag = ReplaceHolderUtils.replaceKeysWithValues(map.get("overdue_flag") + "", mPersonMap);
            if ("1".equals(overdueFlag)) {
                map.put("recently_repay_day", "");
                map.put("overdue_day", ReplaceHolderUtils.replaceKeysWithValues(map.get("overdue_day") + "", mPersonMap));
            } else {
                map.put("overdue_day", "");
                map.put("recently_repay_day", ReplaceHolderUtils.replaceKeysWithValues(map.get("recently_repay_day") + "", mPersonMap));
            }
            map.put("overdue_flag", "1".equals(overdueFlag) ? "是" : "否");
            map.put("repay_state", ReplaceHolderUtils.replaceKeysWithValues(map.get("repay_state") + "", mPersonMap));
            UMengUtil.postEvent(map);
        }
    }

    /**
     * 本月是否已还清
     *
     * @return
     */
    private boolean isCurrentMonthRepayed() {
        String realValue = "";
        if (mPersonMap != null && mPersonMap.get("repayInfo.repayStatus") != null) {
            realValue = mPersonMap.get("repayInfo.repayStatus").toString();
        }
        return "03".equals(realValue) || "02".equals(realValue);
    }

    /**
     * 获取subtext文字颜色
     *
     * @param list
     * @param map
     * @return
     */
    private String getSubTextColor(List<ShowConditionBean> list, Map<String, Object> map) {
        String subTextColor = "#909199";
        if (list != null && list.size() > 0 && map != null) {
            for (int i = 0; i < list.size(); i++) {
                String key = list.get(i).getKey();
                String standard = list.get(i).getStandard();
                String defaultValue = list.get(i).getValue();
                if (standard.equals(ReplaceHolderUtils.replaceKeysWithValues(key, map))) {
                    subTextColor = defaultValue;
                }
                break;
            }
        }
        return subTextColor;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ll_no_pay:
            case R.id.btn_action:
                postClickEvent();
                jumpToRepay();
                break;
        }
    }

    /**
     * 跳转到我的待还
     */
    private void jumpToRepay() {
        if (repayAction != null) {
            if ("multipleRepay".equals(repayAction.getActionType())) {//自营助贷
                if (netHelper != null) {
                    ((BaseActivity) mContext).showProgress(true);
                    Map<String, String> map = new HashMap<>();
                    map.put("flag", "M");//本月
                    String processId = TokenHelper.getInstance().getH5ProcessId();
                    map.put("processId", processId);
                    netHelper.postService(ApiUrl.POST_GO_REPAY, map, PersonalRepayBean.class);
                }
            } else {
                repayAction.setJumpUrl(ReplaceHolderUtils.replaceKeysWithValues(repayAction.getJumpUrl(), mPersonMap));
                JumpUtils.jumpAction(getContext(), repayAction);
            }
        }
    }

    //额度卡片点击事件
    private void postClickEvent() {
        if (!CheckUtil.isEmpty(repayCard) && !CheckUtil.isEmpty(edCardClickEvent)) {
            HashMap<String, Object> map = new HashMap<>(edCardClickEvent);
            String overdueFlag = ReplaceHolderUtils.replaceKeysWithValues(repayCard.getLoanIsOd(), mPersonMap);
            if ("1".equals(overdueFlag)) {
                map.put("recently_repay_day", "");
                map.put("overdue_day", ReplaceHolderUtils.replaceKeysWithValues(repayCard.getRemainDay(), mPersonMap));
            } else {
                map.put("overdue_day", "");
                map.put("recently_repay_day", ReplaceHolderUtils.replaceKeysWithValues(repayCard.getSubText(), mPersonMap));
            }
            map.put("overdue_flag", "1".equals(overdueFlag) ? "是" : "否");
            map.put("repay_state", ReplaceHolderUtils.replaceKeysWithValues(repayCard.getRepayStatusName(), mPersonMap));
            UMengUtil.postEvent(map);
        }
    }

    @Override
    public <T> void onSuccess(T t, String url) {
        ((BaseActivity) mContext).showProgress(false);
        if (ApiUrl.POST_GO_REPAY.equals(url)) {
            PersonalRepayBean repayBean = (PersonalRepayBean) t;
            if (!CheckUtil.isEmpty(repayBean) && !CheckUtil.isEmpty(repayBean.getRepayList())) {
                MainEduBorrowUntil.INSTANCE((BaseActivity) mContext).goRepay(repayBean.getRepayList());
            } else {
                showErrorDialog();
            }
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        ((BaseActivity) mContext).showProgress(false);
        showErrorDialog();
    }
    private void showErrorDialog() {
        DialogUtils.create(mContext)
                .setTitle("")
                .setContent("网络有误，请稍后~")
                .setDialogStyle(DialogStyle.STYLE1)
                .setShowLeftButton(false)
                .setRightButtoText("我知道了")
                .show();
    }
}

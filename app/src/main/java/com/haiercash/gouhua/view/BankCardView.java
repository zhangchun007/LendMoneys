package com.haiercash.gouhua.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import androidx.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.haiercash.gouhua.R;
import com.app.haiercash.base.utils.system.CheckUtil;
import com.haiercash.gouhua.utils.UiUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author: Sun
 * @Date :    2018/10/9
 * @FileName: BankCardView
 * @Description:
 */
public class BankCardView extends FrameLayout {

    private static final int ClearText = 1;//明文样式
    private static final int CipherText = 2;//掩码样式
    @BindView(R.id.icon_bank)
    ImageView iconBank;
    @BindView(R.id.tv_bankcardname)
    TextView tvBankcardname;
    @BindView(R.id.background_img)
    LinearLayout backgroundImg;
    @BindView(R.id.ll_bank_code)
    LinearLayout llBankCode;
    @BindView(R.id.iv_station)
    ImageView ivStation;
    private int mShowStytle = ClearText; //当前样式

    private Context mContext;

    public BankCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }


    private void initView(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.BankCardViewStyle);
            mShowStytle = a.getInt(R.styleable.BankCardViewStyle_text_style, ClearText);
            a.recycle();
        }
        mContext = context;
        View view = View.inflate(context, R.layout.view_bankcard_item, this);
        ButterKnife.bind(view);
    }


    /**
     * 设置银行卡号
     *
     * @param bankCode
     */
    private void addBankCodeTextView(String bankCode) {
        //明文状态
        if (mShowStytle == ClearText) {
            int length = (bankCode.length() + 3) / 4;
            for (int i = 0; i < length; i++) {
                TextView textView = getTextView();
                if (i < length - 1) {
                    textView.setText(bankCode.substring(i * 4, (i + 1) * 4));
                } else {
                    textView.setText(bankCode.substring(i * 4));
                }
                llBankCode.addView(textView);
            }
        } else {
            //掩码状态
            int length = 4;
            for (int i = 0; i < length; i++) {
                TextView textView = getTextView();
                if (i < length - 1) {
                    textView.setText("****");
                } else {
                    textView.setText(CheckUtil.getBankNum(bankCode));
                }
                llBankCode.addView(textView);
            }
        }
    }

    private TextView getTextView() {
        TextView textView = new TextView(mContext);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1.0f);
        layoutParams.setMargins(8, 0, 8, 0);
        textView.setLayoutParams(layoutParams);
        textView.setTextColor(Color.WHITE);
        textView.setGravity(Gravity.CENTER);
        textView.setSingleLine();
        textView.setTextSize(22);
        return textView;
    }

    /**
     * 更新银行卡View
     *
     * @param bankName
     * @param bankCode
     */
    public void updateView(String bankName, String bankCode, String typeName) {
        iconBank.setVisibility(VISIBLE);
        tvBankcardname.setText(String.valueOf(bankName + typeName));
        backgroundImg.setBackgroundResource(UiUtil.getBankCardBgRes(bankName));
        iconBank.setImageResource(UiUtil.getBankCardImageRes(bankName));
        llBankCode.removeAllViews();
        addBankCodeTextView(bankCode);
    }

    public void initView() {
        llBankCode.removeAllViews();
        backgroundImg.setBackgroundResource(R.drawable.backgdefaultd);
        tvBankcardname.setText("");
        iconBank.setVisibility(INVISIBLE);
    }


    public void initStation(String support, String signStatus,boolean isShowArrow) {
        ivStation.setVisibility(VISIBLE);
        if ("N".equals(support)) {
            ivStation.setImageResource(R.drawable.iv_bank_notsupport);
        } else if ("SIGN_SUCCESS".equals(signStatus)) {
            ivStation.setImageResource(R.drawable.iv_bank_signed);
        } else {
            if(isShowArrow){
                ivStation.setImageResource(R.drawable.icon_bank_unsign);
            }else{
                ivStation.setImageResource(R.drawable.iv_bank_unsign);
            }
        }

    }
}

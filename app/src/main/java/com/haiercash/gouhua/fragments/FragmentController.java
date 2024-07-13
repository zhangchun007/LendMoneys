package com.haiercash.gouhua.fragments;

import android.os.Bundle;

import com.haiercash.gouhua.account.CheckPhoneFragment;
import com.haiercash.gouhua.account.CheckRealFourFragment;
import com.haiercash.gouhua.account.CheckRealThreeFragment;
import com.haiercash.gouhua.activity.comm.SetEnvironment;
import com.haiercash.gouhua.activity.contract.MemberForgotPassWord;
import com.haiercash.gouhua.activity.contract.WebSimpleFragment;
import com.haiercash.gouhua.base.BaseFragment;
import com.haiercash.gouhua.bill.AllBillsFragment;
import com.haiercash.gouhua.bill.PeriodBillsFragment;
import com.haiercash.gouhua.fragments.mine.CheckCertNoFragment;
import com.haiercash.gouhua.fragments.mine.MessageListFragment;
import com.haiercash.gouhua.fragments.mine.ResetPasswordForSms;
import com.haiercash.gouhua.repayment.InThePaymentFragment;


public class FragmentController {
    public static BaseFragment obtain(int id, Bundle extra) {
        if (id == WebSimpleFragment.ID) {
            return WebSimpleFragment.newInstance(extra);
        } else if (id == BorrowRepayDetailFragment.ID) {
            return BorrowRepayDetailFragment.newInstance(extra);
        } else if (id == MemberForgotPassWord.ID) {
            return MemberForgotPassWord.newInstance(extra);
        } else if (id == SetEnvironment.ID) {
            return SetEnvironment.newInstance(extra);
        } else if (id == InThePaymentFragment.ID) {
            return InThePaymentFragment.newInstance(extra);
        } else if (id == PeriodBillsFragment.ID) {
            return PeriodBillsFragment.newInstance(extra);
        } else if (id == AllBillsFragment.ID) {
            return AllBillsFragment.newInstance(extra);
        } else if (id == ApplyRecordeFragment.ID) {
            return ApplyRecordeFragment.newInstance(extra);
        } else if (id == CreditLifeBorrowFragment.ID) {
            return CreditLifeBorrowFragment.newInstance(extra);
        } else if (id == CreditApplyDetailFragment.ID) {
            return CreditApplyDetailFragment.newInstance(extra);
        } else if (id == CreditApplyWebFragment.ID) {
            return CreditApplyWebFragment.newInstance(extra);
        } else if (id == CheckRealFourFragment.ID) {
            return CheckRealFourFragment.newInstance(extra);
        }
        return null;
    }

    public static BaseFragment obtainByName(String simpleName, Bundle extra) {
        if (CheckCertNoFragment.class.getSimpleName().equals(simpleName)) {
            return CheckCertNoFragment.newInstance(extra);
        } else if (ResetPasswordForSms.class.getSimpleName().equals(simpleName)) {
            return ResetPasswordForSms.newInstance(extra);
        } else if (CheckRealFourFragment.class.getSimpleName().equals(simpleName)) {
            return CheckRealFourFragment.newInstance(extra);
        } else if (CheckPhoneFragment.class.getSimpleName().equals(simpleName)) {
            return CheckPhoneFragment.newInstance(extra);
        } else if (CheckRealThreeFragment.class.getSimpleName().equals(simpleName)) {
            return CheckRealThreeFragment.newInstance(extra);
        } else if (MessageListFragment.class.getSimpleName().equals(simpleName)) {
            return MessageListFragment.newInstance(extra);
        }
        return null;
    }
}

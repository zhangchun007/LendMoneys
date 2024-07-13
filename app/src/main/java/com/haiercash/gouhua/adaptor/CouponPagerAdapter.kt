package com.haiercash.gouhua.adaptor

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.haiercash.gouhua.fragments.mine.DiscountCouponFragment
import com.haiercash.gouhua.fragments.mine.InterestFreeStampsFragment

class CouponPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    companion object {
        const val DISCOUNT_COUPON = 0
        const val INTEREST_FREE = 1
        const val NUM_PAGE = 2
    }

    override fun getItemCount(): Int {
        return NUM_PAGE
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            DISCOUNT_COUPON -> {
                DiscountCouponFragment()
            }

            INTEREST_FREE -> {
                InterestFreeStampsFragment()
            }

            else -> {
                DiscountCouponFragment()
            }
        }
    }
}
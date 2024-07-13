package com.haiercash.gouhua.activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.widget.CompoundButton
import androidx.viewbinding.ViewBinding
import androidx.viewpager2.widget.ViewPager2
import com.alibaba.android.arouter.facade.annotation.Route
import com.haiercash.gouhua.R
import com.haiercash.gouhua.adaptor.CouponPagerAdapter
import com.haiercash.gouhua.base.BaseActivity
import com.haiercash.gouhua.databinding.ActivityCouponBagV2Binding
import com.haiercash.gouhua.tplibrary.PagePath

/**
 * @author yaozijian@haiercash.com.cn
 * @since 2023/08/30
 * @description 券包V2
 */
@Route(path = PagePath.ACTIVITY_COUPON_BAG_V2)
class CouponBagActivityV2 : BaseActivity(), CompoundButton.OnCheckedChangeListener {
    companion object {
        const val TAB_COUNT = 2
    }
    private lateinit var binding: ActivityCouponBagV2Binding
    override fun initBinding(inflater: LayoutInflater): ViewBinding {
        binding = ActivityCouponBagV2Binding.inflate(inflater)
        return binding
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        //Activity已存活且复用才走这里，即scheme跳转且该activity已在前台展示
        if (intent != null && intent.hasExtra("tab")) {
            var tab: String? = ""
            if (getIntent() != null && getIntent().data != null) {
                tab = getIntent().data!!.getQueryParameter("tab")
            }
            val indexScheme =
                if (TextUtils.isEmpty(tab)) "0" else tab!! //scheme跳转携带tab=1为展示免息券tab，其他默认
            val index = if (indexScheme == "1") 1 else 0
            initTabSelect(index)
        }
    }

    private fun initTabSelect(index: Int) {
        if (index < TAB_COUNT) {
            binding.vpCouponTab.currentItem = index

            when (index) {
                0 -> {
                    binding.rbType1.isChecked = true
                    setTabTextSize(R.id.rb_type1)
                }

                1 -> {
                    binding.rbType2.isChecked = true
                    setTabTextSize(R.id.rb_type2)
                }


            }
        }
    }

    private fun setTabTextSize(id: Int) {
        if (id == R.id.rb_type1) {
            binding.rbType1.textSize = 16f
            binding.rbType2.textSize = 15f
        } else if (id == R.id.rb_type2) {
            binding.rbType1.textSize = 15f
            binding.rbType2.textSize = 16f
        }
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        setTitle("券包")
        binding.rbType1.text = "优惠券"
        binding.rbType2.text = "免息券"
        binding.rbType1.setOnCheckedChangeListener(this)
        binding.rbType2.setOnCheckedChangeListener(this)
        var tab: String? = ""
        if (intent != null && intent.data != null) {
            tab = intent.data!!.getQueryParameter("tab")
        }
        val indexScheme = if (TextUtils.isEmpty(tab)) "0" else tab!! //scheme跳转携带tab=1为展示免息券tab，其他默认

        val index = if (indexScheme == "1") 1 else intent.getIntExtra("couponType", 0)
        initTabSelect(index)

        val couponPagerAdapter = CouponPagerAdapter(this)

        binding.vpCouponTab.adapter = couponPagerAdapter

        binding.vpCouponTab.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                initTabSelect(position)
            }
        })
    }

    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
        if (isChecked) {
            if (buttonView!!.id == R.id.rb_type1) {
                setTabTextSize(R.id.rb_type1)
                binding.vpCouponTab.currentItem = 0
            } else if (buttonView.id == R.id.rb_type2) {
                setTabTextSize(R.id.rb_type2)
                binding.vpCouponTab.currentItem = 1
            }
        }
    }
}
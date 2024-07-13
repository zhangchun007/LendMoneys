package com.haiercash.gouhua.adaptor.loan


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.app.haiercash.base.utils.system.FontCustom
import com.haiercash.gouhua.R
import com.haiercash.gouhua.adaptor.bean.LoanMarketBean
import com.haiercash.gouhua.base.adapter.BaseAdapter
import com.haiercash.gouhua.base.adapter.ViewHolder
import com.haiercash.gouhua.utils.GlideUtils
import java.util.ArrayList

class LoanMarketAdapter(val list: ArrayList<LoanMarketBean>?) :
    BaseAdapter<LoanMarketBean, LoanMarketAdapter.LoanMarketHolder>(R.layout.item_loan_market_container,list) {


    override fun convert(holder: LoanMarketHolder, item: LoanMarketBean) {
        holder.mTvTitle.text = item.organizationName
        holder.mTvTotal.text = item.creditAmount
        holder.mTvTotal.typeface = FontCustom.getDinFont(context)
        holder.mTvInterest.text = item.yearInterest
        GlideUtils.loadFit(context, holder.mIvLogo, item.organizationIcon)
    }

    override fun setNewData(data: MutableList<LoanMarketBean>?) {
        super.setNewData(data)
    }

    class LoanMarketHolder(v: View) : ViewHolder(v) {
        var mIvLogo: ImageView
        var mTvTitle: TextView
        var mTvTotal: TextView
        var mTvInterest: TextView
        private var mTvApply: TextView

        init {
            mIvLogo = v.findViewById(R.id.iv_logo)
            mTvTitle = v.findViewById(R.id.tv_title)
            mTvTotal = v.findViewById(R.id.tv_total)
            mTvApply = v.findViewById(R.id.tv_apply)
            mTvInterest = v.findViewById(R.id.tv_interest)
        }
    }


}
package com.haiercash.adapter


import android.view.View

import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.haiercash.gouhua.R
import com.haiercash.gouhua.base.adapter.ViewHolder
import com.haiercash.gouhua.beans.homepage.ConfigData
import com.haiercash.gouhua.utils.GlideUtils


class HomeGuardNewsAdapter(val list: MutableList<ConfigData>) :
    BaseQuickAdapter<ConfigData, HomeGuardNewsAdapter.HomeGuardNewsHolder>(
        R.layout.item_home_guard, list
    ) {

    override fun convert(holder: HomeGuardNewsHolder, item: ConfigData) {
        holder.mTvTitle.text = item.pushTitle
        holder.mTvContent.text = item.pushSubTitle
        GlideUtils.loadFitRadius(
            context,
            holder.mIvSource,
            item.imgUrl,
            R.drawable.img_xb_new_default_bg,
            GlideUtils.ALL,
            4
        )
    }

    class HomeGuardNewsHolder(v: View) : ViewHolder(v) {
        val mIvSource: ImageView
        val mTvTitle: TextView
        val mTvContent: TextView

        init {
            mIvSource = v.findViewById(R.id.img_source)
            mTvTitle = v.findViewById(R.id.tvTitle)
            mTvContent = v.findViewById(R.id.tvContent)
        }
    }
}
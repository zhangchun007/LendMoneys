package com.haiercash.adapter


import android.view.View

import android.widget.ImageView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.haiercash.gouhua.R
import com.haiercash.gouhua.base.adapter.ViewHolder
import com.haiercash.gouhua.beans.homepage.ConfigData
import com.haiercash.gouhua.utils.GlideUtils


class HomeGuardHeadAdapter(val list: MutableList<ConfigData>) :
    BaseQuickAdapter<ConfigData, HomeGuardHeadAdapter.HomeGuardHeadHolder>(
        R.layout.item_home_guard_header, list
    ) {

    override fun convert(holder: HomeGuardHeadHolder, item: ConfigData) {
        GlideUtils.loadFitRadius(
            context,
            holder.mIvSource,
            item.imgUrl,
            R.drawable.img_xb_top_default_bg,
            GlideUtils.ALL,
            8
        )
    }

    class HomeGuardHeadHolder(v: View) : ViewHolder(v) {
        var mIvSource: ImageView

        init {
            mIvSource = v.findViewById(R.id.img_source)
        }
    }
}
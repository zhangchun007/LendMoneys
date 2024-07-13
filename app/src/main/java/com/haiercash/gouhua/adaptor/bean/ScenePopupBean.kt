package com.haiercash.gouhua.adaptor.bean

data class ScenePopupBean(
    val sceneType:String,
    val popupTitle:String,
    val popupTitleDesc:String,
    val h5Url:String,
    val price:String,
    val isCloseUmengBridging:String,
    val appUserAgent:String,
    val orderType:String,
    val goodsNameList:List<String>,
)

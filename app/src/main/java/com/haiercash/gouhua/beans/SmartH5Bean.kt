package com.haiercash.gouhua.beans

data class SmartH5Bean(
    val banner: Banner,
    val bubble: Bubble,
    val credit: Credit,
    val `dynamic`: Dynamic,
    val general: General,
    val h5ApiHost: String,
    val h5GoudziApiHost: String,
    val h5PmApiHost: String,
    val imgSourceApi: String,
    val notice: Notice,
    val reviewVersion: String
)

data class Banner(
    val `data`: List<Data>,
    val isShow: String,
    val title: String
)

data class Bubble(
    val isShow: String,
    val title: String
)

data class Credit(
    val applyAmount: String,
    val clientGroup: String,
    val creditTitleForScene: String,
    val loanJumpUrl: String,
    val nodeJumpUrl: String,
    val sceneCreditChannelNo: String,
    val sceneLoanChannelNo: String
)

data class Dynamic(
    val `data`: List<DataX>,
    val forwardUrl: String,
    val isShow: String,
    val recode: List<Recode>,
    val title: String,
    val type: String
)

data class General(
    val `data`: List<Data>,
    val isShow: String,
    val title: String
)

data class Notice(
    val `data`: List<DataXXX>,
    val isShow: String,
    val title: String
)

data class Data(
    val cid: String,
    val code: String,
    val forwardUrl: String,
    val groupId: String,
    val imgUrl: String,
    val name: String,
    val picName: String
)

data class DataX(
    val forwardUrl: String,
    val imgUrl: String,
    val pushSubTitle: String,
    val pushTitle: String
)

data class Recode(
    val forwardUrl: String,
    val imgUrl: String,
    val pushSubTitle: String,
    val pushTitle: String
)

data class DataXXX(
    val appType: String,
    val callbackStatus: Boolean,
    val content: String,
    val createTime: Long,
    val id: String,
    val inmailType: String,
    val institution: String,
    val pushContentType: String,
    val pushSubTitle: String,
    val pushTitle: String,
    val readStatus: String,
    val `receiver`: String,
    val recordId: String,
    val sendChannel: String,
    val status: Boolean,
    val tmplId: String,
    val tmplTitle: String,
    val updateTime: Long
)
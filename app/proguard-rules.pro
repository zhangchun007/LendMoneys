# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in D:\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
#指定代码的压缩级别
-optimizationpasses 5
#包明不混合大小写
-dontusemixedcaseclassnames
#不去忽略非公共的库类
-dontskipnonpubliclibraryclasses
##指定不去忽略包可见的库类的成员
-dontskipnonpubliclibraryclassmembers
 #优化  不优化输入的类文件
-dontoptimize
 #预校验
-dontpreverify
 #混淆时是否记录日志
-verbose
 # 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
#保护注解
-keepattributes *Annotation*
# 保持哪些类不被混淆
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService
#-keep public class com.haiercash.gouhua.base.AppUntil

##记录生成的日志数据,gradle build时在本项目根目录输出##
#apk 包内所有 class 的内部结构
-dump proguard/class_files.txt
#未混淆的类和成员
-printseeds proguard/seeds.txt
#列出从 apk 中删除的代码
-printusage proguard/unused.txt
#混淆前后的映射
-printmapping proguard/mapping.txt
########记录生成的日志数据，gradle build时 在本项目根目录输出-end######

####混淆保护自己项目的部分代码以及引用的第三方jar包library-end####

#保持 native 方法不被混淆
-keepclasseswithmembernames class * {
    native <methods>;
}

#保持自定义控件类不被混淆
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

#保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}
-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);     # 保持自定义控件类不被混淆
}

#保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

#保持枚举 enum 类不被混淆
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}

-keepclassmembers class * {
    public void *ButtonClicked(android.view.View);
}

#不混淆资源类
-keepclassmembers class **.R$* {
    public static <fields>;
}

#######################     常用第三方模块的混淆选项         ###################################

#okhttp
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.** { *;}
-dontwarn okio.**
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

#百度地图
-keep class com.baidu.** {*;}
-keep class vi.com.** {*;}
-dontwarn com.baidu.**

#百融
-dontwarn com.bairong.mobile.**
-keep class com.bairong.mobile.**
-keep class com.bairong.mobile.** { *; }

#pdf
-dontwarn com.shockwave.**
-keep class com.shockwave.**

#picasso
-dontwarn com.squareup.okhttp.**

#bugly
-dontwarn com.tencent.bugly.**
-keep public class com.tencent.bugly.**{*;}

#butterknife
-dontwarn butterknife.internal.**
-keep class butterknife.** { *; }
-dontwarn butterknife.internal.**
-keep class *$$ViewBinder { *; }
-keepclasseswithmembernames class * { @butterknife. <fields>;}
-keepclasseswithmembernames class * { @butterknife. <methods>;}

#gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.** { *; }
-keep class com.haiercash.gouhua.beans.** { *; }
-keep class com.haiercash.gouhua.adaptor.** { *; }
-keep class com.haiercash.gouhua.citylist.** { *; }


#GrowingIO
-keep class com.growingio.android.sdk.** {
 *;
}
-dontwarn com.growingio.android.sdk.**
-keepnames class * extends android.view.View

-keep class * extends android.app.Fragment {
 public void setUserVisibleHint(boolean);
 public void onHiddenChanged(boolean);
 public void onResume();
 public void onPause();
}

#TalkingDataSDK
-dontwarn com.tendcloud.tenddata.**
-keep class com.tendcloud.** {*;}
-keep public class com.tendcloud.tenddata.** { public protected *;}
-keepclassmembers class com.tendcloud.tenddata.**{
public void *(***);
}
-keep class com.talkingdata.sdk.TalkingDataSDK {public *;}
-keep class com.apptalkingdata.** {*;}
-keep class dice.** {*; }
-dontwarn dice.**

#信鸽
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep class com.tencent.android.tpush.** {*;}
-keep class com.tencent.tpns.baseapi.** {*;}
-keep class com.tencent.tpns.mqttchannel.** {*;}
-keep class com.tencent.tpns.dataacquisition.** {*;}
#华为信鸽推送
-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable
-keep class com.hianalytics.android.**{*;}
-keep class com.huawei.updatesdk.**{*;}
-keep class com.huawei.hms.**{*;}
-keep class com.huawei.android.hms.agent.**{*;}
#小米信鸽推送
-keep class com.xiaomi.**{*;}
-keep public class * extends com.xiaomi.mipush.sdk.PushMessageReceiver
#魅族信鸽推送
-dontwarn com.meizu.cloud.pushsdk.**
-keep class com.meizu.cloud.pushsdk.**{*;}

#人脸识别
-keep class com.gzt.faceid5sdk.DetectionAuthentic {
    static <fields>;
    <methods>;
}
-keep class com.gzt.faceid5sdk.activity.IdcardCaptorActivity {
    static <fields>;
    <methods>;
}
-keep class com.gzt.faceid5sdk.activity.ImageCaptureActivity {
    static <fields>;
    <methods>;
}
-keep class com.gzt.faceid5sdk.listener.* {
    <methods>;
}
# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}
# BaseQuickAdapter
#-keep class com.chad.library.adapter.** {
#*;
#}
-keep public class * extends com.chad.library.adapter.base.BaseQuickAdapter
-keep public class * extends com.chad.library.adapter.base.viewholder.BaseViewHolder
-keepclassmembers class * extends com.chad.library.adapter.base.viewholder.BaseViewHolder { <init>(...); }
-keepclassmembers class * extends com.chad.library.adapter.base.BaseQuickAdapter { <init>(...); }



-keep class com.google.gson.** { *; }
-keep class com.app.haiercash.base.bean.** { *; }
-keep class com.app.haiercash.base.net.bean.** { *; }

-dontwarn javax.annotation.**
-dontwarn javax.inject.**


# Retrofit
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }


#----------- rxjava rxandroid----------------
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-dontnote rx.internal.util.PlatformDependent

# RxLifeCycle
-keep class com.trello.rxlifecycle2.** { *; }
-keep interface com.trello.rxlifecycle2.** { *; }

#腾讯X5
-dontwarn com.tencent.smtt.**
-keep public class com.tencent.smtt.**{*;}

-keepattributes Signature,*Annotation*


#face 人脸
-keep public class com.megvii.**{*;}

#微信支付、分享
-keep class com.tencent.mm.opensdk.** {
    *;
}

-keep class com.tencent.wxop.** {
    *;
}

-keep class com.tencent.mm.sdk.** {
    *;
}
#greendao3.2.0,此是针对3.2.0，如果是之前的，可能需要更换下包名
-keepclassmembers class * extends org.greenrobot.greendao.AbstractDao {
public static java.lang.String TABLENAME;
}
-keep class **$Properties {*;}

# If you do not use SQLCipher:
-dontwarn net.sqlcipher.database.**
# If you do not use RxJava:
-dontwarn rx.**

###---------阿里路由ARouter----------------------------
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}
# 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider
# 如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
-keep class * implements com.alibaba.android.arouter.facade.template.IProvider

###---------AndroidX----------------------------
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**


-keep class com.appsafekb.safekeyboard.**{*;}
-keep class com.appsafekb.crypto.** {*; }

-keep class com.umeng.** {*;}
-keep class org.repackage.** {*;}

#JsBridge
-keep class com.github.lzyzsd.** {*;}


-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep public class [com.haiercash.gouhua].R$*{
public static final int *;

#旷视

}
#旷视
-keep class com.haiercash.gouhua.tplibrary.** {*;}

#爱加密
-keep class com.appsafekb.safekeyboard.**{*;}
-keep class com.appsafekb.crypto.** {*; }








#一键登录
#noinspection ShrinkerUnresolvedReference
#noinspection ShrinkerInvalidFlags
# --------------------------------------------基本指令区--------------------------------------------#
-optimizationpasses 5                               # 指定代码的压缩级别(在0~7之间，默认为5)
-dontusemixedcaseclassnames                         # 是否使用大小写混合(windows大小写不敏感，建议加入)
-dontskipnonpubliclibraryclasses                    # 是否混淆非公共的库的类
-dontskipnonpubliclibraryclassmembers               # 是否混淆非公共的库的类的成员
-dontpreverify                                      # 混淆时是否做预校验(Android不需要预校验，去掉可以加快混淆速度)
-verbose                                            # 混淆时是否记录日志(混淆后会生成映射文件)

# 混淆时所采用的算法(谷歌推荐算法)
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable

## 不压缩优化代码
#-dontshrink
## 不优化输入的类文件
#-dontoptimize
# 保持注解不被混淆
-keepattributes *Annotation*
# 保持泛型不被混淆
-keepattributes Signature
# 保持反射不被混淆
-keepattributes EnclosingMethod
# 保持异常不被混淆
-keepattributes Exceptions
# 保持内部类不被混淆
-keepattributes InnerClasses
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
# JSBridge 注解不被混淆
-keepattributes JavascriptInterface
#方法参数不被混淆(无法生效)
#-keepattributes MethodParameters

# 将文件来源重命名为"SourceFile"字符串
-renamesourcefileattribute SourceFile

## 不混淆jar包
#-libraryjars libs(*.jar)

## dump.txt文件列出apk包内所有class的内部结构
#-dump class_files.txt
## seeds.txt文件列出未混淆的类和成员
#-printseeds seeds.txt
## usage.txt文件列出从apk中删除的代码
#-printusage unused.txt
## mapping.txt文件列出混淆前后的映射
#-printmapping mapping.txt

-keep class com.geetest.onelogin.OneLoginHelper{*;}
-keep class com.geetest.onelogin.listener.AbstractOneLoginListener{*;}
-keep class com.geetest.onelogin.listener.SecurityPhoneListener{*;}

-keep class com.geetest.onepassv2.OnePassHelper{*;}
-keep class com.geetest.onepassv2.listener.OnePassListener{*;}

-keep class com.geetest.common.support.AnyThread*{*;}
-keep class com.geetest.common.support.ContextCompat*{*;}
-keep class com.geetest.common.support.IntRange*{*;}
-keep class com.geetest.common.support.NonNull*{*;}
-keep class com.geetest.common.support.Nullable*{*;}

-keep class com.geetest.gateauth.**{*;}

-dontwarn com.geetest.deepknow.**
-keep class com.geetest.deepknow.** {
*;
}
-dontwarn com.geetest.mobinfo.**
-keep class com.geetest.mobinfo.** {
*;
}
-dontwarn com.geetest.onelogin.**
-keep class com.geetest.onelogin.** {
*;
}
-dontwarn com.geetest.onepassv2.**
-keep class com.geetest.onepassv2.** {
*;
}
-dontwarn com.geetest.onepassview.**
-keep class com.geetest.onepassview.** {
*;
}
-dontwarn com.geetest.sdk.**
-keep class com.geetest.sdk.**{
*;
}
-dontwarn com.tencent.**
-keep class com.tencent.**{
*;
}

-dontwarn com.cmic.sso.sdk.**
-keep class com.cmic.sso.sdk.** {
*;
}
-dontwarn com.unigeetest.**
-keep class com.unigeetest.** {
*;
}
-dontwarn cn.com.chinatelecom.account.**
-keep class cn.com.chinatelecom.account.** {
*;
}

# ProGuard configurationsfor NetworkBench Lens
-keep class com.networkbench.** { *; }
-dontwarn com.networkbench.**
-keepattributes Exceptions, Signature, InnerClasses


#网易设备指纹
 -keep class com.netease.mobsec.xs.**{*;}

#阿里支付
-keep class com.alipay.android.app.IAlixPay{*;}
-keep class com.alipay.android.app.IAlixPay$Stub{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback{*;}
-keep class com.alipay.android.app.IRemoteServiceCallback$Stub{*;}
-keep class com.alipay.sdk.app.PayTask{ public *;}
-keep class com.alipay.sdk.app.AuthTask{ public *;}
-keep class com.alipay.sdk.app.H5PayCallback {
    <fields>;
    <methods>;
}
-keep class com.alipay.android.phone.mrpc.core.** { *; }
-keep class com.alipay.apmobilesecuritysdk.** { *; }
-keep class com.alipay.mobile.framework.service.annotation.** { *; }
-keep class com.alipay.mobilesecuritysdk.face.** { *; }
-keep class com.alipay.tscenter.biz.rpc.** { *; }
-keep class org.json.alipay.** { *; }
-keep class com.alipay.tscenter.** { *; }
-keep class com.ta.utdid2.** { *;}
-keep class com.ut.device.** { *;}

# 听云
-keep class com.networkbench.** { *; }
-dontwarn com.networkbench.**
-keepattributes Exceptions, Signature, InnerClasses
-keepattributes SourceFile,LineNumberTable
# End NetworkBench Lens

#oppo推送
-keep public class * extends android.app.Service
-keep class com.heytap.mcssdk.** {*;}
-keep class com.heytap.msp.push.** { *;}

#vivo推送
-dontwarn com.vivo.push.**
-keep class com.vivo.push.**{*; }
-keep class com.vivo.vms.**{*; }
-keep class com.tencent.android.vivopush.VivoPushMessageReceiver{*;}

#百度人脸
-dontwarn com.baidu.idl.**
-keep class com.baidu.idl.** { *; }
-dontwarn com.baidu.vis.**
-keep class com.baidu.vis.** { *; }
-dontwarn com.baidu.liantian.**
-keep class com.baidu.liantian.** { *; }
-dontwarn com.baidu.protect.**
-keep class com.baidu.protect.** { *; }
-dontwarn com.baidu.ocr.**
-keep class com.baidu.ocr.** { *; }
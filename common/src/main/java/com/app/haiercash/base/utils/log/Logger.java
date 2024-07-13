package com.app.haiercash.base.utils.log;

import android.content.Context;
import android.os.Environment;

import com.app.haiercash.base.utils.system.SystemUtils;

public class Logger {
    public static boolean isNewDebugDir = false;

    public static String getLoggerFilePath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/haiercash/gouhua/";
    }

    /**
     * 初始化Log
     */
    public static void initLogUntil(Context context, boolean isDebug) {
        if (!SystemUtils.isAllow) {
            return;
        }
        LogUtils.Config config = LogUtils.getConfig(context)
                .setLogSwitch(isDebug)// 设置 log 总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(isDebug)// 设置是否输出到控制台开关，默认开
                .setGlobalTag("LogUtils")// 设置 log 全局标签，默认为空
                // 当全局标签不为空时，我们输出的 log 全部为该 tag，
                // 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
                .setLogHeadSwitch(true)// 设置 log 头信息开关，默认为开
                .setLog2FileSwitch(isDebug)// 打印 log 时是否存到文件的开关，默认关
                .setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setFilePrefix("")// 当文件前缀为空时，默认为"util"，即写入文件为"util-MM-dd.txt"
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                //.setEncryption(true)//输出日志是否加密
                .setConsoleFilter(LogUtils.V)// log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
                .setFileFilter(LogUtils.V)// log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
                .setStackDeep(1);// log 栈深度，默认为 1
//        initFileLog(context);
        LogUtils.d(config.toString());
    }


    public static void setStackDeep(Context context, int stackDeep) {
        if (!SystemUtils.isAllow) {
            return;
        }
        LogUtils.getConfig(context).setStackDeep(stackDeep);
    }

    public static void initNewLogUntil(Context context, String filePrefix) {
        if (!SystemUtils.isAllow) {
            return;
        }
        boolean isDebug = true;
        LogUtils.Config config = LogUtils.getConfig(context)
                .setLogSwitch(isDebug)// 设置 log 总开关，包括输出到控制台和文件，默认开
                .setConsoleSwitch(isDebug)// 设置是否输出到控制台开关，默认开
                .setGlobalTag("LogUtils")// 设置 log 全局标签，默认为空
                // 当全局标签不为空时，我们输出的 log 全部为该 tag，
                // 为空时，如果传入的 tag 为空那就显示类名，否则显示 tag
                .setLogHeadSwitch(true)// 设置 log 头信息开关，默认为开
                .setLog2FileSwitch(isDebug)// 打印 log 时是否存到文件的开关，默认关
                //.setDir("")// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setDir(isNewDebugDir ? getLoggerFilePath() : "")// 当自定义路径为空时，写入应用的/cache/log/目录中
                .setFilePrefix(filePrefix)// 当文件前缀为空时，默认为"util"，即写入文件为"util-MM-dd.txt"
                .setBorderSwitch(true)// 输出日志是否带边框开关，默认开
                .setConsoleFilter(LogUtils.V)// log 的控制台过滤器，和 logcat 过滤器同理，默认 Verbose
                .setFileFilter(LogUtils.W)// log 文件过滤器，和 logcat 过滤器同理，默认 Verbose
                .setStackDeep(1);// log 栈深度，默认为 1
//        initFileLog(context);
        LogUtils.d(config.toString());
    }

    public static void e(Object... contents) {
        if (!SystemUtils.isAllow) {
            return;
        }
        LogUtils.e(contents);
    }

    public static void e(String tag, String msg) {
        if (!SystemUtils.isAllow) {
            return;
        }
        LogUtils.e(tag, msg);
    }

    public static void eTag(String tag, String msg) {
        if (!SystemUtils.isAllow) {
            return;
        }
        LogUtils.eTag(tag, msg);
    }

    public static void d(String msg) {
        if (!SystemUtils.isAllow) {
            return;
        }
        LogUtils.d(msg);
    }

    public static void d(String tag, String msg) {
        if (!SystemUtils.isAllow) {
            return;
        }
        LogUtils.d(tag, msg);
    }

    public static void i(String msg) {
        if (!SystemUtils.isAllow) {
            return;
        }
        LogUtils.i(msg);
    }

    public static void i(String tag, String msg) {
        if (!SystemUtils.isAllow) {
            return;
        }
        LogUtils.i(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (!SystemUtils.isAllow) {
            return;
        }
        LogUtils.v(tag, msg);
    }

    public static void w(String msg) {
        if (!SystemUtils.isAllow) {
            return;
        }
        LogUtils.w(msg);
    }

    public static void w(String tag, String msg) {
        if (!SystemUtils.isAllow) {
            return;
        }
        LogUtils.w(tag, msg);
    }

    public static void j(String tag, String msg) {
        if (!SystemUtils.isAllow) {
            return;
        }
        LogUtils.json(tag, msg);
    }

    public static void j(String msg) {
        if (!SystemUtils.isAllow) {
            return;
        }
        LogUtils.json(msg);
    }

    public static void file(Object object) {
        if (!SystemUtils.isAllow) {
            return;
        }
        LogUtils.file(object);
    }

    public static String getFilePath() {
        return LogUtils.FILE_PATH_NAME;
    }
}

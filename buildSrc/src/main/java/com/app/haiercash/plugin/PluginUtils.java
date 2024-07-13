package com.app.haiercash.plugin;


/**
 * *@Author:    Sun
 * *@Date  :    2019/6/18
 * *@FileName: PluginUtils
 * *@Description:
 */
public class PluginUtils {


    private final static String SUFFIX = ".class";

    /**
     * 添加后缀
     *
     * @param name
     * @return
     */
    public static String addSuffix(String name) {
        if (!name.endsWith(SUFFIX)) {
            name += SUFFIX;
        }
        return name;
    }

    /**
     * 去掉String中的后缀
     *
     * @param name
     * @return
     */
    public static String deleteSuffix(String name) {
        if (name.endsWith(SUFFIX)) {
            name = name.substring(0, name.length() - SUFFIX.length());
        }
        return name;
    }


    /**
     * 过滤无用的class
     *
     * @param name
     * @return
     */
    public static boolean unUsedClassFile(String name) {
        return (name.endsWith(".class") && !name.startsWith("R\\$")
                && !"R.class".equals(name) && !"BuildConfig.class".equals(name));
    }


    /**
     * 检查class文件是否需要处理
     *
     * @param
     * @return
     */
    static boolean checkClassFile(String name) {
        return HookConfig.getHookClass().get(name) != null;
    }


}

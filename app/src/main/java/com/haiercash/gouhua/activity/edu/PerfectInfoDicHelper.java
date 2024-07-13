package com.haiercash.gouhua.activity.edu;

import com.app.haiercash.base.net.api.INetResult;
import com.app.haiercash.base.net.bean.BasicResponse;
import com.haiercash.gouhua.base.ApiUrl;
import com.haiercash.gouhua.network.NetHelper;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 项目名称：信贷字典项
 * 项目作者：胡玉君
 * 创建日期：2016/9/20 13:51.
 * ----------------------------------------------------------------------------------------------------
 * 文件描述：
 * ----------------------------------------------------------------------------------------------------
 */
public class PerfectInfoDicHelper implements INetResult {
    /**
     * 居住地址
     */
    public static final int TAG_LIVE = 0x01;
    /**
     * 工作地址
     */
    public static final int TAG_WORK = 0x02;
    /**
     * 职务
     */
    public static final int TAG_DUTY = 0x03;
    /**
     * 婚姻状态
     */
    public static final int TAG_MARITALSTATUS = 0x04;
    /**
     * 关系
     */
    public static final int TAG_RELATION = 0x05;
    /**
     * 单位性质
     * COM_KIND
     */
    public static final int TAG_COMKIND = 0x06;
    /**
     * 借款用途
     **/
    public static final int TAG_PURPOSE = 0x07;

    /**
     * 学历
     **/
    public static final int TAG_EDU = 0x08;

    /**
     * 关系2
     **/
    public static final int TAG_RELATION2 = 0x09;

    private NetHelper netHelper;


    private PerfectInfoDicHelper() {
        POSITION = new LinkedHashMap<>();
        MAIL_ADDR = new LinkedHashMap<>();
        COM_KIND = new LinkedHashMap<>();
        POSITION_OPT = new LinkedHashMap<>();
        EDU_TYP = new LinkedHashMap<>();
        LOCAL_RESID = new LinkedHashMap<>();
        MARR_STS = new LinkedHashMap<>();
        CURR_SITUATION = new LinkedHashMap<>();
        RELATION = new LinkedHashMap<>();

    }

    private static final class SingleTonHolder {
        private static final PerfectInfoDicHelper instance = new PerfectInfoDicHelper();
    }

    public static PerfectInfoDicHelper getInstance() {
        return SingleTonHolder.instance;
    }

    private INetResult callBack;
    private Map<String, List<Map<String, String>>> datas;

    public void requestDics(INetResult callBack) {
        this.callBack = callBack;
        Map<String, String> map = new HashMap<>();
        netHelper = new NetHelper(this);
        netHelper.getService(ApiUrl.url_zidian, map);
    }

    @Override
    public void onSuccess(Object response, String flag) {

        datas = (Map<String, List<Map<String, String>>>) response;
        pullData("COM_KIND", COM_KIND);
        pullData("MARR_STS", MARR_STS);
        pullData("RELATION", RELATION);
        pullData("EDU_TYP", EDU_TYP);

        //联系人关系去掉本人
        RELATION.remove("07");
        //去掉字典项中的 "未知"
        MARR_STS.remove("60");

        if (callBack != null) {
            callBack.onSuccess(response, flag);
            callBack = null;
        }
    }

    private void pullData(String key, Map<String, String> value) {
        if (value == null) {
            value = new HashMap<>();
        }
        try {
            List<Map<String, String>> data = datas.get(key);
            for (Map<String, String> map : data) {
                value.put(map.get("code"), map.get("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onError(BasicResponse error, String url) {
        if (callBack != null) {
            callBack.onError(error, url);
            callBack = null;
        }
    }

    /**
     * 手动断开连接
     */
    protected void destory() {
        if (netHelper != null) {
            netHelper.recoveryNetHelper();
        }
        callBack = null;
    }

    /**
     * ----------------------------------------字典项存储------------------------------------------------
     */
    private Map<String, String> POSITION;
    private Map<String, String> MAIL_ADDR;
    private Map<String, String> COM_KIND;
    private Map<String, String> POSITION_OPT;
    private Map<String, String> EDU_TYP;
    private Map<String, String> LOCAL_RESID;
    private Map<String, String> MARR_STS;
    private Map<String, String> CURR_SITUATION;
    private Map<String, String> RELATION;

    public boolean isHasData(int selectTag) {
        return getSelectData(selectTag) != null;
    }

    public Map<String, String> getSelectData(int selectTag) {
        if (selectTag == TAG_MARITALSTATUS) {
            return PerfectInfoDicHelper.getInstance().getMARR_STS();
        } else if (selectTag == TAG_RELATION || selectTag == TAG_RELATION2) {
            return PerfectInfoDicHelper.getInstance().getRELATION();
        } else if (selectTag == TAG_COMKIND) {
            return PerfectInfoDicHelper.getInstance().getCOM_KIND();
        } else if (selectTag == TAG_EDU) {
            return PerfectInfoDicHelper.getInstance().getEDU_TYP();
        }
        return null;
    }

    public Map<String, String> getPOSITION() {
        if (POSITION == null || POSITION.size() <= 0) {
            return null;
        }
        return POSITION;
    }

    public Map<String, String> getMAIL_ADDR() {
        if (MAIL_ADDR == null || MAIL_ADDR.size() <= 0) {
            return null;
        }
        return MAIL_ADDR;
    }

    public Map<String, String> getCOM_KIND() {
        if (COM_KIND == null || COM_KIND.size() <= 0) {
            callBack = null;
            onSuccess(null, "");
        }
        return COM_KIND;
    }

    public Map<String, String> getPOSITION_OPT() {
        if (POSITION_OPT == null || POSITION_OPT.size() <= 0) {
            callBack = null;
            onSuccess(null, "");
        }
        return POSITION_OPT;
    }

    public Map<String, String> getEDU_TYP() {
        if (EDU_TYP == null || EDU_TYP.size() <= 0) {
            callBack = null;
            onSuccess(null, "");
        }
        return EDU_TYP;
    }

    public Map<String, String> getLOCAL_RESID() {
        if (LOCAL_RESID == null || LOCAL_RESID.size() <= 0) {
            callBack = null;
            onSuccess(null, "");
        }
        return LOCAL_RESID;
    }

    public Map<String, String> getMARR_STS() {
        if (MARR_STS == null || MARR_STS.size() <= 0) {
            return null;
        }
        return MARR_STS;
    }

    public Map<String, String> getCURR_SITUATION() {
        if (CURR_SITUATION == null || CURR_SITUATION.size() <= 0) {
            callBack = null;
            onSuccess(null, "");
        }
        return CURR_SITUATION;
    }

    public Map<String, String> getRELATION() {
        if (RELATION == null || RELATION.size() <= 0) {
            return null;
        }
        return RELATION;
    }

}

package com.haiercash.gouhua.beans.risk;

import java.io.Serializable;

public class CallLogBean implements Serializable {
    public String call_begin_time;       //通话起始时间	通话起始时间
    public String call_type;      //	呼叫类型	呼叫类型
    public String duration;       //	通话时长	通话时长
    public String to_phone_name;      //	通话人姓名	通话人姓名
    public String to_phone_no;        //	对方号码	对方号码
}

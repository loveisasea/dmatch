package com.fym.core.web.cmd;

import java.util.Map;

/**
 * Owned by Planck System
 * Created by fengy on 2016/2/4.
 */
public class GetListCmd {
    /**
     * 要过滤的键值对
     */
    public Map<String, Object> kvs;

    /**
     * 排序字段,例如" id asc "
     */
    public String ord;

    /**
     * 集合的开始位置
     */
    public int idx;

    /**
     * 集合的大小
     */
    public int size;
}

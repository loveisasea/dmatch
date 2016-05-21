package com.fym.playerlogin.obj;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fym.core.util.DateUtil;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by fengy on 2016/1/29.
 */

@JsonIgnoreProperties(value = {})
public class PlayerLoginS {
    /**
     * 玩家id
     */
    public Integer pid;

    /**
     * 登录票据
     */
    public String t;


    /**
     * 显示的用户姓名
     */
    public String nname;


    /**
     * 角色Id的哈希,默认4个基本足够
     */
    public Set<Integer> roleids = new HashSet<>(4);

    /**
     * 上次操作时间
     */
    public Date lastTouchTime;

    public PlayerLoginS() {
        this.t = UUID.randomUUID().toString();
        this.lastTouchTime = DateUtil.getCurrent();
    }


    public PlayerLoginS(Integer pid) {
        this.pid = pid;
        this.t = UUID.randomUUID().toString();
        this.lastTouchTime = DateUtil.getCurrent();
    }

    public PlayerLoginS(Integer pid, String t) {
        this.pid = pid;
        this.t = t;
        this.lastTouchTime = DateUtil.getCurrent();
    }


}

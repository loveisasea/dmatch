package com.fym.game.obj;

import com.fym.game.enm.ZeatID;
import com.fym.game.enm.ZeatStatus;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/20.
 */
public class Zeat {

    /**
     * 显示的帅，车的id等
     */
    public ZeatID sid;

    /**
     * 实际的帅，车的id等
     */
    public ZeatID rid;

    /**
     * 状态，0-未移动，1-正常，2-挂了
     */
    public ZeatStatus status;

    /**
     * 玩家id
     */
    public Integer pid;


    public Zeat(ZeatID rid) {
        this.rid = rid;
    }

    public Zeat copy() {
        Zeat ret = new Zeat(this.rid);
        ret.sid = this.sid;
        ret.pid = this.pid;
        ret.status = this.status;
        return ret;
    }
}
 

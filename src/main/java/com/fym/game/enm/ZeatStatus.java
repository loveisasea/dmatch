package com.fym.game.enm;
/**
 * Owned by Planck System
 * Created by fengy on 2016/5/27.
 * 棋子状态
 */

import com.fym.core.enm.Enm;
import com.fym.core.enm.obj.IEnm;

public class ZeatStatus extends IEnm {
    public final static String TypeName = "棋子状态";
    public final static ZeatStatus 未动 = new ZeatStatus(1, "未动");
    public final static ZeatStatus 正常 = new ZeatStatus(2, "正常");
    public final static ZeatStatus 挂了 = new ZeatStatus(3, "挂了");


    ZeatStatus(Integer key, String name) {
        super(key, name);
        Enm.add(this);
    }

} 

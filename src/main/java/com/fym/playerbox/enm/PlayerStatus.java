package com.fym.playerbox.enm;
/**
 * Owned by Planck System
 * Created by fengy on 2016/6/1.
 * 玩家状态
 */

import com.fym.core.enm.Enm;
import com.fym.core.enm.obj.IEnm;


public class PlayerStatus extends IEnm {
    public final static String TypeName = "玩家状态";
    public final static PlayerStatus 空闲 = new PlayerStatus(1, "空闲");
    public final static PlayerStatus 准备中 = new PlayerStatus(2, "准备中");
    public final static PlayerStatus 正常战斗中 = new PlayerStatus(3, "正常战斗中");
    public final static PlayerStatus 游戏中未连接 = new PlayerStatus(4, "游戏中未连接");
    public final static PlayerStatus 观看录像 = new PlayerStatus(5, "观看录像");

    PlayerStatus(Integer key, String name) {
        super(key, name);
        Enm.add(this);
    }

} 

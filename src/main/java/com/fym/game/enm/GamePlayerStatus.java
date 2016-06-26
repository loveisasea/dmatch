package com.fym.game.enm;
/**
 * Owned by Planck System
 * Created by fengy on 2016/6/6.
 * 玩家在游戏中的状态
 */

import com.fym.core.enm.Enm;
import com.fym.core.enm.obj.IEnm;

public class GamePlayerStatus extends IEnm {
    public final static String TypeName = "玩家在游戏中的状态";
    public final static GamePlayerStatus 空闲 = new GamePlayerStatus(1, "空闲");
    public final static GamePlayerStatus 匹配中 = new GamePlayerStatus(2, "匹配中");
    public final static GamePlayerStatus 准备中 = new GamePlayerStatus(3, "准备中");
    public final static GamePlayerStatus 正常战斗中 = new GamePlayerStatus(4, "正常战斗中");
    public final static GamePlayerStatus 已断开 = new GamePlayerStatus(5, "已断开");


    GamePlayerStatus(Integer key, String name) {
        super(key, name);
        Enm.add(this);
    }

} 

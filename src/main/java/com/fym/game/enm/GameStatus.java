package com.fym.game.enm;
/**
 * Owned by Planck System
 * Created by fengy on 2016/6/6.
 * 玩家在游戏中的状态
 */

import com.fym.core.enm.Enm;
import com.fym.core.enm.obj.IEnm;

public class GameStatus extends IEnm {
    public final static String TypeName = "游戏状态";
    public final static GameStatus 准备中 = new GameStatus(1, "准备中");
    public final static GameStatus 正常战斗中 = new GameStatus(2, "正常战斗中");
    public final static GameStatus 已结束 = new GameStatus(3, "已结束");


    GameStatus(Integer key, String name) {
        super(key, name);
        Enm.add(this);
    }


} 

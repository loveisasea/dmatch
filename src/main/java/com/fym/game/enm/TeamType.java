package com.fym.game.enm;
/**
 * Owned by Planck System
 * Created by fengy on 2016/5/27.
 * 游戏类型
 */

import com.fym.core.enm.Enm;
import com.fym.core.enm.obj.IEnm;

public class TeamType extends IEnm {
    public final static String TypeName = "红方黑方";
    public final static TeamType 红 = new TeamType(1, "红");
    public final static TeamType 黑 = new TeamType(2, "黑");


    TeamType(Integer key, String name) {
        super(key, name);
        Enm.add(this);
    }
} 

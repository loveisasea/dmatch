package com.fym.game.enm;

import com.fym.core.enm.Enm;
import com.fym.core.enm.obj.IEnm;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/20.
 */
public class GameType extends IEnm {
    public final static String TypeName = "游戏类型";
    public final static GameType _1v1_ladder = new GameType(1, "1v1天梯", 1);
    public final static GameType _1v1_normal = new GameType(2, "1v1普通", 1);
    public final static GameType _nvn_ladder = new GameType(3, "多人对战天梯", 3);
    public final static GameType _nvn_normal = new GameType(4, "多人对战普通", 3);

    public final int pcnt;

    GameType(Integer key, String name, int pcnt) {
        super(key, name);
        this.pcnt = pcnt;
        Enm.add(this);
    }


}

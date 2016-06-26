package com.fym.game.enm;
/**
 * Owned by Planck System
 * Created by fengy on 2016/5/27.
 * 棋盘
 */

import com.fym.core.enm.Enm;
import com.fym.core.enm.obj.IEnm;

public class BattleFieldType extends IEnm {
    public final static String TypeName = "棋盘";
    public final static BattleFieldType _1v1 = new BattleFieldType(1, "1v1", 9, 10);
    public final static BattleFieldType _2v2 = new BattleFieldType(2, "2v2", 18, 10);
    public final static BattleFieldType _3v3 = new BattleFieldType(3, "3v3", 27, 10);


    public final int width;
    public final int height;

    private BattleFieldType(Integer key, String name, int width, int height) {
        super(key, name);
        this.width = width;
        this.height = height;
        Enm.add(this);
    }
}

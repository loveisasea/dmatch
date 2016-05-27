package com.fym.game.obj;

import com.fym.game.enm.BattleFieldType;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/20.
 * 棋盘
 */
public class BattleField {
    public final BattleFieldType type;
    public final Zeat[][] pies;

    public BattleField(BattleFieldType type) {
        this.type = type;
        this.pies = new Zeat[this.type.width][this.type.height];
    }
}
 

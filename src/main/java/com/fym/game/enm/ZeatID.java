package com.fym.game.enm;
/**
 * Owned by Planck System
 * Created by fengy on 2016/5/27.
 * 棋子
 */

import com.fym.core.enm.Enm;
import com.fym.core.enm.obj.IEnm;

public class ZeatID extends IEnm {
    public final static String TypeName = "棋子";
    public final static ZeatID 红帅 = new ZeatID(1, "帅");
    public final static ZeatID 红車 = new ZeatID(2, "車");
    public final static ZeatID 红馬 = new ZeatID(3, "馬");
    public final static ZeatID 红炮 = new ZeatID(4, "炮");
    public final static ZeatID 红仕 = new ZeatID(5, "仕");
    public final static ZeatID 红相 = new ZeatID(6, "相");
    public final static ZeatID 红兵 = new ZeatID(7, "兵");
    public final static ZeatID 黑将 = new ZeatID(8, "将");
    public final static ZeatID 黑車 = new ZeatID(9, "車");
    public final static ZeatID 黑馬 = new ZeatID(10, "馬");
    public final static ZeatID 黑砲 = new ZeatID(11, "砲");
    public final static ZeatID 黑士 = new ZeatID(12, "士");
    public final static ZeatID 黑象 = new ZeatID(13, "象");
    public final static ZeatID 黑卒 = new ZeatID(14, "卒");

    ZeatID(Integer key, String name) {
        super(key, name);
        Enm.add(this);
    }

    public TeamType teamType() {
        return ((Integer) this.key >= (Integer) ZeatID.黑将.key) ? TeamType.黑 : TeamType.红;
    }
} 

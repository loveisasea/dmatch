package com.fym.game.enm;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/20.
 */
public enum ZeatID {
    红帅(1, "帅"),
    红車(2, "車"),
    红馬(3, "馬"),
    红炮(4, "炮"),
    红仕(5, "仕"),
    红相(6, "相"),
    红兵(7, "兵"),
    黑将(8, "将"),
    黑車(9, "車"),
    黑馬(10, "馬"),
    黑砲(11, "砲"),
    黑士(12, "士"),
    黑象(13, "象"),
    黑卒(14, "卒");

    public static String TypeName = "棋子";

    public Integer key;
    public String name;

    ZeatID(Integer key, String name) {
        this.key = key;
        this.name = name;
    }

    public TeamType teamType() {
        return (this.key < 黑将.key) ? TeamType.红 : TeamType.黑;
    }
}

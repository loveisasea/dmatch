package com.fym.game.enm;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/20.
 */
public enum BattleFieldType {
    _1v1(1, "1v1", 9, 10),
    _2v2(2, "2v2", 18, 10),
    _3v3(3, "3v3", 27, 10);

    public static String TypeName = "棋盘";

    public Integer key;
    public String name;
    public int x;
    public int y;

    BattleFieldType(Integer key, String name, int x, int y) {
        this.key = key;
        this.name = name;
        this.x = x;
        this.y = y;
    }
} 

package com.fym.game.enm;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/20.
 */
public enum GameType {
    _1v1_ladder(1, "1v1天梯"),
    _1v1_normal(1, "1v1普通"),
    _nvn_ladder(1, "多人对战天梯"),
    _nvn_normal(1, "多人对战普通");

    public static String TypeName = "游戏类型";

    public Integer key;
    public String name;
    GameType(Integer key, String name) {
        this.key = key;
        this.name = name;
    }
} 

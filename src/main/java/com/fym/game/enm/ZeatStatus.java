package com.fym.game.enm;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/20.
 */
public enum ZeatStatus {
    未动(1, "未动"),
    正常(2, "正常"),
    挂了(3, "挂了");

    public static String TypeName = "棋子状态";

    public Integer key;
    public String name;

    ZeatStatus(Integer key, String name) {
        this.key = key;
        this.name = name;
    }
}

package com.fym.game.enm;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/20.
 */
public enum TeamType {
    红(1, "红"),
    黑(1, "黑");

    public static String TypeName = "红方黑方";

    public Integer key;
    public String name;

    TeamType(Integer key, String name) {
        this.key = key;
        this.name = name;
    }
} 

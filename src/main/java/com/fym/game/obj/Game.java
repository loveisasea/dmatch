package com.fym.game.obj;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fym.game.enm.GameType;

import java.util.Date;
import java.util.List;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/19.
 */
public class Game {
    /**
     * game id
     */
    public int id;

    /**
     * 游戏类型
     */
    public GameType type;

    /**
     * 棋盘
     */
    public BattleField battleField;

    /**
     * 红方玩家
     */
    public List<Gplayer> redPlayers;

    /**
     * 黑方玩家
     */
    public List<Gplayer> blackPlayers;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date startDatetime;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date endDatetime;


}
 

package com.fym.game.obj;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fym.game.enm.GameType;
import com.fym.game.enm.TeamType;

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
    public String id;


    /**
     * 游戏类型
     */
    public GameType type;


    /**
     * 红黑玩家依次隔开
     */
    public List<Gplayer> gplayers;


    /**
     * 初始化棋盘
     */
    public BattleField initbattleField;

    /**
     * 棋盘
     */
    public BattleField currbattleField;

    /**
     * 走棋步骤
     */
    public List<GameStep> steps;


    /**
     * 开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date startDatetime;

    /**
     * 赢的那方
     */
    public TeamType winTeam;

    /**
     * 结束时间
     */
    public long duration;


}
 

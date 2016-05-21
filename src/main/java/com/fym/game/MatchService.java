package com.fym.game;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/19.
 */

public interface MatchService {

    /**
     * 参加游戏匹配
     *
     * @param gameType
     */
    void joinMatch(Integer gameType);


    /**
     * 退出匹配
     */
    void quitMatch();


} 

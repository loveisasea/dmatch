package com.fym.match;

import com.fym.core.err.OpException;
import com.fym.match.obj.Team;

import java.util.List;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/19.
 */

public interface MatchService {


    /**
     * 邀请玩家组队
     *
     * @param matepid
     */
    Team inviteTeam(Integer matepid) throws OpException;


    /**
     * 接受来自玩家的邀请
     *
     * @param leaderpid
     */
    Team acceptTeam(Integer leaderpid) throws OpException;


    /**
     * 拒绝来自玩家的邀请
     *
     * @param leaderpid
     */
    Team rejectTeam(Integer leaderpid) throws OpException;


    /**
     * 退出当前组队
     */
    Team quitTeam() throws OpException;


    /**
     * 参加游戏匹配
     *
     * @param gameTypeKeys
     */
    void startMatch(List<Integer> gameTypeKeys) throws OpException;



    /**
     * 退出匹配
     */
    void quitMatching() throws OpException;


    /**
     * 接受匹配
     */
    void acceptMatch(String matchuuid) throws OpException;


    /**
     * 拒绝匹配
     */
    void rejectMatch(String matchuuid) throws OpException;



}

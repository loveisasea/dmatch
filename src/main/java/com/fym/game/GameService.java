package com.fym.game;

import com.fym.core.err.OpException;
import com.fym.game.obj.Game;
import com.fym.game.obj.Gplayer;
import com.fym.match.obj.Match;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/24.
 */

public interface GameService {


    /**
     * 创建游戏
     *
     * @return
     */
    Game createGame(  Match match) throws OpException;


    /**
     * 走棋
     */
    void goStep(int srtX, int srtY, int endX, int endY) throws OpException;


    /**
     * 退出游戏
     */
    Gplayer quitGame() throws OpException;


    /**
     * 准备游戏
     *
     * @return
     */
    Gplayer gameReady() throws OpException;

    /**
     * 断开连接
     *
     * @return
     */
    Gplayer disconnect() throws OpException;


    /**
     * 重连
     *
     * @return
     */
    Gplayer reconnect() throws OpException;

}

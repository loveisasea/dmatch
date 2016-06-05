package com.fym.game;

import com.fym.core.err.OpException;
import com.fym.game.enm.GameType;
import com.fym.game.obj.Game;
import com.fym.game.obj.GameStep;
import com.fym.match.obj.Match;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/24.
 */

public interface GameService {


    /**
     * 创建游戏
     *
     * @param gameType
     * @return
     */
    Game createGame(GameType gameType, Match match) throws OpException;


    /**
     * 走棋
     *
     * @param step
     */
    void goStep(GameStep step) throws OpException;


    /**
     * 退出游戏
     */
    void quitGame();

} 

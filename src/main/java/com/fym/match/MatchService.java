package com.fym.match;

/**
 *
 * Created by fengy on 2016/5/19.
 */

public interface MatchService {

    /**
     * 参加游戏匹配
     *
     * @param gameTypeStr
     */
    void joinMatch(String gameTypeStr);


    /**
     * 退出匹配
     */
    void quitMatch();


//    /**
//     * 等待匹配
//     *
//     * @return
//     */
//    Game waitingMatch();
}

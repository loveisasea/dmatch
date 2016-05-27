package com.fym.player;

import com.fym.core.err.OpException;
import com.fym.player.obj.Player;

import java.util.List;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/17.
 * 玩家账号服务
 */

public interface PlayerService {


    /**
     * 玩家注册
     *
     * @param account  账号
     * @param password 密码
     * @param igW      是否忽略警告
     * @return
     */
    Player register(String account, String password, Boolean igW) throws OpException;


    /**
     * 获取玩家信息
     *
     * @param pid 玩家id
     * @param field    字段选择
     * @return
     */
    Player get(Integer pid, Integer field) throws OpException;


    /**
     * 获取玩家信息
     *
     * @param pids 玩家id列表
     * @param field     字段选择
     * @return
     */
    List<Player> getList(List<Integer> pids, Integer field);

    /**
     * 检查账户密码
     *
     * @param acct
     * @param password
     */
    Player getByPassword(String acct, String password) throws OpException;
}

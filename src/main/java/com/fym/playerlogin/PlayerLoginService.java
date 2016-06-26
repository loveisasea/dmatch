package com.fym.playerlogin;

import com.fym.core.err.OpException;
import com.fym.playerbox.obj.IMsg;
import com.fym.playerlogin.obj.PlayerLoginS;

import java.util.List;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/14.
 */

public interface PlayerLoginService {



    /**
     * 用户登录系统
     *
     * @param acct 账户名
     * @param password 加密密码
     * @return 登录信息
     */
    PlayerLoginS loginPassword(String acct, String password) throws OpException;



    /**
     * 退出登录系统
     */
    void logout();


    List<IMsg> takeMsg() throws OpException;

    /**
     * 获取当前用户登录
     *
     * @return 登录信息
     */
    PlayerLoginS getLogin() throws OpException;


} 

package com.fym.playerlogin;
/**
 * Owned by Planck System
 * Created by fengy on 2016/5/15.
 */

import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;
import com.fym.playerlogin.cmd.PlayerLoginPasswordCmd;
import com.fym.playerlogin.obj.PlayerLoginS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "player")
public class PlayerLoginController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerLoginController.class);


    @Autowired
    private PlayerLoginService playerLoginService;


    /**
     * 注册
     *
     * @param req 信息
     * @return 空
     * @throws OpException
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public Object login(@RequestBody PlayerLoginPasswordCmd req) throws OpException {

        PlayerLoginS playerLoginS = this.playerLoginService.loginPassword(req.acct, req.password);
        return new OpResult("成功登录", playerLoginS);
    }

    /**
     * 详情
     *
     * @return 空
     * @throws OpException
     */
    @RequestMapping(value = "/logout", method = RequestMethod.POST)
    @ResponseBody
    public Object logout() throws OpException {

        this.playerLoginService.logout();
        return new OpResult("成功退出登录");
    }


} 

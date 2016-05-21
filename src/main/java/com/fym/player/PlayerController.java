package com.fym.player;
/**
 * Owned by Planck System
 * Created by fengy on 2016/5/15.
 */

import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;
import com.fym.player.cmd.PlayerGetCmd;
import com.fym.player.cmd.PlayerGetListCmd;
import com.fym.player.cmd.PlayerRegisterCmd;
import com.fym.player.obj.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping(value = "player")
public class PlayerController {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerController.class);


    @Autowired
    private PlayerService playerService;


    /**
     * 注册
     *
     * @param req 信息
     * @return 空
     * @throws OpException
     */
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    @ResponseBody
    public Object register(@RequestBody PlayerRegisterCmd req) throws OpException {

        Player player = this.playerService.register(req.acct, req.password, req.igW);
        return new OpResult("成功注册玩家<" + player.playerbs.id + ">", player);
    }

    /**
     * 详情
     *
     * @param req 信息
     * @return 空
     * @throws OpException
     */
    @RequestMapping(value = "/get", method = RequestMethod.POST)
    @ResponseBody
    public Object get(@RequestBody PlayerGetCmd req) throws OpException {

        Player player = this.playerService.get(req.id, Integer.MAX_VALUE);
        return new OpResult("获取玩家详情", player);
    }

    /**
     * 列表
     *
     * @param req
     * @return 空
     * @throws OpException
     */
    @RequestMapping(value = "/getlist", method = RequestMethod.POST)
    @ResponseBody
    public Object getList(@RequestBody PlayerGetListCmd req) throws OpException {

        List<Player> players = this.playerService.getList(null, Integer.MAX_VALUE);
        return new OpResult("获取玩家列表",players);
    }



} 

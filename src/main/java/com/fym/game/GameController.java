package com.fym.game;
/**
 * Owned by Planck System
 * Created by fengy on 2016/5/24.
 */


import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;
import com.fym.game.cmd.GameGoStepCmd;
import com.fym.game.obj.Gplayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "game")
public class GameController {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameController.class);


    @Autowired
    private GameService gameService;


    /**
     * 准备好游戏
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/ready", method = RequestMethod.POST)
    @ResponseBody
    public Object ready() throws OpException {

        Gplayer gplayer = this.gameService.gameReady();
        return new OpResult("已准备好game", gplayer);
    }


    /**
     * 走棋
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/gostep", method = RequestMethod.POST)
    @ResponseBody
    public Object gostep(@RequestBody GameGoStepCmd req) throws OpException {

        this.gameService.goStep(req.srtX, req.srtY, req.endX, req.endY);
        return new OpResult("已走棋");
    }

    /**
     * 退出
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/quit", method = RequestMethod.POST)
    @ResponseBody
    public Object quit() throws OpException {

        this.gameService.quitGame();
        return new OpResult("已退出");
    }

    /**
     * 断开连接
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/disconnect", method = RequestMethod.POST)
    @ResponseBody
    public Object disconnect() throws OpException {

        this.gameService.disconnect();
        return new OpResult("已断开连接");
    }


    /**
     * 重新连接
     *
     * @return 列表
     * @throws OpException
     */
    @RequestMapping(value = "/reconnect", method = RequestMethod.POST)
    @ResponseBody
    public Object reconnect() throws OpException {

        this.gameService.reconnect();
        return new OpResult("已重新连接");
    }

//    @Autowired
//    private GameService gameService;


}

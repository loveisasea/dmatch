package com.fym.game;
/**
 * Owned by Planck System
 * Created by fengy on 2016/6/4.
 * 游戏系统
 */

import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;
import com.fym.game.engine.GameEngine;
import com.fym.game.enm.GamePlayerStatus;
import com.fym.game.enm.GameStatus;
import com.fym.game.obj.Game;
import com.fym.game.obj.GameStep;
import com.fym.game.obj.Gplayer;
import com.fym.match.obj.Match;
import com.fym.player.PlayerService;
import com.fym.playerbox.PlayerBoxCom;
import com.fym.playerbox.obj.IMsg;
import com.fym.playerbox.obj.MsgID;
import com.fym.playerlogin.PlayerLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Transactional(rollbackFor = Exception.class)
@Service("gameService")
public class GameServiceImpl implements GameService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameServiceImpl.class);


    // <uuid,Game>
    private Map<String, Game> map_uuid_game = new ConcurrentHashMap<>();

    private Map<Integer, Game> map_pid_game = new ConcurrentHashMap<>();

    @Autowired
    private PlayerLoginService playerLoginService;

    @Autowired
    private PlayerBoxCom playerBoxCom;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private GameEngine gameEngine;

    @Override
    public Game createGame(  Match match) throws OpException {
        Game game = this.gameEngine.genGame(match);
        game.id = UUID.randomUUID().toString();
        this.map_uuid_game.put(game.id, game);
        for (Gplayer gplayer : game.gplayers) {
            this.map_pid_game.put(gplayer.pid, game);
        }
        this.playerBoxCom.putMsg(game.gpids(), new IMsg((Integer) MsgID.游戏准备.key, game.gplayers));
        return game;
    }

    @Override
    public void goStep(int srtX, int srtY, int endX, int endY) throws OpException {
        Integer selfpid = this.playerLoginService.getLogin().pid;
        Game game = this.getGame(selfpid);
        Gplayer gplayer = this.getGplayer(game, selfpid);
        if (!gplayer.status.equals(GamePlayerStatus.正常战斗中)) {
            throw new OpException(OpResult.FAIL, "玩家不在战斗中的状态");
        }
        GameStep step = this.gameEngine.goStep(game, gplayer.pid, srtX, srtY, endX, endY);
        this.playerBoxCom.putMsg(game.gpids(), new IMsg((Integer) MsgID.玩家游戏走棋.key, step));
        if (game.winTeam != null) {
            this.playerBoxCom.putMsg(game.gpids(), new IMsg((Integer) MsgID.游戏结束.key, game.winTeam));
        }
    }

    @Override
    public Gplayer quitGame() throws OpException {
        Integer selfpid = this.playerLoginService.getLogin().pid;
        Game game = this.getGame(selfpid);
        Gplayer gplayer = this.getGplayer(game, selfpid);
        gplayer.status = GamePlayerStatus.空闲;
        this.playerBoxCom.putMsg(game.gpids(), new IMsg((Integer) MsgID.玩家游戏退出.key, gplayer.pid));
        return gplayer;
    }

    @Override
    public Gplayer gameReady() throws OpException {
        Integer selfpid = this.playerLoginService.getLogin().pid;
        Game game = this.getGame(selfpid);
        Gplayer gplayer = this.getGplayer(game, selfpid);
        gplayer.status = GamePlayerStatus.正常战斗中;
        this.playerBoxCom.putMsg(game.gpids(), new IMsg((Integer) MsgID.玩家游戏已准备.key, gplayer));
        //检查是否已全部准备好
        boolean allready = true;
        for (Gplayer gplayer1 : game.gplayers) {
            if (gplayer1.status.equals(GamePlayerStatus.准备中)) {
                allready = false;
                break;
            }
        }
        if (allready) {
            game.status = GameStatus.正常战斗中;
            this.playerBoxCom.putMsg(game.gpids(), new IMsg((Integer) MsgID.游戏开始.key, gplayer));
        }
        return gplayer;
    }

    @Override
    public Gplayer disconnect() throws OpException {
        Integer selfpid = this.playerLoginService.getLogin().pid;
        Game game = this.getGame(selfpid);
        Gplayer gplayer = this.getGplayer(game, selfpid);
        gplayer.status = GamePlayerStatus.已断开;
        this.playerBoxCom.putMsg(game.gpids(), new IMsg((Integer) MsgID.玩家游戏断开连接.key, gplayer.pid));
        return gplayer;
    }

    @Override
    public Gplayer reconnect() throws OpException {
        Integer selfpid = this.playerLoginService.getLogin().pid;
        Game game = this.getGame(selfpid);
        Gplayer gplayer = this.getGplayer(game, selfpid);
        gplayer.status = GamePlayerStatus.正常战斗中;
        this.playerBoxCom.putMsg(game.gpids(), new IMsg((Integer) MsgID.玩家游戏重新连接.key, selfpid));
        return gplayer;
    }


    private Game getGame(Integer pid) throws OpException {
        Game game = this.map_pid_game.get(pid);
        if (game == null) {
            throw new OpException(OpResult.FAIL, "用户<" + pid + ">不在游戏中");
        }
        return game;
    }

    private Gplayer getGplayer(Game game, Integer pid) throws OpException {
        for (Gplayer gplayer : game.gplayers) {
            if (gplayer.pid.equals(pid)) {
                return gplayer;
            }
        }
        throw new OpException(OpResult.FAIL, "找不到该玩家");
    }

}

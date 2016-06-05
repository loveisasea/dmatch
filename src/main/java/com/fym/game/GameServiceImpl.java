package com.fym.game;
/**
 * Owned by Planck System
 * Created by fengy on 2016/6/4.
 * 游戏系统
 */

import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;
import com.fym.game.engine.GameEngine;
import com.fym.game.enm.GameType;
import com.fym.game.obj.Game;
import com.fym.game.obj.GameStep;
import com.fym.match.obj.Match;
import com.fym.player.PlayerService;
import com.fym.playerbox.PlayerBoxCom;
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
    public Game createGame(GameType gameType, Match match) throws OpException {
        Game game = this.gameEngine.genGame(gameType, match);
        game.id = UUID.randomUUID().toString();
        this.map_uuid_game.put(game.id, game);
        return game;
    }

    @Override
    public void goStep(GameStep step) throws OpException {
        Integer pid = this.playerLoginService.getLogin().pid;
        Game game = this.map_pid_game.get(pid);
        if (game == null) {
            throw new OpException(OpResult.FAIL, "用户<" + pid + ">不在游戏中");
        }
        step.pid = pid;
        this.gameEngine.goStep(game, step);
    }

    @Override
    public void quitGame() {

    }
}

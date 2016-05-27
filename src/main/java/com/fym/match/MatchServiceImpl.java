package com.fym.match;
/**
 * Owned by Planck System
 * Created by fengy on 2016/5/24.
 */

import com.fym.game.obj.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(rollbackFor = Exception.class)
@Service("matchService")
public class MatchServiceImpl implements MatchService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchServiceImpl.class);



    public void joinMatch(String gameTypeStr) {
    }

    @Override
    public void quitMatch() {

    }

    @Override
    public Game waitingMatch() {
        return null;
    }


}

package com.fym.match.obj;

import com.fym.game.enm.GameType;

import java.util.ArrayList;
import java.util.List;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/25.
 */
public class Match {
    public GameType gameType;
    public List<IUnit> team1;
    public List<IUnit> team2;

    public Match(GameType gameType) {
        this.gameType = gameType;
        this.team1 = new ArrayList<>(gameType.pcnt);
        this.team2 = new ArrayList<>(gameType.pcnt);
    }
}
 

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

    public int team1size() {
        return this.teamsizeCore(this.team1);
    }

    public int team2size() {
        return this.teamsizeCore(this.team2);
    }


    private static int teamsizeCore(List<IUnit> team) {
        int size = 0;
        for (IUnit iUnit : team) {
            size = size + iUnit.getSize();
        }
        return size;
    }

    public int team1score() {
        return this.teamscoreCore(this.team1);
    }

    public int team2score() {
        return this.teamscoreCore(this.team2);
    }

    public int scoreDiff() {
        return (this.team1score() - this.team2score());
    }

    private static int teamscoreCore(List<IUnit> team) {
        int score = 0;
        for (IUnit iUnit : team) {
            score = score + iUnit.getSize();
        }
        return score;
    }


}
 

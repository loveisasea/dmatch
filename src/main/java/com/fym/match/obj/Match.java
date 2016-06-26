package com.fym.match.obj;

import com.fym.game.enm.GameType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/25.
 */
public class Match {
    public final GameType gameType;
    public final List<IUnit> team1;
    public final List<IUnit> team2;
    public final Set<Integer> accepts;

    public Match(GameType gameType) {
        this.gameType = gameType;
        this.team1 = new ArrayList<>();
        this.team2 = new ArrayList<>();
        this.accepts = new HashSet<>();
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


    /**
     * 获取所有玩家的pid
     *
     * @return
     */
    public List<Integer> allpids() {
        List<Integer> team1pids = this.team1pids();
        team1pids.addAll(team2pids());
        return team1pids;
    }

    public List<Integer> team1pids() {
        List<Integer> pids = new ArrayList<>();
        for (IUnit iUnit : this.team1) {
            if (iUnit instanceof Person) {
                pids.add(((Person) iUnit).pid);
            } else if (iUnit instanceof Group) {
                for (Person person : ((Group) iUnit).persons) {
                    pids.add(person.pid);
                }
            }
        }
        return pids;
    }

    public List<Integer> team2pids() {
        List<Integer> pids = new ArrayList<>();
        for (IUnit iUnit : this.team2) {
            if (iUnit instanceof Person) {
                pids.add(((Person) iUnit).pid);
            } else if (iUnit instanceof Group) {
                for (Person person : ((Group) iUnit).persons) {
                    pids.add(person.pid);
                }
            }
        }
        return pids;
    }

}
 

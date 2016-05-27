package com.fym.match;

import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;
import com.fym.game.enm.GameType;
import com.fym.match.obj.IUnit;
import com.fym.match.obj.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/25.
 */
@Component
public class MatchEngine implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchEngine.class);

    private Map<GameType, TreeMap<Integer, Queue<IUnit>>> matchPools = new HashMap<>();

    private final static int Max_Score_Diff = 10;

    private final static int Buffer_Match = 4;


    public Match tryMatch(final IUnit mUnit, Integer gameTypeKey) throws OpException {
        return this.tryMatch(mUnit, GameType.get(gameTypeKey));
    }

    public Match tryMatch(final IUnit mUnit, GameType gameType) throws OpException {
        TreeMap<Integer, Queue<IUnit>> matchpool = this.matchPools.get(gameType);
        if (matchpool == null) {
            throw new OpException(OpResult.INVALID, "找不到该比赛类型" + gameType);
        }
        if (mUnit == null || mUnit.getSize() == 0 || mUnit.getPersons().size() == 0) {
            throw new OpException(OpResult.FAIL, "匹配玩家或队伍不能为空");
        }
        if (gameType.pcnt < mUnit.getSize()) {
            throw new OpException(OpResult.FAIL, "队伍人数超过游戏类型人数限制");
        }

        int restCnt = gameType.pcnt * 2 - mUnit.getSize() + Buffer_Match; //需要挑出来匹配的玩家总数量
        List<IUnit> results = new ArrayList<>(); //初步挑出来匹配的玩家

        //同一分数匹配
        {
            Queue<IUnit> scoreLevel = matchpool.get(mUnit.getScore());
            if (scoreLevel != null) {
                //先查找同等分数的
                while (restCnt > 0) {
                    IUnit pickUnit = scoreLevel.poll();
                    if (pickUnit == null) {
                        break;
                    }
                    results.add(pickUnit);
                    restCnt = restCnt - pickUnit.getSize();
                }
            }
        }

        //上下分数匹配
        {
            Map.Entry<Integer, Queue<IUnit>> hEntry = matchpool.higherEntry(mUnit.getScore());
            Map.Entry<Integer, Queue<IUnit>> lEntry = matchpool.lowerEntry(mUnit.getScore());
            //每次循环各取高低一个单位
            while (restCnt > 0) {
                if (hEntry == null && lEntry == null) {
                    break;
                }
                //寻找分高的玩家
                while (hEntry.getValue().size() == 0) {
                    hEntry = matchpool.higherEntry(hEntry.getKey());
                    if (hEntry == null) {
                        break;
                    }
                    if (Math.abs(hEntry.getKey() - mUnit.getScore()) > Max_Score_Diff) {
                        hEntry = null;
                    }
                }
                if (hEntry != null) {
                    IUnit pickUnit = hEntry.getValue().poll();
                    if (pickUnit != null) {
                        results.add(pickUnit);
                        restCnt = restCnt - pickUnit.getSize();
                    }
                }
                //寻找分低的玩家
                while (lEntry.getValue().size() == 0) {
                    lEntry = matchpool.lowerEntry(lEntry.getKey());
                    if (lEntry == null) {
                        break;
                    }
                    if (Math.abs(lEntry.getKey() - mUnit.getScore()) > Max_Score_Diff) {
                        lEntry = null;
                    }
                }
                if (lEntry != null) {
                    IUnit pickUnit = lEntry.getValue().poll();
                    if (pickUnit != null) {
                        results.add(pickUnit);
                        restCnt = restCnt - pickUnit.getSize();
                    }
                }
            }
        }
        Match match = new Match(gameType);
        List<IUnit> toReturn = results;
        //有足够的玩家进行二次挑选
        if (restCnt <= 0) {
            Collections.sort(results, new Comparator<IUnit>() {
                //玩家选择顺序规则
                @Override
                public int compare(IUnit o1, IUnit o2) {
                    int absSize1 = Math.abs(mUnit.getSize() - o1.getSize());
                    int absSize2 = Math.abs(mUnit.getSize() - o2.getSize());
                    if (absSize1 == absSize2) {
                        return -1;
                    } else {
                        if (o1.getScore() < o2.getScore()) {
                            return -1;
                        } else if (o1.getScore() > o2.getScore()) {
                            return 1;
                        } else {
                            return 0;
                        }
                    }
                }
            });
            //开始进行二次挑选
            List<IUnit> results2 = new ArrayList<>(results);
            match.team1.add(mUnit);
            IUnit lastTeam1 = mUnit;
            IUnit lastTeam2 = null;
            while (true) {
                //挑选team2
                Iterator<IUnit> iter2 = results2.iterator();
                lastTeam2 = null;
                while (iter2.hasNext()) {
                    IUnit pickUnit = iter2.next();
                    if (pickUnit.getSize() == lastTeam1.getSize()) {
                        lastTeam2 = pickUnit;
                        match.team2.add(lastTeam2);
                        iter2.remove();
                        break;
                    }
                }
                //找不到team2，退出
                if (lastTeam2 == null) {
                    break;
                }
                //挑选team1
                Iterator<IUnit> iter1 = results2.iterator();
                lastTeam1 = null;
                while (iter1.hasNext()) {
                    IUnit pickUnit = iter1.next();
                    if (pickUnit.getSize() + match.team1.size() <= gameType.pcnt) {
                        lastTeam1 = pickUnit;
                        match.team1.add(lastTeam1);
                        iter1.remove();
                        break;
                    }
                }
                //找不到team1,退出
                if (lastTeam1 == null) {
                    break;
                }
            }
            if (match.team1.size() == gameType.pcnt && match.team2.size() == gameType.pcnt) {
                toReturn = results2;
            } else {
                toReturn.add(mUnit); //需要把当前玩家也加进去
                match = null;
            }
        } else {
            toReturn.add(mUnit); //需要把当前玩家也加进去
            match = null;
        }
        //还原
        Iterator<IUnit> iReturn = toReturn.iterator();
        while (iReturn.hasNext()) {
            IUnit picked = iReturn.next();
            Queue<IUnit> queue = matchpool.get(picked.getScore());
            if (queue == null) {
                queue = new PriorityQueue<>();
                matchpool.put(picked.getScore(), queue);
            }
            queue.add(picked);
        }
        return match;
    }

    public Map<GameType, Map<Integer, Queue<IUnit>>> getMatchPools() {
        Map ret = new HashMap();
        for (Map.Entry<GameType, TreeMap<Integer, Queue<IUnit>>> mapEntry : this.matchPools.entrySet()) {
            HashMap<Integer, Queue<IUnit>> retEntry = new HashMap();
            for (Map.Entry<Integer, Queue<IUnit>> entry : mapEntry.getValue().entrySet()) {
                retEntry.put(entry.getKey(), entry.getValue());
            }
            ret.put(mapEntry.getKey(), retEntry);
        }

        return ret;
    }

    public Map<Integer, Queue<IUnit>> getMatchPool(Integer gameTypeKey) throws OpException {
        TreeMap<Integer, Queue<IUnit>> matchpool = this.matchPools.get(GameType.get(gameTypeKey));
        if (matchpool == null) {
            throw new OpException(OpResult.INVALID, "找不到该比赛类型" + gameTypeKey);
        }
        HashMap<Integer, Queue<IUnit>> ret = new HashMap();
        for (Map.Entry<Integer, Queue<IUnit>> entry : matchpool.entrySet()) {
            ret.put(entry.getKey(), entry.getValue());
        }
        return ret;
    }

    public void cleanMatchPool(Integer gameTypeKey) throws OpException {
        TreeMap<Integer, Queue<IUnit>> matchpool = this.matchPools.get(GameType.get(gameTypeKey));
        if (matchpool == null) {
            throw new OpException(OpResult.INVALID, "找不到该比赛类型" + gameTypeKey);
        }
        matchpool.clear();
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        for (GameType gameType : GameType.getall().values()) {
            matchPools.put(gameType, new TreeMap<Integer, Queue<IUnit>>());
        }
    }


}
 

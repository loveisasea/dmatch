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
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * Created by fengy on 2016/5/25.
 */
@Component
public class MatchEngine implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchEngine.class);

    private Map<GameType, TreeMap<Integer, Queue<IUnit>>> matchPools = new HashMap<>();

    private final static int Max_Score_Diff = 100;

    private final static int Buffer_Match = 4;


    public synchronized  Match tryMatch(final IUnit mUnit, Integer gameTypeKey) throws OpException {
        return this.tryMatch(mUnit, GameType.get(gameTypeKey));
    }


    /**
     * 匹配函数
     * 思路：
     * 1. 先从参赛者分数震荡向高低两边取未匹配玩家和组队，需要符合最大分数差，而且选取数量带有一定buffer
     * 2. 把第一步选取出来的玩家按照规模大小接近和分值接近排序
     * 3. 从结果集中
     * @param mUnit
     * @param gameType
     * @return
     * @throws OpException
     */
    public synchronized Match tryMatch(final IUnit mUnit, GameType gameType) throws OpException {
        TreeMap<Integer, Queue<IUnit>> matchpool = this.matchPools.get(gameType);
        if (matchpool == null) {
            throw new OpException(OpResult.INVALID, "找不到该比赛类型" + gameType);
        }
        if (mUnit == null || mUnit.getSize() == 0) {
            throw new OpException(OpResult.FAIL, "匹配玩家或队伍不能为空");
        }
        if (gameType.pcnt < mUnit.getSize()) {
            throw new OpException(OpResult.FAIL, "队伍人数超过游戏类型人数限制");
        }

        int restCnt = gameType.pcnt * 2 - mUnit.getSize() + Buffer_Match; //需要挑出来匹配的玩家总数量
        List<IUnit> results = new ArrayList<>(restCnt); //初步挑出来匹配的玩家


        //上下分数匹配
        {
            Map.Entry<Integer, Queue<IUnit>> hEntry = matchpool.ceilingEntry(mUnit.getScore());
            Map.Entry<Integer, Queue<IUnit>> lEntry = matchpool.floorEntry(mUnit.getScore());
            //每次循环各取高低一个单位
            while (restCnt > 0) {
                if (hEntry == null && lEntry == null) {
                    break;
                }
                //寻找分高的玩家
                while (hEntry != null && hEntry.getValue().size() == 0) {
                    hEntry = matchpool.higherEntry(hEntry.getKey());
                    if (hEntry == null) {
                        break;
                    }
                }
                if (hEntry != null) {
                    if (Math.abs(hEntry.getKey() - mUnit.getScore()) > Max_Score_Diff) {
                        hEntry = null;
                    } else {
                        IUnit pickUnit = hEntry.getValue().poll();
                        if (pickUnit != null) {
                            results.add(pickUnit);
                            restCnt = restCnt - pickUnit.getSize();
                        }
                    }
                }
                //寻找分低的玩家
                while (lEntry != null && lEntry.getValue().size() == 0) {
                    lEntry = matchpool.lowerEntry(lEntry.getKey());
                    if (lEntry == null) {
                        break;
                    }

                }
                if (lEntry != null) {
                    if (Math.abs(lEntry.getKey() - mUnit.getScore()) > Max_Score_Diff) {
                        lEntry = null;
                    } else {
                        IUnit pickUnit = lEntry.getValue().poll();
                        if (pickUnit != null) {
                            results.add(pickUnit);
                            restCnt = restCnt - pickUnit.getSize();
                        }
                    }
                }
            }
        }
        Match match = new Match(gameType);
        List<IUnit> toReturn = results;
        //有足够的玩家进行二次挑选
        if (restCnt <= Buffer_Match) {
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
                //挑选team2，需要满足unit的大小等于team1
                Iterator<IUnit> iter2 = results2.iterator();
                lastTeam2 = null;
                while (match.team2size() < gameType.pcnt && iter2.hasNext()) {
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
                while (match.team1size() < gameType.pcnt && iter1.hasNext()) {
                    IUnit pickUnit = iter1.next();
                    if (pickUnit.getSize() + match.team1size() <= gameType.pcnt) {
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
            if (match.team1size() == gameType.pcnt && match.team2size() == gameType.pcnt) {
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
                queue = new LinkedBlockingQueue<>();
                matchpool.put(picked.getScore(), queue);
            }
            queue.add(picked);
        }
        if (match == null) {
            return null;
        } else {
            //平衡team1和team2的分数
            Match matchret = new Match(match.gameType);
            for (int i = 0; i < match.team1.size(); i++) {
                boolean team1higher = matchret.scoreDiff() > 0;
                boolean team2higher = (match.team2.get(i).getScore() - match.team1.get(i).getScore()) > 0;
                if (team1higher ^ team2higher) {
                    matchret.team1.add(match.team2.get(i));
                    matchret.team2.add(match.team1.get(i));
                } else {
                    matchret.team1.add(match.team1.get(i));
                    matchret.team2.add(match.team2.get(i));
                }
            }
            return match;
        }
    }

    public synchronized Map<GameType, Map<Integer, List<IUnit>>> getMatchPools() {
        Map ret = new HashMap();
        for (Map.Entry<GameType, TreeMap<Integer, Queue<IUnit>>> mapEntry : this.matchPools.entrySet()) {
            HashMap<Integer, List<IUnit>> retEntry = new HashMap();
            for (Map.Entry<Integer, Queue<IUnit>> entry : mapEntry.getValue().entrySet()) {
                retEntry.put(entry.getKey(), new ArrayList<IUnit>(entry.getValue()));
            }
            ret.put(mapEntry.getKey(), retEntry);
        }

        return ret;
    }

    public synchronized Map<Integer, List<IUnit>> getMatchPool(Integer gameTypeKey) throws OpException {
        TreeMap<Integer, Queue<IUnit>> matchpool = this.matchPools.get(GameType.get(gameTypeKey));
        if (matchpool == null) {
            throw new OpException(OpResult.INVALID, "找不到该比赛类型" + gameTypeKey);
        }
        HashMap<Integer, List<IUnit>> ret = new HashMap();
        for (Map.Entry<Integer, Queue<IUnit>> entry : matchpool.entrySet()) {
            ret.put(entry.getKey(), new ArrayList<IUnit>(entry.getValue()));
        }
        return ret;
    }

    public synchronized void cleanMatchPool(Integer gameTypeKey) throws OpException {
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
 

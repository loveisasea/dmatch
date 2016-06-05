package com.fym.match;

import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;
import com.fym.match.obj.IUnit;
import com.fym.match.obj.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/25.
 */
public class MatchEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchEngine.class);

    //<score,queue<IUnit>>
    private TreeMap<Integer, Queue<IUnit>> matchpool;

    //可以接受的最大分差
    public int maxScoreDiff;

    //足够挑选的玩家数量
    public int bufferMatch;

    //单边玩家人数
    public int pcnt;

    public MatchEngine(int pcnt, int maxScoreDiff, int bufferMatch) {
        this.pcnt = pcnt;
        this.maxScoreDiff = maxScoreDiff;
        this.bufferMatch = bufferMatch;
        this.matchpool = new TreeMap<>();
    }

    /**
     * 匹配函数
     * 思路：
     * 1. 先从参赛者分数震荡向高低两边取未匹配玩家和组队，需要符合最大分数差，而且选取数量带有一定buffer
     * 2. 把第一步选取出来的玩家按照规模大小接近和分值接近排序
     * 3. 从结果集中
     *
     * @param mUnit
     * @return
     * @throws OpException
     */
    public Match tryMatch(final IUnit mUnit) throws OpException {
        if (mUnit == null || mUnit.getSize() == 0) {
            throw new OpException(OpResult.FAIL, "匹配玩家或队伍不能为空");
        }
        if (this.pcnt < mUnit.getSize()) {
            throw new OpException(OpResult.FAIL, "队伍人数超过游戏类型人数限制");
        }

        int restCnt = this.pcnt * 2 - mUnit.getSize() + this.bufferMatch; //需要挑出来匹配的玩家总数量
        List<IUnit> results = new ArrayList<>(); //初步挑出来匹配的玩家


        synchronized (this.matchpool) {
            //上下分数匹配
            {
                Map.Entry<Integer, Queue<IUnit>> hEntry = this.matchpool.ceilingEntry(mUnit.getScore());
                Map.Entry<Integer, Queue<IUnit>> lEntry = this.matchpool.floorEntry(mUnit.getScore());
                //每次循环各取高低一个单位
                while (restCnt > 0) {
                    if (hEntry == null && lEntry == null) {
                        break;
                    }
                    //寻找分高的玩家
                    while (hEntry != null && hEntry.getValue().size() == 0) {
                        hEntry = this.matchpool.higherEntry(hEntry.getKey());
                        if (hEntry == null) {
                            break;
                        }
                    }
                    if (hEntry != null) {
                        if (Math.abs(hEntry.getKey() - mUnit.getScore()) > this.maxScoreDiff) {
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
                        lEntry = this.matchpool.lowerEntry(lEntry.getKey());
                        if (lEntry == null) {
                            break;
                        }

                    }
                    if (lEntry != null) {
                        if (Math.abs(lEntry.getKey() - mUnit.getScore()) > this.maxScoreDiff) {
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
            Match match = new Match();
            List<IUnit> toReturn = results;
            //有足够的玩家进行二次挑选
            if (restCnt <= bufferMatch) {
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
                    while (match.team2size() < this.pcnt && iter2.hasNext()) {
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
                    while (match.team1size() < this.pcnt && iter1.hasNext()) {
                        IUnit pickUnit = iter1.next();
                        if (pickUnit.getSize() + match.team1size() <= this.pcnt) {
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
                if (match.team1size() == this.pcnt && match.team2size() == this.pcnt) {
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
                Match matchret = new Match();
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
    }

    public synchronized void quitMatch(IUnit unit) {
        Queue<IUnit> iUnits = this.matchpool.get(unit.getScore());
        if (iUnits != null) {
            Iterator<IUnit> iter = iUnits.iterator();
            while (iter.hasNext()) {
                IUnit iunit = iter.next();
                if (iunit == unit) {
                    iter.remove();
                    break;
                }
            }
        }
    }

    public void cleanMatchPool() throws OpException {
        this.matchpool.clear();
    }
}
 

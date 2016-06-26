package com.fym.game.engine;


import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;
import com.fym.core.util.DateUtil;
import com.fym.game.enm.*;
import com.fym.game.obj.*;
import com.fym.match.obj.Match;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class GameEngine implements InitializingBean {

    public Game genGame(  Match match) throws OpException {
        if (match == null) {
            throw new OpException(OpResult.INVALID, "match为空");
        }
        if (match.team1 == null || match.team1.size() == 0 || match.team2 == null || match.team2.size() == 0) {
            throw new OpException(OpResult.INVALID, "一方玩家数量为空");
        }
        if (match.gameType == null) {
            throw new OpException(OpResult.INVALID, "游戏类型为空");
        }

        List<Integer> pidsR = match.team1pids();
        List<Integer> pidsB = match.team2pids();
        if (pidsR.size() != pidsR.size()) {
            throw new OpException(OpResult.INVALID, "红黑双方玩家数量不一致。 <" + pidsR + "> != <" + pidsB + ">");
        }

        int playerCnt = match.team1.size();
        Game game = new Game();
        game.type = match.gameType;
        game.initbattleField = this.createBattleFied(game.type, playerCnt);
        game.gplayers = new ArrayList<>();
        game.steps = new ArrayList<>();

        //随机分配
        Random random = new Random();
        if (random.nextInt(2) == 1) {
            List<Integer> tmp = pidsR;
            pidsR = pidsB;
            pidsB = tmp;
        }

        Iterator<Integer> iterR = pidsR.iterator();
        Iterator<Integer> iterB = pidsB.iterator();
        while (true) {
            if ((!iterR.hasNext() && iterB.hasNext()) || (iterR.hasNext() && !iterB.hasNext())) {
                throw new OpException(OpResult.FAIL, "玩家数量不匹配");
            }
            if (iterR.hasNext()) {
                Integer pid = iterR.next();
                Gplayer gplayer = new Gplayer();
                gplayer.pid = pid;
                gplayer.status = GamePlayerStatus.准备中;
                gplayer.restTime = 2 * 1000 * 60 * 60;
                game.gplayers.add(gplayer);

                pid = iterB.next();
                gplayer = new Gplayer();
                gplayer.pid = pid;
                gplayer.status = GamePlayerStatus.准备中;
                gplayer.restTime = 2 * 1000 * 60 * 60;
                game.gplayers.add(gplayer);
            } else {
                break;
            }
        }


        for (int i = 0; i < game.gplayers.size(); i = i + 2) {
            List<Zeat> zeats = this.genNoOrderZeats(game.gplayers.get(i), TeamType.红);
            this.fillBattleField(game.initbattleField, zeats, TeamType.红, i);
        }

        for (int i = 1; i < game.gplayers.size(); i = i + 2) {
            List<Zeat> zeats = this.genNoOrderZeats(game.gplayers.get(i), TeamType.黑);
            this.fillBattleField(game.initbattleField, zeats, TeamType.黑, i);
        }


        game.currbattleField = game.initbattleField.copy();


        return game;
    }

    public GameStep goStep(Game game, Integer pid, int srtX, int srtY, int endX, int endY) throws OpException {
        if (game.winTeam != null) {
            throw new OpException(OpResult.FAIL, "游戏已结束");
        }
        //当前玩家
        int pseq = game.steps.size() % game.gplayers.size();
        Gplayer currgplayer = game.gplayers.get(pseq);

        if (!pid.equals(currgplayer.pid)) {
            //如果不是掉线了而且还不是同一队
            if ((!currgplayer.status.equals(GamePlayerStatus.已断开) && !currgplayer.status.equals(GamePlayerStatus.已断开))
                    || (currgplayer.pid - pid) % 2 == 1) {
                throw new OpException(OpResult.FAIL, "不到玩家<" + pid + ">走");
            }
        }

        if (srtX >= game.currbattleField.type.width || srtX < 0) {
            throw new OpException(OpResult.FAIL, "起始横坐标<" + srtX + ">已超出棋盘宽度<" + game.currbattleField.type.width + ">");
        }
        if (srtY >= game.currbattleField.type.height || srtY < 0) {
            throw new OpException(OpResult.FAIL, "起始纵坐标<" + srtY + ">已超出棋盘高度<" + game.currbattleField.type.height + ">");
        }
        if (endX >= game.currbattleField.type.width || endX < 0) {
            throw new OpException(OpResult.FAIL, "结束横坐标<" + endX + ">已超出棋盘宽度<" + game.currbattleField.type.width + ">");
        }
        if (endY >= game.currbattleField.type.height || endY < 0) {
            throw new OpException(OpResult.FAIL, "结束纵坐标<" + endY + ">已超出棋盘高度<" + game.currbattleField.type.height + ">");
        }
        Zeat selfzeat = game.currbattleField.pies[srtX][endY];
        if (selfzeat == null) {
            throw new OpException(OpResult.FAIL, "该位置没有棋子可走");
        }
        if (selfzeat.pid.equals(pid)) {
            throw new OpException(OpResult.FAIL, "棋子并不属于玩家<" + pid + ">");
        }
        Zeat targetzeat = game.currbattleField.pies[endX][endY];
        //TODO 走法校验
        if (targetzeat != null) {
            if (targetzeat.sid.teamType().equals(selfzeat.sid.teamType())) {
                throw new OpException(OpResult.FAIL, "不能吃己方子");
            }
        }


        //设置开始时间
        if (game.startDatetime == null) {
            game.startDatetime = DateUtil.getCurrent();
        }

        //设置本步用时
        GameStep step = new GameStep();
        step.pid = pid;
        step.srtX = srtX;
        step.srtY = srtY;
        step.endX = endX;
        step.endY = endY;
        step.duration = DateUtil.getCurrent().getTime() - game.startDatetime.getTime();
        for (GameStep gameStep : game.steps) {
            step.duration = step.duration - gameStep.duration;
        }


        //设置胜利方和所有时间
        if (ZeatID.红帅.equals(targetzeat.rid)) {
            game.winTeam = TeamType.黑;
            game.duration = DateUtil.getCurrent().getTime() - game.startDatetime.getTime();
        } else if (ZeatID.黑将.equals(targetzeat.rid)) {
            game.winTeam = TeamType.红;
            game.duration = DateUtil.getCurrent().getTime() - game.startDatetime.getTime();
        } else {
            //扣除玩家所用时间
            game.gplayers.get(pseq).restTime -= step.duration;
        }


        step.seq = game.steps.size();
        game.steps.add(step);
        return step;
    }


    /**
     * 创建棋盘
     *
     * @param gameType
     * @param playerCnt
     * @return
     * @throws OpException
     */
    private BattleField createBattleFied(GameType gameType, int playerCnt) throws OpException {
        if (gameType.equals(GameType._1v1_ladder) || gameType.equals(GameType._1v1_normal)) {
            if (playerCnt == 1) {
                return new BattleField(BattleFieldType._1v1);
            } else {
                throw new OpException(OpResult.INVALID, "<" + gameType + ">玩家数量应该是1，不应该是<" + playerCnt + ">");
            }
        } else if (gameType.equals(GameType._nvn_ladder) || gameType.equals(GameType._nvn_normal)) {
            if (playerCnt == 2) {
                return new BattleField(BattleFieldType._2v2);
            } else if (playerCnt == 3) {
                return new BattleField(BattleFieldType._3v3);
            } else {
                throw new OpException(OpResult.INVALID, "<" + gameType + ">玩家数量应该是2或3，不应该是<" + playerCnt + ">");
            }
        } else {
            throw new OpException(OpResult.INVALID, "没有对应的游戏类型");
        }
    }


    /**
     * 填充棋盘
     *
     * @param battleField
     * @param zeats
     * @param teamtype
     * @param idx
     */
    private void fillBattleField(BattleField battleField, List<Zeat> zeats, TeamType teamtype, int idx) {
        int centerX = 4 + idx * 9;
        int centerY = teamtype.equals(TeamType.红) ? 0 : 9;
        int direction = teamtype.equals(TeamType.红) ? 1 : -1;
        int i = 0;
        //帅
        battleField.pies[centerX][centerY] = zeats.get(i++);
        battleField.pies[centerX][centerY].sid = teamtype.equals(TeamType.红) ? ZeatID.红帅 : ZeatID.黑将;
        //車
        battleField.pies[centerX + 4][centerY] = zeats.get(i++);
        battleField.pies[centerX + 4][centerY].sid = teamtype.equals(TeamType.红) ? ZeatID.红車 : ZeatID.黑車;
        battleField.pies[centerX - 4][centerY] = zeats.get(i++);
        battleField.pies[centerX - 4][centerY].sid = teamtype.equals(TeamType.红) ? ZeatID.红車 : ZeatID.黑車;
        //馬
        battleField.pies[centerX + 3][centerY] = zeats.get(i++);
        battleField.pies[centerX + 3][centerY].sid = teamtype.equals(TeamType.红) ? ZeatID.红馬 : ZeatID.黑馬;
        battleField.pies[centerX - 3][centerY] = zeats.get(i++);
        battleField.pies[centerX - 3][centerY].sid = teamtype.equals(TeamType.红) ? ZeatID.红馬 : ZeatID.黑馬;
        //炮
        battleField.pies[centerX + 3][centerY + direction * 2] = zeats.get(i++);
        battleField.pies[centerX + 3][centerY + direction * 2].sid = teamtype.equals(TeamType.红) ? ZeatID.红炮 : ZeatID.黑砲;
        battleField.pies[centerX - 3][centerY + direction * 2] = zeats.get(i++);
        battleField.pies[centerX - 3][centerY + direction * 2].sid = teamtype.equals(TeamType.红) ? ZeatID.红炮 : ZeatID.黑砲;
        //仕
        battleField.pies[centerX + 1][centerY] = zeats.get(i++);
        battleField.pies[centerX + 1][centerY].sid = teamtype.equals(TeamType.红) ? ZeatID.红仕 : ZeatID.黑士;
        battleField.pies[centerX - 1][centerY] = zeats.get(i++);
        battleField.pies[centerX - 1][centerY].sid = teamtype.equals(TeamType.红) ? ZeatID.红仕 : ZeatID.黑士;
        //相
        battleField.pies[centerX + 2][centerY] = zeats.get(i++);
        battleField.pies[centerX + 2][centerY].sid = teamtype.equals(TeamType.红) ? ZeatID.红相 : ZeatID.黑象;
        battleField.pies[centerX - 2][centerY] = zeats.get(i++);
        battleField.pies[centerX - 2][centerY].sid = teamtype.equals(TeamType.红) ? ZeatID.红相 : ZeatID.黑象;
        //兵
        battleField.pies[centerX][centerY + direction * 3] = zeats.get(i++);
        battleField.pies[centerX][centerY + direction * 3].sid = teamtype.equals(TeamType.红) ? ZeatID.红兵 : ZeatID.黑卒;
        battleField.pies[centerX + 2][centerY + direction * 3] = zeats.get(i++);
        battleField.pies[centerX + 2][centerY + direction * 3].sid = teamtype.equals(TeamType.红) ? ZeatID.红兵 : ZeatID.黑卒;
        battleField.pies[centerX - 2][centerY + direction * 3] = zeats.get(i++);
        battleField.pies[centerX - 2][centerY + direction * 3].sid = teamtype.equals(TeamType.红) ? ZeatID.红兵 : ZeatID.黑卒;
        battleField.pies[centerX + 4][centerY + direction * 3] = zeats.get(i++);
        battleField.pies[centerX + 4][centerY + direction * 3].sid = teamtype.equals(TeamType.红) ? ZeatID.红兵 : ZeatID.黑卒;
        battleField.pies[centerX - 4][centerY + direction * 3] = zeats.get(i++);
        battleField.pies[centerX - 4][centerY + direction * 3].sid = teamtype.equals(TeamType.红) ? ZeatID.红兵 : ZeatID.黑卒;

    }


    /**
     * 打印棋盘
     *
     * @param battlefield
     */
    public void printBattleField(BattleField battlefield) {
        int ylength = battlefield.pies[0].length;
        for (int j = 0; j < ylength; j++) {
            for (int i = 0; i < battlefield.pies.length; i++) {
                Zeat zeat = battlefield.pies[i][j];
                if (zeat == null) {
                    System.out.print("一");
                } else {
                    System.out.print(zeat.rid.name);
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * 随机生成棋子
     *
     * @param gplayer
     * @return
     */
    private List<Zeat> genNoOrderZeats(Gplayer gplayer, TeamType teamtype) {
        List<Zeat> list = new LinkedList<>();
        if (teamtype.equals(TeamType.红)) {
            //帅
            Zeat zeat = new Zeat(ZeatID.红帅);
            zeat.pid = gplayer.pid;
            zeat.status = ZeatStatus.未动;
            list.add(zeat);

            //車
            for (int i = 0; i < 2; i++) {
                zeat = new Zeat(ZeatID.红車);
                zeat.pid = gplayer.pid;
                zeat.status = ZeatStatus.未动;
                list.add(zeat);
            }
            //馬
            for (int i = 0; i < 2; i++) {
                zeat = new Zeat(ZeatID.红馬);
                zeat.pid = gplayer.pid;
                zeat.status = ZeatStatus.未动;
                list.add(zeat);
            }
            //炮
            for (int i = 0; i < 2; i++) {
                zeat = new Zeat(ZeatID.红炮);
                zeat.pid = gplayer.pid;
                zeat.status = ZeatStatus.未动;
                list.add(zeat);
            }
            //仕
            for (int i = 0; i < 2; i++) {
                zeat = new Zeat(ZeatID.红仕);
                zeat.pid = gplayer.pid;
                zeat.status = ZeatStatus.未动;
                list.add(zeat);
            }
            //相
            for (int i = 0; i < 2; i++) {
                zeat = new Zeat(ZeatID.红相);
                zeat.pid = gplayer.pid;
                zeat.status = ZeatStatus.未动;
                list.add(zeat);
            }
            //兵
            for (int i = 0; i < 5; i++) {
                zeat = new Zeat(ZeatID.红兵);
                zeat.pid = gplayer.pid;
                zeat.status = ZeatStatus.未动;
                list.add(zeat);
            }
        } else {
            //将
            Zeat zeat = new Zeat(ZeatID.黑将);
            zeat.pid = gplayer.pid;
            zeat.status = ZeatStatus.未动;
            list.add(zeat);

            //車
            for (int i = 0; i < 2; i++) {
                zeat = new Zeat(ZeatID.黑車);
                zeat.pid = gplayer.pid;
                zeat.status = ZeatStatus.未动;
                list.add(zeat);
            }
            //馬
            for (int i = 0; i < 2; i++) {
                zeat = new Zeat(ZeatID.黑馬);
                zeat.pid = gplayer.pid;
                zeat.status = ZeatStatus.未动;
                list.add(zeat);
            }
            //砲
            for (int i = 0; i < 2; i++) {
                zeat = new Zeat(ZeatID.黑砲);
                zeat.pid = gplayer.pid;
                zeat.status = ZeatStatus.未动;
                list.add(zeat);
            }
            //士
            for (int i = 0; i < 2; i++) {
                zeat = new Zeat(ZeatID.黑士);
                zeat.pid = gplayer.pid;
                zeat.status = ZeatStatus.未动;
                list.add(zeat);
            }
            //象
            for (int i = 0; i < 2; i++) {
                zeat = new Zeat(ZeatID.黑象);
                zeat.pid = gplayer.pid;
                zeat.status = ZeatStatus.未动;
                list.add(zeat);
            }
            //卒
            for (int i = 0; i < 5; i++) {
                zeat = new Zeat(ZeatID.黑卒);
                zeat.pid = gplayer.pid;
                zeat.status = ZeatStatus.未动;
                list.add(zeat);
            }
        }
        Random random = new Random();
        List<Zeat> ret = new ArrayList<>(16);
        ret.add(list.get(0));
        list.remove(0);
        for (int i = list.size() - 1; i >= 0; i--) {
            int idx = random.nextInt(i + 1);
            ret.add(list.get(idx));
            list.remove(idx);
        }
        return ret;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
//        this.test();
    }

//    private void test() throws OpException {
//        Match match = new Match();
//        match.team1.add(1);
//        List<Integer> reds = new ArrayList<>();
//        reds.add(1);
//        reds.add(2);
//        reds.add(3);
//
//        List<Integer> blacks = new ArrayList<>();
//        blacks.add(4);
//        blacks.add(5);
//        blacks.add(6);
//        Game game = this.genGame(GameType._nvn_ladder, reds, blacks);
//        this.printBattleField(game.initbattleField);
//
//    }
}

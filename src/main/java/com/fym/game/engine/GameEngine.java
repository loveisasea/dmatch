package com.fym.game.engine;


import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;
import com.fym.game.enm.*;
import com.fym.game.obj.BattleField;
import com.fym.game.obj.Game;
import com.fym.game.obj.Gplayer;
import com.fym.game.obj.Zeat;
import com.fym.playerlogin.obj.PlayerLoginS;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

@Component
public class GameEngine implements InitializingBean {

    public Game genGame(GameType gametype, List<PlayerLoginS> redPlayers, List<PlayerLoginS> blackPlayers) throws OpException {
        if (redPlayers == null) {
            throw new OpException(OpResult.INVALID, "红方玩家数量为空");
        }
        if (blackPlayers == null) {
            throw new OpException(OpResult.INVALID, "黑方玩家数量为空");
        }
        if (redPlayers.size() != blackPlayers.size()) {
            throw new OpException(OpResult.INVALID, "红黑双方玩家数量不一致。红方<" + redPlayers.size() + ">，黑方<" + blackPlayers.size() + ">");
        }
        if (gametype == null) {
            throw new OpException(OpResult.INVALID, "游戏类型为空");
        }

        int playerCnt = redPlayers.size();
        Game game = new Game();
        game.type = gametype;
        game.battleField = this.createBattleFied(game.type, playerCnt);

        game.redPlayers = new ArrayList<>();
        for (int i = 0; i < redPlayers.size(); i++) {
            PlayerLoginS redPlayer = redPlayers.get(i);
            Gplayer gplayer = new Gplayer();
            gplayer.pid = redPlayer.pid;
            gplayer.nname = redPlayer.nname;
            gplayer.teamtype = TeamType.红;
            game.redPlayers.add(gplayer);
            List<Zeat> zeats = this.genNoOrderZeats(gplayer);
            this.fillBattleField(game.battleField, zeats, gplayer.teamtype, i);
        }

        game.blackPlayers = new ArrayList<>();
        for (int i = 0; i < blackPlayers.size(); i++) {
            Gplayer gplayer = new Gplayer();
            gplayer.pid = gplayer.pid;
            gplayer.nname = gplayer.nname;
            gplayer.teamtype = TeamType.黑;
            game.blackPlayers.add(gplayer);
            List<Zeat> zeats = this.genNoOrderZeats(gplayer);
            this.fillBattleField(game.battleField, zeats, gplayer.teamtype, i);
        }

        return game;

    }

    private BattleField createBattleFied(GameType gametype, int playerCnt) throws OpException {
        if (gametype.equals(GameType._1v1_ladder) || gametype.equals(GameType._1v1_normal)) {
            if (playerCnt == 1) {
                return new BattleField(BattleFieldType._1v1);
            } else {
                throw new OpException(OpResult.INVALID, "<" + gametype + ">玩家数量应该是1，不应该是<" + playerCnt + ">");
            }
        } else if (gametype.equals(GameType._nvn_ladder) || gametype.equals(GameType._nvn_normal)) {
            if (playerCnt == 2) {
                return new BattleField(BattleFieldType._2v2);
            } else if (playerCnt == 3) {
                return new BattleField(BattleFieldType._3v3);
            } else {
                throw new OpException(OpResult.INVALID, "<" + gametype + ">玩家数量应该是2或3，不应该是<" + playerCnt + ">");
            }
        } else {
            throw new OpException(OpResult.INVALID, "没有对应的游戏类型");
        }
    }


    /**
     * 填充棋子
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

    private List<Zeat> genNoOrderZeats(Gplayer gplayer) {
        List<Zeat> list = new LinkedList<>();
        if (gplayer.teamtype.equals(TeamType.红)) {
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
        this.test();
    }

    private void test() throws OpException {
        List<PlayerLoginS> reds = new ArrayList<>();
        PlayerLoginS redPlayer = new PlayerLoginS();
        redPlayer.pid = 1;
        redPlayer.nname = "一狗子";
        reds.add(redPlayer);
        redPlayer = new PlayerLoginS();
        redPlayer.pid = 2;
        redPlayer.nname = "二狗子";
        reds.add(redPlayer);
        redPlayer = new PlayerLoginS();
        redPlayer.pid = 3;
        redPlayer.nname = "三狗子";
        reds.add(redPlayer);

        List<PlayerLoginS> blacks = new ArrayList<>();
        PlayerLoginS blackPlayer = new PlayerLoginS();
        blackPlayer.pid = 4;
        blackPlayer.nname = "小强";
        blacks.add(blackPlayer);
        blackPlayer = new PlayerLoginS();
        blackPlayer.pid = 5;
        blackPlayer.nname = "中强";
        blacks.add(blackPlayer);
        blackPlayer = new PlayerLoginS();
        blackPlayer.pid = 6;
        blackPlayer.nname = "大强";
        blacks.add(blackPlayer);
        Game game = this.genGame(GameType._nvn_ladder, reds, blacks);
        this.printBattleField(game.battleField);

    }
}

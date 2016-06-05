package com.fym.match;
/**
 * Owned by Planck System
 * Created by fengy on 2016/5/24.
 */

import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;
import com.fym.game.enm.GameType;
import com.fym.match.msg.MsgMatchFound;
import com.fym.match.msg.MsgTeamEvent;
import com.fym.match.obj.*;
import com.fym.player.PlayerService;
import com.fym.player.obj.Player;
import com.fym.playerbox.PlayerBoxCom;
import com.fym.playerbox.obj.IMsg;
import com.fym.playerbox.obj.MsgID;
import com.fym.playerlogin.PlayerLoginService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Transactional(rollbackFor = Exception.class)
@Service("matchService")
public class MatchServiceImpl implements MatchService, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MatchServiceImpl.class);

    //<pid,Team>
    private Map<Integer, Team> map_pid_team = new ConcurrentHashMap<>();

    //<gametype,matchstatuspool>
    private Map<GameType, MatchStatusPool> matchStatusPools = new HashMap<>();

    //<pid,GameTypeKeys>
    private Map<Integer, List<GameType>> map_pid_gametypes = new ConcurrentHashMap<>();

    //<uuid,match>
    private Map<UUID, Match> map_uuid_matches = new ConcurrentHashMap<>();


    @Autowired
    private PlayerLoginService playerLoginService;

    @Autowired
    private PlayerBoxCom playerBoxCom;

    @Autowired
    private PlayerService playerService;

    @Override
    public Team inviteTeam(Integer matepid) throws OpException {
        Integer selfpid = this.playerLoginService.getLogin().pid;
        Team team = this.getTeam(selfpid);
        //队长组队
        synchronized (team) {
            //添加邀请
            if (!team.apids.contains(matepid)) {
                if (!team.ppids.contains(matepid)) {
                    team.ppids.add(matepid);
                }
            }
        }
        IMsg msg = new IMsg((Integer) MsgID.组队队长邀请.key, new MsgTeamEvent(selfpid, matepid));
        this.playerBoxCom.putMsg(team.apids, msg);
        return team;
    }

    @Override
    public Team acceptTeam(Integer leaderpid) throws OpException {
        //退出匹配
        this.quitMatching();

        this.quitTeam();
        Team team = this.getTeam(leaderpid);
        Integer selfpid = this.playerLoginService.getLogin().pid;

        //添加到新队伍中
        synchronized (team) {
            if (!team.apids.contains(selfpid)) {
                if (!team.ppids.contains(selfpid)) {
                    throw new OpException(OpResult.FAIL, "队长已取消邀请");
                }
                //取消悬挂
                team.ppids.remove(selfpid);
                //添加到team中
                if (!team.apids.contains(selfpid)) {
                    team.apids.add(selfpid);
                }
            }
        }
        IMsg msg = new IMsg((Integer) MsgID.组队候选人同意.key, new MsgTeamEvent(team.leaderpid, selfpid));
        this.playerBoxCom.putMsg(team.apids, msg);
        this.map_pid_team.put(selfpid, team);
        return team;
    }

    @Override
    public Team rejectTeam(Integer leaderpid) throws OpException {
        Team team = this.getTeam(leaderpid);
        Integer selfpid = this.playerLoginService.getLogin().pid;
        //从新队伍中移除
        if (team.ppids.contains(selfpid)) {
            //取消悬挂
            team.ppids.remove(selfpid);
            IMsg msg = new IMsg((Integer) MsgID.组队候选人拒绝.key, new MsgTeamEvent(team.leaderpid, selfpid));
            this.playerBoxCom.putMsg(team.apids, msg);
        }
        return team;

    }

    @Override
    public Team quitTeam() throws OpException {
        //退出匹配
        this.quitMatching();

        Integer selfpid = this.playerLoginService.getLogin().pid;
        Team team = this.getTeam(selfpid);
        //从新队伍中移除
        if (team != null) {
            synchronized (team) {
                if (team.apids.contains(selfpid)) {
                    team.apids.remove(selfpid);
                }
                if (team.leaderpid.equals(selfpid)) {
                    if (team.apids.size() > 0) {
                        team.leaderpid = team.apids.iterator().next();
                    } else {
                        team.ppids.clear();
                        team.leaderpid = null;
                    }
                }
            }
            IMsg msg = new IMsg((Integer) MsgID.组队队员离开.key, new MsgTeamEvent(team.leaderpid, selfpid));
            this.playerBoxCom.putMsg(team.apids, msg);
            this.map_pid_team.remove(selfpid);
        }
        return team;
    }

    @Override
    public void startMatch(List<Integer> gameTypeKeys) throws OpException {
        Integer selfpid = this.playerLoginService.getLogin().pid;
        this.startMatch(selfpid, gameTypeKeys);
    }

    private void startMatch(Integer pid, List<Integer> gameTypeKeys) throws OpException {
        //获取玩家数量
        Team team = this.map_pid_team.get(pid);
        if (team == null) {
            team = new Team(pid);
            this.map_pid_team.put(pid, team);
        }

        //获取玩家积分
        Map<Integer, Player> mapping = this.playerService.getMapping(team.apids, 0);
        if (mapping.entrySet().size() < team.apids.size()) {
            throw new OpException(OpResult.FAIL, "缺少部分玩家信息");
        }

        //和新类型比较，获取本次游戏类型，
        List<GameType> gameTypes = this.map_pid_gametypes.get(pid);
        if (gameTypes == null) {
            gameTypes = new ArrayList<GameType>();
            this.map_pid_gametypes.put(pid, gameTypes);
        }
        if (gameTypeKeys != null) {
            gameTypes.clear();
            for (Integer gameTypeKey : gameTypeKeys) {
                GameType gameType = GameType.get(gameTypeKey);
                gameTypes.add(gameType);
            }
        }
        if (gameTypes.size() == 0) {
            throw new OpException(OpResult.FAIL, "请选择游戏类型开始");
        }


        //发送开始匹配通知
        this.playerBoxCom.putMsg(team.apids, new IMsg((Integer) MsgID.开始匹配.key, null));

        //开始匹配
        Match matchOK = null;
        MatchStatusPool matchStatusPoolOK = null;
        for (GameType gameType : gameTypes) {
            //获取对应的匹配池
            matchStatusPoolOK = this.getMatchStatusPool(gameType);
            matchOK = matchStatusPoolOK.tryMatch(team, mapping.values());
            break;
        }

        //匹配成功
        if (matchOK != null) {
            //退出已有匹配池
            for (GameType gameType : gameTypes) {
                MatchStatusPool matchStatusPool = this.getMatchStatusPool(gameType);
                matchStatusPool.quitMatching(team);
            }
            //获取所有匹配玩家信息
            List<Integer> matchpids = matchOK.allpids();
            //校验是否有重复,一般是没有
            {
                Set<Integer> dupSets = new HashSet<>();
                for (Integer matchpid : matchpids) {
                    if (dupSets.contains(matchpid)) {
                        throw new OpException(OpResult.FAIL, "发现重复玩家<" + matchpid + ">");
                    }
                    dupSets.add(matchpid);
                }
            }

            //保存
            UUID matchid = UUID.randomUUID();
            map_uuid_matches.put(matchid, matchOK);

            //发送通知
            List<Player> players = this.playerService.getList(matchpids, 0);
            MsgMatchFound msg = new MsgMatchFound();
            msg.uuid = matchid;
            msg.gameType = matchStatusPoolOK.gameType;
            msg.players = players;
            for (Player player : players) {
                this.playerBoxCom.putMsg(matchpids, new IMsg((Integer) MsgID.匹配成功.key, msg));
            }
        }
    }


    @Override
    public void acceptMatch(String matchuuid) throws OpException {
        Match match = this.map_uuid_matches.get(UUID.fromString(matchuuid));
        if (match == null) {
            throw new OpException(OpResult.FAIL, "该匹配已取消");
        }
        Integer selfpid = this.playerLoginService.getLogin().pid;
        List<Integer> matchpids = match.allpids();
        if (!matchpids.contains(selfpid)) {
            throw new OpException(OpResult.FAIL, "玩家并不在该匹配中");
        }
        match.accepts.add(selfpid);
        this.playerBoxCom.putMsg(matchpids, new IMsg((Integer) MsgID.玩家接受匹配.key, null));

        if (match.accepts.size() == matchpids.size()) {
            this.map_uuid_matches.remove(matchuuid);
            //TODO 启动游戏系统开始游戏
        }
    }

    @Override
    public void rejectMatch(String matchuuid) throws OpException {
        Match match = this.map_uuid_matches.get(UUID.fromString(matchuuid));
        if (match == null) {
            throw new OpException(OpResult.FAIL, "该匹配已取消");
        }
        Integer selfpid = this.playerLoginService.getLogin().pid;
        List<Integer> matchpids = match.allpids();
        if (!matchpids.contains(selfpid)) {
            throw new OpException(OpResult.FAIL, "玩家并不在该匹配中");
        }
        this.map_uuid_matches.remove(matchuuid);
        this.playerBoxCom.putMsg(matchpids, new IMsg((Integer) MsgID.玩家拒绝匹配.key, null));

        //TODO 惩罚玩家
    }


    @Override
    public void quitMatching() throws OpException {
        Integer selfpid = this.playerLoginService.getLogin().pid;
        Team team = this.getTeam(selfpid);
        for (MatchStatusPool matchStatusPool : this.matchStatusPools.values()) {
            matchStatusPool.quitMatching(team);
        }
        //发送退出通知
        this.playerBoxCom.putMsg(team.apids, new IMsg((Integer) MsgID.玩家取消匹配.key, null));


    }


    private Team getTeam(Integer pid) throws OpException {
        Team team = this.map_pid_team.get(pid);
        if (team == null) {
            team = new Team(pid);
            this.map_pid_team.put(pid, team);
        }
        return team;
    }

    private MatchStatusPool getMatchStatusPool(GameType gameType) throws OpException {
        MatchStatusPool matchStatusPool = this.matchStatusPools.get(gameType);
        if (matchStatusPool == null) {
            throw new OpException(OpResult.FAIL, "没有找到对应的匹配引擎<" + gameType.name + ">");
        }
        return matchStatusPool;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.matchStatusPools.put(GameType._1v1_ladder, new MatchStatusPool(GameType._1v1_ladder, 1, 50, 2));
        this.matchStatusPools.put(GameType._1v1_normal, new MatchStatusPool(GameType._1v1_normal, 1, 50, 2));
        this.matchStatusPools.put(GameType._nvn_ladder, new MatchStatusPool(GameType._nvn_ladder, 3, 200, 4));
        this.matchStatusPools.put(GameType._nvn_normal, new MatchStatusPool(GameType._nvn_normal, 3, 200, 4));
    }


    private class MatchStatusPool {
        //游戏类型
        private GameType gameType;

        //<pid,<gametype,Group>>
        private Map<Team, IUnit> map_team_group;

        //匹配引擎
        private MatchEngine matchEngine;


        private MatchStatusPool(GameType gameType, int pcnt, int maxScoreDiff, int bufferMatch) {
            this.gameType = gameType;
            this.matchEngine = new MatchEngine(pcnt, maxScoreDiff, bufferMatch);
            this.map_team_group = new ConcurrentHashMap<>();
        }


        private Match tryMatch(Team team, Collection<Player> players) throws OpException {
            List<Person> persons = new ArrayList<>();
            for (Player player : players) {
                Person person = new Person(player.playerbs.id, player.playerbs.score_1);
                persons.add(person);
            }
            if (persons.size() > 1) {
                Group group = new Group(persons);
                this.map_team_group.put(team, group);
                return this.matchEngine.tryMatch(group);
            } else if (persons.size() == 1) {
                Person person = persons.get(0);
                this.map_team_group.put(team, person);
                return this.matchEngine.tryMatch(person);
            } else {
                throw new OpException(OpResult.INVALID, "Team<" + team.leaderpid + ">没有任何玩家参赛");
            }
        }

        private void quitMatching(Team team) {
            IUnit unit = this.map_team_group.get(team);
            if (unit != null) {
                this.map_team_group.remove(team);
                this.matchEngine.quitMatch(unit);
            }
        }
    }
}

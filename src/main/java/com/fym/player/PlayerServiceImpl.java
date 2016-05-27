package com.fym.player;
/**
 * Owned by Planck System
 * Created by fengy on 2016/5/15.
 */

import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;
import com.fym.core.util.BitUtil;
import com.fym.player.entity.*;
import com.fym.player.obj.Player;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Transactional(rollbackFor = Exception.class)
@Service("playerService")
public class PlayerServiceImpl implements PlayerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerServiceImpl.class);


    @Autowired
    private PlayerbsDao playerbsDao;

    @Autowired
    private PlayerAccountDao playerAccountDao;

    @Autowired
    private PlayerRoleDao playerRoleDao;

    @Autowired
    private PlayerSecureDao playerSecureDao;


    @Override
    public Player register(String account, String password, Boolean igW) throws OpException {
        if (account == null) {
            throw new OpException(OpResult.FAIL, "请输入账号");
        }
        account = account.trim();
        if (account.length() == 0) {
            throw new OpException(OpResult.FAIL, "请输入账号");
        }
        if (this.playerAccountDao.createPQuery().equal("acct", account).count() > 0) {
            throw new OpException(OpResult.FAIL, "已存在相同账户");
        }

        if (password == null) {
            throw new OpException(OpResult.FAIL, "请输入密码");
        }
        password = password.trim();
        if (password.length() == 0) {
            throw new OpException(OpResult.FAIL, "请输入密码");
        }

        Playerbs playerbs = new Playerbs();
        //TODO 起一个随机的昵称
        playerbs.nname = UUID.randomUUID().toString();
        this.playerbsDao.create(playerbs);

        //账号
        PlayerAccount playerAccount = new PlayerAccount();
        playerAccount.pid = playerbs.id;
        playerAccount.acct = account;
        this.playerAccountDao.create(playerAccount);

        //密码
        PlayerSecure existingPwd = this.playerSecureDao.getSingleton("pid", playerbs.id);
        if (existingPwd == null) {
            PlayerSecure ps = new PlayerSecure();
            ps.pid = playerbs.id;
            ps.password = password;
            this.playerSecureDao.create(ps);
        } else {
            existingPwd.password = password;
            this.playerSecureDao.update(existingPwd);
        }
        Player player = new Player(playerbs);
        this.fill(player, Integer.MAX_VALUE);
        return player;
    }


    @Override
    public Player get(Integer pid, Integer field) throws OpException {
        Playerbs playerbs = this.getCore(pid);
        Player player = new Player(playerbs);
        this.fill(player, field);
        return player;
    }

    @Override
    public List<Player> getList(List<Integer> pids, Integer field) {
        List<Playerbs> playerbss = null;
        if (pids == null) {
            playerbss = this.playerbsDao.getAll();
        } else {
            playerbss = this.playerbsDao.createPQuery().inCollection("pid", pids).query();
        }
        List<Player> players = this.genPlayers(playerbss);
        this.fill(players, field);
        return players;
    }

    @Override
    public Player getByPassword(String acct, String password) throws OpException {
        PlayerAccount acct1 = this.playerAccountDao.getSingleton("acct", acct);
        if (acct1 == null) {
            throw new OpException(OpResult.FAIL, "账户不存在");
        }
        int count = this.playerSecureDao.createPQuery().equal("pid", acct1.pid).equal("password", password).count();
        if (count == 0) {
            throw new OpException(OpResult.FAIL, "密码不正确");
        }
        return this.get(acct1.pid, Integer.MAX_VALUE);
    }

    private Playerbs getCore(Integer pid) throws OpException {
        Playerbs playerbs = this.playerbsDao.get(pid);
        if (playerbs == null) {
            throw new OpException(OpResult.FAIL, "找不到该玩家<" + pid + ">");
        }
        return playerbs;
    }

    private List<Player> genPlayers(List<Playerbs> playerbss) {
        List<Player> ret = new ArrayList<>(playerbss.size());
        for (Playerbs playerbs : playerbss) {
            Player player = new Player(playerbs);
            ret.add(player);
        }
        return ret;
    }

    private Player genPlayer(Playerbs playerbs) {
        Player player = new Player(playerbs);
        return player;
    }


    private void fill(Player player, int field_accounts) {
        if (BitUtil.include(field_accounts, Player.Field_accounts)) {
            List<String> accounts = this.playerAccountDao.createPQuery()
                    .addScale("acct", StringType.INSTANCE)
                    .equal("pid", player.playerbs.id)
                    .queryScale();
            player.accts = accounts;
        }
        if (BitUtil.include(field_accounts, Player.Field_roleids)) {
            List<Integer> roleids = this.playerRoleDao.createPQuery()
                    .addScale("roleid", IntegerType.INSTANCE)
                    .equal("pid", player.playerbs.id)
                    .queryScale();
            player.roleids = new HashSet<>(roleids);
        }
    }

    private void fill(Collection<Player> players, int field_accounts) {
        //TODO 效率有待提高
        for (Player player : players) {
            if (BitUtil.include(field_accounts, Player.Field_accounts)) {
                List<String> accounts = this.playerAccountDao.createPQuery()
                        .addScale("acct", StringType.INSTANCE)
                        .equal("pid", player.playerbs.id)
                        .queryScale();
                player.accts = accounts;
            }
            if (BitUtil.include(field_accounts, Player.Field_roleids)) {
                List<Integer> roleids = this.playerRoleDao.createPQuery()
                        .addScale("roleid", IntegerType.INSTANCE)
                        .equal("pid", player.playerbs.id)
                        .queryScale();
                player.roleids = new HashSet<>(roleids);
            }
        }
    }
}

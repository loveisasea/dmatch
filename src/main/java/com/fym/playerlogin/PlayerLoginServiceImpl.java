package com.fym.playerlogin;
/**
 * Owned by Planck System
 * Created by fengy on 2016/5/14.
 */

import com.fym.core.err.OpException;
import com.fym.core.util.DateUtil;
import com.fym.core.util.StringUtil;
import com.fym.core.web.WebContextHolder;
import com.fym.player.PlayerService;
import com.fym.player.obj.Player;
import com.fym.playerlogin.entity.PlayerLoginHistory;
import com.fym.playerlogin.entity.PlayerLoginHistoryDao;
import com.fym.playerlogin.obj.PlayerLoginS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Transactional(rollbackFor = Exception.class)
@Service("playerLoginService")
public class PlayerLoginServiceImpl implements PlayerLoginService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerLoginServiceImpl.class);


    //<pid,player>
    private Map<Integer, PlayerLoginS> map_id_logins = new ConcurrentHashMap<>();
    private Map<String, PlayerLoginS> map_ticket_logins = new ConcurrentHashMap<>();

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerLoginHistoryDao playerLoginHistoryDao;

    @Autowired
    private WebContextHolder webContextHolder;


    @Override
    public PlayerLoginS loginPassword(String acct, String password) throws OpException {
        //检查用户是否存在于密码表
        Player player = this.playerService.getByPassword(acct, password);

        PlayerLoginS loginS = buildLoginS(player);

        PlayerLoginS oldLoginS = this.map_id_logins.get(loginS.pid);
        if (oldLoginS != null) {
            this.map_id_logins.remove(oldLoginS.pid);
            this.map_ticket_logins.remove(oldLoginS.t);
        }
        this.map_id_logins.put(loginS.pid, loginS);
        this.map_ticket_logins.put(loginS.t, loginS);
        this.webContextHolder.getSession().setAttribute("loginS", loginS);

        //更新数据库
        PlayerLoginHistory history = new PlayerLoginHistory();
        history.t = loginS.t;
        history.pid = loginS.pid;
        history.loginTime = DateUtil.getCurrent();
        this.playerLoginHistoryDao.create(history);
        return loginS;
    }


    @Override
    public void logout() {
        PlayerLoginS oldLoginS = (PlayerLoginS) this.webContextHolder.getSession().getAttribute("loginS");
        if (oldLoginS != null) {
            synchronized (oldLoginS) {
                this.map_id_logins.remove(oldLoginS.pid);
                this.map_ticket_logins.remove(oldLoginS.t);
                this.webContextHolder.getSession().removeAttribute("loginS");
            }
        }
    }

    @Override
    public PlayerLoginS getLogin() {
        try {
            return (PlayerLoginS) this.webContextHolder.getSession().getAttribute("loginS");
        } catch (Exception e) {
            PlayerLoginS loginS = new PlayerLoginS();
            loginS.nname = StringUtil.Unknown;
            loginS.roleids = Collections.EMPTY_SET;
            return loginS;
        }
    }


    private PlayerLoginS buildLoginS(Player player) throws OpException {
        PlayerLoginS loginS = new PlayerLoginS(player.playerbs.id);
        loginS.nname = player.playerbs.nname;
        loginS.lastTouchTime = DateUtil.getCurrent();
        loginS.t = UUID.randomUUID().toString();
        loginS.roleids = new HashSet<>(player.roleids);
        return loginS;
    }


}

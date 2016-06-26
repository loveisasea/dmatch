package com.fym.playerlogin;
/**
 * Owned by Planck System
 * Created by fengy on 2016/5/14.
 */

import com.fym.core.err.OpException;
import com.fym.core.err.OpResult;
import com.fym.core.util.DateUtil;
import com.fym.core.web.WebContextHolder;
import com.fym.player.PlayerService;
import com.fym.player.obj.Player;
import com.fym.playerbox.PlayerBoxCom;
import com.fym.playerbox.obj.IMsg;
import com.fym.playerlogin.entity.PlayerLoginHistory;
import com.fym.playerlogin.entity.PlayerLoginHistoryDao;
import com.fym.playerlogin.obj.PlayerLoginS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Transactional(rollbackFor = Exception.class)
@Service("playerLoginService")
public class PlayerLoginServiceImpl implements PlayerLoginService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerLoginServiceImpl.class);


    //<pid,player>
    private Map<Integer, PlayerLoginS> map_pid_logins = new ConcurrentHashMap<>();
    private Map<String, PlayerLoginS> map_ticket_logins = new ConcurrentHashMap<>();

    @Autowired
    private PlayerService playerService;

    @Autowired
    private PlayerLoginHistoryDao playerLoginHistoryDao;

    @Autowired
    private WebContextHolder webContextHolder;

    @Autowired
    private PlayerBoxCom playerBoxCom;


    /**
     * 密码登录
     * 1.构建PlayerLoginS并添加到map,并移除已有的登录信息
     * 2.获取playerBox
     * 3.添加到session
     * 4.记录用户登录历史
     *
     * @param acct     账户名
     * @param password 加密密码
     * @return
     * @throws OpException
     */
    @Override
    public PlayerLoginS loginPassword(String acct, String password) throws OpException {
        //检查用户是否存在于密码表
        Player player = this.playerService.getByPassword(acct, password);

        PlayerLoginS loginS = buildLoginS(player);

        PlayerLoginS oldLoginS = this.map_pid_logins.get(loginS.pid);
        if (oldLoginS != null) {
            this.map_pid_logins.remove(oldLoginS.pid);
            this.map_ticket_logins.remove(oldLoginS.t);
        }
        this.map_pid_logins.put(loginS.pid, loginS);
        this.map_ticket_logins.put(loginS.t, loginS);
        this.webContextHolder.getSession().setAttribute("loginS", loginS);

        //更新数据库
        PlayerLoginHistory history = new PlayerLoginHistory();
        history.t = loginS.t;
        history.pid = loginS.pid;
        history.loginTime = DateUtil.getCurrent();
        this.playerLoginHistoryDao.create(history);

        //连接PlayerBox
        this.playerBoxCom.connect(loginS.pid);

        return loginS;
    }


    /**
     * 退出当前登录
     * 1.移除session
     * 2.从map移除PlayerLoginS
     */
    @Override
    public void logout() {
        PlayerLoginS oldLoginS = (PlayerLoginS) this.webContextHolder.getSession().getAttribute("loginS");
        if (oldLoginS != null) {
            synchronized (oldLoginS) {
                this.map_pid_logins.remove(oldLoginS.pid);
                this.map_ticket_logins.remove(oldLoginS.t);
                this.webContextHolder.getSession().removeAttribute("loginS");
            }
        }
    }

    @Override
    public List<IMsg> takeMsg() throws OpException {
        Integer selfpid = this.getLogin().pid;
        List<IMsg> ret = this.playerBoxCom.takeMsg(selfpid);
        return ret;
    }

    /**
     * 获取登录信息
     * 1.session必须有
     * 2.map也必须有
     *
     * @return
     */
    @Override
    public PlayerLoginS getLogin() throws OpException {
        try {
            PlayerLoginS sessionLoginS = (PlayerLoginS) this.webContextHolder.getSession().getAttribute("loginS");
            if (sessionLoginS == null) {
                throw new OpException(OpResult.RELOGIN, "登录已超时");
            }
            PlayerLoginS ticketLoginS = this.map_ticket_logins.get(sessionLoginS.t);
            PlayerLoginS pidLoginS = this.map_pid_logins.get(sessionLoginS.pid);
            if (ticketLoginS == null || pidLoginS == null) {
                throw new OpException(OpResult.RELOGIN, "登录已超时");
            }
            if (ticketLoginS != sessionLoginS || pidLoginS != sessionLoginS) {
                this.map_ticket_logins.remove(sessionLoginS.t);
                this.webContextHolder.getSession().removeAttribute("loginS");
                throw new OpException(OpResult.RELOGIN, "登录已超时");
            }
            return sessionLoginS;
        } catch (Exception e) {
            throw new OpException(OpResult.RELOGIN, "发生异常，请重新登录");
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

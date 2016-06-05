package com.fym.playerbox;

import com.fym.playerbox.obj.IMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Owned by Planck System
 * Created by fengy on 2016/6/1.
 */


@Component
public class PlayerBoxCom implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerBoxCom.class);

    //<pid,player>
    private Map<Integer, PlayerBox> map_id_box = new ConcurrentHashMap<>();


    /**
     * 连接
     *
     * @param pid
     * @return
     */
    public PlayerBox connect(Integer pid) {
        PlayerBox playerBox = map_id_box.get(pid);
        if (playerBox != null) {
            return playerBox;
        } else {
            playerBox = new PlayerBox(pid);
        }
        return playerBox;
    }

    /**
     * 注销
     *
     * @param pid
     */
    public void quit(Integer pid) {
        this.map_id_box.remove(pid);
    }

    /**
     * 外部调用，给玩家添加信息
     *
     * @param pid
     * @param msg
     */
    public void putMsg(Integer pid, IMsg msg) {
        PlayerBox playerBox = this.map_id_box.get(pid);
        if (playerBox == null) {
            LOGGER.error("无法发送消息，玩家<" + pid + ">处于离线退出状态");
        }
        playerBox.msgs.offer(msg);
    }


    /**
     * 外部调用，给玩家添加信息
     *
     * @param boxpids
     * @param msg
     */
    public void putMsg(Collection<Integer> boxpids, IMsg msg) {
        for (Integer boxpid : boxpids) {
            this.putMsg(boxpid, msg);
        }
    }

    /**
     * 阻塞获取玩家收到的消息
     *
     * @param pid
     * @return
     */
    public List<IMsg> takeMsg(Integer pid) {
        PlayerBox playerBox = this.map_id_box.get(pid);
        if (playerBox == null) {
            LOGGER.error("无法发送消息，玩家<" + pid + ">处于离线退出状态");
        }
        List<IMsg> ret = new ArrayList<>();
        try {
            IMsg msg = playerBox.msgs.take();
            ret.add(msg);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Thread.sleep(40);
            while (true) {
                IMsg msg = playerBox.msgs.poll();
                if (msg != null) {
                    ret.add(msg);
                } else {
                    break;
                }

            }
        } catch (InterruptedException e) {
        }
        return ret;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

    }
}

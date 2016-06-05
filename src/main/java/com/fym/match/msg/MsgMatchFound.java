package com.fym.match.msg;

import com.fym.game.enm.GameType;
import com.fym.player.obj.Player;

import java.util.List;
import java.util.UUID;

/**
 * Owned by Planck System
 * Created by fengy on 2016/6/2.
 */
public class MsgMatchFound {
    public UUID uuid;
    public GameType gameType;
    public List<Player> players;
}
 

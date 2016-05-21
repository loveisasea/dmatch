package com.fym.player.obj;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fym.player.entity.Playerbs;

import java.util.Collection;
import java.util.Set;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/17.
 */
public class Player {

    public final static int Field_accounts = 0x1;
    public final static int Field_roleids = 0x1;

    //值域的mask
    public int _field;


    public Player(Playerbs playerbs) {
        this.playerbs = playerbs;
    }

    @JsonUnwrapped
    public Playerbs playerbs;

    public Set<Integer> roleids;

    //账号
    public Collection<String> accts;

}
 

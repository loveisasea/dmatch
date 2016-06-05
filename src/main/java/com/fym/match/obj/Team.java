package com.fym.match.obj;

import java.util.HashSet;
import java.util.Set;

/**
 * Owned by Planck System
 * Created by fengy on 2016/6/2.
 * 队伍
 */
public class Team {
    //群主id
    public Integer leaderpid;
    //已在队中
    public Set<Integer> apids;
    //邀请中
    public Set<Integer> ppids;

    public Team(Integer leaderpid) {
        this.leaderpid = leaderpid;
        this.apids = new HashSet<>();
        this.ppids = new HashSet<>();
        this.apids.add(leaderpid);
    }
}
 

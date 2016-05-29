package com.fym.core.status;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * Created by fengy on 2016/4/11.
 */
public class ProceedStatus {
    public Object status;
    public Set<Object> proceeds;

    public ProceedStatus() {
        this.proceeds = new HashSet<>();
    }

    public ProceedStatus(Object status) {
        this.status = status;
        this.proceeds = new HashSet<>();
    }


    public ProceedStatus copy() {
        ProceedStatus ret = new ProceedStatus(this.status);
        ret.proceeds = new HashSet<>(this.proceeds);
        return ret;
    }
} 

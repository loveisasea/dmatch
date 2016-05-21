package com.fym.role.obj;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fym.role.entity.Rolebs;

import java.util.Set;

/**
 * Created by fengy on 2016/1/30.
 */
public class Role {
    public final static int Field_permissionKeys = 0x1;
    //值域的mask
    public int _field;


    public Role(Rolebs rolebs) {
        this.rolebs = rolebs;
    }

    @JsonUnwrapped
    public Rolebs rolebs;


    public Set<String> permissions;

}

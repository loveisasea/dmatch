package com.fym.role.cmd;

import com.fym.core.web.cmd.BaseCmd;

import java.util.Collection;

/**
 * Created by fengy on 2016/1/29.
 */
public class RoleCreateCmd extends BaseCmd {
    public String name;
    public String description;
    public Collection<String> permissions;
}

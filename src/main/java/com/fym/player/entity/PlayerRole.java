package com.fym.player.entity;

import javax.persistence.*;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/17.
 * 账号
 */
@Entity
@Table(name = "playerrole", indexes = {
        @Index(columnList = "pid"),
        @Index(columnList = "roleid")
})
public class PlayerRole {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;


    /**
     * 玩家id
     */
    @Column(length = 64)
    public Integer pid;


    /**
     * 角色名称
     */
    @Column(length = 255)
    public String roleid;


}
 

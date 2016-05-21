package com.fym.player.entity;

import javax.persistence.*;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/17.
 * 账号
 */
@Entity
@Table(name = "playeraccount", indexes = {
        @Index(columnList = "pid"),
        @Index(columnList = "acct", unique = true)
})

public class PlayerAccount {
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
     * 登录名
     */
    @Column(length = 255)
    public String acct;


}
 

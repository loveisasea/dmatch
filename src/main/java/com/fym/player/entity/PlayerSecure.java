package com.fym.player.entity;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/15.
 * 玩家
 */
@Entity
@Table(name = "playersecure", indexes = {
        @Index(columnList = "pid", unique = true)
})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class PlayerSecure {

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
     * 密码
     */
    @Column(length = 256)
    public String password;

}
 

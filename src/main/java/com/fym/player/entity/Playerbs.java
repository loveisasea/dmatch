package com.fym.player.entity;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Owned by Planck System
 * Created by fengy on 2016/5/15.
 * 玩家
 */
@Entity
@Table(name = "playerbs", indexes = {
})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Playerbs {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;


    /**
     * 玩家显示名称
     */
    @Column(length = 64)
    public String nname;


    /**
     * 积分1
     */
    @Column()
    public Integer score_1;


    /**
     * 积分2
     */
    @Column()
    public Integer score_2;


    /**
     * 积分3
     */
    @Column()
    public Integer score_3;



}
 

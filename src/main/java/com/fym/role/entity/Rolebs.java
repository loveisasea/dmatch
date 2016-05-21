package com.fym.role.entity;

import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;

/**
 * Created by fengy on 2016/1/30.
 */
@Entity
@Table(name = "rolebs", indexes = {
        @Index(columnList = "name", unique = true)
})
@org.hibernate.annotations.Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Rolebs {

    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;


    /**
     * 角色名称
     */
    @Column(length = 64)
    public String name;


    /**
     * 角色描述
     */
    @Column(length = 256)
    public String description;



}

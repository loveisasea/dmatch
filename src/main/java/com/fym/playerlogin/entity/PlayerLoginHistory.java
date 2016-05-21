package com.fym.playerlogin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by fengy on 2016/2/2.
 */
@Entity
@Table(name = "playerloginhistory", indexes = {
        @Index(columnList = "t"),
        @Index(columnList = "pid")
})
public class PlayerLoginHistory {
    @Id
    @Column
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer id;

    /**
     * 登录票据
     */
    @Column(length = 255)
    public String t;

    /**
     * 用户名
     */
    @Column(length = 255)
    public Integer pid;

    /**
     * 登录时间
     */
    @Column
    @Temporal(TemporalType.TIMESTAMP)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    public Date loginTime;


}
